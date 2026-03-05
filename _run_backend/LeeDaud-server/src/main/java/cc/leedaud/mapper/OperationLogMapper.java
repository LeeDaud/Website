package cc.leedaud.mapper;

import cc.leedaud.dto.OperationLogPageQueryDTO;
import cc.leedaud.entity.OperationLogs;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperationLogMapper {
    /**
     * 淇濆瓨鎿嶄綔鏃ュ織
     * @param operationLogs
     */
    void save(OperationLogs operationLogs);

    /**
     * 鍒嗛〉鏌ヨ鎿嶄綔鏃ュ織
     * @param operationLogPageQueryDTO
     * @return
     */
    Page<OperationLogs> pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO);

    /**
     * 鎵归噺鍒犻櫎鎿嶄綔鏃ュ織
     * @param ids
     */
    void batchDelete(List<Long> ids);
}

