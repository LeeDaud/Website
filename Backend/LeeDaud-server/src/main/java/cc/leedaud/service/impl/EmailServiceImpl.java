package cc.leedaud.service.impl;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.exception.EmailSendErrorException;
import cc.leedaud.properties.EmailProperties;
import cc.leedaud.properties.WebsiteProperties;
import cc.leedaud.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 閭欢鏈嶅姟
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private EmailProperties emailProperties;
    @Autowired
    private WebsiteProperties websiteProperties;

    /**
     * 鍙戦€侀獙璇佺爜閭欢
     * @param code
     * @return
     */
    public void sendVerifyCode(String toEmail, String code){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom(), emailProperties.getPersonal());
            helper.setTo(toEmail);
            helper.setSubject(websiteProperties.getTitle() + "绠＄悊绔?- 楠岃瘉鐮?);
            helper.setText(buildSendVerifyCodeEmailContent(code), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("鍙戦€侀獙璇佺爜閭欢澶辫触 to={}, ex={}", toEmail, e.getMessage());
            throw new EmailSendErrorException(MessageConstant.EMAIL_SEND_ERROR);
        }
    }

    /**
     * 鏋勫缓鍙戦€侀獙璇佺爜鐨勯偖浠跺唴瀹?     * @param code
     * @return
     */
    private String buildSendVerifyCodeEmailContent(String code) {
        String year = String.valueOf(LocalDate.now().getYear());
        return "<!DOCTYPE html>" +
                "<html lang='zh-CN'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>"+ websiteProperties.getTitle() +"楠岃瘉鐮?/title>" +
                "    <style>" +
                "        body {" +
                "            margin: 0;" +
                "            padding: 0;" +
                "            font-family: \"PingFang SC\", \"Microsoft YaHei\", sans-serif;" +
                "            background-color: #f5f5f5;" +
                "        }" +
                "        .email-container {" +
                "            max-width: 600px;" +
                "            margin: 40px auto;" +
                "            background: white;" +
                "            border-radius: 8px;" +
                "            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);" +
                "            overflow: hidden;" +
                "        }" +
                "        .email-header {" +
                "            background: #333;" +
                "            padding: 24px 0;" +
                "            text-align: center;" +
                "            border-bottom: 1px solid #222;" +
                "        }" +
                "        .email-content {" +
                "            padding: 36px 30px;" +
                "        }" +
                "        .verification-code {" +
                "            background: #f9f9f9;" +
                "            border-radius: 8px;" +
                "            padding: 18px;" +
                "            text-align: center;" +
                "            margin: 26px 0;" +
                "            border: 1px solid #ddd;" +
                "        }" +
                "        .code-display {" +
                "            display: inline-block;" +
                "            background: white;" +
                "            padding: 12px 24px;" +
                "            border-radius: 6px;" +
                "            border: 1px solid #333;" +
                "            margin: 10px 0;" +
                "        }" +
                "        .footer {" +
                "            background: #222;" +
                "            padding: 16px;" +
                "            text-align: center;" +
                "            color: #aaa;" +
                "            font-size: 12px;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='email-container'>" +
                "        <div class='email-header'>" +
                "            <h1 style='color: white; margin: 0; font-size: 24px; font-weight: 500;'>"+websiteProperties.getTitle()+"</h1>" +
                "            <p style='color: #bbb; margin: 8px 0 0; font-size: 14px;'>鈥斺€?閭楠岃瘉 鈥斺€?/p>" +
                "        </div>" +
                "        " +
                "        <div class='email-content'>" +
                "            <h2 style='color: #333; margin: 0 0 20px; font-size: 20px; font-weight: 500;'>鎮ㄥソ锛?/h2>" +
                "            <p style='color: #555; font-size: 15px; line-height: 1.6; margin: 0 0 25px;'>" +
                "                璇蜂娇鐢ㄤ互涓嬮獙璇佺爜瀹屾垚閭楠岃瘉锛? +
                "            </p>" +
                "            " +
                "            <div class='verification-code'>" +
                "                <p style='color: #666; margin: 0 0 12px; font-size: 14px;'>楠岃瘉鐮?/p>" +
                "                <div class='code-display'>" +
                "                    <span style='color: #333; font-size: 28px; font-weight: bold; letter-spacing: 6px; font-family: \"Courier New\", monospace;'>" + code + "</span>" +
                "                </div>" +
                "                <p style='color: #888; margin: 12px 0 0; font-size: 13px; font-weight: 500;'>鏈夋晥鏈?5 鍒嗛挓锛岃鍙婃椂浣跨敤</p>" +
                "            </div>" +
                "        </div>" +
                "        " +
                "        <div class='footer'>" +
                "            <p style='margin: 0; line-height: 1.5;'>" +
                "                姝や负绯荤粺鑷姩鍙戦€佺殑閭欢锛岃鍕跨洿鎺ュ洖澶?br>" +
                "                漏 "+ year +" "+ websiteProperties.getTitle() +" - 淇濈暀鎵€鏈夋潈鍒? +
                "            </p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * 鍙戦€佽瘎璁?鐣欒█鍥炲閫氱煡閭欢
     */
    public void sendReplyNotification(String toEmail, String parentNickname, String parentContent,
                                      String replyNickname, String replyContent, String type) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom(), emailProperties.getPersonal());
            helper.setTo(toEmail);
            String typeText = "comment".equals(type) ? "鏂囩珷璇勮" : "鐣欒█";
            helper.setSubject(websiteProperties.getTitle() + " - 鎮ㄧ殑" + typeText + "鏀跺埌浜嗘柊鍥炲");
            helper.setText(buildReplyNotificationEmailContent(parentNickname, parentContent,
                    replyNickname, replyContent, typeText), true);
            mailSender.send(message);
            log.info("鍙戦€佸洖澶嶉€氱煡閭欢鎴愬姛: to={}, type={}", toEmail, type);
        } catch (Exception e) {
            log.error("鍙戦€佸洖澶嶉€氱煡閭欢澶辫触 to={}, type={}, ex={}", toEmail, type, e.getMessage());
        }
    }

    /**
     * 鏋勫缓鍥炲閫氱煡閭欢鍐呭
     */
    private String buildReplyNotificationEmailContent(String parentNickname, String parentContent,
                                                       String replyNickname, String replyContent, String typeText) {
        String year = String.valueOf(LocalDate.now().getYear());
        return "<!DOCTYPE html>" +
                "<html lang='zh-CN'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>"+ websiteProperties.getTitle() +"鍥炲閫氱煡</title>" +
                "    <style>" +
                "        body { margin: 0; padding: 0; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; background-color: #f5f5f5; }" +
                "        .email-container { max-width: 600px; margin: 40px auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }" +
                "        .email-header { background: #333; padding: 24px 0; text-align: center; border-bottom: 1px solid #222; }" +
                "        .email-content { padding: 36px 30px; }" +
                "        .quote-block { background: #f9f9f9; border-left: 4px solid #333; padding: 16px 20px; margin: 16px 0; border-radius: 0 8px 8px 0; }" +
                "        .reply-block { background: #f0f7ff; border-left: 4px solid #4a90d9; padding: 16px 20px; margin: 16px 0; border-radius: 0 8px 8px 0; }" +
                "        .footer { background: #222; padding: 16px; text-align: center; color: #aaa; font-size: 12px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='email-container'>" +
                "        <div class='email-header'>" +
                "            <h1 style='color: white; margin: 0; font-size: 24px; font-weight: 500;'>"+websiteProperties.getTitle()+"</h1>" +
                "            <p style='color: #bbb; margin: 8px 0 0; font-size: 14px;'>鈥斺€?" + typeText + "鍥炲閫氱煡 鈥斺€?/p>" +
                "        </div>" +
                "        <div class='email-content'>" +
                "            <h2 style='color: #333; margin: 0 0 20px; font-size: 20px; font-weight: 500;'>" + parentNickname + "锛屾偍濂斤紒</h2>" +
                "            <p style='color: #555; font-size: 15px; line-height: 1.6; margin: 0 0 16px;'>鎮ㄧ殑" + typeText + "鏀跺埌浜嗘潵鑷?<strong>" + replyNickname + "</strong> 鐨勫洖澶嶏細</p>" +
                "            <div class='quote-block'>" +
                "                <p style='color: #888; margin: 0 0 6px; font-size: 13px;'>鎮ㄧ殑鍘熷鍐呭锛?/p>" +
                "                <p style='color: #555; margin: 0; font-size: 14px; line-height: 1.6;'>" + parentContent + "</p>" +
                "            </div>" +
                "            <div class='reply-block'>" +
                "                <p style='color: #666; margin: 0 0 6px; font-size: 13px;'>" + replyNickname + " 鐨勫洖澶嶏細</p>" +
                "                <p style='color: #333; margin: 0; font-size: 14px; line-height: 1.6;'>" + replyContent + "</p>" +
                "            </div>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p style='margin: 0; line-height: 1.5;'>姝や负绯荤粺鑷姩鍙戦€佺殑閭欢锛岃鍕跨洿鎺ュ洖澶?br>漏 " + year + " "+websiteProperties.getTitle()+" - 淇濈暀鎵€鏈夋潈鍒?/p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * 鍙戦€佹柊鏂囩珷閫氱煡閭欢
     */
    public void sendNewArticleNotification(String toEmail, String nickname, String articleTitle,
                                           String articleSummary, String articleUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom(), emailProperties.getPersonal());
            helper.setTo(toEmail);
            helper.setSubject(websiteProperties.getTitle() + " - 鏂版枃绔犲彂甯冿細" + articleTitle);
            helper.setText(buildNewArticleNotificationEmailContent(nickname, articleTitle, articleSummary, articleUrl), true);
            mailSender.send(message);
            log.info("鍙戦€佹柊鏂囩珷閫氱煡閭欢鎴愬姛: to={}, title={}", toEmail, articleTitle);
        } catch (Exception e) {
            log.error("鍙戦€佹柊鏂囩珷閫氱煡閭欢澶辫触 to={}, title={}, ex={}", toEmail, articleTitle, e.getMessage());
        }
    }

    /**
     * 鏋勫缓鏂版枃绔犻€氱煡閭欢鍐呭
     */
    private String buildNewArticleNotificationEmailContent(String nickname, String articleTitle,
                                                           String articleSummary, String articleUrl) {
        String year = String.valueOf(LocalDate.now().getYear());
        return "<!DOCTYPE html>" +
                "<html lang='zh-CN'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>"+websiteProperties.getTitle()+"鏂版枃绔犻€氱煡</title>" +
                "    <style>" +
                "        body { margin: 0; padding: 0; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; background-color: #f5f5f5; }" +
                "        .email-container { max-width: 600px; margin: 40px auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }" +
                "        .email-header { background: #333; padding: 24px 0; text-align: center; border-bottom: 1px solid #222; }" +
                "        .email-content { padding: 36px 30px; }" +
                "        .article-card { background: #f9f9f9; border-radius: 8px; padding: 20px 24px; margin: 20px 0; border: 1px solid #eee; }" +
                "        .read-btn { display: inline-block; background: #333; color: white; padding: 12px 28px; border-radius: 6px; text-decoration: none; font-size: 15px; font-weight: 500; margin-top: 10px; }" +
                "        .footer { background: #222; padding: 16px; text-align: center; color: #aaa; font-size: 12px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='email-container'>" +
                "        <div class='email-header'>" +
                "            <h1 style='color: white; margin: 0; font-size: 24px; font-weight: 500;'>"+websiteProperties.getTitle()+"</h1>" +
                "            <p style='color: #bbb; margin: 8px 0 0; font-size: 14px;'>鈥斺€?鏂版枃绔犲彂甯冮€氱煡 鈥斺€?/p>" +
                "        </div>" +
                "        <div class='email-content'>" +
                "            <h2 style='color: #333; margin: 0 0 20px; font-size: 20px; font-weight: 500;'>" + nickname + "锛屾偍濂斤紒</h2>" +
                "            <p style='color: #555; font-size: 15px; line-height: 1.6; margin: 0 0 16px;'>鎮ㄨ闃呯殑鍗氬鏈夋柊鏂囩珷鍙戝竷浜嗭細</p>" +
                "            <div class='article-card'>" +
                "                <h3 style='color: #333; margin: 0 0 12px; font-size: 18px; font-weight: 600;'>" + articleTitle + "</h3>" +
                "                <p style='color: #666; margin: 0 0 16px; font-size: 14px; line-height: 1.6;'>" + (articleSummary != null ? articleSummary : "") + "</p>" +
                "                <a href='" + articleUrl + "' class='read-btn' style='color: white;'>闃呰鍏ㄦ枃 鈫?/a>" +
                "            </div>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p style='margin: 0; line-height: 1.5;'>姝や负绯荤粺鑷姩鍙戦€佺殑閭欢锛岃鍕跨洿鎺ュ洖澶?br>漏 " + year + " "+websiteProperties.getTitle()+" - 淇濈暀鎵€鏈夋潈鍒?/p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

}

