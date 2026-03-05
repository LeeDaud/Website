package cc.leedaud.controller.blog;

import cc.leedaud.entity.ArticleCategories;
import cc.leedaud.result.Result;
import cc.leedaud.service.ArticleCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 鍗氬绔枃绔犲垎绫绘帴鍙? */
@RestController("blogArticleCategoryController")
@RequestMapping("/blog/articleCategory")
@Slf4j
public class ArticleCategoryController {

    @Autowired
    private ArticleCategoryService articleCategoryService;

    /**
     * 鑾峰彇鎵€鏈夊彲瑙佹枃绔犲垎绫伙紙鏈夊凡鍙戝竷鏂囩珷鐨勫垎绫伙級
     */
    @GetMapping
    public Result<List<ArticleCategories>> getVisibleCategories() {
        log.info("鍗氬绔幏鍙栧彲瑙佹枃绔犲垎绫?);
        List<ArticleCategories> categoryList = articleCategoryService.getVisibleCategories();
        return Result.success(categoryList);
    }
}

