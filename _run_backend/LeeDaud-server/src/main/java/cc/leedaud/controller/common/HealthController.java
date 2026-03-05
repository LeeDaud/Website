package cc.leedaud.controller.common;

import cc.leedaud.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 鍋ュ悍妫€鏌ユ帴鍙? */
@Slf4j
@RestController
@RequestMapping("/health")
public class HealthController {
    /**
     * 鍋ュ悍妫€鏌?     */
    @GetMapping
    public Result<String> health() {
        return Result.success("Server is running");
    }
}

