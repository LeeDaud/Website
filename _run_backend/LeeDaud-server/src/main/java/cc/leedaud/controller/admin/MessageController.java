package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.MessagePageQueryDTO;
import cc.leedaud.dto.MessageReplyDTO;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.PageResult;
import cc.leedaud.result.Result;
import cc.leedaud.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔暀瑷€鎺ュ彛
 */
@Slf4j
@RestController("adminMessageController")
@RequestMapping("/admin/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 鍒嗛〉鏉′欢鏌ヨ鐣欒█
     * @param messagePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(MessagePageQueryDTO messagePageQueryDTO) {
        log.info("鍒嗛〉鏉′欢鏌ヨ鐣欒█: {}", messagePageQueryDTO);
        PageResult pageResult = messageService.pageQuery(messagePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 鎵归噺瀹℃牳閫氳繃鐣欒█
     * @param ids
     * @return
     */
    @PutMapping("/approve")
    @OperationLog(value = OperationType.UPDATE, target = "message", targetId = "#ids")
    public Result<String> batchApprove(@RequestParam List<Long> ids) {
        log.info("鎵归噺瀹℃牳閫氳繃鐣欒█: {}", ids);
        messageService.batchApprove(ids);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎鐣欒█
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "message", targetId = "#ids")
    public Result<String> batchDelete(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎鐣欒█: {}", ids);
        messageService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 绠＄悊鍛樺洖澶嶇暀瑷€
     * @param messageReplyDTO
     * @return
     */
    @PostMapping("/reply")
    @OperationLog(value = OperationType.INSERT, target = "message", targetId = "#messageReplyDTO.parentId")
    public Result<String> adminReply(@Valid @RequestBody MessageReplyDTO messageReplyDTO,
                                     HttpServletRequest request) {
        log.info("绠＄悊鍛樺洖澶嶇暀瑷€: {}", messageReplyDTO);
        messageService.adminReply(messageReplyDTO, request);
        return Result.success();
    }
}

