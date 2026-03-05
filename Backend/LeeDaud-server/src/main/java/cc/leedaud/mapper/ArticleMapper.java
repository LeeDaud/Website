package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.dto.ArticlePageQueryDTO;
import cc.leedaud.dto.ArticleTitleViewCountDTO;
import cc.leedaud.entity.Articles;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.vo.ArticleArchiveItemVO;
import cc.leedaud.vo.ArticleVO;
import cc.leedaud.vo.BlogArticleDetailVO;
import cc.leedaud.vo.BlogArticleVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleMapper {

    /**
     * 鎻掑叆鏂囩珷
     * @param articles
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Articles articles);

    /**
     * 鍒嗛〉鏉′欢鏌ヨ鏂囩珷锛堝惈鍒嗙被鍚嶇О锛?     * @param articlePageQueryDTO
     * @return
     */
    Page<ArticleVO> pageQuery(ArticlePageQueryDTO articlePageQueryDTO);

    /**
     * 鏍规嵁ID鏌ヨ鏂囩珷
     * @param id
     * @return
     */
    @Select("select * from articles where id = #{id}")
    Articles getById(Long id);

    /**
     * 鏇存柊鏂囩珷
     * @param articles
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Articles articles);

    /**
     * 鎵归噺鍒犻櫎鏂囩珷
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 鍏ㄦ枃鎼滅储鏂囩珷锛堟爣棰樸€佸唴瀹癸級
     * @param keyword
     * @return
     */
    Page<ArticleVO> search(String keyword);

    // ===== 鍗氬绔柟娉?=====

    /**
     * 鑾峰彇宸插彂甯冩枃绔犲垪琛紙鍒嗛〉锛?     */
    Page<BlogArticleVO> getPublishedPage();

    /**
     * 鏍规嵁slug鑾峰彇鏂囩珷璇︽儏
     */
    BlogArticleDetailVO getBySlug(String slug);

    /**
     * 鏍规嵁鍒嗙被ID鑾峰彇宸插彂甯冩枃绔犲垪琛紙鍒嗛〉锛?     */
    Page<BlogArticleVO> getPublishedByCategoryId(Long categoryId);

    /**
     * 鑾峰彇鏂囩珷褰掓。鍒楄〃
     */
    List<ArticleArchiveItemVO> getArchiveList();

    /**
     * 鍗氬绔枃绔犳悳绱紙浠呭凡鍙戝竷锛?     */
    Page<BlogArticleVO> searchPublished(String keyword);

    /**
     * 娴忚閲?1
     */
    @Update("update articles set view_count = view_count + 1 where id = #{id}")
    void incrementViewCount(Long id);

    /**
     * 娴忚閲忔壒閲忕疮鍔狅紙瀹氭椂鍚屾Redis澧為噺锛?     */
    @Update("update articles set view_count = view_count + #{increment} where id = #{id}")
    void addViewCount(@Param("id") Long id, @Param("increment") int increment);

    /**
     * 鐐硅禐鏁?1
     */
    @Update("update articles set like_count = like_count + 1 where id = #{id}")
    void incrementLikeCount(Long id);

    /**
     * 鐐硅禐鏁?1
     */
    @Update("update articles set like_count = case when like_count > 0 then like_count - 1 else 0 end where id = #{id}")
    void decrementLikeCount(Long id);

    /**
     * 缁熻宸插彂甯冩枃绔犳暟
     */
    @Select("select count(*) from articles where is_published = 1")
    Integer countPublished();

    /**
     * 鏍规嵁鍒嗙被ID缁熻鏂囩珷鏁?     */
    @Select("select count(*) from articles where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    List<ArticleTitleViewCountDTO> getViewTop10();

    /**
     * 鏍规嵁鏍囩ID鑾峰彇宸插彂甯冩枃绔犲垪琛紙鍒嗛〉锛?     */
    Page<BlogArticleVO> getPublishedByTagId(Long tagId);

    /**
     * 鑾峰彇褰撳墠鏂囩珷鐨勪笂涓€绡?鎸夊彂甯冩椂闂?
     */
    BlogArticleVO getPrevArticle(Long id);

    /**
     * 鑾峰彇褰撳墠鏂囩珷鐨勪笅涓€绡?鎸夊彂甯冩椂闂?
     */
    BlogArticleVO getNextArticle(Long id);

    /**
     * 鑾峰彇鐩稿叧鏂囩珷(鍚屽垎绫?鎺掗櫎褰撳墠鏂囩珷,鏈€澶?绡?
     */
    List<BlogArticleVO> getRelatedArticles(Long articleId, Long categoryId);
}

