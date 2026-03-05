param(
  [switch]$DryRun
)

$ErrorActionPreference = 'Stop'

function Write-Step([string]$Message) {
  Write-Host "[INFRA] $Message" -ForegroundColor Cyan
}

function Get-ProjectRoot {
  if ($PSScriptRoot) { return $PSScriptRoot }
  return (Get-Location).Path
}

function Resolve-DependenciesRoot([string]$ProjectRoot) {
  $parent = Split-Path -Parent $ProjectRoot
  $candidates = @(
    (Join-Path $ProjectRoot 'dependencies'),
    (Join-Path $parent 'dependencies')
  )
  foreach ($c in $candidates) {
    if (Test-Path $c) { return $c }
  }
  throw "Dependencies folder not found. Expected at '$ProjectRoot\\dependencies' or '$parent\\dependencies'."
}

function Get-ListeningPid([int]$Port) {
  $line = netstat -ano | Select-String (":$Port\s+.*LISTENING\s+(\d+)$") | Select-Object -First 1
  if (-not $line) { return $null }
  return [int]$line.Matches[0].Groups[1].Value
}

function Wait-Port([int]$Port, [int]$TimeoutSeconds) {
  $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
  while ((Get-Date) -lt $deadline) {
    $processId = Get-ListeningPid -Port $Port
    if ($processId) { return $processId }
    Start-Sleep -Milliseconds 400
  }
  return $null
}

function Get-IniValue([string]$IniPath, [string]$Key) {
  if (-not (Test-Path $IniPath)) { return $null }
  $pattern = "^\s*$([regex]::Escape($Key))\s*=\s*(.+?)\s*$"
  foreach ($line in Get-Content $IniPath) {
    if ($line -match '^\s*[#;]') { continue }
    if ($line -match $pattern) {
      return $matches[1].Trim()
    }
  }
  return $null
}

function Test-DirectoryWritable([string]$PathValue) {
  try {
    if (-not (Test-Path $PathValue)) {
      New-Item -ItemType Directory -Path $PathValue -Force | Out-Null
    }
    $probeFile = Join-Path $PathValue ".write-test-$([Guid]::NewGuid().ToString('N')).tmp"
    'ok' | Set-Content -Path $probeFile -Encoding Ascii
    Remove-Item $probeFile -Force -ErrorAction SilentlyContinue
    return $true
  } catch {
    return $false
  }
}

function Ensure-MariaDbRuntimeConfig(
  [string]$DependenciesRoot,
  [string]$ProjectRoot,
  [string]$MariaExePath,
  [string]$SourceDefaultsFile
) {
  $configuredDataDir = Get-IniValue -IniPath $SourceDefaultsFile -Key 'datadir'
  if (-not $configuredDataDir) {
    $configuredDataDir = Join-Path $DependenciesRoot 'mariadb-data'
  }

  if (Test-DirectoryWritable -PathValue $configuredDataDir) {
    return [pscustomobject]@{
      DefaultsFile = $SourceDefaultsFile
      DataDir      = $configuredDataDir
      UsingRuntime = $false
    }
  }

  $runtimeRoot = Join-Path $ProjectRoot '.runtime'
  $runtimeDataDir = Join-Path $runtimeRoot 'mariadb-data'
  $runtimeIni = Join-Path $runtimeRoot 'mariadb.my.ini'
  New-Item -ItemType Directory -Path $runtimeRoot -Force | Out-Null
  New-Item -ItemType Directory -Path $runtimeDataDir -Force | Out-Null

  if (-not (Test-DirectoryWritable -PathValue $runtimeDataDir)) {
    throw "MariaDB data directory is not writable: $configuredDataDir, fallback also not writable: $runtimeDataDir"
  }

  # Initialize fallback data directory once by copying readable seed data.
  $runtimeItems = Get-ChildItem -Path $runtimeDataDir -Force -ErrorAction SilentlyContinue
  $runtimeHasData = ($runtimeItems | Measure-Object).Count -gt 0
  $runtimeLooksValid = (Test-Path (Join-Path $runtimeDataDir 'mysql')) -or (Test-Path (Join-Path $runtimeDataDir 'aria_log_control'))

  if ($runtimeHasData -and -not $runtimeLooksValid) {
    $backupDir = "$runtimeDataDir.broken.$((Get-Date).ToString('yyyyMMddHHmmss'))"
    Move-Item -Path $runtimeDataDir -Destination $backupDir -Force -ErrorAction SilentlyContinue
    New-Item -ItemType Directory -Path $runtimeDataDir -Force | Out-Null
    $runtimeHasData = $false
  }

  if (-not $runtimeHasData -and (Test-Path $configuredDataDir)) {
    Copy-Item -Path (Join-Path $configuredDataDir '*') -Destination $runtimeDataDir -Recurse -Force -ErrorAction SilentlyContinue
  }

  $mariaHome = Split-Path -Parent (Split-Path -Parent $MariaExePath)
  $pluginDir = Join-Path $mariaHome 'lib\plugin'
  $runtimeDataDirIni = $runtimeDataDir -replace '\\', '/'

  @"
[mysqld]
datadir=$runtimeDataDirIni
port=3306

[client]
port=3306
plugin-dir=$pluginDir
"@ | Set-Content -Path $runtimeIni -Encoding Ascii

  return [pscustomobject]@{
    DefaultsFile = $runtimeIni
    DataDir      = $runtimeDataDir
    UsingRuntime = $true
  }
}

