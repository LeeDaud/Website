param(
  [string]$ConfigPath = '',
  [string]$Branch = '',
  [string]$FrontendNodeOptions = '--max-old-space-size=6144',
  [string]$FrontendBuildArgs = '--minify=esbuild',
  [string]$HomeDist = '/var/www/licheng.website',
  [string]$BlogDist = '/var/www/blog.licheng.website',
  [string]$CvDist = '/var/www/cv.licheng.website',
  [string]$AdminDist = '/var/www/admin.licheng.website',
  [string]$BackendRuntimeDir = '/root/website/runtime/backend',
  [string]$BackendJarName = 'LeeDaud-server.jar',
  [switch]$SkipFrontendBuild,
  [switch]$SkipBackendBuild,
  [switch]$SkipUpload,
  [switch]$SkipRemoteRestart,
  [switch]$NoCleanRemoteDist,
  [switch]$NoAutoStashBeforeDeploy,
  [switch]$PreserveServerDeployEnv,
  [int]$SshRetryCount = 3,
  [int]$SshRetryDelaySeconds = 5,
  [int]$SshConnectTimeoutSeconds = 20
)

$ErrorActionPreference = 'Stop'

function Write-Step([string]$Message) {
  Write-Host "[PREBUILT] $Message" -ForegroundColor Cyan
}

function Assert-Command([string]$Name) {
  if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
    throw "Missing command: $Name"
  }
}

function Assert-Path([string]$Path, [string]$Message) {
  if (-not (Test-Path -LiteralPath $Path)) {
    throw "${Message}: $Path"
  }
}

function Invoke-Checked {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Name,
    [Parameter(Mandatory = $true)]
    [scriptblock]$Action
  )

  & $Action
  if ($LASTEXITCODE -ne 0) {
    throw "$Name failed (exit code: $LASTEXITCODE)"
  }
}

function Invoke-RemoteSsh {
  param(
    [Parameter(Mandatory = $true)][string]$RemoteCommand,
    [Parameter(Mandatory = $true)][string]$ServerHost,
    [Parameter(Mandatory = $true)][string]$ServerUser,
    [Parameter(Mandatory = $true)][int]$SshPort,
    [string]$KeyPath = '',
    [int]$SshConnectTimeoutSeconds = 20
  )

  $sshArgs = @()
  if ($KeyPath) {
    $sshArgs += @('-i', $KeyPath)
  }
  $sshArgs += @(
    '-o', "ConnectTimeout=$SshConnectTimeoutSeconds",
    '-o', 'ConnectionAttempts=1',
    '-o', 'ServerAliveInterval=15',
    '-o', 'ServerAliveCountMax=3',
    '-p', "$SshPort",
    "$ServerUser@$ServerHost",
    $RemoteCommand
  )

  Invoke-Checked -Name 'ssh' -Action { & ssh @sshArgs }
}

function Copy-ToRemoteDir {
  param(
    [Parameter(Mandatory = $true)][string]$LocalPath,
    [Parameter(Mandatory = $true)][string]$RemoteDir,
    [Parameter(Mandatory = $true)][string]$ServerHost,
    [Parameter(Mandatory = $true)][string]$ServerUser,
    [Parameter(Mandatory = $true)][int]$SshPort,
    [string]$KeyPath = '',
    [switch]$Recurse
  )

  $scpArgs = @()
  if ($KeyPath) {
    $scpArgs += @('-i', $KeyPath)
  }
  $scpArgs += @('-P', "$SshPort")
  if ($Recurse) {
    $scpArgs += '-r'
  }
  $scpArgs += @($LocalPath, "${ServerUser}@${ServerHost}:${RemoteDir}/")

  Invoke-Checked -Name "scp $LocalPath -> $RemoteDir" -Action { & scp @scpArgs }
}

Assert-Command 'git'
Assert-Command 'npm'
Assert-Command 'mvn'
Assert-Command 'ssh'
Assert-Command 'scp'
Assert-Command 'powershell'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$invokeScript = Join-Path $PSScriptRoot 'invoke-remote-deploy.ps1'
Assert-Path -Path $invokeScript -Message 'invoke-remote-deploy.ps1 not found'

if (-not $ConfigPath) {
  $ConfigPath = Join-Path $PSScriptRoot 'deploy.local.psd1'
}
$examplePath = Join-Path $PSScriptRoot 'deploy.local.example.psd1'
if (-not (Test-Path -LiteralPath $ConfigPath)) {
  if ($ConfigPath -eq (Join-Path $PSScriptRoot 'deploy.local.psd1') -and (Test-Path -LiteralPath $examplePath)) {
    Write-Warning "Deploy config not found, fallback to example: $examplePath"
    $ConfigPath = $examplePath
  } else {
    throw "Deploy config not found: $ConfigPath`nCopy example and edit: $examplePath"
  }
}

