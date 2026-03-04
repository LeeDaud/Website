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
- `deploy/server/nginx-sites.example.conf`: Nginx site config template.
- `deploy/server/leedaud-backend.service.example`: systemd backend service template.
- `deploy/windows/invoke-remote-deploy.ps1`: local PowerShell remote deploy trigger.
- `deploy/windows/push-and-deploy.ps1`: push current branch then trigger remote deploy.
- `deploy/windows/deploy-server.ps1`: one-click wrapper (pre-filled for `66.63.173.143` + `/root/website`).

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

## First-Time Bootstrap (Ubuntu, optional)

```bash
chmod +x /root/website/deploy/server/bootstrap-ubuntu.sh
bash /root/website/deploy/server/bootstrap-ubuntu.sh
```

## One-Click Deploy (Run on Local Windows)

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
