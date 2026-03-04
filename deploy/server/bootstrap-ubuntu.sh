#!/usr/bin/env bash
set -Eeuo pipefail
IFS=$'\n\t'

REPO_URL="${REPO_URL:-https://github.com/LeeDaud/Website.git}"
APP_ROOT="${APP_ROOT:-/root/website}"
BRANCH="${BRANCH:-main}"
NGINX_CONF_TARGET="${NGINX_CONF_TARGET:-/etc/nginx/conf.d/website.conf}"
SERVICE_TARGET="${SERVICE_TARGET:-/etc/systemd/system/leedaud-backend.service}"

log() {
  printf '[BOOTSTRAP] %s\n' "$*"
}

fail() {
  printf '[BOOTSTRAP][ERROR] %s\n' "$*" >&2
  exit 1
}

need_root() {
  [[ "${EUID}" -eq 0 ]] || fail "Run as root."
}

install_base_deps() {
  export DEBIAN_FRONTEND=noninteractive
  apt-get update
  apt-get install -y \
    ca-certificates \
    curl \
    git \
    rsync \
    nginx \
    redis-server \
    mariadb-server \
    maven \
    openjdk-21-jdk \
    software-properties-common
}

install_node22_if_needed() {
  if command -v node >/dev/null 2>&1; then
    local v
    v="$(node -v | sed 's/^v//')"
    local major
    major="${v%%.*}"
    if [[ "${major}" -ge 22 ]]; then
      log "Node already installed: v${v}"
      return
    fi
  fi

  log "Installing Node.js 22.x ..."
  curl -fsSL https://deb.nodesource.com/setup_22.x | bash -
  apt-get install -y nodejs
  node -v
  npm -v
}

sync_repo() {
  if [[ ! -d "${APP_ROOT}/.git" ]]; then
    log "Cloning repo to ${APP_ROOT} ..."
    git clone --branch "${BRANCH}" "${REPO_URL}" "${APP_ROOT}"
  else
    log "Repo exists, pulling latest ${BRANCH} ..."
    git -C "${APP_ROOT}" fetch origin "${BRANCH}"
    git -C "${APP_ROOT}" checkout "${BRANCH}"
    git -C "${APP_ROOT}" pull --ff-only origin "${BRANCH}"
  fi
}

prepare_files() {
  [[ -f "${APP_ROOT}/deploy/deploy.env" ]] || cp "${APP_ROOT}/deploy/deploy.env.example" "${APP_ROOT}/deploy/deploy.env"
  chmod +x "${APP_ROOT}/deploy/server/deploy-all.sh"

  cp -f "${APP_ROOT}/deploy/server/nginx-sites.example.conf" "${NGINX_CONF_TARGET}"
  nginx -t
  systemctl enable nginx
  systemctl reload nginx

  cp -f "${APP_ROOT}/deploy/server/leedaud-backend.service.example" "${SERVICE_TARGET}"
  systemctl daemon-reload
  systemctl enable leedaud-backend.service
}

check_prod_config() {
  local prod_cfg="${APP_ROOT}/Backend/LeeDaud-server/src/main/resources/application-prod.yml"
  [[ -f "${prod_cfg}" ]] || fail "Missing ${prod_cfg}"

  if grep -q "your_production" "${prod_cfg}"; then
    cat <<'EOF'
[BOOTSTRAP][WARN] application-prod.yml still has placeholder values.
Edit this file first:
  /root/website/Backend/LeeDaud-server/src/main/resources/application-prod.yml

Then run:
  bash /root/website/deploy/server/deploy-all.sh
EOF
    exit 2
  fi
}

main() {
  need_root
  install_base_deps
  install_node22_if_needed
  sync_repo
  prepare_files
  check_prod_config
  bash "${APP_ROOT}/deploy/server/deploy-all.sh"
  log "Bootstrap + first deploy done."
}

main "$@"
