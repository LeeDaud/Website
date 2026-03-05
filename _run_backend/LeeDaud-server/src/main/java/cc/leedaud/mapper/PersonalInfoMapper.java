package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.entity.PersonalInfo;
import cc.leedaud.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public interface PersonalInfoMapper {

    /**
     * 鑾峰彇涓汉淇℃伅
     */
    @Select("select * from personal_info where id = 1")
    PersonalInfo getPersonalInfo();

    /**
     * 鏇存柊涓汉淇℃伅
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateById(PersonalInfo personalInfo);
}

