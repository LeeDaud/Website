package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.ArticleCategoryDTO;
import cc.leedaud.entity.ArticleCategories;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.Result;
import cc.leedaud.service.ArticleCategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔枃绔犲垎绫绘帴鍙? */
@Slf4j
@RestController("adminArticleCategoryController")
@RequestMapping("/admin/articleCategory")
public class ArticleCategoryController {

    @Autowired
    private ArticleCategoryService articleCategoryService;

    /**
     * 鑾峰彇鎵€鏈夋枃绔犲垎绫?     * @return
     */
    @GetMapping
    public Result<List<ArticleCategories>> listAll() {
        List<ArticleCategories> categoryList = articleCategoryService.listAll();
        return Result.success(categoryList);
    }

    /**
     * 娣诲姞鏂囩珷鍒嗙被
     * @param articleCategoryDTO
     * @return
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "articleCategory")
    public Result addCategory(@Valid @RequestBody ArticleCategoryDTO articleCategoryDTO) {
        log.info("娣诲姞鏂囩珷鍒嗙被,{}", articleCategoryDTO);
        articleCategoryService.addCategory(articleCategoryDTO);
        return Result.success();
    }

    /**
     * 鏇存柊鏂囩珷鍒嗙被
     * @param articleCategoryDTO
     * @return
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "articleCategory", targetId = "#articleCategoryDTO.id")
    public Result updateCategory(@Valid @RequestBody ArticleCategoryDTO articleCategoryDTO) {
        log.info("鏇存柊鏂囩珷鍒嗙被,{}", articleCategoryDTO);
        articleCategoryService.updateCategory(articleCategoryDTO);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎鏂囩珷鍒嗙被
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "articleCategory", targetId = "#ids")
    public Result deleteCategory(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎鏂囩珷鍒嗙被,{}", ids);
        articleCategoryService.batchDelete(ids);
        return Result.success();
    }
}

