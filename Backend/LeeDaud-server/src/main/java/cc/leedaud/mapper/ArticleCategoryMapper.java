package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.entity.ArticleCategories;
import cc.leedaud.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleCategoryMapper {
    /**
     * 鑾峰彇鎵€鏈夋枃绔犲垎绫?     * @return
     */
    @Select("select * from article_categories order by sort asc, id desc")
    List<ArticleCategories> listAll();

    /**
     * 娣诲姞鏂囩珷鍒嗙被
     * @param articleCategories
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(ArticleCategories articleCategories);

    /**
     * 鏇存柊鏂囩珷鍒嗙被
     * @param articleCategories
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(ArticleCategories articleCategories);

    /**
     * 鍒犻櫎鏂囩珷鍒嗙被
     * @param id
     */
    @Delete("delete from article_categories where id = #{id}")
    void deleteById(Long id);

    /**
     * 鎵归噺鍒犻櫎鏂囩珷鍒嗙被
     * @param ids
     */
    void batchDelete(List<Long> ids);

    // ===== 鍗氬绔柟娉?=====

    /**
     * 鑾峰彇鎵€鏈夊垎绫诲強鍏跺凡鍙戝竷鏂囩珷鏁帮紙鍖呮嫭娌℃湁鏂囩珷鐨勫垎绫伙級
     */
    @Select("select ac.*, count(a.id) as article_count from article_categories ac " +
            "left join articles a on ac.id = a.category_id and a.is_published = 1 " +
            "group by ac.id " +
            "order by ac.sort asc, ac.id desc")
    List<ArticleCategories> getVisibleCategories();

    /**
     * 缁熻鏂囩珷鍒嗙被鏁?     */
    @Select("select count(*) from article_categories")
    Integer countTotal();
}

