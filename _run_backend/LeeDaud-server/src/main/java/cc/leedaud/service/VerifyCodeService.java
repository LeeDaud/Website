package cc.leedaud.service;

/**
 * 楠岃瘉鐮佹湇鍔? */
public interface VerifyCodeService {
    
    /**
     * 鐢熸垚楠岃瘉鐮?     */
    String generateCode();
    
    /**
     * 淇濆瓨楠岃瘉鐮佸苟璁剧疆鍙戦€侀鐜?     */
    void saveCode(String code);
    
    /**
     * 閭鏄惁鍙互鍙戦€侀獙璇佺爜锛堥鐜囬檺鍒讹級
     */
    boolean canSendCode();
    
    /**
     * 鑾峰彇鍓╀綑楠岃瘉鐮佸喎鍗存椂闂?绉?
     */
    Long getRemainingCooldown();
    
    /**
     * 鏄惁琚攣瀹?     */
    boolean isLocked();
    
    /**
     * 鑾峰彇閿佸畾鍓╀綑鏃堕棿锛堝垎閽燂級
     */
    Long getLockRemainingMinutes();
    
    /**
     * 鏄惁鍏佽灏濊瘯楠岃瘉
     */
    boolean canAttempt();
    
    /**
     * 楠岃瘉楠岃瘉鐮?     */
    boolean verifyCode(String code);
    
    /**
     * 鑾峰彇鍓╀綑灏濊瘯娆℃暟
     */
    Long getRemainingAttempts();
}

