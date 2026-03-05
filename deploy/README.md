# Deploy Guide

## Recommended Domain Layout

- `licheng.website` -> Frontend Home (`Frontend-Home`)
- `blog.licheng.website` -> Blog (`Blog`)
- `cv.licheng.website` -> CV (`Cv`)
- `admin.licheng.website` -> Admin Console (`Admin`)
- `api.licheng.website` -> Spring Boot Backend (`Backend`, port `5922`)

Current project sub-sites can stay as-is:
- `timelinejournal.licheng.website`
- `longevityhabits.licheng.website`
- `launch.licheng.website`

DNS `A` records should point to:
- `66.63.173.143`

## Files

- `deploy/deploy.env.example`: server deploy variables template.
- `deploy/server/deploy-all.sh`: one-click sync/build/publish/restart on server.
- `deploy/server/bootstrap-ubuntu.sh`: first-time bootstrap for a clean Ubuntu server.
- `deploy/server/backup-mysql.sh`: MySQL backup script (gzip + retention cleanup).
- `deploy/server/install-backup-cron.sh`: install daily cron job for backup script.
- `deploy/server/nginx-sites.example.conf`: Nginx site config template.
- `deploy/server/leedaud-backend.service.example`: systemd backend service template.
- `deploy/windows/invoke-remote-deploy.ps1`: local PowerShell remote deploy trigger.
- `deploy/windows/push-and-deploy.ps1`: push current branch then trigger remote deploy.
- `deploy/windows/deploy-server.ps1`: one-click wrapper (pre-filled for `66.63.173.143` + `/root/website`).
- `deploy/windows/one-click-deploy.ps1`: local one-click pipeline (`git add/commit/push` + remote deploy).
- `deploy/windows/deploy.local.example.psd1`: local deploy config template.

## Server First-Time Setup

1. Clone repo to server path, for example:
   - `/root/website`
2. Install dependencies:
   - `git`, `node`, `npm`, `maven`, `java(21+)`, `rsync`, `nginx`
3. Copy env template:
   - `cp deploy/deploy.env.example deploy/deploy.env`
4. Edit `deploy/deploy.env` paths and options.
5. Install backend service:
   - copy `deploy/server/leedaud-backend.service.example` to `/etc/systemd/system/leedaud-backend.service`
   - current example matches `/root/website`; if you change path/user, edit service file first, then `systemctl daemon-reload`

## One-Click Deploy (Run on Server)

```bash
bash /root/website/deploy/server/deploy-all.sh
```

## Common Troubleshooting

If homepage avatar/links are empty, or sub-sites open but show network errors, verify API proxy and backend health:

```bash
# 1) backend local health
curl -fsS http://127.0.0.1:5922/health

# 2) each frontend host must proxy /api/*
curl -I https://licheng.website/api/home/personalInfo
curl -I https://blog.licheng.website/api/blog/systemConfig?configKey=site-title
curl -I https://cv.licheng.website/api/cv/personalInfo
curl -I https://admin.licheng.website/api/health

# 3) dedicated API host
curl -I https://api.licheng.website/health
```

If any `/api` endpoint returns `404`, check your Nginx site file includes `location /api/ { proxy_pass http://127.0.0.1:5922/; ... }` for that domain.

If remote deploy exits with `Aborted (core dumped)` / exit code `134`, this is usually Node OOM during frontend build:

1. Increase server frontend build memory in `/root/website/deploy/deploy.env`:

```bash
FRONTEND_NODE_OPTIONS=--max-old-space-size=3072
FRONTEND_BUILD_ARGS=--minify=esbuild
```

2. Re-run one-click deploy from local.

3. Emergency fallback (backend only):

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\one-click-deploy.ps1 -SkipFrontendBuild
```

4. `deploy-all.sh` now supports auto swap creation (default enabled):

```bash
ENABLE_AUTO_SWAP=1
SWAP_SIZE_MB=2048
SWAP_FILE_PATH=/swapfile.leedaud
```

## First-Time Bootstrap (Ubuntu, optional)

```bash
chmod +x /root/website/deploy/server/bootstrap-ubuntu.sh
bash /root/website/deploy/server/bootstrap-ubuntu.sh
```

## Daily Auto Backup (MySQL)

1. Create backup config:

```bash
cp /root/website/deploy/backup.env.example /root/website/deploy/backup.env
nano /root/website/deploy/backup.env
```

2. Test one backup manually:

```bash
chmod +x /root/website/deploy/server/backup-mysql.sh
bash /root/website/deploy/server/backup-mysql.sh
```

3. Install daily cron job (default `03:30`):

```bash
chmod +x /root/website/deploy/server/install-backup-cron.sh
bash /root/website/deploy/server/install-backup-cron.sh
```

4. Verify:

```bash
crontab -l
ls -R /root/backup/mysql
tail -n 50 /var/log/leedaud-backup-cron.log
```

## One-Click Deploy (Run on Local Windows)

1) Create local deploy config (only once):

```powershell
copy .\deploy\windows\deploy.local.example.psd1 .\deploy\windows\deploy.local.psd1
```

2) One command from local:

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\one-click-deploy.ps1
```

This will do:
- `git add -A`
- auto `git commit` (if there are changes)
- `git push origin <branch>`
- SSH trigger remote `deploy-all.sh`
- auto stash unexpected server local changes before pull (to avoid merge abort)

`deploy-server.ps1` now wraps the same one-click flow:

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\deploy-server.ps1
```

Optional examples:

```powershell
# custom commit message
powershell -ExecutionPolicy Bypass -File .\deploy\windows\one-click-deploy.ps1 `
  -CommitMessage "feat: update homepage links"

# temporary higher memory for remote frontend build
powershell -ExecutionPolicy Bypass -File .\deploy\windows\one-click-deploy.ps1 `
  -FrontendNodeOptions "--max-old-space-size=3072"

# git push unstable network: add retries
powershell -ExecutionPolicy Bypass -File .\deploy\windows\one-click-deploy.ps1 `
  -PushRetryCount 5 -PushRetryDelaySeconds 6

# SSH unstable/reset: add retries + connect timeout
powershell -ExecutionPolicy Bypass -File .\deploy\windows\one-click-deploy.ps1 `
  -SshRetryCount 5 -SshRetryDelaySeconds 6 -SshConnectTimeoutSeconds 25

# only deploy server (skip local commit/push)
powershell -ExecutionPolicy Bypass -File .\deploy\windows\one-click-deploy.ps1 `
  -SkipCommit -SkipPush
```

---

Legacy split mode remains available:

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\push-and-deploy.ps1 `
  -ServerHost 66.63.173.143 `
  -ServerUser root `
  -Branch main `
  -ServerAppRoot /root/website
```

If you already pushed code and only want to trigger server deploy:

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\invoke-remote-deploy.ps1 `
  -ServerHost 66.63.173.143 `
  -ServerUser root `
  -Branch main `
  -ServerAppRoot /root/website
```

Pre-filled one-click command:

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\deploy-server.ps1
```
