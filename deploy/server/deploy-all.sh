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

BACKEND_RUNTIME_DIR="${BACKEND_RUNTIME_DIR:-/root/website/runtime/backend}"
BACKEND_JAR_NAME="${BACKEND_JAR_NAME:-LeeDaud-server.jar}"
BACKEND_SERVICE_NAME="${BACKEND_SERVICE_NAME:-leedaud-backend}"
BACKEND_PROFILE="${BACKEND_PROFILE:-prod}"
BACKEND_PORT="${BACKEND_PORT:-5922}"
BACKEND_JAVA_OPTS="${BACKEND_JAVA_OPTS:--Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true}"
BACKEND_WRITE_RUNTIME_CONFIG="${BACKEND_WRITE_RUNTIME_CONFIG:-1}"
BACKEND_CONFIG_FILE="${BACKEND_CONFIG_FILE:-${BACKEND_RUNTIME_DIR}/application.yml}"

BACKEND_DB_HOST="${BACKEND_DB_HOST:-127.0.0.1}"
BACKEND_DB_PORT="${BACKEND_DB_PORT:-3306}"
BACKEND_DB_NAME="${BACKEND_DB_NAME:-LeeDaud}"
BACKEND_DB_USER="${BACKEND_DB_USER:-leedaud}"
BACKEND_DB_PASSWORD="${BACKEND_DB_PASSWORD:-LeeDaud@2026}"
BACKEND_SYNC_ADMIN_CREDENTIALS="${BACKEND_SYNC_ADMIN_CREDENTIALS:-0}"
BACKEND_ADMIN_TARGET_ID="${BACKEND_ADMIN_TARGET_ID:-1}"
BACKEND_ADMIN_USERNAME="${BACKEND_ADMIN_USERNAME:-}"
BACKEND_ADMIN_PASSWORD="${BACKEND_ADMIN_PASSWORD:-}"

BACKEND_REDIS_HOST="${BACKEND_REDIS_HOST:-127.0.0.1}"
BACKEND_REDIS_PORT="${BACKEND_REDIS_PORT:-6379}"
BACKEND_REDIS_PASSWORD="${BACKEND_REDIS_PASSWORD:-}"
BACKEND_REDIS_DATABASE="${BACKEND_REDIS_DATABASE:-0}"

BACKEND_MAIL_HOST="${BACKEND_MAIL_HOST:-smtp.qq.com}"
BACKEND_MAIL_PORT="${BACKEND_MAIL_PORT:-465}"
BACKEND_MAIL_USER="${BACKEND_MAIL_USER:-1015976714@qq.com}"
BACKEND_MAIL_PASSWORD="${BACKEND_MAIL_PASSWORD:-kptvjbecjhtubffi}"
BACKEND_EMAIL_PERSONAL="${BACKEND_EMAIL_PERSONAL:-LeeDaud}"
BACKEND_EMAIL_FROM="${BACKEND_EMAIL_FROM:-${BACKEND_MAIL_USER}}"
BACKEND_MAIL_SSL_ENABLE="${BACKEND_MAIL_SSL_ENABLE:-true}"
BACKEND_MAIL_STARTTLS_ENABLE="${BACKEND_MAIL_STARTTLS_ENABLE:-false}"
BACKEND_MAIL_STARTTLS_REQUIRED="${BACKEND_MAIL_STARTTLS_REQUIRED:-false}"
BACKEND_MAIL_CONNECTION_TIMEOUT_MS="${BACKEND_MAIL_CONNECTION_TIMEOUT_MS:-5000}"
BACKEND_MAIL_TIMEOUT_MS="${BACKEND_MAIL_TIMEOUT_MS:-5000}"
BACKEND_MAIL_WRITE_TIMEOUT_MS="${BACKEND_MAIL_WRITE_TIMEOUT_MS:-5000}"

BACKEND_JWT_SECRET="${BACKEND_JWT_SECRET:-LeeDaud-jwt-secret-2026-0123456789abcdef}"
BACKEND_JWT_TTL="${BACKEND_JWT_TTL:-7200000}"
BACKEND_JWT_TOKEN_NAME="${BACKEND_JWT_TOKEN_NAME:-Authorization}"

BACKEND_VISITOR_VERIFY_CODE="${BACKEND_VISITOR_VERIFY_CODE:-123456}"
BACKEND_MYBATIS_MAPPER_LOCATIONS="${BACKEND_MYBATIS_MAPPER_LOCATIONS:-classpath:mapper/*.xml}"
BACKEND_MYBATIS_ALIASES_PACKAGE="${BACKEND_MYBATIS_ALIASES_PACKAGE:-cc.leedaud.entity,cc.feitwnd.entity}"
BACKEND_LOG_ROOT_PACKAGE="${BACKEND_LOG_ROOT_PACKAGE:-cc}"

