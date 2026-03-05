param(
  [string]$ConfigPath = '',
  [string]$Branch = '',
  [string]$CommitMessage = '',
  [string]$FrontendNodeOptions = '',
  [switch]$SkipCommit,
  [switch]$SkipPush,
  [switch]$SkipRemoteDeploy,
  [switch]$SkipGitPull,
  [switch]$SkipFrontendBuild,
  [switch]$SkipBackendBuild
)

$ErrorActionPreference = 'Stop'

function Write-Step([string]$Message) {
  Write-Host "[ONE-CLICK] $Message" -ForegroundColor Cyan
}

function Assert-Command([string]$Name) {
  if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
    throw "Missing command: $Name"
  }
}

function Invoke-Git([string[]]$GitArgs) {
  & git @GitArgs
  if ($LASTEXITCODE -ne 0) {
    throw "git $($GitArgs -join ' ') failed (exit code: $LASTEXITCODE)"
  }
}

function Assert-ConfigValue([object]$Value, [string]$Name) {
  if ($null -eq $Value -or [string]::IsNullOrWhiteSpace([string]$Value)) {
    throw "Missing '$Name' in deploy config."
  }
}

Assert-Command 'git'
Assert-Command 'powershell'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$invokeScript = Join-Path $PSScriptRoot 'invoke-remote-deploy.ps1'
if (-not (Test-Path $invokeScript)) {
  throw "invoke-remote-deploy.ps1 not found: $invokeScript"
}

if (-not $ConfigPath) {
  $ConfigPath = Join-Path $PSScriptRoot 'deploy.local.psd1'
}

$examplePath = Join-Path $PSScriptRoot 'deploy.local.example.psd1'
if (-not (Test-Path $ConfigPath)) {
  if ($ConfigPath -eq (Join-Path $PSScriptRoot 'deploy.local.psd1') -and (Test-Path $examplePath)) {
    Write-Warning "Deploy config not found, fallback to example: $examplePath"
    $ConfigPath = $examplePath
  } else {
    throw "Deploy config not found: $ConfigPath`nCopy example and edit: $examplePath"
  }
}

$cfg = Import-PowerShellDataFile -Path $ConfigPath
Assert-ConfigValue $cfg.ServerHost 'ServerHost'
Assert-ConfigValue $cfg.ServerUser 'ServerUser'
Assert-ConfigValue $cfg.ServerAppRoot 'ServerAppRoot'

$serverHost = [string]$cfg.ServerHost
$serverUser = [string]$cfg.ServerUser
$serverAppRoot = [string]$cfg.ServerAppRoot
$remoteScriptPath = if ($cfg.RemoteScriptPath) { [string]$cfg.RemoteScriptPath } else { '/root/website/deploy/server/deploy-all.sh' }
$sshPort = if ($cfg.SshPort) { [int]$cfg.SshPort } else { 22 }
$keyPath = if ($cfg.KeyPath) { [string]$cfg.KeyPath } else { '' }
if (-not $FrontendNodeOptions) {
  $FrontendNodeOptions = if ($cfg.FrontendNodeOptions) { [string]$cfg.FrontendNodeOptions } else { '--max-old-space-size=2048' }
}

Push-Location $repoRoot
try {
  if (-not $Branch) {
    if ($cfg.Branch) {
      $Branch = [string]$cfg.Branch
    } else {
      $Branch = (& git branch --show-current).Trim()
    }
  }
  Assert-ConfigValue $Branch 'Branch'

  $currentBranch = (& git branch --show-current).Trim()
  if ($currentBranch -and $currentBranch -ne $Branch) {
    Write-Warning "Current branch is '$currentBranch', but deploy branch is '$Branch'."
  }

  if (-not $SkipCommit) {
    Write-Step 'Staging all local changes ...'
    Invoke-Git -GitArgs @('add', '-A')

    & git diff --cached --quiet
    if ($LASTEXITCODE -eq 0) {
      Write-Step 'No staged changes, skip commit.'
    } elseif ($LASTEXITCODE -eq 1) {
      if (-not $CommitMessage) {
        $CommitMessage = "chore: deploy $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
      }
      Write-Step "Committing changes: $CommitMessage"
      Invoke-Git -GitArgs @('commit', '-m', $CommitMessage)
    } else {
      throw "git diff --cached failed (exit code: $LASTEXITCODE)"
    }
  } else {
    Write-Step 'Skip local commit by -SkipCommit.'
  }

  if (-not $SkipPush) {
    Write-Step "Pushing branch '$Branch' to origin ..."
    Invoke-Git -GitArgs @('push', 'origin', $Branch)
  } else {
    Write-Step 'Skip git push by -SkipPush.'
  }

  if (-not $SkipRemoteDeploy) {
    $argsList = @(
      '-ExecutionPolicy', 'Bypass',
      '-File', $invokeScript,
      '-ServerHost', $serverHost,
      '-ServerUser', $serverUser,
      '-Branch', $Branch,
      '-SshPort', $sshPort,
      '-ServerAppRoot', $serverAppRoot,
      '-RemoteScriptPath', $remoteScriptPath,
      '-FrontendNodeOptions', $FrontendNodeOptions
    )

    if ($keyPath) {
      $argsList += @('-KeyPath', $keyPath)
    }
    if ($SkipGitPull) {
      $argsList += '-SkipGitPull'
    }
    if ($SkipFrontendBuild) {
      $argsList += '-SkipFrontendBuild'
    }
    if ($SkipBackendBuild) {
      $argsList += '-SkipBackendBuild'
    }

    Write-Step "Triggering remote deploy: $serverUser@$serverHost"
    & powershell @argsList
    if ($LASTEXITCODE -ne 0) {
      throw "Remote deploy failed (exit code: $LASTEXITCODE)"
    }
  } else {
    Write-Step 'Skip remote deploy by -SkipRemoteDeploy.'
  }
} finally {
  Pop-Location
}

Write-Step 'One-click deploy finished.'
