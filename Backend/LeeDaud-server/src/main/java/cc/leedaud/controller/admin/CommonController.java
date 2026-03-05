package cc.leedaud.controller.admin;

import cc.leedaud.result.Result;
import cc.leedaud.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 绠＄悊绔€氱敤鎺ュ彛
 */
@RestController("adminCommonController")
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private CommonService commonService;

    /**
     * 鏂囦欢涓婁紶
     */
    @PostMapping("/upload")
    public Result uploadFile(MultipartFile file){
        log.info("鏂囦欢涓婁紶锛歿}",file);
        String fileUrl = commonService.uploadFile(file);
        return Result.success(fileUrl);
    }
}

