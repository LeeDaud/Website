package cc.leedaud.service;

import cc.leedaud.dto.MusicDTO;
import cc.leedaud.dto.MusicPageQueryDTO;
import cc.leedaud.entity.Music;
import cc.leedaud.result.PageResult;
import cc.leedaud.vo.MusicVO;

import java.util.List;

public interface MusicService {
    /**
     * 娣诲姞闊充箰
     * @param musicDTO
     */
    void addMusic(MusicDTO musicDTO);

    /**
     * 鍒嗛〉鏌ヨ闊充箰鍒楄〃
     * @param musicPageQueryDTO
     * @return
     */
    PageResult pageQuery(MusicPageQueryDTO musicPageQueryDTO);

    /**
     * 鏇存柊闊充箰
     * @param musicDTO
     */
    void updateMusic(MusicDTO musicDTO);

    /**
     * 鎵归噺鍒犻櫎闊充箰
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 鏍规嵁ID鏌ヨ闊充箰
     * @param id
     * @return
     */
    Music getById(Long id);

    /**
     * 鑾峰彇鎵€鏈夊彲瑙佺殑闊充箰
     * @return
     */
    List<MusicVO> getAllVisibleMusic();
}

