package cc.leedaud.service.impl;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.constant.StatusConstant;
import cc.leedaud.dto.MessageDTO;
import cc.leedaud.dto.MessageEditDTO;
import cc.leedaud.dto.MessagePageQueryDTO;
import cc.leedaud.dto.MessageReplyDTO;
import cc.leedaud.entity.Messages;
import cc.leedaud.exception.ValidationException;
import cc.leedaud.mapper.MessageMapper;
import cc.leedaud.properties.WebsiteProperties;
import cc.leedaud.result.PageResult;
import cc.leedaud.service.AsyncEmailService;
import cc.leedaud.service.MessageService;
import cc.leedaud.service.UserAgentService;
import cc.leedaud.utils.IpUtil;
import cc.leedaud.utils.MarkdownUtil;
import cc.leedaud.vo.MessageVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 鐣欒█鏈嶅姟瀹炵幇
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserAgentService userAgentService;

    @Autowired
    private AsyncEmailService asyncEmailService;

    @Autowired
    private WebsiteProperties websiteProperties;

    // 閭姝ｅ垯
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // QQ鍙锋鍒?(5-11浣嶆暟瀛?
    private static final Pattern QQ_PATTERN = Pattern.compile("^[1-9][0-9]{4,10}$");

    /**
     * 璁垮鎻愪氦鐣欒█
     * @param messageDTO
     * @param request
     */
    public void submitMessage(MessageDTO messageDTO, HttpServletRequest request) {
        // 1. 鏍￠獙閭鎴朡Q鍙?        validateEmailOrQq(messageDTO.getEmailOrQq());

        // 2. 鍒涘缓鐣欒█瀹炰綋
        Messages messages = new Messages();
        BeanUtils.copyProperties(messageDTO, messages);

        // 3. 澶勭悊Markdown鍐呭
        if (messageDTO.getIsMarkdown() != null && messageDTO.getIsMarkdown() == 1) {
            // 濡傛灉鏄疢arkdown锛岃浆鎹负HTML
            String html = MarkdownUtil.toHtml(messageDTO.getContent());
            messages.setContentHtml(html);
        } else {
            // 濡傛灉涓嶆槸Markdown锛岀洿鎺ヤ娇鐢ㄥ師鍐呭
            messages.setContentHtml(messageDTO.getContent());
        }

        // 4. 璁剧疆璁垮ID
        Long visitorId = messageDTO.getVisitorId();
        messages.setVisitorId(visitorId);

        // 5. 鑾峰彇IP鍦板潃淇℃伅
        String clientIp = IpUtil.getClientIp(request);
        Map<String, String> geoInfo = IpUtil.getGeoInfo(clientIp);
        // 鎷兼帴鍦板潃: 鐪佷唤-鍩庡競
        String province = geoInfo.getOrDefault("province", "");
        String city = geoInfo.getOrDefault("city", "");
        String location = province.isEmpty() && city.isEmpty() ? null
                : province.equals(city) ? province
                : String.format("%s-%s", province, city).replaceAll("^-|-$", "");
        if(location != null && !location.isEmpty()) {
            messages.setLocation(location);
        }

        // 6. 瑙ｆ瀽UserAgent
        String userAgent = request.getHeader("User-Agent");
        String osName = userAgentService.getOsName(userAgent);
        String browserName = userAgentService.getBrowserName(userAgent);
        messages.setUserAgentOs(osName);
        messages.setUserAgentBrowser(browserName);

        // 7. 璁剧疆榛樿鍊?        messages.setIsApproved(0); // 榛樿鏈鏍?        messages.setIsEdited(0);   // 榛樿鏈紪杈?        messages.setCreateTime(LocalDateTime.now());
        messages.setUpdateTime(LocalDateTime.now());

        // 8. 淇濆瓨鍒版暟鎹簱
        messageMapper.save(messages);

        // 9. 妫€鏌ョ埗鐣欒█鏄惁寮€鍚偖绠遍€氱煡
        if (messageDTO.getParentId() != null) {
            notifyParentIfNeeded(messageDTO.getParentId(),
                    messageDTO.getNickname(), messageDTO.getContent(), "message");
        }

        log.info("璁垮鎻愪氦鐣欒█鎴愬姛: {}", messages);
    }

    /**
     * 鏍￠獙閭鎴朡Q鍙?     * @param emailOrQq
     */
    private void validateEmailOrQq(String emailOrQq) {
        if (emailOrQq == null || emailOrQq.trim().isEmpty()) {
            throw new ValidationException(MessageConstant.EMAIL_OR_QQ_REQUIRED);
        }

        emailOrQq = emailOrQq.trim();

        // 鍏堝垽鏂槸鍚︽槸QQ鍙?        if (QQ_PATTERN.matcher(emailOrQq).matches()) {
            return; // QQ鍙锋牸寮忔纭?        }

        // 鍐嶅垽鏂槸鍚︽槸閭
        if (EMAIL_PATTERN.matcher(emailOrQq).matches()) {
            return; // 閭鏍煎紡姝ｇ‘
        }

        // 閮戒笉鍖归厤锛屾姏鍑哄紓甯?        // 鍒ゆ柇鏇村儚QQ鍙疯繕鏄偖绠?        if (emailOrQq.matches("^[0-9]+$")) {
            throw new ValidationException(MessageConstant.INVALID_QQ_FORMAT);
        } else {
            throw new ValidationException(MessageConstant.INVALID_EMAIL_FORMAT);
        }
    }

    /**
     * 鍒嗛〉鏌ヨ鐣欒█
     * @param messagePageQueryDTO
     * @return
     */
    public PageResult pageQuery(MessagePageQueryDTO messagePageQueryDTO) {
        PageHelper.startPage(messagePageQueryDTO.getPage(), messagePageQueryDTO.getPageSize());
        Page<Messages> page = (Page<Messages>) messageMapper.pageQuery(messagePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 鎵归噺瀹℃牳鐣欒█
     * @param ids
     */
    public void batchApprove(List<Long> ids) {
        messageMapper.batchApprove(ids);
    }

    /**
     * 鎵归噺鍒犻櫎鐣欒█
     * @param ids
     */
    @Transactional
    public void batchDelete(List<Long> ids) {
        // 濡傛灉鏄牴鐣欒█锛岀骇鑱斿垹闄ゆ墍鏈夊瓙鐣欒█
        for (Long id : ids) {
            Messages message = messageMapper.getById(id);
            if (message != null && (message.getRootId() == null || message.getRootId() == 0)) {
                Integer childCount = messageMapper.countByRootId(id);
                if (childCount != null && childCount > 0) {
                    messageMapper.deleteByRootId(id);
                }
            }
        }
        messageMapper.batchDelete(ids);
    }

    /**
     * 绠＄悊鍛樺洖澶嶇暀瑷€
     * @param messageReplyDTO
     */
    public void adminReply(MessageReplyDTO messageReplyDTO, HttpServletRequest request) {
        // 1. 鍒涘缓鐣欒█瀹炰綋
        Messages messages = new Messages();
        BeanUtils.copyProperties(messageReplyDTO, messages);

        // 2. 澶勭悊Markdown鍐呭
        if (messageReplyDTO.getIsMarkdown() != null && messageReplyDTO.getIsMarkdown() == 1) {
            String html = MarkdownUtil.toHtml(messageReplyDTO.getContent());
            messages.setContentHtml(html);
        } else {
            messages.setContentHtml(messageReplyDTO.getContent());
        }

        // 3. 璁剧疆绠＄悊鍛樺洖澶嶆爣璇?        messages.setIsAdminReply(StatusConstant.ENABLE);
        messages.setIsApproved(StatusConstant.ENABLE); // 绠＄悊鍛樺洖澶嶈嚜鍔ㄥ鏍搁€氳繃
        messages.setIsEdited(StatusConstant.DISABLE);
        messages.setNickname(websiteProperties.getTitle());
        messages.setCreateTime(LocalDateTime.now());
        messages.setUpdateTime(LocalDateTime.now());

        // 4. 鎹曡幏 IP / 鍦扮悊浣嶇疆 / UserAgent
        if (request != null) {
            String clientIp = IpUtil.getClientIp(request);
            Map<String, String> geoInfo = IpUtil.getGeoInfo(clientIp);
            String province = geoInfo.getOrDefault("province", "");
            String city = geoInfo.getOrDefault("city", "");
            String location = province.isEmpty() && city.isEmpty() ? null
                    : province.equals(city) ? province
                    : String.format("%s-%s", province, city).replaceAll("^-|-$", "");
            if(location != null && !location.isEmpty()) {
                messages.setLocation(location);
            }
            String userAgent = request.getHeader("User-Agent");
            messages.setUserAgentOs(userAgentService.getOsName(userAgent));
            messages.setUserAgentBrowser(userAgentService.getBrowserName(userAgent));
        }

        // 5. 淇濆瓨鍒版暟鎹簱
        messageMapper.save(messages);

        // 6. 妫€鏌ョ埗鐣欒█鏄惁寮€鍚偖绠遍€氱煡
        notifyParentIfNeeded(messageReplyDTO.getParentId(), websiteProperties.getTitle(),
                messageReplyDTO.getContent(), "message");

        log.info("绠＄悊鍛樺洖澶嶇暀瑷€鎴愬姛: parentId={}, content={}", messageReplyDTO.getParentId(), messageReplyDTO.getContent());
    }

    // ===== 鍗氬绔柟娉?=====

    public List<MessageVO> getMessageTree(Long visitorId) {
        List<MessageVO> allMessages = messageMapper.getApprovedList(visitorId);
        // 鏋勫缓鏍戝舰缁撴瀯锛氭牴鐣欒█锛坮ootId涓簄ull鎴?锛変綔涓轰竴绾э紝鍏朵綑鎸傚埌鏍圭暀瑷€涓?        List<MessageVO> rootMessages = new ArrayList<>();
        Map<Long, MessageVO> messageMap = allMessages.stream()
                .collect(Collectors.toMap(MessageVO::getId, m -> m));

        for (MessageVO msg : allMessages) {
            if (msg.getRootId() == null || msg.getRootId() == 0) {
                msg.setChildren(new ArrayList<>());
                rootMessages.add(msg);
            } else {
                MessageVO rootMsg = messageMap.get(msg.getRootId());
                if (rootMsg != null) {
                    if (rootMsg.getChildren() == null) {
                        rootMsg.setChildren(new ArrayList<>());
                    }
                    rootMsg.getChildren().add(msg);
                }
            }
        }
        return rootMessages;
    }

    /**
     * 璁垮缂栬緫鐣欒█
     */
    public void editMessage(MessageEditDTO editDTO) {
        Messages message = messageMapper.getById(editDTO.getId());
        if (message == null) {
            throw new ValidationException("鐣欒█涓嶅瓨鍦?);
        }
        if (!message.getVisitorId().equals(editDTO.getVisitorId())) {
            throw new ValidationException("鏃犳潈缂栬緫姝ょ暀瑷€");
        }

        Messages updateMessage = new Messages();
        updateMessage.setId(editDTO.getId());
        updateMessage.setContent(editDTO.getContent());

        if (editDTO.getIsMarkdown() != null && editDTO.getIsMarkdown() == 1) {
            updateMessage.setContentHtml(MarkdownUtil.toHtml(editDTO.getContent()));
        } else {
            updateMessage.setContentHtml(editDTO.getContent());
        }

        messageMapper.updateContent(updateMessage);
        log.info("璁垮缂栬緫鐣欒█鎴愬姛: id={}, visitorId={}", editDTO.getId(), editDTO.getVisitorId());
    }

    /**
     * 璁垮鍒犻櫎鐣欒█
     */
    @Transactional
    public void visitorDeleteMessage(Long id, Long visitorId) {
        Messages message = messageMapper.getById(id);
        if (message == null) {
            throw new ValidationException("鐣欒█涓嶅瓨鍦?);
        }
        if (!message.getVisitorId().equals(visitorId)) {
            throw new ValidationException("鏃犳潈鍒犻櫎姝ょ暀瑷€");
        }

        // 濡傛灉鏄牴鐣欒█锛岀骇鑱斿垹闄ゆ墍鏈夊瓙鐣欒█
        if (message.getRootId() == null || message.getRootId() == 0) {
            Integer childCount = messageMapper.countByRootId(id);
            if (childCount != null && childCount > 0) {
                messageMapper.deleteByRootId(id);
            }
        }

        messageMapper.deleteById(id);
        log.info("璁垮鍒犻櫎鐣欒█鎴愬姛: id={}, visitorId={}", id, visitorId);
    }

    /**
     * 妫€鏌ョ埗鐣欒█鏄惁寮€鍚偖绠遍€氱煡锛屽鏋滄槸鍒欏彂閫侀€氱煡閭欢
     */
    private void notifyParentIfNeeded(Long parentId, String replyNickname, String replyContent, String type) {
        if (parentId == null) {
            return;
        }
        try {
            Messages parentMessage = messageMapper.getById(parentId);
            if (parentMessage != null
                    && parentMessage.getIsNotice() != null
                    && parentMessage.getIsNotice() == 1
                    && parentMessage.getEmailOrQq() != null) {
                String emailOrQq = parentMessage.getEmailOrQq().trim();
                String email;
                if (emailOrQq.contains("@")) {
                    // 鏈韩灏辨槸閭锛岀洿鎺ヤ娇鐢?                    email = emailOrQq;
                } else if (emailOrQq.matches("^[1-9]\\d{4,10}$")) {
                    // QQ 鍙凤紝鎷兼帴 @qq.com 鏋勯€犻偖绠卞湴鍧€
                    email = emailOrQq + "@qq.com";
                } else {
                    return; // 鏍煎紡涓嶈瘎鍒紝璺宠繃
                }
                asyncEmailService.sendReplyNotificationAsync(
                        email,
                        parentMessage.getNickname(),
                        parentMessage.getContent(),
                        replyNickname,
                        replyContent,
                        type
                );
            }
        } catch (Exception e) {
            log.error("鍙戦€佺暀瑷€鍥炲閫氱煡閭欢寮傚父: parentId={}, ex={}", parentId, e.getMessage());
        }
    }
}

