package cc.leedaud.controller.home;

import cc.leedaud.result.Result;
import cc.leedaud.service.SocialMediaService;
import cc.leedaud.vo.SocialMediaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  棣栭〉绔ぞ浜ゅ獟浣撴帴鍙? */
@RestController("homeSocialMediaController")
@RequestMapping("/home/socialMedia")
public class SocialMediaController {

    @Autowired
    private SocialMediaService socialMediaService;

    /**
     * 鑾峰彇鍙绀句氦濯掍綋淇℃伅
     */
    @GetMapping
    public Result<List<SocialMediaVO>> getSocialVisibleMedia() {
        List<SocialMediaVO> socialMediaVOList = socialMediaService.getVisibleSocialMedia();
        return Result.success(socialMediaVOList);
    }
}

