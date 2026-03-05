param(
  [string]$Branch = 'main',
  [string]$CommitMessage = '',
  [string]$FrontendNodeOptions = '',
  [int]$PushRetryCount = 3,
  [int]$PushRetryDelaySeconds = 5,
  [switch]$NoAutoStashBeforeDeploy,
  [switch]$SkipCommit,
  [switch]$SkipPush,
  [switch]$SkipFrontendBuild,
  [switch]$SkipBackendBuild
)

$ErrorActionPreference = 'Stop'

$scriptPath = Join-Path $PSScriptRoot 'one-click-deploy.ps1'

$localConfig = Join-Path $PSScriptRoot 'deploy.local.psd1'
if (-not (Test-Path $localConfig)) {
  $localConfig = Join-Path $PSScriptRoot 'deploy.local.example.psd1'
}

$argsList = @(
  '-ExecutionPolicy', 'Bypass',
  '-File', $scriptPath,
  '-ConfigPath', $localConfig,
  '-Branch', $Branch
)

if ($CommitMessage) { $argsList += @('-CommitMessage', $CommitMessage) }
if ($FrontendNodeOptions) { $argsList += @('-FrontendNodeOptions', $FrontendNodeOptions) }
$argsList += @('-PushRetryCount', $PushRetryCount)
$argsList += @('-PushRetryDelaySeconds', $PushRetryDelaySeconds)
if ($NoAutoStashBeforeDeploy) { $argsList += '-NoAutoStashBeforeDeploy' }
if ($SkipCommit) { $argsList += '-SkipCommit' }
if ($SkipPush) { $argsList += '-SkipPush' }
if ($SkipFrontendBuild) { $argsList += '-SkipFrontendBuild' }
if ($SkipBackendBuild) { $argsList += '-SkipBackendBuild' }

& powershell @argsList
if ($LASTEXITCODE -ne 0) {
  throw "Deploy failed (exit code: $LASTEXITCODE)"
}
