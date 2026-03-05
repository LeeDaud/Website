package cc.leedaud.service;

import cc.leedaud.dto.ArticleDTO;
import cc.leedaud.dto.ArticlePageQueryDTO;
import cc.leedaud.entity.Articles;
import cc.leedaud.result.PageResult;
import cc.leedaud.vo.ArticleArchiveVO;
import cc.leedaud.vo.BlogArticleDetailVO;

import java.util.List;

/**
 * 鏂囩珷鏈嶅姟
 */
public interface ArticleService {

    /**
     * 鍒涘缓鏂囩珷
     * @param articleDTO
     */
    void createArticle(ArticleDTO articleDTO);

    /**
     * 鍒嗛〉鏉′欢鏌ヨ鏂囩珷鍒楄〃锛堝惈鑽夌锛?     * @param articlePageQueryDTO
     * @return
     */
    PageResult pageQuery(ArticlePageQueryDTO articlePageQueryDTO);

    /**
     * 鏍规嵁ID鑾峰彇鏂囩珷璇︽儏
     * @param id
     * @return
     */
    Articles getById(Long id);

    /**
     * 鏇存柊鏂囩珷
     * @param articleDTO
     */
    void updateArticle(ArticleDTO articleDTO);

    /**
     * 鎵归噺鍒犻櫎鏂囩珷
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 鍙戝竷/鍙栨秷鍙戝竷鏂囩珷
     * @param id
     * @param isPublished
     */
    void publishOrCancel(Long id, Integer isPublished);

    /**
     * 缃《/鍙栨秷缃《鏂囩珷
     * @param id
     * @param isTop
     */
    void toggleTop(Long id, Integer isTop);

    /**
     * 鏂囩珷鎼滅储锛堟爣棰樸€佸唴瀹癸級
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    PageResult search(String keyword, int page, int pageSize);

    // ===== 鍗氬绔柟娉?=====

    /**
     * 鑾峰彇宸插彂甯冩枃绔犲垪琛紙鍒嗛〉锛?     */
    PageResult getPublishedPage(int page, int pageSize);

    /**
     * 鏍规嵁slug鑾峰彇鏂囩珷璇︽儏锛堟祻瑙堥噺+1锛?     */
    BlogArticleDetailVO getBySlug(String slug);

    /**
     * 鏂囩珷娴忚閲?1锛堝啓鍏edis锛屽熀浜庢枃绔營D锛?     */
    void incrementViewCount(Long articleId);

    /**
     * 鏍规嵁鍒嗙被ID鑾峰彇宸插彂甯冩枃绔犲垪琛紙鍒嗛〉锛?     */
    PageResult getPublishedByCategoryId(Long categoryId, int page, int pageSize);

    /**
     * 鑾峰彇鏂囩珷褰掓。锛堟寜骞存湀鍒嗙粍锛?     */
    List<ArticleArchiveVO> getArchive();

    /**
     * 鍗氬绔枃绔犳悳绱紙浠呭凡鍙戝竷锛?     */
    PageResult searchPublished(String keyword, int page, int pageSize);

    /**
     * 鏍规嵁鏍囩ID鑾峰彇宸插彂甯冩枃绔犲垪琛?     */
    PageResult getPublishedByTagId(Long tagId, int page, int pageSize);
}

