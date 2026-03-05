package cc.leedaud.service.impl;

import cc.leedaud.constant.StatusConstant;
import cc.leedaud.dto.ArticleCommentDTO;
import cc.leedaud.dto.ArticleCommentEditDTO;
import cc.leedaud.dto.ArticleCommentPageQueryDTO;
import cc.leedaud.dto.ArticleCommentReplyDTO;
import cc.leedaud.entity.ArticleComments;
import cc.leedaud.exception.ValidationException;
import cc.leedaud.mapper.ArticleCommentMapper;
import cc.leedaud.properties.WebsiteProperties;
import cc.leedaud.result.PageResult;
import cc.leedaud.service.ArticleCommentService;
import cc.leedaud.service.AsyncEmailService;
import cc.leedaud.service.UserAgentService;
import cc.leedaud.utils.IpUtil;
import cc.leedaud.utils.MarkdownUtil;
import cc.leedaud.vo.ArticleCommentVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 鏂囩珷璇勮鏈嶅姟瀹炵幇
 */
@Slf4j
@Service
public class ArticleCommentServiceImpl implements ArticleCommentService {

    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    @Autowired
    private UserAgentService userAgentService;

    @Autowired
    private AsyncEmailService asyncEmailService;

    @Autowired
    private WebsiteProperties websiteProperties;

    /**
     * 鍒嗛〉鏉′欢鏌ヨ璇勮锛堟椂闂淬€佹槸鍚﹀鏍革級
     * @param articleCommentPageQueryDTO
     * @return
     */
    public PageResult pageQuery(ArticleCommentPageQueryDTO articleCommentPageQueryDTO) {
        PageHelper.startPage(articleCommentPageQueryDTO.getPage(), articleCommentPageQueryDTO.getPageSize());
        Page<ArticleComments> page = (Page<ArticleComments>) articleCommentMapper.pageQuery(articleCommentPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 鏍规嵁鏂囩珷ID鏌ヨ璇勮
     * @param articleId
     * @return
     */
    public List<ArticleComments> getByArticleId(Long articleId) {
        return articleCommentMapper.getByArticleId(articleId);
    }

    /**
     * 鎵归噺瀹℃牳閫氳繃璇勮
     * @param ids
     */
    @Transactional
    public void batchApprove(List<Long> ids) {
        // 鍏堟煡璇㈡瘡鏉¤瘎璁猴紝鍙"褰撳墠鏈鏍?鐨勮瘎璁哄鍔犳枃绔犺瘎璁烘暟
        for (Long id : ids) {
            ArticleComments comment = articleCommentMapper.getById(id);
            if (comment != null && comment.getArticleId() != null
                    && (comment.getIsApproved() == null || comment.getIsApproved() == 0)) {
                articleCommentMapper.incrementCommentCount(comment.getArticleId());
            }
        }
        articleCommentMapper.batchApprove(ids);
    }

    /**
     * 鎵归噺鍒犻櫎璇勮
     * @param ids
     */
    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            ArticleComments comment = articleCommentMapper.getById(id);
            if (comment == null || comment.getArticleId() == null) {
                continue;
            }
            // 濡傛灉鏄牴璇勮锛岀骇鑱斿垹闄ゆ墍鏈夊瓙璇勮
            if (comment.getRootId() == null || comment.getRootId() == 0) {
                // 鍙宸插鏍哥殑瀛愯瘎璁哄噺灏戣瘎璁烘暟
                Integer approvedChildCount = articleCommentMapper.countApprovedByRootId(id);
                if (approvedChildCount != null && approvedChildCount > 0) {
                    for (int i = 0; i < approvedChildCount; i++) {
                        articleCommentMapper.decrementCommentCount(comment.getArticleId());
                    }
                }
                articleCommentMapper.deleteByRootId(id);
            }
            // 鍙湁宸插鏍哥殑璇勮鎵嶅噺灏戞枃绔犺瘎璁烘暟
            if (comment.getIsApproved() != null && comment.getIsApproved() == 1) {
                articleCommentMapper.decrementCommentCount(comment.getArticleId());
            }
        }
        articleCommentMapper.batchDelete(ids);
    }

