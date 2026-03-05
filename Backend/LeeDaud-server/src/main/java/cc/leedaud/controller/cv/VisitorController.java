package cc.leedaud.controller.cv;

import cc.leedaud.annotation.RateLimit;
import cc.leedaud.dto.VisitorRecordDTO;
import cc.leedaud.result.Result;
import cc.leedaud.service.VisitorService;
import cc.leedaud.vo.VisitorRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绠€鍘嗙璁垮鎺ュ彛
 */
@RestController("cvVisitorController")
@RequestMapping("/cv/visitor")
@Slf4j
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    /**
     * 璁板綍璁垮璁块棶淇℃伅
     * @param visitorRecordDTO
     * @param httpRequest
     * @return
     */
    @PostMapping("/record")
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
            timeWindow = 60, message = "璇锋眰杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<VisitorRecordVO> recordVisitorViewInfo(@Valid @RequestBody VisitorRecordDTO visitorRecordDTO,
                                                         HttpServletRequest httpRequest) {
        log.info("璁板綍璁垮璁块棶淇℃伅锛歿}", visitorRecordDTO);
        VisitorRecordVO visitorRecordVO = visitorService.recordVisitorViewInfo(visitorRecordDTO, httpRequest);
        return Result.success(visitorRecordVO);
    }
}

