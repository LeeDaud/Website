package cc.leedaud.service;

import cc.leedaud.dto.OperationLogPageQueryDTO;
import cc.leedaud.entity.OperationLogs;
import cc.leedaud.result.PageResult;

import java.util.List;

public interface OperationLogService {
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
    PageResult pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO);

    /**
     * 鎵归噺鍒犻櫎鎿嶄綔鏃ュ織
     * @param ids
     */
    void batchDelete(List<Long> ids);
}

