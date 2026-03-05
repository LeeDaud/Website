param(
  [switch]$InstallDeps,
  [switch]$RebuildBackend,
  [switch]$SkipBackend,
  [switch]$SkipInfra,
  [switch]$DryRun
)

$ErrorActionPreference = 'Stop'

function Write-Step([string]$Message) {
  Write-Host "[START] $Message" -ForegroundColor Cyan
}

function Assert-Command([string]$Name) {
  if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
    throw "Missing command: $Name"
  }
}

function Assert-Path([string]$PathValue, [string]$Hint) {
  if (-not (Test-Path $PathValue)) {
    throw "$Hint`nMissing path: $PathValue"
  }
}

function Resolve-JavaRuntime([string]$ProjectRoot) {
  $parentRoot = Split-Path -Parent $ProjectRoot
  $fromPath = Get-Command java -ErrorAction SilentlyContinue
  if ($fromPath -and $fromPath.Source) {
    $javaExe = $fromPath.Source
    $javaHome = $null
    if (Test-Path $javaExe) {
      $javaHome = Split-Path -Parent (Split-Path -Parent $javaExe)
    }
    return [pscustomobject]@{
      JavaExe  = $javaExe
      JavaHome = $javaHome
      Source   = 'PATH'
    }
  }

  if ($env:JAVA_HOME) {
    $javaExe = Join-Path $env:JAVA_HOME 'bin\\java.exe'
    if (Test-Path $javaExe) {
      return [pscustomobject]@{
        JavaExe  = $javaExe
        JavaHome = $env:JAVA_HOME
        Source   = 'JAVA_HOME'
      }
    }
  }

  $searchRoots = @(
    (Join-Path $ProjectRoot 'dependencies'),
    (Join-Path $ProjectRoot 'dependencies\jdk'),
    (Join-Path $ProjectRoot 'dependencies\jdk21'),
    (Join-Path $parentRoot 'dependencies'),
    (Join-Path $parentRoot 'dependencies\jdk'),
    (Join-Path $parentRoot 'dependencies\jdk21'),
    (Join-Path $env:ProgramFiles 'Java'),
    (Join-Path $env:ProgramFiles 'Eclipse Adoptium'),
    (Join-Path $env:ProgramFiles 'Microsoft'),
    (Join-Path $env:ProgramFiles 'Zulu'),
    (Join-Path $env:ProgramFiles 'Amazon Corretto'),
    (Join-Path $env:ProgramFiles 'BellSoft'),
    (Join-Path $env:LOCALAPPDATA 'Programs\\Microsoft\\jdk'),
    (Join-Path $env:USERPROFILE '.jdks')
  ) | Where-Object { $_ -and (Test-Path $_) }

  $javaCandidates = @()
  foreach ($root in $searchRoots) {
    $javaCandidates += Get-ChildItem -Path $root -Recurse -File -Filter java.exe -ErrorAction SilentlyContinue |
      Where-Object { $_.FullName -match '\\bin\\java\.exe$' }
  }

  $picked = $javaCandidates |
    Sort-Object -Property LastWriteTime -Descending |
    Select-Object -First 1

  if ($picked) {
    $javaExe = $picked.FullName
    $javaHome = Split-Path -Parent (Split-Path -Parent $javaExe)
    return [pscustomobject]@{
      JavaExe  = $javaExe
      JavaHome = $javaHome
      Source   = 'Search'
    }
  }

  throw @"
Java runtime was not found.
Please install JDK 21+ and configure PATH or JAVA_HOME.

Quick workaround:
  1) Install JDK 21 (Temurin/Oracle/Corretto)
  2) Set JAVA_HOME to your JDK path
  3) Add %JAVA_HOME%\\bin to PATH

If you only need frontends now, run:
  ./start-all.ps1 -SkipBackend
"@
}

function Start-DevWindow(
  [string]$Title,
  [string]$WorkingDir,
  [string]$Command
) {
  Assert-Path $WorkingDir "Start failed, working directory does not exist."

  if ($DryRun) {
    Write-Host "DRYRUN => [$Title] $Command"
    return
  }

  $script = @"
`$Host.UI.RawUI.WindowTitle = '$Title'
Set-Location '$WorkingDir'
$Command
"@

  Start-Process -FilePath powershell -ArgumentList @(
    '-NoExit',
    '-ExecutionPolicy',
    'Bypass',
    '-Command',
    $script
  ) | Out-Null
}

function Get-ListeningPid([int]$Port) {
  $line = netstat -ano | Select-String (":$Port\s+.*LISTENING\s+(\d+)$") | Select-Object -First 1
  if (-not $line) { return $null }
  return [int]$line.Matches[0].Groups[1].Value
}

function Ensure-FrontendDeps([string]$AppName, [string]$AppDir) {
  $viteScript = Join-Path $AppDir 'node_modules\\.bin\\vite.ps1'
  if (Test-Path $viteScript) {
    return
  }

  if (-not $InstallDeps) {
    throw "[$AppName] Missing dependencies (not found: $viteScript).`nRun: ./start-all.ps1 -InstallDeps"
  }

  Assert-Command 'npm'
  Write-Step "[$AppName] npm install ..."

  if ($DryRun) {
    Write-Host "DRYRUN => npm install (cwd=$AppDir)"
  } else {
    Push-Location $AppDir
    try {
      npm install
    } finally {
      Pop-Location
    }
  }

  if (-not (Test-Path $viteScript)) {
    throw "[$AppName] vite script still missing after npm install: $viteScript"
  }
}

