package cc.leedaud.service.impl;

import cc.leedaud.service.AsyncEmailService;
import cc.leedaud.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 寮傛閭欢鏈嶅姟瀹炵幇锛堢嫭绔?Service 淇濊瘉 @Async 浠ｇ悊鐢熸晥锛? */
@Service
@Slf4j
public class AsyncEmailServiceImpl implements AsyncEmailService {

    @Autowired
    private EmailService emailService;

    /**
     * 寮傛鍙戦€佽瘎璁?鐣欒█鍥炲閫氱煡閭欢
     */
    @Async("taskExecutor")
    public void sendReplyNotificationAsync(String toEmail, String parentNickname, String parentContent,
                                           String replyNickname, String replyContent, String type) {
        try {
            emailService.sendReplyNotification(toEmail, parentNickname, parentContent,
                    replyNickname, replyContent, type);
        } catch (Exception e) {
            log.error("寮傛鍙戦€佸洖澶嶉€氱煡閭欢澶辫触: to={}, type={}, ex={}", toEmail, type, e.getMessage());
        }
    }

    /**
     * 寮傛鍙戦€佹柊鏂囩珷閫氱煡閭欢
     */
    @Async("taskExecutor")
    public void sendNewArticleNotificationAsync(String toEmail, String nickname, String articleTitle,
                                                String articleSummary, String articleUrl) {
        try {
            emailService.sendNewArticleNotification(toEmail, nickname, articleTitle, articleSummary, articleUrl);
        } catch (Exception e) {
            log.error("寮傛鍙戦€佹柊鏂囩珷閫氱煡閭欢澶辫触: to={}, title={}, ex={}", toEmail, articleTitle, e.getMessage());
        }
    }
}

