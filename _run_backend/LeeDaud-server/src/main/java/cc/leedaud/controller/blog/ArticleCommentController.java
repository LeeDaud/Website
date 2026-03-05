package cc.leedaud.controller.blog;

import cc.leedaud.annotation.RateLimit;
import cc.leedaud.dto.ArticleCommentDTO;
import cc.leedaud.dto.ArticleCommentEditDTO;
import cc.leedaud.result.Result;
import cc.leedaud.service.ArticleCommentService;
import cc.leedaud.vo.ArticleCommentVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 鍗氬绔枃绔犺瘎璁烘帴鍙? */
@RestController("blogArticleCommentController")
@RequestMapping("/blog/articleComment")
@Slf4j
public class ArticleCommentController {

    @Autowired
    private ArticleCommentService articleCommentService;

    /**
     * 鏍规嵁鏂囩珷ID鑾峰彇璇勮鍒楄〃锛堟爲褰㈢粨鏋勶紝鍚綋鍓嶈瀹㈢殑鏈鏍歌瘎璁猴級
     */
    @GetMapping("/article/{articleId}")
    public Result<List<ArticleCommentVO>> getCommentTree(@PathVariable Long articleId,
                                                         @RequestParam(required = false) Long visitorId) {
        log.info("鍗氬绔幏鍙栨枃绔犺瘎璁烘爲: articleId={}, visitorId={}", articleId, visitorId);
        List<ArticleCommentVO> commentTree = articleCommentService.getCommentTree(articleId, visitorId);
        return Result.success(commentTree);
    }

    /**
     * 鎻愪氦璇勮锛堟坊鍔犺瘎璁?鍥炲璇勮锛?     */
    @PostMapping
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "璇勮杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<String> submitComment(@Valid @RequestBody ArticleCommentDTO articleCommentDTO,
                                        HttpServletRequest request) {
        log.info("璁垮鎻愪氦鏂囩珷璇勮: {}", articleCommentDTO);
        articleCommentService.submitComment(articleCommentDTO, request);
        return Result.success();
    }

    /**
     * 璁垮缂栬緫璇勮
     */
    @PutMapping("/edit")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "鎿嶄綔杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<String> editComment(@Valid @RequestBody ArticleCommentEditDTO editDTO) {
        log.info("璁垮缂栬緫璇勮: {}", editDTO);
        articleCommentService.editComment(editDTO);
        return Result.success();
    }

    /**
     * 璁垮鍒犻櫎璇勮
     */
    @DeleteMapping("/{id}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "鎿嶄綔杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<String> deleteComment(@PathVariable Long id, @RequestParam Long visitorId) {
        log.info("璁垮鍒犻櫎璇勮: id={}, visitorId={}", id, visitorId);
        articleCommentService.visitorDeleteComment(id, visitorId);
        return Result.success();
    }
}