function Get-BackendJar([string]$RootDir) {
  $candidates = @(
    (Join-Path $RootDir 'Backend\\LeeDaud-server\\target\\LeeDaud-server-1.0-SNAPSHOT.jar'),
    (Join-Path $RootDir '_run_backend\\LeeDaud-server\\target\\LeeDaud-server-1.0-SNAPSHOT.jar')
  )

  foreach ($candidate in $candidates) {
    if (Test-Path $candidate) {
      return $candidate
    }
  }
  return $null
}

function Start-All {
  $root = if ($PSScriptRoot) { $PSScriptRoot } else { (Get-Location).Path }
  Set-Location $root

  Assert-Command 'powershell'
  Assert-Command 'node'

  $effectiveSkipBackend = $SkipBackend
  $javaRuntime = $null
  if (-not $effectiveSkipBackend -or $RebuildBackend) {
    $javaRuntime = Resolve-JavaRuntime -ProjectRoot $root
    if ($javaRuntime.JavaHome) {
      $env:JAVA_HOME = $javaRuntime.JavaHome
    }
    Write-Step "Using Java ($($javaRuntime.Source)): $($javaRuntime.JavaExe)"
  }

  $backendRoot = Join-Path $root 'Backend'
  $backendPom = Join-Path $backendRoot 'pom.xml'
  $infraScript = Join-Path $root 'start-infra.ps1'

  if (-not $effectiveSkipBackend -and -not $SkipInfra) {
    Assert-Path $infraScript 'Infrastructure script not found (start-infra.ps1).'
    Write-Step 'Ensuring infrastructure (MariaDB/Redis) ...'
    try {
      & $infraScript -DryRun:$DryRun
    } catch {
      $infraError = $_.Exception.Message
      Write-Warning "Infrastructure startup failed: $infraError"
      Write-Step 'Continue with frontends only. Resolve MariaDB/Redis and run ./restart-backend.ps1 afterwards.'
      $effectiveSkipBackend = $true
    }
  } elseif (-not $effectiveSkipBackend -and $SkipInfra) {
    Write-Step 'Skip infrastructure startup by -SkipInfra'
  }

  if (-not $effectiveSkipBackend -or $RebuildBackend) {
    Assert-Path $backendPom 'Backend project not found (Backend/pom.xml).'
  }

  if ($RebuildBackend) {
    Assert-Command 'mvn'
    Write-Step 'Rebuilding backend (mvn -DskipTests package) ...'

    if ($DryRun) {
      Write-Host "DRYRUN => mvn -f $backendPom -DskipTests package"
    } else {
      mvn -f $backendPom -DskipTests package
    }
  }

  $backendJar = $null
  $backendAlreadyRunning = $false
  if (-not $effectiveSkipBackend) {
    $backendPid = Get-ListeningPid -Port 5922
    if ($backendPid) {
      $backendAlreadyRunning = $true
      Write-Step "Backend already listening on 5922 (PID $backendPid), skip backend startup."
    } else {
      $backendJar = Get-BackendJar -RootDir $root
      if (-not $backendJar) {
        throw "Backend jar not found. Run once with: ./start-all.ps1 -RebuildBackend"
      }
    }
  }

  $apps = @(
    @{ Name = 'Blog';          Dir = (Join-Path $root 'Blog');          Port = 5173 },
    @{ Name = 'Admin';         Dir = (Join-Path $root 'Admin');         Port = 5174 },
    @{ Name = 'Frontend-Home'; Dir = (Join-Path $root 'Frontend-Home'); Port = 5175 },
    @{ Name = 'Cv';            Dir = (Join-Path $root 'Cv');            Port = 5176 }
  )

  $frontendNeedsStart = @{}
  foreach ($app in $apps) {
    $existingPid = Get-ListeningPid -Port $app.Port
    if ($existingPid) {
      Write-Step "[$($app.Name)] already listening on $($app.Port) (PID $existingPid), skip startup."
      $frontendNeedsStart[$app.Name] = $false
      continue
    }
    $frontendNeedsStart[$app.Name] = $true
    Ensure-FrontendDeps -AppName $app.Name -AppDir $app.Dir
  }

  if (-not $effectiveSkipBackend -and -not $backendAlreadyRunning) {
    Write-Step 'Starting backend service ...'
    $backendCmd = "& '$($javaRuntime.JavaExe)' -jar '$backendJar' --spring.profiles.active=dev"
    Start-DevWindow -Title 'Backend-5922' -WorkingDir $backendRoot -Command $backendCmd
  } elseif ($effectiveSkipBackend) {
    Write-Step 'Skip backend startup for this run.'
  }

  foreach ($app in $apps) {
    if (-not $frontendNeedsStart[$app.Name]) { continue }
    Write-Step "Starting frontend: $($app.Name) ..."
    $viteCmd = "& '.\\node_modules\\.bin\\vite.ps1'"
    Start-DevWindow -Title $app.Name -WorkingDir $app.Dir -Command $viteCmd
  }

  Write-Host ''
  Write-Host 'All services start commands have been triggered.' -ForegroundColor Green
  Write-Host '  Blog          -> http://localhost:5173'
  Write-Host '  Admin         -> http://localhost:5174'
  Write-Host '  Frontend-Home -> http://localhost:5175'
  Write-Host '  Cv            -> http://localhost:5176'
  if (-not $effectiveSkipBackend) {
    Write-Host '  Backend API   -> http://localhost:5922'
  }
  Write-Host ''
  Write-Host 'Options:'
  Write-Host '  -InstallDeps    Auto run npm install if frontend deps are missing'
  Write-Host '  -RebuildBackend Build backend jar before startup'
  Write-Host '  -SkipInfra      Skip MariaDB/Redis startup checks'
  Write-Host '  -SkipBackend    Start frontends only (no Java required)'
  Write-Host '  -DryRun         Print commands only, do not launch processes'
}

Start-All