BACKEND_WEBSITE_TITLE="${BACKEND_WEBSITE_TITLE:-LeeDaud}"
BACKEND_WEBSITE_HOME="${BACKEND_WEBSITE_HOME:-https://licheng.website}"
BACKEND_WEBSITE_ADMIN="${BACKEND_WEBSITE_ADMIN:-https://admin.licheng.website}"
BACKEND_WEBSITE_CV="${BACKEND_WEBSITE_CV:-https://cv.licheng.website}"
BACKEND_WEBSITE_BLOG="${BACKEND_WEBSITE_BLOG:-https://blog.licheng.website}"

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

sql_escape() {
  local value="${1:-}"
  value="${value//\\/\\\\}"
  value="${value//\'/\'\'}"
  printf '%s' "${value}"
}

write_backend_runtime_config() {
  if [[ "${BACKEND_WRITE_RUNTIME_CONFIG}" != "1" ]]; then
    log "Skip backend runtime config generation (BACKEND_WRITE_RUNTIME_CONFIG=${BACKEND_WRITE_RUNTIME_CONFIG})."
    return
  fi

  local config_dir config_file profile_file tmp_cfg tmp_profile
  config_file="${BACKEND_CONFIG_FILE}"
  config_dir="$(dirname "${config_file}")"
  profile_file="${config_dir}/application-prod.yml"
  tmp_cfg="$(mktemp)"
  tmp_profile="$(mktemp)"

  cat > "${tmp_cfg}" <<EOF
server:
  port: ${BACKEND_PORT}

spring:
  profiles:
    active: ${BACKEND_PROFILE}
  main:
    allow-circular-references: true
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${BACKEND_DB_HOST}:${BACKEND_DB_PORT}/${BACKEND_DB_NAME}?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true
    username: ${BACKEND_DB_USER}
    password: "${BACKEND_DB_PASSWORD}"
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
  data:
    redis:
      host: ${BACKEND_REDIS_HOST}
      port: ${BACKEND_REDIS_PORT}
      password: "${BACKEND_REDIS_PASSWORD}"
      database: ${BACKEND_REDIS_DATABASE}
  mail:
    host: ${BACKEND_MAIL_HOST}
    port: ${BACKEND_MAIL_PORT}
    username: ${BACKEND_MAIL_USER}
    password: "${BACKEND_MAIL_PASSWORD}"
    properties:
      mail:
        smtp:
          auth: true
          ssl:
            enable: ${BACKEND_MAIL_SSL_ENABLE}
          starttls:
            enable: ${BACKEND_MAIL_STARTTLS_ENABLE}
            required: ${BACKEND_MAIL_STARTTLS_REQUIRED}
          connectiontimeout: ${BACKEND_MAIL_CONNECTION_TIMEOUT_MS}
          timeout: ${BACKEND_MAIL_TIMEOUT_MS}
          writetimeout: ${BACKEND_MAIL_WRITE_TIMEOUT_MS}

mybatis:
  mapper-locations: ${BACKEND_MYBATIS_MAPPER_LOCATIONS}
  type-aliases-package: ${BACKEND_MYBATIS_ALIASES_PACKAGE}
  configuration:
    map-underscore-to-camel-case: true

logging:
  level:
    ${BACKEND_LOG_ROOT_PACKAGE}:
      mapper: debug
      service: info
      controller: info

leedaud:
  jwt:
    secret-key: ${BACKEND_JWT_SECRET}
    ttl: ${BACKEND_JWT_TTL}
    token-name: ${BACKEND_JWT_TOKEN_NAME}
  alioss:
    endpoint: ""
    access-key-id: ""
    access-key-secret: ""
    bucket-name: ""
  email:
    personal: ${BACKEND_EMAIL_PERSONAL}
    from: ${BACKEND_EMAIL_FROM}
  visitor:
    verify-code: "${BACKEND_VISITOR_VERIFY_CODE}"
  image:
    compress:
      enabled: true
      max-size-kb: 500
      quality: 0.9
      output-format: webp
  website:
    title: ${BACKEND_WEBSITE_TITLE}
    home: ${BACKEND_WEBSITE_HOME}
    admin: ${BACKEND_WEBSITE_ADMIN}
    cv: ${BACKEND_WEBSITE_CV}
    blog: ${BACKEND_WEBSITE_BLOG}
EOF

  cat > "${tmp_profile}" <<'EOF'
# Auto-generated by deploy-all.sh
# Keep this file valid to avoid parser failures from stale manual edits.
runtime:
  generated: true
EOF

  run_maybe_sudo mkdir -p "${config_dir}"
  run_maybe_sudo cp -f "${tmp_cfg}" "${config_file}"
  run_maybe_sudo cp -f "${tmp_profile}" "${profile_file}"
  run_maybe_sudo chmod 600 "${config_file}" "${profile_file}"
  rm -f "${tmp_cfg}" "${tmp_profile}"

  log "Backend runtime config generated: ${config_file}"
}

