package cc.leedaud.controller.blog;

import cc.leedaud.result.Result;
import cc.leedaud.service.FriendLinkService;
import cc.leedaud.vo.FriendLinkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 鍗氬绔弸閾炬帴鍙? */
@RestController("blogFriendLinkController")
@RequestMapping("/blog/friendLink")
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 鑾峰彇鍙鍙嬫儏閾炬帴
     */
    @GetMapping
    public Result<List<FriendLinkVO>> getVisibleFriendLink() {
        List<FriendLinkVO> friendLinkVOList = friendLinkService.getVisibleFriendLink();
        return Result.success(friendLinkVOList);
    }
}

