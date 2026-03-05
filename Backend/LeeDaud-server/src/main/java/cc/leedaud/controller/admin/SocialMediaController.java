package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.SocialMediaDTO;
import cc.leedaud.entity.SocialMedia;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.Result;
import cc.leedaud.service.SocialMediaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  绠＄悊绔ぞ浜ゅ獟浣撴帴鍙? */
@RestController("adminSocialMediaController")
@RequestMapping("/admin/socialMedia")
@Slf4j
public class SocialMediaController {

    @Autowired
    private SocialMediaService socialMediaService;

    /**
     * 鑾峰彇鎵€鏈夌ぞ浜ゅ獟浣撲俊鎭?     */
    @GetMapping
    public Result<List<SocialMedia>> getAllSocialMedia() {
        List<SocialMedia> socialMediaList = socialMediaService.getAllSocialMedia();
        return Result.success(socialMediaList);
    }

    /**
     * 娣诲姞绀句氦濯掍綋淇℃伅
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "socialMedia")
    public Result addSocialMedia(@Valid @RequestBody SocialMediaDTO socialMediaDTO) {
        log.info("娣诲姞绀句氦濯掍綋淇℃伅: {}", socialMediaDTO);
        socialMediaService.addSocialMedia(socialMediaDTO);
        return Result.success();
    }
    /**
     * 鎵归噺鍒犻櫎绀句氦濯掍綋淇℃伅
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "socialMedia", targetId = "#ids")
    public Result deleteSocialMedia(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎绀句氦濯掍綋淇℃伅: {}", ids);
        socialMediaService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 淇敼绀句氦濯掍綋淇℃伅
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "socialMedia", targetId = "#socialMediaDTO.id")
    public Result updateSocialMedia(@Valid @RequestBody SocialMediaDTO socialMediaDTO) {
        log.info("淇敼绀句氦濯掍綋淇℃伅: {}", socialMediaDTO);
        socialMediaService.updateSocialMedia(socialMediaDTO);
        return Result.success();
    }
}

