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

  Write-Step "Starting MariaDB ..."
  if ($DryRun) {
    if (Test-Path $defaultsFile) {
      Write-Host "DRYRUN => & '$($mariaExe.FullName)' --defaults-file='$defaultsFile' --console"
    } else {
      Write-Host "DRYRUN => & '$($mariaExe.FullName)' --console"
    }
    return
  }

  if (Test-Path $outLog) { Remove-Item $outLog -Force -ErrorAction SilentlyContinue }
  if (Test-Path $errLog) { Remove-Item $errLog -Force -ErrorAction SilentlyContinue }

  $args = @('--console')
  if (Test-Path $defaultsFile) {
    $args = @("--defaults-file=$defaultsFile", '--console')
  }

  Start-Process -FilePath $mariaExe.FullName -ArgumentList $args -RedirectStandardOutput $outLog -RedirectStandardError $errLog | Out-Null
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