    /**
     * 绠＄悊鍛樺洖澶嶈瘎璁?     * @param articleCommentReplyDTO
     */
    public void adminReply(ArticleCommentReplyDTO articleCommentReplyDTO, HttpServletRequest request) {
        ArticleComments articleComments = new ArticleComments();
        BeanUtils.copyProperties(articleCommentReplyDTO, articleComments);

        // 澶勭悊Markdown鍐呭
        if (articleCommentReplyDTO.getIsMarkdown() != null && articleCommentReplyDTO.getIsMarkdown() == 1) {
            String html = MarkdownUtil.toHtml(articleCommentReplyDTO.getContent());
            articleComments.setContentHtml(html);
        } else {
            articleComments.setContentHtml(articleCommentReplyDTO.getContent());
        }

        // 璁剧疆绠＄悊鍛樺洖澶嶆爣璇?        articleComments.setIsAdminReply(StatusConstant.ENABLE);
        articleComments.setIsApproved(StatusConstant.ENABLE);
        articleComments.setIsEdited(StatusConstant.DISABLE);
        articleComments.setNickname(websiteProperties.getTitle());

        // 鎹曡幏 IP / 鍦扮悊浣嶇疆 / UserAgent
        if (request != null) {
            String clientIp = IpUtil.getClientIp(request);
            Map<String, String> geoInfo = IpUtil.getGeoInfo(clientIp);
            String province = geoInfo.getOrDefault("province", "");
            String city = geoInfo.getOrDefault("city", "");
            String location = province.isEmpty() && city.isEmpty() ? null
                    : province.equals(city) ? province
                    : String.format("%s-%s", province, city).replaceAll("^-|-$", "");
            if(location != null && !location.isEmpty()) {
                articleComments.setLocation(location);
            }
            String userAgent = request.getHeader("User-Agent");
            articleComments.setUserAgentOs(userAgentService.getOsName(userAgent));
            articleComments.setUserAgentBrowser(userAgentService.getBrowserName(userAgent));
        }

        articleCommentMapper.save(articleComments);

        // 绠＄悊鍛樺洖澶嶈嚜鍔ㄩ€氳繃瀹℃牳锛屾枃绔犺瘎璁烘暟+1
        if (articleCommentReplyDTO.getArticleId() != null) {
            articleCommentMapper.incrementCommentCount(articleCommentReplyDTO.getArticleId());
        }

        // 妫€鏌ョ埗璇勮鏄惁寮€鍚偖绠遍€氱煡
        notifyParentIfNeeded(articleCommentReplyDTO.getParentId(), "LeeDaud",
                articleCommentReplyDTO.getContent(), "comment");
    }

    // ===== 鍗氬绔柟娉?=====

    /**
     * 鏍规嵁鏂囩珷ID鑾峰彇璇勮鍒楄〃锛堟爲褰㈢粨鏋勶級
     * @param articleId
     * @return
     */
    public List<ArticleCommentVO> getCommentTree(Long articleId, Long visitorId) {
        List<ArticleCommentVO> allComments = articleCommentMapper.getApprovedByArticleId(articleId, visitorId);
        // 鏋勫缓鏍戝舰缁撴瀯锛氭牴璇勮锛坮ootId涓簄ull鎴?锛変綔涓轰竴绾э紝鍏朵綑鎸傚埌鏍硅瘎璁轰笅
        List<ArticleCommentVO> rootComments = new ArrayList<>();
        Map<Long, ArticleCommentVO> commentMap = allComments.stream()
                .collect(Collectors.toMap(ArticleCommentVO::getId, c -> c));

        for (ArticleCommentVO comment : allComments) {
            if (comment.getRootId() == null || comment.getRootId() == 0) {
                // 鏍硅瘎璁?                comment.setChildren(new ArrayList<>());
                rootComments.add(comment);
            } else {
                // 瀛愯瘎璁猴紝鎸傚埌鏍硅瘎璁轰笅
                ArticleCommentVO rootComment = commentMap.get(comment.getRootId());
                if (rootComment != null) {
                    if (rootComment.getChildren() == null) {
                        rootComment.setChildren(new ArrayList<>());
                    }
                    rootComment.getChildren().add(comment);
                }
            }
        }
        return rootComments;
    }

