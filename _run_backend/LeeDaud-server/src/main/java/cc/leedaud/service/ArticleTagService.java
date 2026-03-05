package cc.leedaud.service;

import cc.leedaud.dto.ArticleTagDTO;
import cc.leedaud.entity.ArticleTags;

import java.util.List;

public interface ArticleTagService {

    /**
     * 鑾峰彇鎵€鏈夋爣绛?     * @return
     */
    List<ArticleTags> listAll();

    /**
     * 娣诲姞鏍囩
     * @param articleTagDTO
     * @return
     */
    void addTag(ArticleTagDTO articleTagDTO);

    /**
     * 淇敼鏍囩
     * @param articleTagDTO
     * @return
     */
    void updateTag(ArticleTagDTO articleTagDTO);

    /**
     * 鎵归噺鍒犻櫎鏍囩
     * @param ids
     * @return
     */
    void batchDelete(List<Long> ids);

    /**
     * 鑾峰彇鏈夊凡鍙戝竷鏂囩珷鐨勬爣绛惧垪琛紙鍗氬绔級
     * @return
     */
    List<ArticleTags> getVisibleTags();
}

