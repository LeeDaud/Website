package cc.leedaud.controller.blog;

import cc.leedaud.annotation.RateLimit;
import cc.leedaud.dto.MessageDTO;
import cc.leedaud.dto.MessageEditDTO;
import cc.leedaud.result.Result;
import cc.leedaud.service.MessageService;
import cc.leedaud.vo.MessageVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 鍗氬绔暀瑷€鎺ュ彛
 */
@RestController("blogMessageController")
@RequestMapping("/blog/message")
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 璁垮鎻愪氦鐣欒█
     * @param messageDTO
     * @param request
     * @return
     */
    @PostMapping
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "鐣欒█杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<String> submitMessage(@Valid @RequestBody MessageDTO messageDTO, HttpServletRequest request) {
        log.info("璁垮鎻愪氦鐣欒█: {}", messageDTO);
        messageService.submitMessage(messageDTO, request);
        return Result.success();
    }

    /**
     * 鑾峰彇鐣欒█鍒楄〃锛堟爲褰㈢粨鏋勶紝鍚綋鍓嶈瀹㈢殑鏈鏍哥暀瑷€锛?     */
    @GetMapping
    public Result<List<MessageVO>> getMessageTree(@RequestParam(required = false) Long visitorId) {
        log.info("鍗氬绔幏鍙栫暀瑷€鏍? visitorId={}", visitorId);
        List<MessageVO> messageTree = messageService.getMessageTree(visitorId);
        return Result.success(messageTree);
    }

    /**
     * 璁垮缂栬緫鐣欒█
     */
    @PutMapping("/edit")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "鎿嶄綔杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<String> editMessage(@Valid @RequestBody MessageEditDTO editDTO) {
        log.info("璁垮缂栬緫鐣欒█: {}", editDTO);
        messageService.editMessage(editDTO);
        return Result.success();
    }

    /**
     * 璁垮鍒犻櫎鐣欒█
     */
    @DeleteMapping("/{id}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "鎿嶄綔杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<String> deleteMessage(@PathVariable Long id, @RequestParam Long visitorId) {
        log.info("璁垮鍒犻櫎鐣欒█: id={}, visitorId={}", id, visitorId);
        messageService.visitorDeleteMessage(id, visitorId);
        return Result.success();
    }
}

