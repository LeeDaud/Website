package cc.leedaud.service;

/**
 * 閭欢鏈嶅姟
 */
public interface EmailService {
    
    /**
     * 鍙戦€侀獙璇佺爜閭欢
     * @param toEmail
     * @param code
     */
    void sendVerifyCode(String toEmail, String code);

    /**
     * 鍙戦€佽瘎璁?鐣欒█鍥炲閫氱煡閭欢
     * @param toEmail 鏀朵欢浜洪偖绠?     * @param parentNickname 琚洖澶嶄汉鏄电О
     * @param parentContent 琚洖澶嶇殑鍐呭
     * @param replyNickname 鍥炲浜烘樀绉?     * @param replyContent 鍥炲鍐呭
     * @param type 绫诲瀷锛歮essage-鐣欒█ / comment-鏂囩珷璇勮
     */
    void sendReplyNotification(String toEmail, String parentNickname, String parentContent,
                               String replyNickname, String replyContent, String type);

    /**
     * 鍙戦€佹柊鏂囩珷閫氱煡閭欢
     * @param toEmail 鏀朵欢浜洪偖绠?     * @param nickname 璁㈤槄鑰呮樀绉?     * @param articleTitle 鏂囩珷鏍囬
     * @param articleSummary 鏂囩珷鎽樿
     * @param articleUrl 鏂囩珷閾炬帴
     */
    void sendNewArticleNotification(String toEmail, String nickname, String articleTitle,
                                    String articleSummary, String articleUrl);
}