$cfg = Import-PowerShellDataFile -Path $ConfigPath
$serverHost = [string]$cfg.ServerHost
$serverUser = [string]$cfg.ServerUser
$serverAppRoot = [string]$cfg.ServerAppRoot
$remoteScriptPath = if ($cfg.RemoteScriptPath) { [string]$cfg.RemoteScriptPath } else { '/root/website/deploy/server/deploy-all.sh' }
$sshPort = if ($cfg.SshPort) { [int]$cfg.SshPort } else { 22 }
$keyPath = if ($cfg.KeyPath) { [string]$cfg.KeyPath } else { '' }
if (-not $Branch) {
  if ($cfg.Branch) {
    $Branch = [string]$cfg.Branch
  } else {
    $Branch = (& git -C $repoRoot branch --show-current).Trim()
  }
}

if ([string]::IsNullOrWhiteSpace($serverHost)) { throw 'Missing ServerHost in deploy config.' }
if ([string]::IsNullOrWhiteSpace($serverUser)) { throw 'Missing ServerUser in deploy config.' }
if ([string]::IsNullOrWhiteSpace($serverAppRoot)) { throw 'Missing ServerAppRoot in deploy config.' }
if ([string]::IsNullOrWhiteSpace($Branch)) { throw 'Missing Branch value.' }

$frontendApps = @(
  @{ Name = 'Frontend-Home'; LocalDir = 'Frontend-Home'; RemoteDir = $HomeDist },
  @{ Name = 'Blog';          LocalDir = 'Blog';          RemoteDir = $BlogDist },
  @{ Name = 'Cv';            LocalDir = 'Cv';            RemoteDir = $CvDist },
  @{ Name = 'Admin';         LocalDir = 'Admin';         RemoteDir = $AdminDist }
)

$backendJarLocal = Join-Path $repoRoot 'Backend\LeeDaud-server\target\LeeDaud-server-1.0-SNAPSHOT.jar'

