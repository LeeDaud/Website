package cc.leedaud.controller.blog;

import cc.leedaud.annotation.RateLimit;
import cc.leedaud.result.Result;
import cc.leedaud.service.ArticleLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 鍗氬绔枃绔犵偣璧炴帴鍙? */
@RestController("blogArticleLikeController")
@RequestMapping("/blog/articleLike")
@Slf4j
public class ArticleLikeController {

    @Autowired
    private ArticleLikeService articleLikeService;

    /**
     * 鐐硅禐鏂囩珷
     */
    @PostMapping("/{articleId}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
              timeWindow = 60, message = "鐐硅禐鎿嶄綔杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<String> like(@PathVariable Long articleId, @RequestParam Long visitorId) {
        log.info("璁垮鐐硅禐鏂囩珷: articleId={}, visitorId={}", articleId, visitorId);
        articleLikeService.likeArticle(articleId, visitorId);
        return Result.success();
    }

    /**
     * 鍙栨秷鐐硅禐
     */
    @DeleteMapping("/{articleId}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
              timeWindow = 60, message = "鎿嶄綔杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<String> unlike(@PathVariable Long articleId, @RequestParam Long visitorId) {
        log.info("璁垮鍙栨秷鐐硅禐: articleId={}, visitorId={}", articleId, visitorId);
        articleLikeService.unlikeArticle(articleId, visitorId);
        return Result.success();
    }

    /**
     * 妫€鏌ユ槸鍚﹀凡鐐硅禐
     */
    @GetMapping("/{articleId}")
    public Result<Boolean> hasLiked(@PathVariable Long articleId, @RequestParam Long visitorId) {
        log.info("妫€鏌ユ槸鍚﹀凡鐐硅禐: articleId={}, visitorId={}", articleId, visitorId);
        boolean liked = articleLikeService.hasLiked(articleId, visitorId);
        return Result.success(liked);
    }
}

