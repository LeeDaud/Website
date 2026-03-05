package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.entity.Experiences;
import cc.leedaud.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExperienceMapper {
    /**
     * 鏍规嵁绫诲瀷鑾峰彇缁忓巻淇℃伅
     */
    List<Experiences> getExperienceByType(Integer type);

    /**
     * 娣诲姞缁忓巻淇℃伅
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Experiences experiences);

    /**
     * 淇敼缁忓巻淇℃伅
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Experiences experiences);

    /**
     * 鍒犻櫎缁忓巻淇℃伅
     */
    @Delete("delete from experiences where id = #{id}")
    void deleteById(Long id);

    /**
     * 鎵归噺鍒犻櫎缁忓巻
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 鑾峰彇鍏ㄩ儴缁忓巻淇℃伅
     */
    @Select("select * from experiences where is_visible = 1 order by start_date desc")
    List<Experiences> getAllExperience();
}

