package cc.leedaud.controller.admin;

import cc.leedaud.result.Result;
import cc.leedaud.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("adminCommonController")
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private CommonService commonService;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("上传文件: {}", file == null ? null : file.getOriginalFilename());
        String fileUrl = commonService.uploadFile(file);
        return Result.success(fileUrl);
    }
}
