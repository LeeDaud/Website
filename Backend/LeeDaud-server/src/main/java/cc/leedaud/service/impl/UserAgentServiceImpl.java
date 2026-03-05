package cc.leedaud.service.impl;

import cc.leedaud.service.UserAgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * UserAgent瑙ｆ瀽鏈嶅姟瀹炵幇
 */
@Slf4j
@Service
public class UserAgentServiceImpl implements UserAgentService {

    /**
     * 鑾峰彇鎿嶄綔绯荤粺鍚嶇О
     * @param userAgent
     * @return
     */
    public String getOsName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }
        
        String ua = userAgent.toLowerCase();
        
        // Windows绯诲垪
        if (ua.contains("windows nt 10.0")) {
            return "Windows";
        } else if (ua.contains("windows nt 6.3")) {
            return "Windows";
        } else if (ua.contains("windows nt 6.2")) {
            return "Windows";
        } else if (ua.contains("windows nt 6.1")) {
            return "Windows";
        } else if (ua.contains("windows")) {
            return "Windows";
        }
        
        // macOS绯诲垪
        if (ua.contains("mac os x") || ua.contains("macintosh")) {
            return "Macos";
        }
        
        // Linux绯诲垪
        if (ua.contains("linux")) {
            // Android鍩轰簬Linux锛屼絾浼樺厛璇嗗埆涓篈ndroid
            if (ua.contains("android")) {
                return "Android";
            }
            return "Linux";
        }
        
        // 绉诲姩璁惧
        if (ua.contains("android")) {
            return "Android";
        }
        if (ua.contains("iphone") || ua.contains("ipad")) {
            return "Ios";
        }
        
        // 鍏朵粬绯荤粺
        if (ua.contains("freebsd")) {
            return "Freebsd";
        }
        if (ua.contains("ubuntu")) {
            return "Ubuntu";
        }
        
        return "Unknown";
    }

    /**
     * 鑾峰彇娴忚鍣ㄥ悕绉?     * @param userAgent
     * @return
     */
    public String getBrowserName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }
        
        String ua = userAgent.toLowerCase();
        
        // 寰俊鍐呯疆娴忚鍣紙UA鍖呭惈Chrome锛屽繀椤讳紭鍏堝垽鏂級
        if (ua.contains("micromessenger")) {
            return "Wechat";
        }
        
        // QQ鍐呯疆娴忚鍣紙UA鍖呭惈Chrome锛屽繀椤讳紭鍏堝垽鏂級
        if (ua.contains("qq/")) {
            return "QQ";
        }
        
        // Edge娴忚鍣?(鏂扮増鍩轰簬Chromium)
        if (ua.contains("edg/") || ua.contains("edge/")) {
            return "Edge";
        }
        
        // Opera娴忚鍣紙UA鍖呭惈Chrome锛岄渶鍦–hrome涔嬪墠鍒ゆ柇锛?        if (ua.contains("opr/") || ua.contains("opera/")) {
            return "Opera";
        }
        
        // Chrome娴忚鍣?(闇€鍦⊿afari涔嬪墠鍒ゆ柇,鍥犱负Chrome鐨刄A鍖呭惈Safari)
        if (ua.contains("chrome/") && !ua.contains("edg")) {
            return "Chrome";
        }
        
        // Safari娴忚鍣?        if (ua.contains("safari/") && !ua.contains("chrome")) {
            return "Safari";
        }
        
        // Firefox娴忚鍣?        if (ua.contains("firefox/")) {
            return "Firefox";
        }
        
        // IE娴忚鍣?        if (ua.contains("msie") || ua.contains("trident/")) {
            return "Ie";
        }
        
        return "Unknown";
    }
}

