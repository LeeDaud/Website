package cc.leedaud.service;

/**
 * 瀵嗙爜鍔犲瘑鏈嶅姟
 */
public interface EncryptPasswordService {
    
    /**
     * 璁＄畻瀵嗙爜+鐩愮殑鍝堝笇
     * @param password
     * @param salt
     * @return
     * @throws Exception
     */
    String hashPassword(String password, String salt) throws Exception;
}

