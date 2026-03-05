package cc.leedaud.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * IP鍦板潃宸ュ叿绫? */
@Slf4j
public class IpUtil {
    // IP鍦板潃鏌ヨ鎺ュ彛
    public static final String IP_API = "http://ip-api.com/json/";
    public static final String LANGUAGE = "zh-CN";

    // 鑾峰彇鐪熷疄IP鍦板潃锛堝吋瀹笴DN/鍙嶅悜浠ｇ悊锛?    public static String getClientIp(HttpServletRequest request) {
        // CDN涓撶敤澶达紙浼樺厛绾ф渶楂橈級
        String ip = request.getHeader("CF-Connecting-IP");      // Cloudflare
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("True-Client-IP");            // Cloudflare Enterprise / Akamai
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Ali-CDN-Real-IP");           // 闃块噷浜慍DN
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");                 // Nginx / 閫氱敤CDN
        }
        // 鏍囧噯浠ｇ悊澶?        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 澶氱骇浠ｇ悊鏃讹紝鍙栫涓€涓狪P锛堝嵆鐪熷疄瀹㈡埛绔疘P锛?        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    // 鑾峰彇IP鍦板潃淇℃伅
    public static Map<String, String> getGeoInfo(String ip){
        Map<String,String> params = new HashMap<>();
        params.put("lang",LANGUAGE);
        String doneGet = HttpClientUtil.doGet(IP_API + ip, params);
        log.info("IP鍦板潃淇℃伅鏌ヨ缁撴灉锛歿}",doneGet);
        // 灏佽杩斿洖缁撴灉
        Map<String, String> geoInfo = new HashMap<>();

        try {
            // 浣跨敤Jackson ObjectMapper瑙ｆ瀽JSON
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(doneGet, Map.class);

            // 鎻愬彇闇€瑕佺殑淇℃伅
            geoInfo.put("country", (String) jsonMap.getOrDefault("country", ""));
            geoInfo.put("province", stripAdminSuffix((String) jsonMap.getOrDefault("regionName", "")));
            geoInfo.put("city", stripAdminSuffix((String) jsonMap.getOrDefault("city", "")));
            geoInfo.put("latitude", String.valueOf(jsonMap.getOrDefault("lat", "")));
            geoInfo.put("longitude", String.valueOf(jsonMap.getOrDefault("lon", "")));

        } catch (Exception e) {
            log.error("瑙ｆ瀽IP鍦板潃淇℃伅澶辫触", e);
        }
        return geoInfo;
    }

    /**
     * 鍘绘帀琛屾斂鍖哄垝鍚庣紑锛堢渷銆佸競銆佽嚜娌诲尯銆佺壒鍒鏀垮尯锛?     * 姣忎釜瀛楁閮界嫭绔嬫牎楠?鐪?鍜?甯?鍚庣紑
     */
    private static String stripAdminSuffix(String name) {
        if (name == null || name.isEmpty()) return name;
        // 鍏堝幓闄ゅ鏉傜殑琛屾斂鍖哄垝鍚庣紑
        name = name.replaceAll("澹棌鑷不鍖簗缁村惥灏旇嚜娌诲尯|鍥炴棌鑷不鍖簗鑷不鍖簗鐗瑰埆琛屾斂鍖?, "");
        // 鍐嶅幓闄ゆ湯灏剧殑"鐪?鎴?甯?锛堜繚璇佸幓闄ゅ悗鑷冲皯淇濈暀 1 涓瓧绗︼級
        if (name.length() > 1 && (name.endsWith("鐪?) || name.endsWith("甯?))) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }
}

