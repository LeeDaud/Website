param(
  [string]$RootPath,
  [switch]$DryRun
)

$ErrorActionPreference = 'Stop'

function Write-Step([string]$Message) {
  Write-Host "[CLEAN] $Message" -ForegroundColor Cyan
}

function Format-Size([long]$Bytes) {
  if ($Bytes -ge 1GB) { return ('{0:N2} GB' -f ($Bytes / 1GB)) }
  if ($Bytes -ge 1MB) { return ('{0:N2} MB' -f ($Bytes / 1MB)) }
  if ($Bytes -ge 1KB) { return ('{0:N2} KB' -f ($Bytes / 1KB)) }
  return "$Bytes B"
}

if ([string]::IsNullOrWhiteSpace($RootPath)) {
  $RootPath = if ($PSScriptRoot) { $PSScriptRoot } else { (Get-Location).Path }
}

if (-not (Test-Path $RootPath)) {
  throw "Root path not found: $RootPath"
}

$root = (Resolve-Path $RootPath).Path
Write-Step "Scanning logs under: $root"

$nameRegex = '(\.log$|\.err$|\.out$|\.pid$|\.stackdump$|^npm-debug\.log|^yarn-error\.log)'
$excludeRegex = '(\\node_modules\\|\\\.git\\|\\target\\)'

$files = Get-ChildItem -Path $root -Recurse -File -Force -ErrorAction SilentlyContinue |
  Where-Object {
    $_.Name -match $nameRegex -and $_.FullName -notmatch $excludeRegex
  }

if (-not $files -or $files.Count -eq 0) {
  Write-Host 'No matching log files found.' -ForegroundColor Green
  exit 0
}

$totalBytes = ($files | Measure-Object -Property Length -Sum).Sum
Write-Host ("Matched files : {0}" -f $files.Count)
Write-Host ("Total size    : {0}" -f (Format-Size $totalBytes))

if ($DryRun) {
  Write-Host ''
  Write-Host 'Dry run mode, files that would be deleted:' -ForegroundColor Yellow
  $files |
    Sort-Object Length -Descending |
    Select-Object @{Name='Size';Expression={Format-Size $_.Length}}, FullName |
    Format-Table -AutoSize
  exit 0
}

$deletedCount = 0
$deletedBytes = 0L
$failed = @()

foreach ($file in $files) {
  try {
    Remove-Item -LiteralPath $file.FullName -Force -ErrorAction Stop
    $deletedCount += 1
    $deletedBytes += [long]$file.Length
  } catch {
    $failed += $file.FullName
  }
}

Write-Host ''
Write-Host ("Deleted files : {0}" -f $deletedCount) -ForegroundColor Green
Write-Host ("Freed space   : {0}" -f (Format-Size $deletedBytes)) -ForegroundColor Green

if ($failed.Count -gt 0) {
  Write-Host ''
  Write-Host ("Failed files  : {0}" -f $failed.Count) -ForegroundColor Yellow
  $failed | ForEach-Object { Write-Host "  $_" }
  exit 1
}

exit 0
