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
  [string]$FrontendBuildArgs = '',
  [int]$SshRetryCount = 3,
  [int]$SshRetryDelaySeconds = 5,
  [int]$SshConnectTimeoutSeconds = 20,
  [switch]$NoAutoStashBeforeDeploy,
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
if ($FrontendBuildArgs) { $envParts += "FRONTEND_BUILD_ARGS='$FrontendBuildArgs'" }

$preflightCommand = ("if [ -d '{0}/.git' ]; then cd '{0}'; if git status --porcelain | grep -q .; then echo '[REMOTE-DEPLOY] Auto-stashing local changes before deploy...'; if [ -f deploy/deploy.env ]; then cp -f deploy/deploy.env /tmp/leedaud.deploy.env.backup; fi; git stash push --include-untracked --message auto-deploy-$(date +%s) >/dev/null 2>&1 || true; if [ -f /tmp/leedaud.deploy.env.backup ]; then cp -f /tmp/leedaud.deploy.env.backup deploy/deploy.env; rm -f /tmp/leedaud.deploy.env.backup; fi; fi; fi" -f $ServerAppRoot)
$deployCommand = "$($envParts -join ' ') bash '$RemoteScriptPath'"

if ($NoAutoStashBeforeDeploy) {
  $remoteCommand = $deployCommand
} else {
  $remoteCommand = "$preflightCommand; $deployCommand"
}

$sshArgs = @()
if ($KeyPath) {
  $sshArgs += '-i'
  $sshArgs += $KeyPath
}
$sshArgs += '-o'
$sshArgs += "ConnectTimeout=$SshConnectTimeoutSeconds"
$sshArgs += '-o'
$sshArgs += 'ConnectionAttempts=1'
$sshArgs += '-o'
$sshArgs += 'ServerAliveInterval=15'
$sshArgs += '-o'
$sshArgs += 'ServerAliveCountMax=3'
$sshArgs += '-p'
$sshArgs += "$SshPort"
$sshArgs += "$ServerUser@$ServerHost"
$sshArgs += $remoteCommand

Write-Step "Running remote deploy on $ServerUser@$ServerHost ..."
$maxAttempts = [Math]::Max(1, $SshRetryCount)
$baseDelay = [Math]::Max(1, $SshRetryDelaySeconds)
$attempt = 1

while ($true) {
  & ssh @sshArgs
  if ($LASTEXITCODE -eq 0) {
    break
  }

  if ($LASTEXITCODE -eq 134) {
    throw "Remote deploy failed (exit code: 134, likely Node OOM during frontend build). Retry with -SkipFrontendBuild, or set -FrontendNodeOptions '--max-old-space-size=3072'."
  }

  if ($attempt -ge $maxAttempts) {
    if ($LASTEXITCODE -eq 255) {
      throw "Remote deploy failed (exit code: 255, SSH connection reset/refused). Check server SSH status/firewall/fail2ban, then retry."
    }
    throw "Remote deploy failed (exit code: $LASTEXITCODE)"
  }

  $sleepSeconds = $baseDelay * [Math]::Pow(2, $attempt - 1)
  Write-Warning "Remote deploy failed (exit code: $LASTEXITCODE). Retry in $sleepSeconds seconds ... (attempt $attempt/$maxAttempts)"
  Start-Sleep -Seconds $sleepSeconds
  $attempt++
}

if ($LASTEXITCODE -ne 0) {
  if ($LASTEXITCODE -eq 134) {
    throw "Remote deploy failed (exit code: 134, likely Node OOM during frontend build). Retry with -SkipFrontendBuild, or set -FrontendNodeOptions '--max-old-space-size=3072'."
  }
  throw "Remote deploy failed (exit code: $LASTEXITCODE)"
}

Write-Step 'Remote deploy finished.'
