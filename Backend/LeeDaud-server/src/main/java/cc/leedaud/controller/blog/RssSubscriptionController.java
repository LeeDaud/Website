package cc.leedaud.controller.blog;

import cc.leedaud.annotation.RateLimit;
import cc.leedaud.dto.RssSubscriptionDTO;
import cc.leedaud.result.Result;
import cc.leedaud.service.RssSubscriptionService;
import cc.leedaud.vo.RssSubscriptionStatusVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 鍗氬绔疪SS璁㈤槄鎺ュ彛
 */
@Slf4j
@RestController("blogRssSubscriptionController")
@RequestMapping("/blog/rssSubscription")
public class RssSubscriptionController {

    @Autowired
    private RssSubscriptionService rssSubscriptionService;

    /**
     * 娣诲姞RSS璁㈤槄
     * @param rssSubscriptionDTO
     * @return
     */
    @PostMapping
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
            timeWindow = 60, message = "鎿嶄綔杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result addSubscription(@Valid @RequestBody RssSubscriptionDTO rssSubscriptionDTO) {
        log.info("娣诲姞RSS璁㈤槄,{}", rssSubscriptionDTO);
        rssSubscriptionService.addSubscription(rssSubscriptionDTO);
        return Result.success();
    }

    /**
     * 鍙栨秷RSS璁㈤槄锛堣瀹㈢锛?     * @param email
     * @return
     */
    @PutMapping("/unsubscribe")
    public Result unsubscribe(@RequestParam String email) {
        log.info("鍙栨秷RSS璁㈤槄,{}", email);
        rssSubscriptionService.unsubscribeByEmail(email);
        return Result.success();
    }

    /**
     * 妫€鏌ヨ瀹㈣闃呯姸鎬侊紙杩斿洖璁㈤槄璇︽儏锛?     */
    @GetMapping("/check")
    public Result<RssSubscriptionStatusVO> checkSubscription(@RequestParam Long visitorId) {
        log.info("妫€鏌ヨ闃呯姸鎬? visitorId={}", visitorId);
        RssSubscriptionStatusVO status = rssSubscriptionService.getSubscriptionStatus(visitorId);
        return Result.success(status);
    }
}

