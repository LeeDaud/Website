package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.RssSubscriptionPageQueryDTO;
import cc.leedaud.entity.RssSubscriptions;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.PageResult;
import cc.leedaud.result.Result;
import cc.leedaud.service.RssSubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔疪SS璁㈤槄鎺ュ彛
 */
@Slf4j
@RestController("adminRssSubscriptionController")
@RequestMapping("/admin/rssSubscription")
public class RssSubscriptionController {

    @Autowired
    private RssSubscriptionService rssSubscriptionService;

    /**
     * 鍒嗛〉鏌ヨRSS璁㈤槄鍒楄〃
     * @param rssSubscriptionPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> getSubscriptionList(RssSubscriptionPageQueryDTO rssSubscriptionPageQueryDTO) {
        log.info("鑾峰彇RSS璁㈤槄鍒楄〃,{}", rssSubscriptionPageQueryDTO);
        PageResult pageResult = rssSubscriptionService.pageQuery(rssSubscriptionPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 鑾峰彇鎵€鏈夋縺娲荤殑璁㈤槄
     * @return
     */
    @GetMapping
    public Result<List<RssSubscriptions>> getAllActiveSubscriptions() {
        List<RssSubscriptions> rssSubscriptionsList = rssSubscriptionService.getAllActiveSubscriptions();
        return Result.success(rssSubscriptionsList);
    }

    /**
     * 鏍规嵁ID鏌ヨRSS璁㈤槄
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<RssSubscriptions> getById(@PathVariable Long id) {
        log.info("鏍规嵁ID鏌ヨRSS璁㈤槄,{}", id);
        RssSubscriptions rssSubscriptions = rssSubscriptionService.getById(id);
        return Result.success(rssSubscriptions);
    }

    /**
     * 鏇存柊RSS璁㈤槄
     * @param rssSubscriptions
     * @return
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "rssSubscription", targetId = "#rssSubscriptions.id")
    public Result updateSubscription(@RequestBody RssSubscriptions rssSubscriptions) {
        log.info("鏇存柊RSS璁㈤槄,{}", rssSubscriptions);
        rssSubscriptionService.updateSubscription(rssSubscriptions);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎RSS璁㈤槄
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "rssSubscription", targetId = "#ids")
    public Result deleteSubscription(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎RSS璁㈤槄,{}", ids);
        rssSubscriptionService.batchDelete(ids);
        return Result.success();
    }
}