ensure_backend_systemd_override() {
  local service="${BACKEND_SERVICE_NAME}.service"

  if ! command -v systemctl >/dev/null 2>&1; then
    return
  fi
  if ! systemctl list-unit-files | grep -q "^${service}[[:space:]]"; then
    return
  fi

  local dropin_dir override_file tmp_override runtime_jar
  runtime_jar="${BACKEND_RUNTIME_DIR}/${BACKEND_JAR_NAME}"
  dropin_dir="/etc/systemd/system/${service}.d"
  override_file="${dropin_dir}/override.conf"
  tmp_override="$(mktemp)"

  cat > "${tmp_override}" <<EOF
[Service]
WorkingDirectory=${BACKEND_RUNTIME_DIR}
ExecStart=
ExecStart=/usr/bin/java ${BACKEND_JAVA_OPTS} -jar ${runtime_jar} --spring.profiles.active=${BACKEND_PROFILE} --spring.config.additional-location=file:${BACKEND_RUNTIME_DIR}/
EOF

  run_maybe_sudo mkdir -p "${dropin_dir}"
  run_maybe_sudo cp -f "${tmp_override}" "${override_file}"
  rm -f "${tmp_override}"
  run_maybe_sudo systemctl daemon-reload || true

  log "Updated systemd override: ${override_file}"
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
    ensure_backend_systemd_override
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
    ${SUDO} bash -lc "nohup java ${BACKEND_JAVA_OPTS} -jar '${runtime_jar}' --spring.profiles.active='${BACKEND_PROFILE}' > '${out_log}' 2>&1 &"
  else
    nohup java ${BACKEND_JAVA_OPTS} -jar "${runtime_jar}" --spring.profiles.active="${BACKEND_PROFILE}" > "${out_log}" 2>&1 &
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

sync_admin_credentials() {
  if [[ "${BACKEND_SYNC_ADMIN_CREDENTIALS}" != "1" ]]; then
    log "Skip admin credential sync (BACKEND_SYNC_ADMIN_CREDENTIALS=${BACKEND_SYNC_ADMIN_CREDENTIALS})."
    return
  fi

  if [[ -z "${BACKEND_ADMIN_USERNAME}" || -z "${BACKEND_ADMIN_PASSWORD}" ]]; then
    warn "Admin credential sync enabled, but BACKEND_ADMIN_USERNAME/BACKEND_ADMIN_PASSWORD is empty. Skip."
    return
  fi

  ensure_cmd mysql

  local escaped_username escaped_password sql
  escaped_username="$(sql_escape "${BACKEND_ADMIN_USERNAME}")"
  escaped_password="$(sql_escape "${BACKEND_ADMIN_PASSWORD}")"

  read -r -d '' sql <<EOF || true
SET NAMES utf8mb4;
UPDATE admin
SET username='${escaped_username}',
    password=SHA2(CONCAT('${escaped_password}', salt), 256),
    update_time=NOW()
WHERE id=${BACKEND_ADMIN_TARGET_ID};
SELECT id, username, role, update_time
FROM admin
WHERE id=${BACKEND_ADMIN_TARGET_ID}
LIMIT 1;
EOF

  mysql \
    --host="${BACKEND_DB_HOST}" \
    --port="${BACKEND_DB_PORT}" \
    --user="${BACKEND_DB_USER}" \
    --password="${BACKEND_DB_PASSWORD}" \
    --database="${BACKEND_DB_NAME}" \
    --batch --raw --skip-column-names \
    -e "${sql}" \
    || fail "Failed to sync admin credentials to database."

  log "Admin credentials synced for id=${BACKEND_ADMIN_TARGET_ID}."
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
  else
    log "Skip backend package build (SKIP_BACKEND_BUILD=1)."
  fi

  write_backend_runtime_config
  sync_admin_credentials
  restart_backend
  check_backend

  log "Deploy completed."
}

main "$@"
