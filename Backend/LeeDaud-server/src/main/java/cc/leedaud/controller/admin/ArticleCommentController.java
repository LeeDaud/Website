package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.ArticleCommentPageQueryDTO;
import cc.leedaud.dto.ArticleCommentReplyDTO;
import cc.leedaud.entity.ArticleComments;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.PageResult;
import cc.leedaud.result.Result;
import cc.leedaud.service.ArticleCommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔枃绔犺瘎璁烘帴鍙? */
@Slf4j
@RestController("adminArticleCommentController")
@RequestMapping("/admin/article/comment")
public class ArticleCommentController {

    @Autowired
    private ArticleCommentService articleCommentService;

    /**
     * 鍒嗛〉鏉′欢鏌ヨ璇勮锛堟椂闂淬€佹槸鍚﹀鏍革級
     * @param articleCommentPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(ArticleCommentPageQueryDTO articleCommentPageQueryDTO) {
        log.info("鍒嗛〉鏉′欢鏌ヨ鏂囩珷璇勮: {}", articleCommentPageQueryDTO);
        PageResult pageResult = articleCommentService.pageQuery(articleCommentPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 鏍规嵁鏂囩珷ID鏌ヨ璇勮
     * @param articleId
     * @return
     */
    @GetMapping("/{articleId}")
    public Result<List<ArticleComments>> getByArticleId(@PathVariable Long articleId) {
        log.info("鏍规嵁鏂囩珷ID鏌ヨ璇勮: articleId={}", articleId);
        List<ArticleComments> comments = articleCommentService.getByArticleId(articleId);
        return Result.success(comments);
    }

    /**
     * 鎵归噺瀹℃牳閫氳繃璇勮
     * @param ids
     * @return
     */
    @PutMapping("/approve")
    @OperationLog(value = OperationType.UPDATE, target = "articleComment", targetId = "#ids")
    public Result<String> batchApprove(@RequestParam List<Long> ids) {
        log.info("鎵归噺瀹℃牳閫氳繃鏂囩珷璇勮: {}", ids);
        articleCommentService.batchApprove(ids);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎璇勮
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "articleComment", targetId = "#ids")
    public Result<String> batchDelete(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎鏂囩珷璇勮: {}", ids);
        articleCommentService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 绠＄悊鍛樺洖澶嶈瘎璁?     * @param articleCommentReplyDTO
     * @return
     */
    @PostMapping("/reply")
    @OperationLog(value = OperationType.INSERT, target = "articleComment", targetId = "#articleCommentReplyDTO.parentId")
    public Result<String> adminReply(@Valid @RequestBody ArticleCommentReplyDTO articleCommentReplyDTO,
                                     HttpServletRequest request) {
        log.info("绠＄悊鍛樺洖澶嶆枃绔犺瘎璁? {}", articleCommentReplyDTO);
        articleCommentService.adminReply(articleCommentReplyDTO, request);
        return Result.success();
    }
}

