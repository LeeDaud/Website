package cc.leedaud.service;

import cc.leedaud.dto.ArticleCategoryDTO;
import cc.leedaud.entity.ArticleCategories;

import java.util.List;

public interface ArticleCategoryService {
    /**
     * 鑾峰彇鎵€鏈夋枃绔犲垎绫?     * @return
     */
    List<ArticleCategories> listAll();

    /**
     * 娣诲姞鏂囩珷鍒嗙被
     * @param articleCategoryDTO
     */
    void addCategory(ArticleCategoryDTO articleCategoryDTO);

    /**
     * 鏇存柊鏂囩珷鍒嗙被锛堝惈鎺掑簭锛?     * @param articleCategoryDTO
     */
    void updateCategory(ArticleCategoryDTO articleCategoryDTO);

    /**
     * 鎵归噺鍒犻櫎鏂囩珷鍒嗙被
     * @param ids
     */
    void batchDelete(List<Long> ids);

    // ===== 鍗氬绔柟娉?=====

    /**
     * 鑾峰彇鎵€鏈夋湁宸插彂甯冩枃绔犵殑鍙鍒嗙被
     */
    List<ArticleCategories> getVisibleCategories();
}

