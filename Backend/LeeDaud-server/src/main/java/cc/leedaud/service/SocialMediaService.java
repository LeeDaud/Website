package cc.leedaud.service;

import cc.leedaud.dto.SocialMediaDTO;
import cc.leedaud.entity.SocialMedia;
import cc.leedaud.vo.SocialMediaVO;

import java.util.List;

public interface SocialMediaService {
    /**
     * 鑾峰彇鍙绀句氦濯掍綋淇℃伅
     * @return
     */
    List<SocialMediaVO> getVisibleSocialMedia();

    /**
     * 鑾峰彇鎵€鏈夌ぞ浜ゅ獟浣撲俊鎭?     * @return
     */
    List<SocialMedia> getAllSocialMedia();

    /**
     * 娣诲姞绀句氦濯掍綋淇℃伅
     * @param socialMediaDTO
     */
    void addSocialMedia(SocialMediaDTO socialMediaDTO);

    /**
     * 鎵归噺鍒犻櫎绀句氦濯掍綋
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 淇敼绀句氦濯掍綋淇℃伅
     * @param socialMediaDTO
     */
    void updateSocialMedia(SocialMediaDTO socialMediaDTO);
}

