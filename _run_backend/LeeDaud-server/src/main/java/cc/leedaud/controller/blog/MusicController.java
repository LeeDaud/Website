package cc.leedaud.controller.blog;

import cc.leedaud.result.Result;
import cc.leedaud.service.MusicService;
import cc.leedaud.vo.MusicVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 鍗氬绔煶涔愭帴鍙? */
@Slf4j
@RestController("blogMusicController")
@RequestMapping("/blog/music")
public class MusicController {

    @Autowired
    private MusicService musicService;

    /**
     * 鑾峰彇鎵€鏈夊彲瑙佺殑闊充箰
     * @return
     */
    @GetMapping
    public Result<List<MusicVO>> getAllVisibleMusic() {
        List<MusicVO> musicVOList = musicService.getAllVisibleMusic();
        return Result.success(musicVOList);
    }
}

