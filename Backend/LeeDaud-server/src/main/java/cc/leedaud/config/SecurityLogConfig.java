package cc.leedaud.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 瀹夊叏鎵弿鏃ュ織杩囨护鍣? * 鎷︽埅甯歌鐨勫畨鍏ㄦ帰娴?/ 婕忔礊鎵弿璇锋眰锛岀洿鎺ヨ繑鍥?404 骞惰褰曞埌鐙珛鏃ュ織鏂囦欢
 */
@Configuration
public class SecurityLogConfig {

    public static final Logger SECURITY_SCAN_LOG = LoggerFactory.getLogger("SECURITY_SCAN");

    /** 椤圭洰鑷韩鐨勫悎娉曡矾寰勫墠缂€锛屽懡涓垯璺宠繃妫€娴?*/
    private static final Set<String> ALLOWED_PREFIXES = Set.of(
            "/blog/", "/admin/", "/home/", "/cv/", "/health", "/ws"
    );

    /** 鍏稿瀷鎵弿璺緞涓嚭鐜扮殑鍚庣紑 */
    private static final Set<String> SCAN_EXTENSIONS = Set.of(
            ".env", ".php", ".bak", ".swp", ".old", ".save",
            ".config", ".ini", ".yml", ".yaml", ".properties",
            ".sql", ".tar.gz", ".zip", ".rar"
    );

    /** 鍏稿瀷鎵弿璺緞涓嚭鐜扮殑鐗囨 */
    private static final Set<String> SCAN_SEGMENTS = Set.of(
            "/.env", "/.git", "/.svn", "/.htaccess", "/.htpasswd",
            "/wp-admin", "/wp-login", "/wp-content", "/wp-includes", "/wordpress",
            "/phpmyadmin", "/phpinfo", "/info.php", "/config.php",
            "/actuator", "/debug", "/console", "/druid",
            "/manager/html", "/solr", "/jenkins", "/struts",
            "/.aws", "/.docker", "/cgi-bin"
    );

    @Bean
    public OncePerRequestFilter securityScanLogFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain)
                    throws IOException, ServletException {

                String path = request.getRequestURI();
                String ip = getClientIp(request);

                // 鍏堝垽鏂槸鍚︿负椤圭洰鍚堟硶璺緞锛屾槸鍒欑洿鎺ユ斁琛?                if (isAllowedPath(path)) {
                    org.slf4j.MDC.put("ip", ip);
                    try {
                        chain.doFilter(request, response);
                    } finally {
                        org.slf4j.MDC.remove("ip");
                    }
                    return;
                }

                // 妫€鏌ユ槸鍚︿负鎵弿璇锋眰
                if (isScanningRequest(path)) {
                    SECURITY_SCAN_LOG.warn("SCAN_ATTEMPT: {} {} from {} UA: {}",
                            request.getMethod(), path, ip, request.getHeader("User-Agent"));
                    response.setStatus(404);
                    response.setContentType("text/plain");
                    response.getWriter().write("Not Found");
                    return;
                }

                // 鏅€氳姹傛斁琛?                org.slf4j.MDC.put("ip", ip);
                try {
                    chain.doFilter(request, response);
                } finally {
                    org.slf4j.MDC.remove("ip");
                }
            }

            private boolean isAllowedPath(String path) {
                for (String prefix : ALLOWED_PREFIXES) {
                    if (path.startsWith(prefix)) {
                        return true;
                    }
                }
                return "/".equals(path);
            }

            private boolean isScanningRequest(String path) {
                String lower = path.toLowerCase();

                // 妫€鏌ュ悗缂€
                for (String ext : SCAN_EXTENSIONS) {
                    if (lower.endsWith(ext)) {
                        return true;
                    }
                }
                // 妫€鏌ヨ矾寰勭墖娈?                for (String seg : SCAN_SEGMENTS) {
                    if (lower.contains(seg)) {
                        return true;
                    }
                }
                return false;
            }

            private String getClientIp(HttpServletRequest request) {
                String ip = request.getHeader("X-Forwarded-For");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    // X-Forwarded-For 鍙兘鍖呭惈澶氫釜 IP锛屽彇绗竴涓?                    return ip.split(",")[0].trim();
                }
                ip = request.getHeader("X-Real-IP");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
                return request.getRemoteAddr();
            }
        };
    }
}

