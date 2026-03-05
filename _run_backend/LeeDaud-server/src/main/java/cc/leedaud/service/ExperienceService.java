package cc.leedaud.service;

import cc.leedaud.dto.ExperienceDTO;
import cc.leedaud.entity.Experiences;
import cc.leedaud.vo.ExperienceVO;

import java.util.List;

public interface ExperienceService {
    /**
     * 鏍规嵁绫诲瀷鑾峰彇缁忓巻淇℃伅
     * @param type
     * @return
     */
    List<Experiences> getExperience(Integer type);

    /**
     * 娣诲姞缁忓巻淇℃伅
     * @param experienceDTO
     */
    void addExperience(ExperienceDTO experienceDTO);

    /**
     * 淇敼缁忓巻淇℃伅
     * @param experienceDTO
     */
    void updateExperience(ExperienceDTO experienceDTO);

    /**
     * 鎵归噺鍒犻櫎缁忓巻
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * cv绔幏鍙栧叏閮ㄧ粡鍘嗕俊鎭?     * @return
     */
    List<ExperienceVO> getAllExperience();
}

