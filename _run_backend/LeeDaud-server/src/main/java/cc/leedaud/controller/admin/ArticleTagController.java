package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.ArticleTagDTO;
import cc.leedaud.entity.ArticleTags;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.Result;
import cc.leedaud.service.ArticleTagService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔枃绔犳爣绛炬帴鍙? */
@Slf4j
@RestController("adminArticleTagController")
@RequestMapping("/admin/article/tag")
public class ArticleTagController {

    @Autowired
    private ArticleTagService articleTagService;

    /**
     * 鑾峰彇鎵€鏈夋爣绛?     * @return
     */
    @GetMapping
    public Result<List<ArticleTags>> listAll() {
        List<ArticleTags> list = articleTagService.listAll();
        return Result.success(list);
    }

    /**
     * 娣诲姞鏍囩
     * @param articleTagDTO
     * @return
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "articleTag")
    public Result addTag(@Valid @RequestBody ArticleTagDTO articleTagDTO) {
        log.info("娣诲姞鏂囩珷鏍囩: {}", articleTagDTO);
        articleTagService.addTag(articleTagDTO);
        return Result.success();
    }

    /**
     * 淇敼鏍囩
     * @param articleTagDTO
     * @return
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "articleTag", targetId = "#articleTagDTO.id")
    public Result updateTag(@Valid @RequestBody ArticleTagDTO articleTagDTO) {
        log.info("淇敼鏂囩珷鏍囩: {}", articleTagDTO);
        articleTagService.updateTag(articleTagDTO);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎鏍囩
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "articleTag", targetId = "#ids")
    public Result batchDelete(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎鏂囩珷鏍囩: {}", ids);
        articleTagService.batchDelete(ids);
        return Result.success();
    }
}

