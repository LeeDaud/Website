param(
  [Parameter(Mandatory = $true)]
  [string]$ServerHost,
  [Parameter(Mandatory = $true)]
  [string]$ServerUser,
  [string]$Branch = 'main',
  [int]$SshPort = 22,
  [string]$ServerAppRoot = '/root/website',
  [string]$RemoteScriptPath = '/root/website/deploy/server/deploy-all.sh',
  [string]$KeyPath = '',
  [switch]$SkipPush,
  [switch]$SkipFrontendBuild,
  [switch]$SkipBackendBuild
)

$ErrorActionPreference = 'Stop'

function Write-Step([string]$Message) {
  Write-Host "[PUSH-DEPLOY] $Message" -ForegroundColor Cyan
}

function Assert-Command([string]$Name) {
  if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
    throw "Missing command: $Name"
  }
}

Assert-Command 'git'
Assert-Command 'powershell'

if (-not $SkipPush) {
  Write-Step "Pushing branch '$Branch' to origin ..."
  & git push origin $Branch
  if ($LASTEXITCODE -ne 0) {
    throw "git push failed (exit code: $LASTEXITCODE)"
  }
} else {
  Write-Step 'Skip git push by -SkipPush.'
}

$invokeScript = Join-Path $PSScriptRoot 'invoke-remote-deploy.ps1'
if (-not (Test-Path $invokeScript)) {
  throw "invoke-remote-deploy.ps1 not found: $invokeScript"
}

$argsList = @(
  '-ExecutionPolicy', 'Bypass',
  '-File', $invokeScript,
  '-ServerHost', $ServerHost,
  '-ServerUser', $ServerUser,
  '-Branch', $Branch,
  '-SshPort', $SshPort,
  '-ServerAppRoot', $ServerAppRoot,
  '-RemoteScriptPath', $RemoteScriptPath
)

if ($KeyPath) {
  $argsList += @('-KeyPath', $KeyPath)
}
if ($SkipFrontendBuild) {
  $argsList += '-SkipFrontendBuild'
}
if ($SkipBackendBuild) {
  $argsList += '-SkipBackendBuild'
}

& powershell @argsList
if ($LASTEXITCODE -ne 0) {
  throw "Remote deploy invocation failed (exit code: $LASTEXITCODE)"
}

Write-Step 'Push + deploy finished.'
