param(
  [switch]$KeepInfra,
  [switch]$DryRun
)

$ErrorActionPreference = 'Stop'

function Write-Step([string]$Message) {
  Write-Host "[STOP] $Message" -ForegroundColor Yellow
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
  foreach ($candidate in $candidates) {
    if (Test-Path $candidate) {
      return $candidate
    }
  }
  return $null
}

function Get-ListeningPids([int]$Port) {
  $lines = netstat -ano | Select-String (":$Port\s+.*LISTENING\s+(\d+)$")
  $pids = @()
  foreach ($line in $lines) {
    $pids += [int]$line.Matches[0].Groups[1].Value
  }
  return ($pids | Sort-Object -Unique)
}

function Queue-StopPid(
  [int]$ProcessId,
  [string]$Reason,
  [System.Collections.Generic.HashSet[int]]$Targets
) {
  if ($ProcessId -le 0) { return }
  if ($Targets.Add($ProcessId)) {
    if ($DryRun) {
      Write-Step "DRYRUN => Stop PID $ProcessId ($Reason)"
    } else {
      Stop-Process -Id $ProcessId -Force -ErrorAction SilentlyContinue
      Write-Step "Stopped PID $ProcessId ($Reason)"
    }
  }
}

function Queue-AppByPorts([int[]]$Ports, [System.Collections.Generic.HashSet[int]]$Targets) {
  foreach ($port in $Ports) {
    $processIds = Get-ListeningPids -Port $port
    if ($processIds.Count -eq 0) {
      Write-Step "No process is listening on port $port."
      continue
    }
    foreach ($processId in $processIds) {
      Queue-StopPid -ProcessId $processId -Reason "port:$port" -Targets $Targets
    }
  }
}

function Queue-AppByCommandLine([System.Collections.Generic.HashSet[int]]$Targets) {
  try {
    $processes = Get-CimInstance Win32_Process -ErrorAction Stop |
      Where-Object {
        ($_.Name -eq 'java.exe' -or $_.Name -eq 'node.exe') -and
        $_.CommandLine -and
        (
          $_.CommandLine -like '*LeeDaud-server-1.0-SNAPSHOT.jar*' -or
          $_.CommandLine -like '*node_modules\.bin\vite*' -or
          $_.CommandLine -like '*vite.ps1*'
        )
      }
    foreach ($proc in $processes) {
      Queue-StopPid -ProcessId ([int]$proc.ProcessId) -Reason "cmd:$($proc.Name)" -Targets $Targets
    }
  } catch {
    Write-Step "Skip command-line scan (Win32_Process unavailable in current shell)."
  }
}

function Queue-DevWindows([string[]]$WindowTitles, [System.Collections.Generic.HashSet[int]]$Targets) {
  $hosts = Get-Process powershell,pwsh,cmd,WindowsTerminal -ErrorAction SilentlyContinue
  if (-not $hosts) { return }

  foreach ($procHost in $hosts) {
    $title = $procHost.MainWindowTitle
    if ([string]::IsNullOrWhiteSpace($title)) { continue }

    foreach ($expectedTitle in $WindowTitles) {
      if ($title -eq $expectedTitle) {
        Queue-StopPid -ProcessId $procHost.Id -Reason "window:$expectedTitle" -Targets $Targets
        break
      }
    }
  }
}

function Force-CloseDevWindows([string[]]$WindowTitles) {
  foreach ($windowTitle in $WindowTitles) {
    foreach ($imageName in @('powershell.exe', 'pwsh.exe')) {
      $imageFilter = "IMAGENAME eq $imageName"
      $titleFilter = "WINDOWTITLE eq $windowTitle*"
      if ($DryRun) {
        Write-Step "DRYRUN => taskkill /F /T /FI `"$imageFilter`" /FI `"$titleFilter`""
      } else {
        & taskkill /F /T /FI $imageFilter /FI $titleFilter 2>$null | Out-Null
      }
    }
  }
}

function Queue-Infra([string]$DependenciesRoot, [System.Collections.Generic.HashSet[int]]$Targets) {
  if (-not $DependenciesRoot) {
    Write-Step 'Dependencies folder not found. Skip infra shutdown.'
    return
  }

  $ownedPrefix = $DependenciesRoot.TrimEnd('\') + '\'

  $infraCandidates = @()
  $infraCandidates += Get-Process mariadbd,mysqld -ErrorAction SilentlyContinue
  $infraCandidates += Get-Process redis-server -ErrorAction SilentlyContinue
  $infraCandidates = $infraCandidates | Sort-Object Id -Unique

  foreach ($proc in $infraCandidates) {
    $path = $proc.Path
    if ($path -and $path.StartsWith($ownedPrefix, [System.StringComparison]::OrdinalIgnoreCase)) {
      Queue-StopPid -ProcessId $proc.Id -Reason "infra:$($proc.ProcessName)" -Targets $Targets
    } elseif ($path) {
      Write-Step "Skip infra PID $($proc.Id): path not under dependencies."
    } else {
      Write-Step "Skip infra PID $($proc.Id): process path is unavailable."
    }
  }

  foreach ($port in @(3306, 6379)) {
    $processIds = Get-ListeningPids -Port $port
    foreach ($processId in $processIds) {
      $proc = Get-Process -Id $processId -ErrorAction SilentlyContinue
      if (-not $proc) { continue }
      $path = $proc.Path
      if ($path -and $path.StartsWith($ownedPrefix, [System.StringComparison]::OrdinalIgnoreCase)) {
        Queue-StopPid -ProcessId $processId -Reason "infra-port:$port" -Targets $Targets
      }
    }
  }
}

function Main {
  $root = Get-ProjectRoot
  Set-Location $root

  $dependenciesRoot = Resolve-DependenciesRoot -ProjectRoot $root
  $targets = [System.Collections.Generic.HashSet[int]]::new()

  Write-Step "Project root: $root"
  if ($dependenciesRoot) {
    Write-Step "Dependencies root: $dependenciesRoot"
  }

  Queue-AppByPorts -Ports @(5173, 5174, 5175, 5176, 5922) -Targets $targets
  Queue-AppByCommandLine -Targets $targets
  Queue-DevWindows -WindowTitles @('Backend-5922', 'Blog', 'Admin', 'Frontend-Home', 'Cv') -Targets $targets

  if (-not $KeepInfra) {
    Queue-Infra -DependenciesRoot $dependenciesRoot -Targets $targets
  } else {
    Write-Step 'Keep infrastructure processes by -KeepInfra.'
  }

  Force-CloseDevWindows -WindowTitles @('Backend-5922', 'Blog', 'Admin', 'Frontend-Home', 'Cv')

  if ($DryRun) {
    Write-Host ''
    Write-Host 'Dry run completed.' -ForegroundColor Green
  } else {
    Start-Sleep -Milliseconds 600
    Write-Host ''
    Write-Host 'Stop completed.' -ForegroundColor Green
  }
}

Main
