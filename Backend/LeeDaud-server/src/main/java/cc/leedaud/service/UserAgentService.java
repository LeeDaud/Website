package cc.leedaud.service;

/**
 * UserAgent瑙ｆ瀽鏈嶅姟
 */
public interface UserAgentService {
    
    /**
     * 瑙ｆ瀽UserAgent鑾峰彇鎿嶄綔绯荤粺鍚嶇О
     * @param userAgent UserAgent瀛楃涓?     * @return 鎿嶄綔绯荤粺鍚嶇О(棣栧瓧姣嶅ぇ鍐?
     */
    String getOsName(String userAgent);
    
    /**
     * 瑙ｆ瀽UserAgent鑾峰彇娴忚鍣ㄥ悕绉?     * @param userAgent UserAgent瀛楃涓?     * @return 娴忚鍣ㄥ悕绉?棣栧瓧姣嶅ぇ鍐?
     */
    String getBrowserName(String userAgent);
}

