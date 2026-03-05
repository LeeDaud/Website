package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.FriendLinkDTO;
import cc.leedaud.entity.FriendLinks;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.Result;
import cc.leedaud.service.FriendLinkService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔弸閾炬帴鍙? */
@RestController("adminFriendLinkController")
@RequestMapping("/admin/friendLink")
@Slf4j
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 鑾峰彇鎵€鏈夊弸鎯呴摼鎺ヤ俊鎭?     */
    @GetMapping
    public Result<List<FriendLinks>> getAllFriendLink() {
        List<FriendLinks> friendLinkList = friendLinkService.getAllFriendLink();
        return Result.success(friendLinkList);
    }

    /**
     * 娣诲姞鍙嬫儏閾炬帴淇℃伅
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "friendLink")
    public Result addFriendLink(@Valid @RequestBody FriendLinkDTO friendLinkDTO) {
        log.info("娣诲姞鍙嬫儏閾炬帴淇℃伅:{}", friendLinkDTO);
        friendLinkService.addFriendLink(friendLinkDTO);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎鍙嬫儏閾炬帴淇℃伅
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "friendLink", targetId = "#ids")
    public Result deleteFriendLink(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎鍙嬫儏閾炬帴淇℃伅:{}", ids);
        friendLinkService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 淇敼鍙嬫儏閾炬帴淇℃伅
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "friendLink", targetId = "#friendLinkDTO.id")
    public Result updateFriendLink(@Valid @RequestBody FriendLinkDTO friendLinkDTO) {
        log.info("淇敼鍙嬫儏閾炬帴淇℃伅:{}", friendLinkDTO);
        friendLinkService.updateFriendLink(friendLinkDTO);
        return Result.success();
    }
}

