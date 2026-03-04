param(
  [switch]$SkipBuild,
  [switch]$SkipStart,
  [switch]$SkipInfra,
  [switch]$DryRun
)

$ErrorActionPreference = 'Stop'

function Write-Step([string]$Message) {
  Write-Host "[BACKEND] $Message" -ForegroundColor Cyan
}

function Assert-Path([string]$PathValue, [string]$Hint) {
  if (-not (Test-Path $PathValue)) {
    throw "$Hint`nMissing path: $PathValue"
  }
}

function Resolve-JavaExe([string]$ProjectRoot) {
  if ($env:JAVA_HOME) {
    $javaFromHome = Join-Path $env:JAVA_HOME 'bin\java.exe'
    if (Test-Path $javaFromHome) {
      return $javaFromHome
    }
  }

  $fromPath = Get-Command java -ErrorAction SilentlyContinue
  if ($fromPath -and $fromPath.Source -and (Test-Path $fromPath.Source)) {
    return $fromPath.Source
  }

  $parentRoot = Split-Path -Parent $ProjectRoot
  $searchRoots = @(
    (Join-Path $ProjectRoot 'dependencies\jdk21'),
    (Join-Path $ProjectRoot 'dependencies\jdk'),
    (Join-Path $ProjectRoot 'dependencies'),
    (Join-Path $parentRoot 'dependencies\jdk21'),
    (Join-Path $parentRoot 'dependencies\jdk'),
    (Join-Path $parentRoot 'dependencies')
  ) | Where-Object { $_ -and (Test-Path $_) }

  $found = $null
  foreach ($root in $searchRoots) {
    $found = Get-ChildItem -Path $root -Recurse -File -Filter java.exe -ErrorAction SilentlyContinue |
      Where-Object { $_.FullName -match '\\bin\\java\.exe$' } |
      Sort-Object LastWriteTime -Descending |
      Select-Object -First 1
    if ($found) {
      break
    }
  }

  if ($found) {
    return $found.FullName
  }

  throw "Java runtime not found. Please configure JAVA_HOME or install JDK 21+."
}

function Resolve-MavenCmd([string]$ProjectRoot) {
  $fromPath = Get-Command mvn.cmd -ErrorAction SilentlyContinue
  if ($fromPath -and $fromPath.Source -and (Test-Path $fromPath.Source)) {
    return $fromPath.Source
  }

  $fromPathMvn = Get-Command mvn -ErrorAction SilentlyContinue
  if ($fromPathMvn -and $fromPathMvn.Source -and (Test-Path $fromPathMvn.Source)) {
    return $fromPathMvn.Source
  }

  if ($env:MAVEN_HOME) {
    $mvnFromHome = Join-Path $env:MAVEN_HOME 'bin\mvn.cmd'
    if (Test-Path $mvnFromHome) {
      return $mvnFromHome
    }
  }

  $parentRoot = Split-Path -Parent $ProjectRoot
  $searchRoots = @(
    (Join-Path $ProjectRoot 'dependencies\maven'),
    (Join-Path $ProjectRoot 'dependencies'),
    (Join-Path $parentRoot 'dependencies\maven'),
    (Join-Path $parentRoot 'dependencies')
  ) | Where-Object { $_ -and (Test-Path $_) }

  foreach ($root in $searchRoots) {
    $found = Get-ChildItem -Path $root -Recurse -File -Filter mvn.cmd -ErrorAction SilentlyContinue |
      Where-Object { $_.FullName -match '\\bin\\mvn\.cmd$' } |
      Sort-Object LastWriteTime -Descending |
      Select-Object -First 1
    if ($found) {
      return $found.FullName
    }
  }

  throw "Maven command not found. Please configure MAVEN_HOME or install Maven."
}

