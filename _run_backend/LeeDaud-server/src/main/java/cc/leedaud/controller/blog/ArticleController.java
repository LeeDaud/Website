package cc.leedaud.controller.blog;

import cc.leedaud.annotation.RateLimit;
import cc.leedaud.result.PageResult;
import cc.leedaud.result.Result;
import cc.leedaud.service.ArticleService;
import cc.leedaud.vo.ArticleArchiveVO;
import cc.leedaud.vo.BlogArticleDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 鍗氬绔枃绔犳帴鍙? */
@RestController("blogArticleController")
@RequestMapping("/blog/article")
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String VIEW_COUNT_KEY = "article:viewCount";

    /**
     * 鑾峰彇宸插彂甯冩枃绔犲垪琛紙鍒嗛〉锛?     */
    @GetMapping("/page")
    public Result<PageResult> getPublishedPage(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        log.info("鍗氬绔幏鍙栧凡鍙戝竷鏂囩珷鍒楄〃: page={}, pageSize={}", page, pageSize);
        PageResult pageResult = articleService.getPublishedPage(page, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 鏍规嵁slug鑾峰彇鏂囩珷璇︽儏锛堟祻瑙堥噺+1锛?     */
    @GetMapping("/detail/{slug}")
    public Result<BlogArticleDetailVO> getBySlug(@PathVariable String slug) {
        log.info("鍗氬绔幏鍙栨枃绔犺鎯? slug={}", slug);
        // 鍏堜粠缂撳瓨鑾峰彇鏂囩珷璇︽儏锛堥伩鍏嶅啑浣橠B鏌ヨ锛?        BlogArticleDetailVO articleDetail = articleService.getBySlug(slug);
        // 娴忚閲?1锛堝啓鍏edis锛屽熀浜庢枃绔營D锛屼笉鍐嶉渶瑕侀澶栨煡搴擄級
        articleService.incrementViewCount(articleDetail.getId());
        // 鍚堝苟Redis涓皻鏈悓姝ュ埌MySQL鐨勬祻瑙堥噺澧為噺锛屽睍绀哄疄鏃舵祻瑙堟暟
        Object pending = redisTemplate.opsForHash().get(VIEW_COUNT_KEY, articleDetail.getId().toString());
        if (pending != null) {
            long pendingCount = ((Number) pending).longValue();
            // 杩斿洖 MySQL鍩哄噯鍊?+ Redis寰呭悓姝ュ閲忥紙鍚湰娆?1锛?            articleDetail.setViewCount(articleDetail.getViewCount() + pendingCount);
        }
        return Result.success(articleDetail);
    }

    /**
     * 鏍规嵁鍒嗙被ID鑾峰彇鏂囩珷鍒楄〃锛堝垎椤碉級
     */
    @GetMapping("/category/{categoryId}")
    public Result<PageResult> getByCategory(@PathVariable Long categoryId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("鍗氬绔牴鎹垎绫昏幏鍙栨枃绔犲垪琛? categoryId={}, page={}, pageSize={}", categoryId, page, pageSize);
        PageResult pageResult = articleService.getPublishedByCategoryId(categoryId, page, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 鑾峰彇鏂囩珷褰掓。锛堟寜骞存湀鍒嗙粍锛?     */
    @GetMapping("/archive")
    public Result<List<ArticleArchiveVO>> getArchive() {
        log.info("鍗氬绔幏鍙栨枃绔犲綊妗?);
        List<ArticleArchiveVO> archiveList = articleService.getArchive();
        return Result.success(archiveList);
    }

    /**
     * 鏂囩珷鎼滅储锛堜粎宸插彂甯冿級
     */
    @GetMapping("/search")
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
            timeWindow = 60, message = "鎼滅储杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<PageResult> search(@RequestParam String keyword,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        log.info("鍗氬绔枃绔犳悳绱? keyword={}", keyword);
        PageResult pageResult = articleService.searchPublished(keyword, page, pageSize);
        return Result.success(pageResult);
    }
}

