package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.entity.ArticleTags;
import cc.leedaud.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleTagMapper {

    /**
     * 鑾峰彇鎵€鏈夋爣绛?     */
    @Select("select * from article_tags order by id")
    List<ArticleTags> listAll();

    /**
     * 鏍规嵁鏂囩珷ID鑾峰彇鏍囩ID鍒楄〃
     */
    @Select("select tag_id from article_tag_relations where article_id = #{articleId}")
    List<Long> getTagIdsByArticleId(Long articleId);

    /**
     * 娣诲姞鏍囩
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(ArticleTags articleTag);

    /**
     * 鏇存柊鏍囩
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(ArticleTags articleTag);

    /**
     * 鎵归噺鍒犻櫎鏍囩
     */
    void batchDelete(List<Long> ids);

    /**
     * 鏍规嵁ID鏌ヨ鏍囩
     */
    @Select("select * from article_tags where id = #{id}")
    ArticleTags getById(Long id);

    /**
     * 鏍规嵁鏂囩珷ID鏌ヨ鍏宠仈鐨勬爣绛惧垪琛?     */
    List<ArticleTags> getTagsByArticleId(Long articleId);

    /**
     * 鎵归噺鎻掑叆鏂囩珷-鏍囩鍏宠仈
     */
    void batchInsertRelations(Long articleId, List<Long> tagIds);

    /**
     * 鍒犻櫎鏂囩珷鐨勬墍鏈夋爣绛惧叧鑱?     */
    @Delete("delete from article_tag_relations where article_id = #{articleId}")
    void deleteRelationsByArticleId(Long articleId);

    /**
     * 鎵归噺鍒犻櫎鏂囩珷鐨勬爣绛惧叧鑱旓紙鎸夋枃绔營D鍒楄〃锛?     */
    void batchDeleteRelationsByArticleIds(List<Long> articleIds);

    /**
     * 鑾峰彇鏈夊凡鍙戝竷鏂囩珷鐨勬爣绛惧垪琛紙鍗氬绔級
     */
    List<ArticleTags> getVisibleTags();

    /**
     * 鑾峰彇鏍囩鎬绘暟
     */
    @Select("select count(*) from article_tags")
    Integer countTotal();
}

