#!/usr/bin/env bash
set -Eeuo pipefail
IFS=$'\n\t'

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_ENV_FILE="${SCRIPT_DIR}/../backup.env"

if [[ -n "${BACKUP_ENV_FILE:-}" && -f "${BACKUP_ENV_FILE}" ]]; then
  # shellcheck disable=SC1090
  source "${BACKUP_ENV_FILE}"
elif [[ -f "${DEFAULT_ENV_FILE}" ]]; then
  # shellcheck disable=SC1090
  source "${DEFAULT_ENV_FILE}"
fi

BACKUP_ROOT="${BACKUP_ROOT:-/root/backup}"
MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-leedaud}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-}"
MYSQL_DATABASE="${MYSQL_DATABASE:-LeeDaud}"
MYSQL_CNF="${MYSQL_CNF:-/root/.my.cnf}"
RETENTION_DAYS="${RETENTION_DAYS:-14}"

log() {
  printf '[BACKUP] %s\n' "$*"
}

fail() {
  printf '[BACKUP][ERROR] %s\n' "$*" >&2
  exit 1
}

ensure_cmd() {
  local cmd="$1"
  command -v "${cmd}" >/dev/null 2>&1 || fail "Missing command: ${cmd}"
}

build_mysqldump_cmd() {
  local -n cmd_ref=$1
  cmd_ref=(mysqldump --single-transaction --routines --triggers --events --hex-blob --set-gtid-purged=OFF)

  if [[ -f "${MYSQL_CNF}" ]]; then
    cmd_ref+=(--defaults-extra-file="${MYSQL_CNF}")
  else
    [[ -n "${MYSQL_PASSWORD}" ]] || fail "MYSQL_PASSWORD is empty and MYSQL_CNF not found: ${MYSQL_CNF}"
    cmd_ref+=(
      -h "${MYSQL_HOST}"
      -P "${MYSQL_PORT}"
      -u "${MYSQL_USER}"
      "-p${MYSQL_PASSWORD}"
    )
  fi

  cmd_ref+=("${MYSQL_DATABASE}")
}

main() {
  ensure_cmd mysqldump
  ensure_cmd gzip
  ensure_cmd find

  local ts out_dir out_file
  ts="$(date +'%Y%m%d_%H%M%S')"
  out_dir="${BACKUP_ROOT}/mysql/$(date +'%Y/%m')"
  out_file="${out_dir}/${MYSQL_DATABASE}_${ts}.sql.gz"

  mkdir -p "${out_dir}"

  local dump_cmd=()
  build_mysqldump_cmd dump_cmd

  log "Starting MySQL backup: db=${MYSQL_DATABASE}"
  "${dump_cmd[@]}" | gzip -9 > "${out_file}"

  gzip -t "${out_file}" || fail "Backup gzip verification failed: ${out_file}"
  log "Backup created: ${out_file}"

  find "${BACKUP_ROOT}/mysql" -type f -name '*.sql.gz' -mtime +"${RETENTION_DAYS}" -print -delete || true
  log "Retention cleanup done: keep ${RETENTION_DAYS} days"
}

main "$@"
