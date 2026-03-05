package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.dto.ArticleCommentPageQueryDTO;
import cc.leedaud.entity.ArticleComments;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.vo.ArticleCommentVO;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface ArticleCommentMapper {

    /**
     * 淇濆瓨璇勮
     * @param articleComments
     */
    @AutoFill(value = OperationType.INSERT)
    void save(ArticleComments articleComments);

    /**
     * 鍒嗛〉鏉′欢鏌ヨ璇勮
     * @param articleCommentPageQueryDTO
     * @return
     */
    List<ArticleComments> pageQuery(ArticleCommentPageQueryDTO articleCommentPageQueryDTO);

    /**
     * 鏍规嵁鏂囩珷ID鏌ヨ璇勮
     * @param articleId
     * @return
     */
    List<ArticleComments> getByArticleId(Long articleId);

    /**
     * 鎵归噺瀹℃牳閫氳繃璇勮
     * @param ids
     */
    @AutoFill(value = OperationType.UPDATE)
    void batchApprove(List<Long> ids);

    /**
     * 鎵归噺鍒犻櫎璇勮
     * @param ids
     */
    void batchDelete(List<Long> ids);

    // ===== 鍗氬绔柟娉?=====

    /**
     * 鏍规嵁鏂囩珷ID鑾峰彇璇勮鍒楄〃锛堝凡瀹℃牳 + 鎸囧畾璁垮鐨勬湭瀹℃牳璇勮锛?     */
    List<ArticleCommentVO> getApprovedByArticleId(@Param("articleId") Long articleId, @Param("visitorId") Long visitorId);

    /**
     * 璇勮鏁?1
     */
    @Update("update articles set comment_count = comment_count + 1 where id = #{articleId}")
    void incrementCommentCount(Long articleId);

    /**
     * 璇勮鏁?1锛堟渶灏忎负0锛?     */
    @Update("update articles set comment_count = case when comment_count > 0 then comment_count - 1 else 0 end where id = #{articleId}")
    void decrementCommentCount(Long articleId);

    /**
     * 鏍规嵁ID鏌ヨ璇勮
     */
    @Select("select * from article_comments where id = #{id}")
    ArticleComments getById(Long id);

    /**
     * 鏇存柊璇勮鍐呭锛堣瀹㈢紪杈戯級
     */
    void updateContent(ArticleComments articleComments);

    /**
     * 鍒犻櫎鍗曟潯璇勮
     */
    @Delete("delete from article_comments where id = #{id}")
    void deleteById(Long id);

    /**
     * 缁熻鎬昏瘎璁烘暟
     */
    @Select("select count(*) from article_comments")
    Integer countTotal();

    /**
     * 缁熻寰呭鏍歌瘎璁烘暟
     */
    @Select("select count(*) from article_comments where is_approved = 0")
    Integer countPending();

    /**
     * 鏍规嵁鏍硅瘎璁篒D鍒犻櫎鎵€鏈夊瓙璇勮
     */
    @Delete("delete from article_comments where root_id = #{rootId}")
    void deleteByRootId(Long rootId);

    /**
     * 缁熻鏌愭牴璇勮涓嬬殑瀛愯瘎璁烘暟
     */
    @Select("select count(*) from article_comments where root_id = #{rootId}")
    Integer countByRootId(Long rootId);

    /**
     * 缁熻鏌愭牴璇勮涓嬪凡瀹℃牳鐨勫瓙璇勮鏁?     */
    @Select("select count(*) from article_comments where root_id = #{rootId} and is_approved = 1")
    Integer countApprovedByRootId(Long rootId);
}

