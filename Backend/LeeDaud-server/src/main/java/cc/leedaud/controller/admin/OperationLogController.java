package cc.leedaud.controller.admin;

import cc.leedaud.dto.OperationLogPageQueryDTO;
import cc.leedaud.result.PageResult;
import cc.leedaud.result.Result;
import cc.leedaud.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔搷浣滄棩蹇楁帴鍙? */
@Slf4j
@RestController("adminOperationLogController")
@RequestMapping("/admin/operationLog")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 鍒嗛〉鏌ヨ鎿嶄綔鏃ュ織
     * @param operationLogPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO) {
        log.info("鍒嗛〉鏌ヨ鎿嶄綔鏃ュ織,{}", operationLogPageQueryDTO);
        PageResult pageResult = operationLogService.pageQuery(operationLogPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 鎵归噺鍒犻櫎鎿嶄綔鏃ュ織
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result batchDelete(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎鎿嶄綔鏃ュ織,{}", ids);
        operationLogService.batchDelete(ids);
        return Result.success();
    }
}

