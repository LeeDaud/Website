package cc.leedaud.service;

import cc.leedaud.dto.ArticleCommentDTO;
import cc.leedaud.dto.ArticleCommentPageQueryDTO;
import cc.leedaud.dto.ArticleCommentReplyDTO;
import cc.leedaud.entity.ArticleComments;
import cc.leedaud.result.PageResult;
import cc.leedaud.vo.ArticleCommentVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 鏂囩珷璇勮鏈嶅姟
 */
public interface ArticleCommentService {

    /**
     * 鍒嗛〉鏉′欢鏌ヨ璇勮锛堟椂闂淬€佹槸鍚﹀鏍革級
     * @param articleCommentPageQueryDTO
     * @return
     */
    PageResult pageQuery(ArticleCommentPageQueryDTO articleCommentPageQueryDTO);

    /**
     * 鏍规嵁鏂囩珷ID鏌ヨ璇勮
     * @param articleId
     * @return
     */
    List<ArticleComments> getByArticleId(Long articleId);

    /**
     * 鎵归噺瀹℃牳閫氳繃璇勮
     * @param ids
     */
    void batchApprove(List<Long> ids);

    /**
     * 鎵归噺鍒犻櫎璇勮
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 绠＄悊鍛樺洖澶嶈瘎璁?     * @param articleCommentReplyDTO
     * @param request
     */
    void adminReply(ArticleCommentReplyDTO articleCommentReplyDTO, HttpServletRequest request);

    // ===== 鍗氬绔柟娉?=====

    /**
     * 鏍规嵁鏂囩珷ID鑾峰彇璇勮鍒楄〃锛堟爲褰㈢粨鏋勶紝宸插鏍?+ 褰撳墠璁垮鐨勬湭瀹℃牳锛?     */
    List<ArticleCommentVO> getCommentTree(Long articleId, Long visitorId);

    /**
     * 璁垮鎻愪氦璇勮
     */
    void submitComment(ArticleCommentDTO articleCommentDTO, HttpServletRequest request);

    /**
     * 璁垮缂栬緫璇勮
     */
    void editComment(cc.leedaud.dto.ArticleCommentEditDTO editDTO);

    /**
     * 璁垮鍒犻櫎璇勮
     */
    void visitorDeleteComment(Long id, Long visitorId);
}

