package cc.leedaud.service.impl;

import cc.leedaud.dto.MusicDTO;
import cc.leedaud.dto.MusicPageQueryDTO;
import cc.leedaud.entity.Music;
import cc.leedaud.mapper.MusicMapper;
import cc.leedaud.result.PageResult;
import cc.leedaud.service.MusicService;
import cc.leedaud.vo.MusicVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MusicServiceImpl implements MusicService {

    @Autowired
    private MusicMapper musicMapper;

    /**
     * 娣诲姞闊充箰
     * @param music
     */
    @CacheEvict(value = "musicList", allEntries = true)
    public void addMusic(MusicDTO musicDTO) {
        Music music = new Music();
        BeanUtils.copyProperties(musicDTO, music);
        musicMapper.insert(music);
    }

    /**
     * 鍒嗛〉鏌ヨ闊充箰鍒楄〃
     * @param musicPageQueryDTO
     * @return
     */
    public PageResult pageQuery(MusicPageQueryDTO musicPageQueryDTO) {
        PageHelper.startPage(musicPageQueryDTO.getPage(), musicPageQueryDTO.getPageSize());
        Page<Music> page = musicMapper.pageQuery(musicPageQueryDTO);
        long total = page.getTotal();
        List<Music> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 鏇存柊闊充箰
     * @param music
     */
    @CacheEvict(value = "musicList", allEntries = true)
    public void updateMusic(MusicDTO musicDTO) {
        Music music = new Music();
        BeanUtils.copyProperties(musicDTO, music);
        musicMapper.update(music);
    }

    /**
     * 鎵归噺鍒犻櫎闊充箰
     * @param ids
     */
    @CacheEvict(value = "musicList", allEntries = true)
    public void batchDelete(List<Long> ids) {
        musicMapper.batchDelete(ids);
    }

    /**
     * 鏍规嵁ID鏌ヨ闊充箰
     * @param id
     * @return
     */
    public Music getById(Long id) {
        return musicMapper.getById(id);
    }

    /**
     * 鑾峰彇鎵€鏈夊彲瑙佺殑闊充箰
     * @return
     */
    @Cacheable(value = "musicList", key = "'visible'")
    public List<MusicVO> getAllVisibleMusic() {
        List<Music> musicList = musicMapper.getAllVisibleMusic();
        if(musicList != null && !musicList.isEmpty()) {
            // 杞崲涓篤O
            List<MusicVO> musicVOList = musicList.stream().map(music -> MusicVO.builder()
                    .id(music.getId())
                    .title(music.getTitle())
                    .artist(music.getArtist())
                    .duration(music.getDuration())
                    .coverImage(music.getCoverImage())
                    .musicUrl(music.getMusicUrl())
                    .lyricUrl(music.getLyricUrl())
                    .hasLyric(music.getHasLyric())
                    .lyricType(music.getLyricType())
                    .build()
            ).toList();
            return musicVOList;
        }
        return Collections.emptyList();
    }
}