    /**
     * 鎻愪氦璇勮锛堟坊鍔犺瘎璁?鍥炲璇勮锛?     * @param articleCommentDTO
     */
    @Transactional
    public void submitComment(ArticleCommentDTO articleCommentDTO, HttpServletRequest request) {
        // 1. 鏍￠獙閭鎴朡Q鍙?        validateEmailOrQq(articleCommentDTO.getEmailOrQq());

        // 2. 鍒涘缓璇勮瀹炰綋
        ArticleComments articleComments = new ArticleComments();
        BeanUtils.copyProperties(articleCommentDTO, articleComments);

        // 3. 澶勭悊Markdown鍐呭
        if (articleCommentDTO.getIsMarkdown() != null && articleCommentDTO.getIsMarkdown() == 1) {
            String html = MarkdownUtil.toHtml(articleCommentDTO.getContent());
            articleComments.setContentHtml(html);
        } else {
            articleComments.setContentHtml(articleCommentDTO.getContent());
        }

        // 4. 璁剧疆璁垮ID
        Long visitorId = articleCommentDTO.getVisitorId();
        articleComments.setVisitorId(visitorId);

        // 5. 鑾峰彇IP鍦板潃淇℃伅
        String clientIp = IpUtil.getClientIp(request);
        Map<String, String> geoInfo = IpUtil.getGeoInfo(clientIp);
        String province = geoInfo.getOrDefault("province", "");
        String city = geoInfo.getOrDefault("city", "");
        String location = province.isEmpty() && city.isEmpty() ? null
                : province.equals(city) ? province
                : String.format("%s-%s", province, city).replaceAll("^-|-$", "");
        if(location != null && !location.isEmpty()) {
            articleComments.setLocation(location);
        }

        // 6. 瑙ｆ瀽UserAgent
        String userAgent = request.getHeader("User-Agent");
        String osName = userAgentService.getOsName(userAgent);
        String browserName = userAgentService.getBrowserName(userAgent);
        articleComments.setUserAgentOs(osName);
        articleComments.setUserAgentBrowser(browserName);

        // 7. 璁剧疆榛樿鍊?        articleComments.setIsApproved(0);
        articleComments.setIsEdited(0);
        articleComments.setIsAdminReply(0);

        // 8. 淇濆瓨鍒版暟鎹簱
        articleCommentMapper.save(articleComments);

        // 9. 璇勮鏁颁笉鍦ㄦ彁浜ゆ椂+1锛屾敼涓哄鏍搁€氳繃鏃?1锛堣 batchApprove锛?
        // 10. 妫€鏌ョ埗璇勮鏄惁寮€鍚偖绠遍€氱煡
        if (articleCommentDTO.getParentId() != null) {
            notifyParentIfNeeded(articleCommentDTO.getParentId(),
                    articleCommentDTO.getNickname(), articleCommentDTO.getContent(), "comment");
        }

        log.info("璁垮鎻愪氦鏂囩珷璇勮鎴愬姛: {}", articleComments);
    }

