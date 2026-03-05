package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.MusicDTO;
import cc.leedaud.dto.MusicPageQueryDTO;
import cc.leedaud.entity.Music;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.PageResult;
import cc.leedaud.result.Result;
import cc.leedaud.service.MusicService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔煶涔愭帴鍙? */
@Slf4j
@RestController("adminMusicController")
@RequestMapping("/admin/music")
public class MusicController {

    @Autowired
    private MusicService musicService;

    /**
     * 鍒嗛〉鏌ヨ闊充箰鍒楄〃
     * @param musicPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> getMusicList(MusicPageQueryDTO musicPageQueryDTO) {
        log.info("鑾峰彇闊充箰鍒楄〃,{}", musicPageQueryDTO);
        PageResult pageResult = musicService.pageQuery(musicPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 鏍规嵁ID鏌ヨ闊充箰
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Music> getById(@PathVariable Long id) {
        log.info("鏍规嵁ID鏌ヨ闊充箰,{}", id);
        Music music = musicService.getById(id);
        return Result.success(music);
    }

    /**
     * 娣诲姞闊充箰
     * @param music
     * @return
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "music")
    public Result addMusic(@Valid @RequestBody MusicDTO musicDTO) {
        log.info("娣诲姞闊充箰,{}", musicDTO);
        musicService.addMusic(musicDTO);
        return Result.success();
    }

    /**
     * 鏇存柊闊充箰
     * @param music
     * @return
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "music", targetId = "#musicDTO.id")
    public Result updateMusic(@Valid @RequestBody MusicDTO musicDTO) {
        log.info("鏇存柊闊充箰,{}", musicDTO);
        musicService.updateMusic(musicDTO);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎闊充箰
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "music", targetId = "#ids")
    public Result deleteMusic(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎闊充箰,{}", ids);
        musicService.batchDelete(ids);
        return Result.success();
    }
}

