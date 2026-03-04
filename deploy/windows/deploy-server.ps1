param(
  [string]$Branch = 'main',
  [switch]$SkipPush,
  [switch]$SkipFrontendBuild,
  [switch]$SkipBackendBuild
)

$ErrorActionPreference = 'Stop'

$scriptPath = Join-Path $PSScriptRoot 'push-and-deploy.ps1'

$argsList = @(
  '-ExecutionPolicy', 'Bypass',
  '-File', $scriptPath,
  '-ServerHost', '66.63.173.143',
  '-ServerUser', 'root',
  '-Branch', $Branch,
  '-ServerAppRoot', '/root/website'
)

if ($SkipPush) { $argsList += '-SkipPush' }
if ($SkipFrontendBuild) { $argsList += '-SkipFrontendBuild' }
if ($SkipBackendBuild) { $argsList += '-SkipBackendBuild' }

& powershell @argsList
if ($LASTEXITCODE -ne 0) {
  throw "Deploy failed (exit code: $LASTEXITCODE)"
}