    /**
     * 璁垮缂栬緫璇勮
     */
    public void editComment(ArticleCommentEditDTO editDTO) {
        ArticleComments comment = articleCommentMapper.getById(editDTO.getId());
        if (comment == null) {
            throw new ValidationException("璇勮涓嶅瓨鍦?);
        }
        if (!comment.getVisitorId().equals(editDTO.getVisitorId())) {
            throw new ValidationException("鏃犳潈缂栬緫姝よ瘎璁?);
        }

        ArticleComments updateComment = new ArticleComments();
        updateComment.setId(editDTO.getId());
        updateComment.setContent(editDTO.getContent());

        if (editDTO.getIsMarkdown() != null && editDTO.getIsMarkdown() == 1) {
            updateComment.setContentHtml(MarkdownUtil.toHtml(editDTO.getContent()));
        } else {
            updateComment.setContentHtml(editDTO.getContent());
        }

        articleCommentMapper.updateContent(updateComment);
        log.info("璁垮缂栬緫璇勮鎴愬姛: id={}, visitorId={}", editDTO.getId(), editDTO.getVisitorId());
    }

    /**
     * 璁垮鍒犻櫎璇勮
     */
    @Transactional
    public void visitorDeleteComment(Long id, Long visitorId) {
        ArticleComments comment = articleCommentMapper.getById(id);
        if (comment == null) {
            throw new ValidationException("璇勮涓嶅瓨鍦?);
        }
        if (!comment.getVisitorId().equals(visitorId)) {
            throw new ValidationException("鏃犳潈鍒犻櫎姝よ瘎璁?);
        }

        // 濡傛灉鏄牴璇勮锛岀骇鑱斿垹闄ゆ墍鏈夊瓙璇勮
        if (comment.getRootId() == null || comment.getRootId() == 0) {
            // 鍙宸插鏍哥殑瀛愯瘎璁哄噺灏戣瘎璁烘暟
            Integer approvedChildCount = articleCommentMapper.countApprovedByRootId(id);
            if (approvedChildCount != null && approvedChildCount > 0) {
                for (int i = 0; i < approvedChildCount; i++) {
                    articleCommentMapper.decrementCommentCount(comment.getArticleId());
                }
            }
            Integer totalChildCount = articleCommentMapper.countByRootId(id);
            if (totalChildCount != null && totalChildCount > 0) {
                articleCommentMapper.deleteByRootId(id);
            }
        }

        articleCommentMapper.deleteById(id);
        // 鍙湁宸插鏍哥殑璇勮鎵嶅噺灏戞枃绔犺瘎璁烘暟
        if (comment.getIsApproved() != null && comment.getIsApproved() == 1) {
            articleCommentMapper.decrementCommentCount(comment.getArticleId());
        }
        log.info("璁垮鍒犻櫎璇勮鎴愬姛: id={}, visitorId={}", id, visitorId);
    }

    /**
     * 妫€鏌ョ埗璇勮鏄惁寮€鍚偖绠遍€氱煡锛屽鏋滄槸鍒欏彂閫侀€氱煡閭欢
     */
    private void notifyParentIfNeeded(Long parentId, String replyNickname, String replyContent, String type) {
        if (parentId == null) {
            return;
        }
        try {
            ArticleComments parentComment = articleCommentMapper.getById(parentId);
            if (parentComment != null
                    && parentComment.getIsNotice() != null
                    && parentComment.getIsNotice() == 1
                    && parentComment.getEmailOrQq() != null) {
                String emailOrQq = parentComment.getEmailOrQq().trim();
                String email;
                if (emailOrQq.contains("@")) {
                    // 鏈韩灏辨槸閭锛岀洿鎺ヤ娇鐢?                    email = emailOrQq;
                } else if (emailOrQq.matches("^[1-9]\\d{4,10}$")) {
                    // QQ 鍙凤紝鎷兼帴 @qq.com 鏋勯€犻偖绠卞湴鍧€
                    email = emailOrQq + "@qq.com";
                } else {
                    return; // 鏍煎紡涓嶈瘑鍒紝璺宠繃
                }
                asyncEmailService.sendReplyNotificationAsync(
                        email,
                        parentComment.getNickname(),
                        parentComment.getContent(),
                        replyNickname,
                        replyContent,
                        type
                );
            }
        } catch (Exception e) {
            log.error("鍙戦€佽瘎璁哄洖澶嶉€氱煡閭欢寮傚父: parentId={}, ex={}", parentId, e.getMessage());
        }
    }

    /**
     * 鏍￠獙閭鎴朡Q鍙锋牸寮?     */
    private void validateEmailOrQq(String emailOrQq) {
        if (emailOrQq == null || emailOrQq.isEmpty()) {
            throw new ValidationException("璇疯緭鍏ラ偖绠辨垨QQ鍙?);
        }
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String qqRegex = "^[1-9]\\d{4,10}$";
        if (!emailOrQq.matches(emailRegex) && !emailOrQq.matches(qqRegex)) {
            throw new ValidationException("閭鎴朡Q鍙锋牸寮忎笉姝ｇ‘");
        }
    }
}

