package cc.leedaud.service.impl;

import cc.leedaud.dto.OperationLogPageQueryDTO;
import cc.leedaud.entity.OperationLogs;
import cc.leedaud.mapper.OperationLogMapper;
import cc.leedaud.result.PageResult;
import cc.leedaud.service.OperationLogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 淇濆瓨鎿嶄綔鏃ュ織
     * @param operationLogs
     */
    public void save(OperationLogs operationLogs) {
        operationLogMapper.save(operationLogs);
    }

    /**
     * 鍒嗛〉鏌ヨ鎿嶄綔鏃ュ織
     * @param operationLogPageQueryDTO
     * @return
     */
    public PageResult pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO) {
        PageHelper.startPage(operationLogPageQueryDTO.getPage(), operationLogPageQueryDTO.getPageSize());
        Page<OperationLogs> page = operationLogMapper.pageQuery(operationLogPageQueryDTO);
        long total = page.getTotal();
        List<OperationLogs> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 鎵归噺鍒犻櫎鎿嶄綔鏃ュ織
     * @param ids
     */
    public void batchDelete(List<Long> ids) {
        operationLogMapper.batchDelete(ids);
    }
}

