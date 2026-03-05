package cc.leedaud.mapper;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.entity.SystemConfig;
import cc.leedaud.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SystemConfigMapper {
    /**
     * 鑾峰彇鎵€鏈夌郴缁熼厤缃?     * @return
     */
    @Select("select * from system_config order by id desc")
    List<SystemConfig> listAll();

    /**
     * 鏍规嵁閰嶇疆閿幏鍙栭厤缃?     * @param configKey
     * @return
     */
    @Select("select * from system_config where config_key = #{configKey}")
    SystemConfig getByKey(String configKey);

    /**
     * 鏍规嵁ID鑾峰彇閰嶇疆
     * @param id
     * @return
     */
    @Select("select * from system_config where id = #{id}")
    SystemConfig getById(Long id);

    /**
     * 娣诲姞绯荤粺閰嶇疆
     * @param systemConfig
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(SystemConfig systemConfig);

    /**
     * 鏇存柊绯荤粺閰嶇疆
     * @param systemConfig
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(SystemConfig systemConfig);

    /**
     * 鍒犻櫎绯荤粺閰嶇疆
     * @param id
     */
    @Delete("delete from system_config where id = #{id}")
    void deleteById(Long id);

    /**
     * 鎵归噺鍒犻櫎绯荤粺閰嶇疆
     * @param ids
     */
    void batchDelete(List<Long> ids);
}

