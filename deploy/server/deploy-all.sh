#!/usr/bin/env bash
set -Eeuo pipefail
IFS=$'\n\t'

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_ENV_FILE="${SCRIPT_DIR}/../deploy.env"

if [[ -n "${DEPLOY_ENV_FILE:-}" && -f "${DEPLOY_ENV_FILE}" ]]; then
  # shellcheck disable=SC1090
  source "${DEPLOY_ENV_FILE}"
elif [[ -f "${DEFAULT_ENV_FILE}" ]]; then
  # shellcheck disable=SC1090
  source "${DEFAULT_ENV_FILE}"
fi

BRANCH="${BRANCH:-main}"
APP_ROOT="${APP_ROOT:-$(cd -- "${SCRIPT_DIR}/../.." && pwd)}"

WWW_ROOT="${WWW_ROOT:-/var/www}"
HOME_DIST="${HOME_DIST:-${WWW_ROOT}/licheng.website}"
BLOG_DIST="${BLOG_DIST:-${WWW_ROOT}/blog.licheng.website}"
CV_DIST="${CV_DIST:-${WWW_ROOT}/cv.licheng.website}"
ADMIN_DIST="${ADMIN_DIST:-${WWW_ROOT}/admin.licheng.website}"

BACKEND_RUNTIME_DIR="${BACKEND_RUNTIME_DIR:-/opt/leedaud/backend}"
BACKEND_JAR_NAME="${BACKEND_JAR_NAME:-LeeDaud-server.jar}"
BACKEND_SERVICE_NAME="${BACKEND_SERVICE_NAME:-leedaud-backend}"
BACKEND_PROFILE="${BACKEND_PROFILE:-prod}"
BACKEND_PORT="${BACKEND_PORT:-5922}"

SKIP_GIT_PULL="${SKIP_GIT_PULL:-0}"
SKIP_FRONTEND_BUILD="${SKIP_FRONTEND_BUILD:-0}"
SKIP_BACKEND_BUILD="${SKIP_BACKEND_BUILD:-0}"
FRONTEND_NODE_OPTIONS="${FRONTEND_NODE_OPTIONS:---max-old-space-size=2048}"
FRONTEND_BUILD_ARGS="${FRONTEND_BUILD_ARGS:---minify=esbuild}"
ENABLE_AUTO_SWAP="${ENABLE_AUTO_SWAP:-1}"
SWAP_SIZE_MB="${SWAP_SIZE_MB:-2048}"
SWAP_FILE_PATH="${SWAP_FILE_PATH:-/swapfile.leedaud}"

if [[ "${EUID}" -eq 0 ]]; then
  SUDO=""
else
  SUDO="${SUDO_CMD:-sudo}"
fi

log() {
  printf '[DEPLOY] %s\n' "$*"
}

warn() {
  printf '[WARN] %s\n' "$*" >&2
}

fail() {
  printf '[ERROR] %s\n' "$*" >&2
  exit 1
}

ensure_cmd() {
  local cmd="$1"
  command -v "${cmd}" >/dev/null 2>&1 || fail "Missing command: ${cmd}"
}

run_maybe_sudo() {
  if [[ -n "${SUDO}" ]]; then
    ${SUDO} "$@"
  else
    "$@"
  fi
}

run_root_bash() {
  local cmd="$1"
  if [[ -n "${SUDO}" ]]; then
    ${SUDO} bash -lc "${cmd}"
  else
    bash -lc "${cmd}"
  fi
}

ensure_swap() {
  [[ "${ENABLE_AUTO_SWAP}" == "1" ]] || {
    log "Auto swap disabled (ENABLE_AUTO_SWAP=${ENABLE_AUTO_SWAP})."
    return
  }

  ensure_cmd free
  ensure_cmd awk

  local current_swap_mb
  current_swap_mb="$(free -m | awk '/^Swap:/ {print $2}')"
  current_swap_mb="${current_swap_mb:-0}"

  if [[ "${current_swap_mb}" -ge 512 ]]; then
    log "Swap already available: ${current_swap_mb} MB"
    return
  fi

  log "Swap is low (${current_swap_mb} MB). Creating swap file ${SWAP_FILE_PATH} (${SWAP_SIZE_MB} MB) ..."

  if ! run_root_bash "
set -Eeuo pipefail
if [[ ! -f '${SWAP_FILE_PATH}' ]]; then
  if command -v fallocate >/dev/null 2>&1; then
    fallocate -l ${SWAP_SIZE_MB}M '${SWAP_FILE_PATH}' || dd if=/dev/zero of='${SWAP_FILE_PATH}' bs=1M count='${SWAP_SIZE_MB}' status=none
  else
    dd if=/dev/zero of='${SWAP_FILE_PATH}' bs=1M count='${SWAP_SIZE_MB}' status=none
  fi
  chmod 600 '${SWAP_FILE_PATH}'
  mkswap '${SWAP_FILE_PATH}' >/dev/null
fi
swapon --show | grep -q '${SWAP_FILE_PATH}' || swapon '${SWAP_FILE_PATH}'
grep -qF '${SWAP_FILE_PATH} none swap sw 0 0' /etc/fstab || echo '${SWAP_FILE_PATH} none swap sw 0 0' >> /etc/fstab
"
  then
    warn "Auto swap setup failed, continue without swap."
    return
  fi

  local updated_swap_mb
  updated_swap_mb="$(free -m | awk '/^Swap:/ {print $2}')"
  updated_swap_mb="${updated_swap_mb:-0}"
  log "Swap ready: ${updated_swap_mb} MB"
}

