package cc.leedaud.service.impl;

import cc.leedaud.dto.ExperienceDTO;
import cc.leedaud.entity.Experiences;
import cc.leedaud.mapper.ExperienceMapper;
import cc.leedaud.service.ExperienceService;
import cc.leedaud.vo.ExperienceVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    @Autowired
    private ExperienceMapper experienceMapper;

    /**
     * 鑾峰彇缁忓巻淇℃伅
     * @param type
     * @return
     */
    @Cacheable(value = "experiences", key = "'type_' + #type")
    public List<Experiences> getExperience(Integer type) {
        List<Experiences> experienceList = experienceMapper.getExperienceByType(type);
        return experienceList;
    }

    /**
     * 娣诲姞缁忓巻淇℃伅
     * @param experiences
     */
    @CacheEvict(value = "experiences", allEntries = true)
    public void addExperience(ExperienceDTO experienceDTO) {
        Experiences experiences = new Experiences();
        BeanUtils.copyProperties(experienceDTO, experiences);
        experienceMapper.insert(experiences);
    }

    /**
     * 淇敼缁忓巻淇℃伅
     * @param experiences
     */
    @CacheEvict(value = "experiences", allEntries = true)
    public void updateExperience(ExperienceDTO experienceDTO) {
        Experiences experiences = new Experiences();
        BeanUtils.copyProperties(experienceDTO, experiences);
       experienceMapper.update(experiences);
    }

    /**
     * 鎵归噺鍒犻櫎缁忓巻淇℃伅
     * @param ids
     */
    @CacheEvict(value = "experiences", allEntries = true)
    public void batchDelete(List<Long> ids) {
        experienceMapper.batchDelete(ids);
    }

    /**
     * cv绔幏鍙栧叏閮ㄧ粡鍘嗕俊鎭?     * @return
     */
    @Cacheable(value = "experiences", key = "'all'")
    public List<ExperienceVO> getAllExperience() {
        List<Experiences> experienceList = experienceMapper.getAllExperience();
        if(experienceList != null && !experienceList.isEmpty()) {
            // 杞崲涓篤O
            List<ExperienceVO> experienceVOList = experienceList.stream().map(experiences -> ExperienceVO.builder()
                    .id(experiences.getId())
                    .type(experiences.getType())
                    .title(experiences.getTitle())
                    .subtitle(experiences.getSubtitle())
                    .logoUrl(experiences.getLogoUrl())
                    .projectLink(experiences.getProjectLink())
                    .startDate(experiences.getStartDate())
                    .endDate(experiences.getEndDate())
                    .content(experiences.getContent())
                    .build()
            ).toList();
            return experienceVOList;
        }
        return Collections.emptyList();
    }
}

