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
  [string]$FrontendNodeOptions = '',
  [switch]$SkipGitPull,
  [switch]$SkipFrontendBuild,
  [switch]$SkipBackendBuild
)

$ErrorActionPreference = 'Stop'

function Write-Step([string]$Message) {
  Write-Host "[REMOTE-DEPLOY] $Message" -ForegroundColor Cyan
}

function Assert-Command([string]$Name) {
  if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
    throw "Missing command: $Name"
  }
}

Assert-Command 'ssh'

$envParts = @(
  "APP_ROOT='$ServerAppRoot'",
  "BRANCH='$Branch'"
)

if ($SkipGitPull) { $envParts += 'SKIP_GIT_PULL=1' }
if ($SkipFrontendBuild) { $envParts += 'SKIP_FRONTEND_BUILD=1' }
if ($SkipBackendBuild) { $envParts += 'SKIP_BACKEND_BUILD=1' }
if ($FrontendNodeOptions) { $envParts += "FRONTEND_NODE_OPTIONS='$FrontendNodeOptions'" }

$remoteCommand = "$($envParts -join ' ') bash '$RemoteScriptPath'"

$sshArgs = @()
if ($KeyPath) {
  $sshArgs += '-i'
  $sshArgs += $KeyPath
}
$sshArgs += '-p'
$sshArgs += "$SshPort"
$sshArgs += "$ServerUser@$ServerHost"
$sshArgs += $remoteCommand

Write-Step "Running remote deploy on $ServerUser@$ServerHost ..."
& ssh @sshArgs

if ($LASTEXITCODE -ne 0) {
  if ($LASTEXITCODE -eq 134) {
    throw "Remote deploy failed (exit code: 134, likely Node OOM during frontend build). Retry with -SkipFrontendBuild, or set -FrontendNodeOptions '--max-old-space-size=3072'."
  }
  throw "Remote deploy failed (exit code: $LASTEXITCODE)"
}

Write-Step 'Remote deploy finished.'
