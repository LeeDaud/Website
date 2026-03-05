package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.dto.AdminChangePasswordDTO;
import cc.leedaud.entity.Admin;
import cc.leedaud.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminMapper {
    /**
     * 鏍规嵁鐢ㄦ埛鍚嶆煡璇㈢鐞嗗憳
     * @param username 鐢ㄦ埛鍚?     * @return 绠＄悊鍛?     */
    @Select("select * from admin where username = #{username}")
    Admin getByUsername(String username);

    /**
     * 鏍规嵁id鏌ヨ绠＄悊鍛?     * @param adminId 绠＄悊鍛榠d
     * @return 绠＄悊鍛?     */
    @Select("select * from admin where id = #{adminId}")
    Admin getById(Long adminId);

    /**
     * 淇敼绠＄悊鍛樹俊鎭?     * @param admin
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Admin admin);
}

