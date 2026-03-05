package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.VisitorPageQueryDTO;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.PageResult;
import cc.leedaud.result.Result;
import cc.leedaud.service.VisitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端访客接口
 */
@Slf4j
@RestController("adminVisitorController")
@RequestMapping("/admin/visitor")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    /**
     * 获取访客列表
     * @param visitorPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> getVisitorList(VisitorPageQueryDTO visitorPageQueryDTO) {
        log.info("获取访客列表,{}", visitorPageQueryDTO);
        PageResult pageResult = visitorService.pageQuery(visitorPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量封禁访客
     * @param ids
     * @return
     */
    @PutMapping("/block")
    @OperationLog(value = OperationType.UPDATE, target = "visitor", targetId = "#ids")
    public Result batchBlock(@RequestParam List<Long> ids) {
        log.info("批量封禁访客: {}", ids);
        visitorService.batchBlock(ids);
        return Result.success();
    }

    /**
     * 批量解封访客
     * @param ids
     * @return
     */
    @PutMapping("/unblock")
    @OperationLog(value = OperationType.UPDATE, target = "visitor", targetId = "#ids")
    public Result<String> batchUnblock(@RequestParam List<Long> ids) {
        log.info("批量解封访客: {}", ids);
        visitorService.batchUnblock(ids);
        return Result.success();
    }

}

