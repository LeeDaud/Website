package cc.leedaud.service;

/**
 * 灏佺鏈嶅姟
 */
public interface BlockService {
    
    /**
     * 妫€鏌ョ紦瀛樻槸鍚︽湁琚皝绂佽褰?     * @param fingerprint
     */
    void checkIfBlocked(String fingerprint);
    
    /**
     * 妫€鏌ヨ姹傞鐜?     * @param fingerprint
     * @param ip
     */
    void checkRateLimit(String fingerprint, String ip);
}