Push-Location $repoRoot
try {
  if (-not $SkipFrontendBuild) {
    $oldNodeOptions = $env:NODE_OPTIONS
    $oldHusky = $env:HUSKY
    $env:NODE_OPTIONS = $FrontendNodeOptions
    $env:HUSKY = '0'
    try {
      foreach ($app in $frontendApps) {
        $appPath = Join-Path $repoRoot $app.LocalDir
        $pkg = Join-Path $appPath 'package.json'
        Assert-Path -Path $pkg -Message 'package.json not found'

        Write-Step "Building frontend: $($app.Name)"
        Push-Location $appPath
        try {
          $hasPnpmLock = (Test-Path -LiteralPath (Join-Path $appPath 'pnpm-lock.yaml'))
          $hasNpmLock = (
            (Test-Path -LiteralPath (Join-Path $appPath 'package-lock.json')) -or
            (Test-Path -LiteralPath (Join-Path $appPath 'npm-shrinkwrap.json'))
          )

          if ($hasPnpmLock) {
            if (-not (Get-Command corepack -ErrorAction SilentlyContinue)) {
              throw "pnpm-lock.yaml found in $($app.LocalDir), but 'corepack' is not available. Install Node.js with Corepack or remove pnpm lockfile."
            }
            Invoke-Checked -Name "pnpm install ($($app.Name))" -Action { & corepack pnpm install --frozen-lockfile }
            if ($FrontendBuildArgs) {
              Invoke-Checked -Name "pnpm run build ($($app.Name))" -Action { & corepack pnpm run build -- $FrontendBuildArgs }
            } else {
              Invoke-Checked -Name "pnpm run build ($($app.Name))" -Action { & corepack pnpm run build }
            }
          } elseif ($hasNpmLock) {
            Invoke-Checked -Name "npm ci ($($app.Name))" -Action { & npm ci --no-audit --no-fund }
            if ($FrontendBuildArgs) {
              Invoke-Checked -Name "npm run build ($($app.Name))" -Action { & npm run build -- $FrontendBuildArgs }
            } else {
              Invoke-Checked -Name "npm run build ($($app.Name))" -Action { & npm run build }
            }
          } else {
            Invoke-Checked -Name "npm install ($($app.Name))" -Action { & npm install --no-audit --no-fund }
            if ($FrontendBuildArgs) {
              Invoke-Checked -Name "npm run build ($($app.Name))" -Action { & npm run build -- $FrontendBuildArgs }
            } else {
              Invoke-Checked -Name "npm run build ($($app.Name))" -Action { & npm run build }
            }
          }
        } finally {
          Pop-Location
        }

        Assert-Path -Path (Join-Path $appPath 'dist') -Message "dist not found after build ($($app.Name))"
      }
    } finally {
      $env:NODE_OPTIONS = $oldNodeOptions
      $env:HUSKY = $oldHusky
    }
  } else {
    Write-Step 'Skip local frontend build.'
  }

  if (-not $SkipBackendBuild) {
    Write-Step 'Building backend jar locally ...'
    Invoke-Checked -Name 'mvn package' -Action { & mvn -f (Join-Path $repoRoot 'Backend\pom.xml') -DskipTests package }
    Assert-Path -Path $backendJarLocal -Message 'Backend jar not found after build'
  } else {
    Write-Step 'Skip local backend build.'
    Assert-Path -Path $backendJarLocal -Message 'Backend jar not found (required for upload)'
  }

  if (-not $SkipUpload) {
    foreach ($app in $frontendApps) {
      $dist = Join-Path (Join-Path $repoRoot $app.LocalDir) 'dist'
      Assert-Path -Path $dist -Message "dist not found ($($app.Name))"

      $remoteDirQ = $app.RemoteDir
      if ($NoCleanRemoteDist) {
        Invoke-RemoteSsh -ServerHost $serverHost -ServerUser $serverUser -SshPort $sshPort -KeyPath $keyPath -SshConnectTimeoutSeconds $SshConnectTimeoutSeconds -RemoteCommand "mkdir -p -- $remoteDirQ"
      } else {
        Invoke-RemoteSsh -ServerHost $serverHost -ServerUser $serverUser -SshPort $sshPort -KeyPath $keyPath -SshConnectTimeoutSeconds $SshConnectTimeoutSeconds -RemoteCommand "mkdir -p -- $remoteDirQ && find $remoteDirQ -mindepth 1 -maxdepth 1 -exec rm -rf -- {} +"
      }

      $items = @(Get-ChildItem -LiteralPath $dist -Force)
      if ($items.Count -eq 0) {
        throw "dist is empty: $dist"
      }

      Write-Step "Uploading frontend dist: $($app.Name) -> $($app.RemoteDir)"
      foreach ($item in $items) {
        Copy-ToRemoteDir -LocalPath $item.FullName -RemoteDir $app.RemoteDir -ServerHost $serverHost -ServerUser $serverUser -SshPort $sshPort -KeyPath $keyPath -Recurse:$item.PSIsContainer
      }
    }

    $backendDirQ = $BackendRuntimeDir
    Invoke-RemoteSsh -ServerHost $serverHost -ServerUser $serverUser -SshPort $sshPort -KeyPath $keyPath -SshConnectTimeoutSeconds $SshConnectTimeoutSeconds -RemoteCommand "mkdir -p -- $backendDirQ"
    Write-Step "Uploading backend jar -> $BackendRuntimeDir/$BackendJarName"

    $scpJarArgs = @()
    if ($keyPath) {
      $scpJarArgs += @('-i', $keyPath)
    }
    $scpJarArgs += @('-P', "$sshPort", $backendJarLocal, "${serverUser}@${serverHost}:${BackendRuntimeDir}/${BackendJarName}")
    Invoke-Checked -Name 'scp backend jar' -Action { & scp @scpJarArgs }
  } else {
    Write-Step 'Skip upload.'
  }

  if (-not $SkipRemoteRestart) {
    Write-Step "Triggering remote restart (no server build): $serverUser@$serverHost"
    $argsList = @(
      '-ExecutionPolicy', 'Bypass',
      '-File', $invokeScript,
      '-ServerHost', $serverHost,
      '-ServerUser', $serverUser,
      '-Branch', $Branch,
      '-SshPort', $sshPort,
      '-SshRetryCount', $SshRetryCount,
      '-SshRetryDelaySeconds', $SshRetryDelaySeconds,
      '-SshConnectTimeoutSeconds', $SshConnectTimeoutSeconds,
      '-ServerAppRoot', $serverAppRoot,
      '-RemoteScriptPath', $remoteScriptPath,
      '-SkipGitPull',
      '-SkipFrontendBuild',
      '-SkipBackendBuild'
    )
    if ($keyPath) {
      $argsList += @('-KeyPath', $keyPath)
    }
    if ($NoAutoStashBeforeDeploy) {
      $argsList += '-NoAutoStashBeforeDeploy'
    }
    if ($PreserveServerDeployEnv) {
      $argsList += '-PreserveServerDeployEnv'
    }

    & powershell @argsList
    if ($LASTEXITCODE -ne 0) {
      throw "Remote restart failed (exit code: $LASTEXITCODE)"
    }
  } else {
    Write-Step 'Skip remote restart.'
  }
} finally {
  Pop-Location
}

Write-Step 'Prebuilt publish completed.'
