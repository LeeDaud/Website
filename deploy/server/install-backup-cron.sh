#!/usr/bin/env bash
set -Eeuo pipefail
IFS=$'\n\t'

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
BACKUP_SCRIPT_PATH="${BACKUP_SCRIPT_PATH:-${SCRIPT_DIR}/backup-mysql.sh}"
CRON_EXPR="${CRON_EXPR:-30 3 * * *}"
CRON_LOG="${CRON_LOG:-/var/log/leedaud-backup-cron.log}"
CRON_TAG="# leedaud-mysql-backup"

log() {
  printf '[CRON] %s\n' "$*"
}

fail() {
  printf '[CRON][ERROR] %s\n' "$*" >&2
  exit 1
}

[[ -f "${BACKUP_SCRIPT_PATH}" ]] || fail "Backup script not found: ${BACKUP_SCRIPT_PATH}"
chmod +x "${BACKUP_SCRIPT_PATH}"

line="${CRON_EXPR} /bin/bash ${BACKUP_SCRIPT_PATH} >> ${CRON_LOG} 2>&1 ${CRON_TAG}"

tmp_file="$(mktemp)"
trap 'rm -f "${tmp_file}"' EXIT

crontab -l 2>/dev/null | grep -v "${CRON_TAG}" > "${tmp_file}" || true
echo "${line}" >> "${tmp_file}"
crontab "${tmp_file}"

log "Installed cron entry:"
log "${line}"
log "Current crontab:"
crontab -l
