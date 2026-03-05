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
 * 绠＄悊绔瀹㈡帴鍙? */
@Slf4j
@RestController("adminVisitorController")
@RequestMapping("/admin/visitor")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    /**
     * 鑾峰彇璁垮鍒楄〃
     * @param visitorPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> getVisitorList(VisitorPageQueryDTO visitorPageQueryDTO) {
        log.info("鑾峰彇璁垮鍒楄〃,{}", visitorPageQueryDTO);
        PageResult pageResult = visitorService.pageQuery(visitorPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 鎵归噺灏佺璁垮
     * @param ids
     * @return
     */
    @PutMapping("/block")
    @OperationLog(value = OperationType.UPDATE, target = "visitor", targetId = "#ids")
    public Result batchBlock(@RequestParam List<Long> ids) {
        log.info("鎵归噺灏佺璁垮: {}", ids);
        visitorService.batchBlock(ids);
        return Result.success();
    }

    /**
     * 鎵归噺瑙ｅ皝璁垮
     * @param ids
     * @return
     */
    @PutMapping("/unblock")
    @OperationLog(value = OperationType.UPDATE, target = "visitor", targetId = "#ids")
    public Result<String> batchUnblock(@RequestParam List<Long> ids) {
        log.info("鎵归噺瑙ｅ皝璁垮: {}", ids);
        visitorService.batchUnblock(ids);
        return Result.success();
    }

}