sync_repo() {
  if [[ "${SKIP_GIT_PULL}" == "1" ]]; then
    log "Skip git pull (SKIP_GIT_PULL=1)."
    return
  fi

  [[ -d "${APP_ROOT}/.git" ]] || fail "APP_ROOT is not a git repo: ${APP_ROOT}"

  log "Syncing repository: branch=${BRANCH}"
  git -C "${APP_ROOT}" fetch origin "${BRANCH}"
  git -C "${APP_ROOT}" checkout "${BRANCH}"
  git -C "${APP_ROOT}" pull --ff-only origin "${BRANCH}"
}

build_frontend() {
  local app_name="$1"
  local app_dir="$2"
  local dist_dir="$3"

  log "Building frontend: ${app_name}"
  log "Frontend Node options: ${FRONTEND_NODE_OPTIONS}"
  log "Frontend build args: ${FRONTEND_BUILD_ARGS}"
  [[ -f "${app_dir}/package.json" ]] || fail "package.json not found: ${app_dir}"

  pushd "${app_dir}" >/dev/null
  if [[ -f package-lock.json || -f npm-shrinkwrap.json ]]; then
    HUSKY=0 npm ci --no-audit --no-fund
  else
    HUSKY=0 npm install --no-audit --no-fund
  fi
  if [[ -n "${FRONTEND_BUILD_ARGS}" ]]; then
    # shellcheck disable=SC2086
    HUSKY=0 NODE_OPTIONS="${FRONTEND_NODE_OPTIONS}" npm run build -- ${FRONTEND_BUILD_ARGS}
  else
    HUSKY=0 NODE_OPTIONS="${FRONTEND_NODE_OPTIONS}" npm run build
  fi
  popd >/dev/null

  run_maybe_sudo mkdir -p "${dist_dir}"
  run_maybe_sudo rsync -a --delete "${app_dir}/dist/" "${dist_dir}/"
  log "Published ${app_name} -> ${dist_dir}"
}

build_backend() {
  local backend_pom="${APP_ROOT}/Backend/pom.xml"
  local backend_jar="${APP_ROOT}/Backend/LeeDaud-server/target/LeeDaud-server-1.0-SNAPSHOT.jar"
  local runtime_jar="${BACKEND_RUNTIME_DIR}/${BACKEND_JAR_NAME}"

  log "Building backend jar"
  [[ -f "${backend_pom}" ]] || fail "Backend pom not found: ${backend_pom}"
  mvn -f "${backend_pom}" -DskipTests package
  [[ -f "${backend_jar}" ]] || fail "Jar not found after build: ${backend_jar}"

  run_maybe_sudo mkdir -p "${BACKEND_RUNTIME_DIR}"
  run_maybe_sudo cp -f "${backend_jar}" "${runtime_jar}"
  log "Backend jar copied -> ${runtime_jar}"
}

restart_backend() {
  local service="${BACKEND_SERVICE_NAME}.service"
  local runtime_jar="${BACKEND_RUNTIME_DIR}/${BACKEND_JAR_NAME}"
  local log_dir="${BACKEND_RUNTIME_DIR}/logs"
  local out_log="${log_dir}/backend.out.log"

  if command -v systemctl >/dev/null 2>&1 && systemctl list-unit-files | grep -q "^${service}"; then
    log "Restarting systemd service: ${service}"
    run_maybe_sudo systemctl daemon-reload || true
    run_maybe_sudo systemctl restart "${service}"
    run_maybe_sudo systemctl status "${service}" --no-pager --lines=5 || true
    return
  fi

  warn "Service ${service} not found, fallback to nohup java process."
  run_maybe_sudo mkdir -p "${log_dir}"
  run_maybe_sudo pkill -f "${BACKEND_JAR_NAME}" || true
  sleep 1

  if [[ -n "${SUDO}" ]]; then
    ${SUDO} bash -lc "nohup java -jar '${runtime_jar}' --spring.profiles.active='${BACKEND_PROFILE}' > '${out_log}' 2>&1 &"
  else
    nohup java -jar "${runtime_jar}" --spring.profiles.active="${BACKEND_PROFILE}" > "${out_log}" 2>&1 &
  fi
  log "Started backend by nohup: ${runtime_jar}"
}

check_backend() {
  if ! command -v curl >/dev/null 2>&1; then
    warn "curl not found, skip backend health check."
    return
  fi

  local url="http://127.0.0.1:${BACKEND_PORT}/health"
  if curl -fsS "${url}" >/dev/null; then
    log "Backend health check passed: ${url}"
  else
    warn "Backend health check failed: ${url}"
  fi
}

main() {
  ensure_cmd git
  ensure_cmd npm
  ensure_cmd mvn
  ensure_cmd rsync
  ensure_cmd java

  [[ -d "${APP_ROOT}" ]] || fail "APP_ROOT not found: ${APP_ROOT}"

  sync_repo

  if [[ "${SKIP_FRONTEND_BUILD}" != "1" ]]; then
    ensure_swap
    build_frontend "Frontend-Home" "${APP_ROOT}/Frontend-Home" "${HOME_DIST}"
    build_frontend "Blog" "${APP_ROOT}/Blog" "${BLOG_DIST}"
    build_frontend "Cv" "${APP_ROOT}/Cv" "${CV_DIST}"
    build_frontend "Admin" "${APP_ROOT}/Admin" "${ADMIN_DIST}"
  else
    log "Skip frontend build (SKIP_FRONTEND_BUILD=1)."
  fi

  if [[ "${SKIP_BACKEND_BUILD}" != "1" ]]; then
    build_backend
    restart_backend
    check_backend
  else
    log "Skip backend build/restart (SKIP_BACKEND_BUILD=1)."
  fi

  log "Deploy completed."
}

main "$@"
