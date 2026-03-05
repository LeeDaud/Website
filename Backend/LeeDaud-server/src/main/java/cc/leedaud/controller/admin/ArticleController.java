package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.ArticleDTO;
import cc.leedaud.dto.ArticlePageQueryDTO;
import cc.leedaud.entity.Articles;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.PageResult;
import cc.leedaud.result.Result;
import cc.leedaud.service.ArticleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔枃绔犳帴鍙? */
@Slf4j
@RestController("adminArticleController")
@RequestMapping("/admin/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 鍒嗛〉鏉′欢鏌ヨ鏂囩珷鍒楄〃
     * @param articlePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(ArticlePageQueryDTO articlePageQueryDTO) {
        log.info("鍒嗛〉鏉′欢鏌ヨ鏂囩珷鍒楄〃: {}", articlePageQueryDTO);
        PageResult pageResult = articleService.pageQuery(articlePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 鏍规嵁ID鑾峰彇鏂囩珷璇︽儏
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Articles> getById(@PathVariable Long id) {
        log.info("鏍规嵁ID鑾峰彇鏂囩珷璇︽儏: {}", id);
        Articles articles = articleService.getById(id);
        return Result.success(articles);
    }

    /**
     * 鍒涘缓鏂囩珷
     * @param articleDTO
     * @return
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "article")
    public Result createArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        log.info("鍒涘缓鏂囩珷: {}", articleDTO);
        articleService.createArticle(articleDTO);
        return Result.success();
    }

    /**
     * 鏇存柊鏂囩珷
     * @param articleDTO
     * @return
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "article", targetId = "#articleDTO.id")
    public Result updateArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        log.info("鏇存柊鏂囩珷: {}", articleDTO);
        articleService.updateArticle(articleDTO);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎鏂囩珷
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "article", targetId = "#ids")
    public Result batchDelete(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎鏂囩珷: {}", ids);
        articleService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 鍙戝竷/鍙栨秷鍙戝竷鏂囩珷
     * @param id
     * @param isPublished 0-鍙栨秷鍙戝竷锛?-鍙戝竷
     * @return
     */
    @PutMapping("/publish/{id}")
    @OperationLog(value = OperationType.UPDATE, target = "article", targetId = "#id")
    public Result publishOrCancel(@PathVariable Long id, @RequestParam Integer isPublished) {
        log.info("鍙戝竷/鍙栨秷鍙戝竷鏂囩珷: id={}, isPublished={}", id, isPublished);
        articleService.publishOrCancel(id, isPublished);
        return Result.success();
    }

    /**
     * 缃《/鍙栨秷缃《鏂囩珷
     * @param id
     * @param isTop 0-鍙栨秷缃《锛?-缃《
     * @return
     */
    @PutMapping("/top/{id}")
    @OperationLog(value = OperationType.UPDATE, target = "article", targetId = "#id")
    public Result toggleTop(@PathVariable Long id, @RequestParam Integer isTop) {
        log.info("缃《/鍙栨秷缃《鏂囩珷: id={}, isTop={}", id, isTop);
        articleService.toggleTop(id, isTop);
        return Result.success();
    }

    /**
     * 鏂囩珷鎼滅储锛堟爣棰樸€佸唴瀹癸級
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/search")
    public Result<PageResult> search(@RequestParam String keyword,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        log.info("鏂囩珷鎼滅储: keyword={}", keyword);
        PageResult pageResult = articleService.search(keyword, page, pageSize);
        return Result.success(pageResult);
    }
}

