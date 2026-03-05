package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.entity.Skills;
import cc.leedaud.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SkillMapper {
    /**
     * 鑾峰彇鎵€鏈夋妧鑳戒俊鎭?     */
    @Select("select * from skills order by sort")
    List<Skills> getAllSkill();

    /**
     * 娣诲姞鎶€鑳戒俊鎭?     */
    @AutoFill(value = OperationType.INSERT)
    void addSkill(Skills skills);

    /**
     * 鍒犻櫎鎶€鑳戒俊鎭?     */
    @Delete("delete from skills where id = #{id}")
    void deleteById(Long id);

    /**
     * 鎵归噺鍒犻櫎鎶€鑳?     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 淇敼鎶€鑳戒俊鎭?     */
    @AutoFill(value = OperationType.UPDATE)
    void updateSkill(Skills skills);

    /**
     * 鑾峰彇鍙鎶€鑳戒俊鎭?     */
    @Select("select * from skills where is_visible = 1 order by sort")
    List<Skills> getVisibleSkill();
}

