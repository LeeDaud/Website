package cc.leedaud.service;

/**
 * 寮傛閭欢鏈嶅姟锛堥€氳繃鐙珛 Service 纭繚 @Async 浠ｇ悊鐢熸晥锛? */
public interface AsyncEmailService {

    /**
     * 寮傛鍙戦€佽瘎璁?鐣欒█鍥炲閫氱煡閭欢
     */
    void sendReplyNotificationAsync(String toEmail, String parentNickname, String parentContent,
                                    String replyNickname, String replyContent, String type);

    /**
     * 寮傛鍙戦€佹柊鏂囩珷閫氱煡閭欢
     */
    void sendNewArticleNotificationAsync(String toEmail, String nickname, String articleTitle,
                                         String articleSummary, String articleUrl);
}

