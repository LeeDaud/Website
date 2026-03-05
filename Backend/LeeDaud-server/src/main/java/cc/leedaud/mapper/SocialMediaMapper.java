package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.entity.SocialMedia;
import cc.leedaud.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SocialMediaMapper {
    /**
     * 鑾峰彇鍙绀句氦濯掍綋淇℃伅
     */
    @Select("select * from social_media where is_visible = 1")
    List<SocialMedia> getVisibleSocialMedia();

    /**
     * 鑾峰彇鎵€鏈夌ぞ浜ゅ獟浣撲俊鎭?     */
    @Select("select * from social_media")
    List<SocialMedia> getAllSocialMedia();

    /**
     * 娣诲姞绀句氦濯掍綋
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into social_media (name, icon, link, sort, is_visible, create_time, update_time) values (#{name}, #{icon}, #{link}, #{sort}, #{isVisible}, #{createTime}, #{updateTime})")
    void insert(SocialMedia socialMedia);

    /**
     * 鍒犻櫎绀句氦濯掍綋
     */
    @Delete("delete from social_media where id = #{id}")
    void deleteById(Long id);

    /**
     * 鎵归噺鍒犻櫎绀句氦濯掍綋
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 淇敼绀句氦濯掍綋淇℃伅
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateById(SocialMedia socialMedia);
}

