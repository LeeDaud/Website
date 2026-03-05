package cc.leedaud.controller.blog;

import cc.leedaud.entity.ArticleTags;
import cc.leedaud.result.PageResult;
import cc.leedaud.result.Result;
import cc.leedaud.service.ArticleService;
import cc.leedaud.service.ArticleTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 鍗氬绔枃绔犳爣绛炬帴鍙? */
@Slf4j
@RestController("blogArticleTagController")
@RequestMapping("/blog/article/tag")
public class ArticleTagController {

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private ArticleService articleService;

    /**
     * 鑾峰彇鏈夊凡鍙戝竷鏂囩珷鐨勬爣绛惧垪琛?     */
    @GetMapping
    public Result<List<ArticleTags>> getVisibleTags() {
        List<ArticleTags> list = articleTagService.getVisibleTags();
        return Result.success(list);
    }

    /**
     * 鏍规嵁鏍囩ID鑾峰彇宸插彂甯冩枃绔犲垪琛?     */
    @GetMapping("/{tagId}")
    public Result<PageResult> getPublishedByTagId(@PathVariable Long tagId,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        log.info("鍗氬绔牴鎹爣绛捐幏鍙栨枃绔犲垪琛? tagId={}", tagId);
        PageResult pageResult = articleService.getPublishedByTagId(tagId, page, pageSize);
        return Result.success(pageResult);
    }
}

