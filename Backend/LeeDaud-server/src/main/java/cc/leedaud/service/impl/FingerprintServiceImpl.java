package cc.leedaud.service.impl;

import cc.leedaud.dto.VisitorRecordDTO;
import cc.leedaud.service.FingerprintService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class FingerprintServiceImpl implements FingerprintService {
    
    /**
     * 鐢熸垚璁垮鎸囩汗
     * @param dto
     * @param request
     * @return
     */
    public String generateVisitorFingerprint(VisitorRecordDTO dto, HttpServletRequest request) {
        // 鎻愬彇鏇寸ǔ瀹氱殑鐗瑰緛
        String userAgent = request.getHeader("User-Agent");

        // 绠€鍖栧钩鍙颁俊鎭?        String simplifiedPlatform = simplifyPlatform(dto.getPlatform());

        // 瀵瑰睆骞曞垎杈ㄧ巼杩涜鍒嗙粍
        String screenGroup = groupScreenResolution(dto.getScreen());

        // 澶勭悊鍙兘涓簄ull鐨勫€?        Integer hardwareConcurrency = dto.getHardwareConcurrency() != null ?
                dto.getHardwareConcurrency() : 0;
        Integer deviceMemory = dto.getDeviceMemory() != null ?
                dto.getDeviceMemory() : 0;

        String fingerprintSource = String.format("%s|%s|%s|%s|%d|%d|%s",
                simplifiedPlatform,
                StringUtils.hasText(dto.getLanguage()) ? dto.getLanguage() : "unknown",
                StringUtils.hasText(dto.getTimezone()) ? dto.getTimezone() : "unknown",
                screenGroup,
                hardwareConcurrency,
                deviceMemory,
                extractBrowserInfo(userAgent)
        );

        return DigestUtils.md5DigestAsHex(fingerprintSource.getBytes());
    }

    /**
     * 鎻愬彇娴忚鍣ㄦ牳蹇冧俊鎭?     * 浠嶶ser-Agent涓彁鍙栨祻瑙堝櫒绫诲瀷鍜屼富鐗堟湰鍙?     * @param userAgent User-Agent瀛楃涓?     * @return 娴忚鍣ㄦ牳蹇冧俊鎭?     */
    private String extractBrowserInfo(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }

        String lowerUserAgent = userAgent.toLowerCase();

        // 妫€娴嬫祻瑙堝櫒绫诲瀷
        String browser = "Other";
        String version = "";

        if (lowerUserAgent.contains("chrome") && !lowerUserAgent.contains("edg")) {
            browser = "Chrome";
            // 鎻愬彇Chrome鐗堟湰鍙?            int chromeIndex = lowerUserAgent.indexOf("chrome/");
            if (chromeIndex > 0) {
                int endIndex = Math.min(chromeIndex + 12, userAgent.length());
                version = userAgent.substring(chromeIndex + 7, endIndex).split("\\.")[0];
            }
        } else if (lowerUserAgent.contains("firefox")) {
            browser = "Firefox";
            int firefoxIndex = lowerUserAgent.indexOf("firefox/");
            if (firefoxIndex > 0) {
                int endIndex = Math.min(firefoxIndex + 12, userAgent.length());
                version = userAgent.substring(firefoxIndex + 8, endIndex).split("\\.")[0];
            }
        } else if (lowerUserAgent.contains("safari") && !lowerUserAgent.contains("chrome")) {
            browser = "Safari";
            int versionIndex = lowerUserAgent.indexOf("version/");
            if (versionIndex > 0) {
                int endIndex = Math.min(versionIndex + 12, userAgent.length());
                version = userAgent.substring(versionIndex + 8, endIndex).split("\\.")[0];
            }
        } else if (lowerUserAgent.contains("edge")) {
            browser = "Edge";
            int edgeIndex = lowerUserAgent.indexOf("edg");
            if (edgeIndex > 0) {
                int endIndex = Math.min(edgeIndex + 8, userAgent.length());
                version = userAgent.substring(edgeIndex + 4, endIndex).split("\\.")[0];
            }
        } else if (lowerUserAgent.contains("opera")) {
            browser = "Opera";
            int operaIndex = lowerUserAgent.indexOf("opr/");
            if (operaIndex > 0) {
                int endIndex = Math.min(operaIndex + 8, userAgent.length());
                version = userAgent.substring(operaIndex + 4, endIndex).split("\\.")[0];
            }
        }

        return String.format("%s_%s", browser, version);
    }

    /**
     * 绠€鍖栧钩鍙颁俊鎭?     * @param platform 骞冲彴淇℃伅
     * @return 绠€鍖栧悗鐨勫钩鍙颁俊鎭?     */
    private String simplifyPlatform(String platform) {
        if (platform == null) return "unknown";
        if (platform.contains("Win")) return "Windows";
        if (platform.contains("Mac")) return "MacOS";
        if (platform.contains("Linux")) return "Linux";
        if (platform.contains("iPhone") || platform.contains("iPad")) return "iOS";
        if (platform.contains("Android")) return "Android";
        return platform;
    }

    /**
     * 鍒嗙粍灞忓箷鍒嗚鲸鐜?     * @param screen
     * @return
     */
    private String groupScreenResolution(String screen) {
        if (screen == null) return "unknown";
        try {
            String[] parts = screen.split("x");
            if (parts.length == 2) {
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);

                // 鎸夊父瑙佸垎杈ㄧ巼鍒嗙粍
                if (width >= 3840) return "4K";
                if (width >= 2560) return "2K";
                if (width >= 1920) return "FHD";
                if (width >= 1366) return "HD";
                if (width >= 1024) return "Tablet";
                return "Mobile";
            }
        } catch (Exception e) {
            return "unknown";
        }
        return screen;
    }
}