function Ensure-MariaDB([string]$DependenciesRoot, [string]$ProjectRoot) {
  $existingPid = Get-ListeningPid -Port 3306
  if ($existingPid) {
    Write-Step "MariaDB already listening on 3306 (PID $existingPid)."
    return
  }

  $mariaExe = Get-ChildItem -Path (Join-Path $DependenciesRoot 'mariadb') -Recurse -File -Filter mariadbd.exe -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -match '\\bin\\mariadbd\.exe$' } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

  if (-not $mariaExe) {
    throw "mariadbd.exe not found under $DependenciesRoot\\mariadb"
  }

  $defaultsFile = Join-Path $DependenciesRoot 'mariadb-data\my.ini'
  $outLog = Join-Path $ProjectRoot 'mariadb.out.log'
  $errLog = Join-Path $ProjectRoot 'mariadb.err.log'
  $runtimeConfig = Ensure-MariaDbRuntimeConfig -DependenciesRoot $DependenciesRoot -ProjectRoot $ProjectRoot -MariaExePath $mariaExe.FullName -SourceDefaultsFile $defaultsFile
  $activeDefaultsFile = $runtimeConfig.DefaultsFile
  $mariaWorkingDir = $runtimeConfig.DataDir

  Write-Step "Starting MariaDB ..."
  if ($runtimeConfig.UsingRuntime) {
    Write-Step "MariaDB datadir fallback enabled: $($runtimeConfig.DataDir)"
  }
  if ($DryRun) {
    if (Test-Path $activeDefaultsFile) {
      Write-Host "DRYRUN => & '$($mariaExe.FullName)' --defaults-file='$activeDefaultsFile' --console"
    } else {
      Write-Host "DRYRUN => & '$($mariaExe.FullName)' --console"
    }
    return
  }

  if (Test-Path $outLog) { Remove-Item $outLog -Force -ErrorAction SilentlyContinue }
  if (Test-Path $errLog) { Remove-Item $errLog -Force -ErrorAction SilentlyContinue }

  $args = @('--console')
  if (Test-Path $activeDefaultsFile) {
    $args = @("--defaults-file=$activeDefaultsFile", '--console')
  }

  Start-Process -FilePath $mariaExe.FullName -WorkingDirectory $mariaWorkingDir -ArgumentList $args -RedirectStandardOutput $outLog -RedirectStandardError $errLog | Out-Null
  $startedPid = Wait-Port -Port 3306 -TimeoutSeconds 20
  if (-not $startedPid) {
    Write-Host "MariaDB failed to start. Check logs:"
    if (Test-Path $errLog) { Get-Content $errLog -Tail 80 }
    throw "MariaDB is not listening on port 3306."
  }

  Write-Step "MariaDB started on 3306 (PID $startedPid)."
}

function Ensure-Redis([string]$DependenciesRoot, [string]$ProjectRoot) {
  $existingPid = Get-ListeningPid -Port 6379
  if ($existingPid) {
    Write-Step "Redis already listening on 6379 (PID $existingPid)."
    return
  }

  $redisExe = Join-Path $DependenciesRoot 'redis\redis-server.exe'
  if (-not (Test-Path $redisExe)) {
    throw "redis-server.exe not found: $redisExe"
  }

  $outLog = Join-Path $ProjectRoot 'redis.out.log'
  $errLog = Join-Path $ProjectRoot 'redis.err.log'

  Write-Step "Starting Redis ..."
  if ($DryRun) {
    Write-Host "DRYRUN => & '$redisExe'"
    return
  }

  if (Test-Path $outLog) { Remove-Item $outLog -Force -ErrorAction SilentlyContinue }
  if (Test-Path $errLog) { Remove-Item $errLog -Force -ErrorAction SilentlyContinue }

  Start-Process -FilePath $redisExe -RedirectStandardOutput $outLog -RedirectStandardError $errLog | Out-Null
  $startedPid = Wait-Port -Port 6379 -TimeoutSeconds 10
  if (-not $startedPid) {
    Write-Host "Redis failed to start. Check logs:"
    if (Test-Path $errLog) { Get-Content $errLog -Tail 80 }
    if (Test-Path $outLog) { Get-Content $outLog -Tail 80 }
    throw "Redis is not listening on port 6379."
  }

  Write-Step "Redis started on 6379 (PID $startedPid)."
}

function Main {
  $projectRoot = Get-ProjectRoot
  Set-Location $projectRoot
  $dependenciesRoot = Resolve-DependenciesRoot -ProjectRoot $projectRoot

  Write-Step "Dependencies root: $dependenciesRoot"
  Ensure-MariaDB -DependenciesRoot $dependenciesRoot -ProjectRoot $projectRoot
  Ensure-Redis -DependenciesRoot $dependenciesRoot -ProjectRoot $projectRoot
}

Main