function Stop-BackendProcess([string]$JarPath) {
  $targets = @()

  $listenLines = netstat -ano | Select-String ':5922\s+.*LISTENING\s+(\d+)$'
  foreach ($line in $listenLines) {
    $listenPid = [int]$line.Matches[0].Groups[1].Value
    if ($listenPid -gt 0) {
      $targets += $listenPid
      if (-not $DryRun) {
        Stop-Process -Id $listenPid -Force -ErrorAction SilentlyContinue
      }
    }
  }

  try {
    Get-CimInstance Win32_Process -ErrorAction Stop |
      Where-Object { ($_.Name -eq 'java.exe' -or $_.Name -eq 'javaw.exe') -and $_.CommandLine -and ($_.CommandLine -like "*$JarPath*") } |
      ForEach-Object {
        $targets += $_.ProcessId
        if (-not $DryRun) {
          Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue
        }
      }
  } catch {
    # Ignore CIM query failure in restricted environments.
  }

  $unique = $targets | Sort-Object -Unique
  if ($unique.Count -gt 0) {
    if ($DryRun) {
      Write-Step ("DRYRUN would stop backend process IDs: " + ($unique -join ', '))
    } else {
      Write-Step ("Stopped backend process IDs: " + ($unique -join ', '))
    }
  } else {
    Write-Step "No running backend process found on port 5922."
  }
}

function Build-Backend([string]$MavenCmd, [string]$BackendPom, [string]$JarPath, [string]$JarOriginalPath) {
  Write-Step "Packaging backend with Maven ..."
  if ($DryRun) {
    Write-Host "DRYRUN => & '$MavenCmd' -f '$BackendPom' -DskipTests package"
    return
  }

  try {
    & $MavenCmd -f $BackendPom -DskipTests package
  } catch {
    $message = $_.Exception.Message
    if ($message -and $message -like '*Unable to rename*') {
      Write-Step "Detected jar rename conflict. Retrying once after cleanup ..."
      Stop-BackendProcess -JarPath $JarPath
      Start-Sleep -Seconds 1
      Remove-Item -Force $JarPath, $JarOriginalPath -ErrorAction SilentlyContinue
      & $MavenCmd -f $BackendPom -DskipTests package
    } else {
      throw
    }
  }
}

function Start-Backend([string]$JavaExe, [string]$BackendRoot, [string]$JarPath) {
  Write-Step "Starting backend service ..."
  $script = @"
`$Host.UI.RawUI.WindowTitle = 'Backend-5922'
Set-Location '$BackendRoot'
& '$JavaExe' -jar '$JarPath' --spring.profiles.active=dev
"@

  if ($DryRun) {
    Write-Host "DRYRUN => powershell -NoExit -ExecutionPolicy Bypass -Command <backend-start-script>"
    return
  }

  Start-Process -FilePath powershell -ArgumentList @(
    '-NoExit',
    '-ExecutionPolicy',
    'Bypass',
    '-Command',
    $script
  ) | Out-Null
}

function Main {
  $root = if ($PSScriptRoot) { $PSScriptRoot } else { (Get-Location).Path }
  Set-Location $root

  $backendRoot = Join-Path $root 'Backend'
  $backendPom = Join-Path $backendRoot 'pom.xml'
  $jarPath = Join-Path $backendRoot 'LeeDaud-server\target\LeeDaud-server-1.0-SNAPSHOT.jar'
  $jarOriginalPath = "$jarPath.original"
  $infraScript = Join-Path $root 'start-infra.ps1'

  Assert-Path $backendRoot 'Backend directory not found.'
  Assert-Path $backendPom 'Backend pom.xml not found.'

  $javaExe = Resolve-JavaExe -ProjectRoot $root
  $mavenCmd = Resolve-MavenCmd -ProjectRoot $root

  Write-Step "Using Java: $javaExe"
  Write-Step "Using Maven: $mavenCmd"

  if (-not $SkipInfra) {
    Assert-Path $infraScript 'start-infra.ps1 not found.'
    Write-Step 'Ensuring infrastructure (MariaDB/Redis) ...'
    & $infraScript -DryRun:$DryRun
  } else {
    Write-Step 'Skip infrastructure startup by -SkipInfra.'
  }

  Stop-BackendProcess -JarPath $jarPath

  if (-not $SkipBuild) {
    Build-Backend -MavenCmd $mavenCmd -BackendPom $backendPom -JarPath $jarPath -JarOriginalPath $jarOriginalPath
  } else {
    Write-Step "Skip build by -SkipBuild."
  }

  Assert-Path $jarPath 'Backend jar was not found after packaging.'

  if (-not $SkipStart) {
    Start-Backend -JavaExe $javaExe -BackendRoot $backendRoot -JarPath $jarPath
    Write-Host ''
    Write-Host 'Backend restart completed.' -ForegroundColor Green
    Write-Host '  API: http://localhost:5922'
  } else {
    Write-Step "Skip start by -SkipStart."
  }
}

Main
