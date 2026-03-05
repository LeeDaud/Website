package cc.leedaud.service;

/**
 * Token鏈嶅姟
 */
public interface TokenService {
    
    /**
     * 鍒涘缓骞朵繚瀛榯oken
     */
    String createAndStoreToken(Long userId, Integer role);
    
    /**
     * 楠岃瘉token鏈夋晥鎬?     */
    boolean isValidToken(Long userId, String token);
    
    /**
     * 閫€鍑虹櫥褰?- 鍒犻櫎token
     */
    void logout(Long userId, String token);
    
    /**
     * 閫€鍑虹櫥褰?- 鍒犻櫎鎵€鏈塼oken
     */
    void logoutAll(Long userId);
}

