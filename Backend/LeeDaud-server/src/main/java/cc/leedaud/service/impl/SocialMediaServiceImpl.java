package cc.leedaud.service.impl;

import cc.leedaud.dto.SocialMediaDTO;
import cc.leedaud.entity.SocialMedia;
import cc.leedaud.mapper.SocialMediaMapper;
import cc.leedaud.service.SocialMediaService;
import cc.leedaud.vo.SocialMediaVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SocialMediaServiceImpl implements SocialMediaService {

    @Autowired
    private SocialMediaMapper socialMediaMapper;

    /**
     * 鑾峰彇鍙绀句氦濯掍綋淇℃伅
     * @return
     */
    @Cacheable(value = "socialMedia", key = "'visible'")
    public List<SocialMediaVO> getVisibleSocialMedia() {
        // 鑾峰彇鏁版嵁搴撴暟鎹?        List<SocialMedia> socialMediaList = socialMediaMapper.getVisibleSocialMedia();
        // 杞崲涓篤O
        if (socialMediaList != null && socialMediaList.size() > 0) {
            return socialMediaList.stream().map(socialMedia -> SocialMediaVO.builder()
                    .id(socialMedia.getId())
                    .name(socialMedia.getName())
                    .icon(socialMedia.getIcon())
                    .link(socialMedia.getLink())
                    .sort(socialMedia.getSort())
                    .build()).toList();
        }
        return Collections.emptyList();
    }

    /**
     * 鑾峰彇鎵€鏈夌ぞ浜ゅ獟浣撲俊鎭?     * @return
     */
    @Cacheable(value = "socialMedia", key = "'all'")
    public List<SocialMedia> getAllSocialMedia() {
        // 鑾峰彇鏁版嵁搴撴暟鎹?        List<SocialMedia> socialMediaList = socialMediaMapper.getAllSocialMedia();
        if (socialMediaList != null && socialMediaList.size() > 0) {
            return socialMediaList;
        }
        return Collections.emptyList();
    }

    /**
     * 娣诲姞绀句氦濯掍綋
     * @param socialMediaDTO
     */
    @CacheEvict(value = "socialMedia", allEntries = true)
    public void addSocialMedia(SocialMediaDTO socialMediaDTO) {
        SocialMedia socialMedia = new SocialMedia();
        BeanUtils.copyProperties(socialMediaDTO, socialMedia);
        // 娣诲姞鍒版暟鎹簱
        socialMediaMapper.insert(socialMedia);
    }

    /**
     * 鎵归噺鍒犻櫎绀句氦濯掍綋
     * @param ids
     */
    @CacheEvict(value = "socialMedia", allEntries = true)
    public void batchDelete(List<Long> ids) {
        socialMediaMapper.batchDelete(ids);
    }

    /**
     * 淇敼绀句氦濯掍綋
     * @param socialMedia
     */
    @CacheEvict(value = "socialMedia", allEntries = true)
    public void updateSocialMedia(SocialMediaDTO socialMediaDTO) {
        SocialMedia socialMedia = new SocialMedia();
        BeanUtils.copyProperties(socialMediaDTO, socialMedia);
        // 鏇存柊鍒版暟鎹簱
        socialMediaMapper.updateById(socialMedia);
    }
}

