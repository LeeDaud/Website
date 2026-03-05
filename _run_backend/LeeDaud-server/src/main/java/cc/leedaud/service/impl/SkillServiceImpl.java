package cc.leedaud.service.impl;

import cc.leedaud.dto.SkillDTO;
import cc.leedaud.entity.Skills;
import cc.leedaud.mapper.SkillMapper;
import cc.leedaud.service.SkillService;
import cc.leedaud.vo.SkillVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillMapper skillMapper;

    /**
     * 鑾峰彇鎵€鏈夋妧鑳戒俊鎭?     * @return
     */
    @Cacheable(value = "skills", key = "'all'")
    public List<Skills> getAllSkill() {
        List<Skills> skillList = skillMapper.getAllSkill();
        return skillList;
    }

    /**
     * 娣诲姞鎶€鑳戒俊鎭?     * @param skills
     */
    @CacheEvict(value = "skills", allEntries = true)
    public void addSkill(SkillDTO skillDTO) {
        Skills skills = new Skills();
        BeanUtils.copyProperties(skillDTO, skills);
        skillMapper.addSkill(skills);
    }

    /**
     * 鎵归噺鍒犻櫎鎶€鑳戒俊鎭?     * @param ids
     */
    @CacheEvict(value = "skills", allEntries = true)
    public void batchDelete(List<Long> ids) {
        skillMapper.batchDelete(ids);
    }

    /**
     * 淇敼鎶€鑳戒俊鎭?     * @param skills
     */
    @CacheEvict(value = "skills", allEntries = true)
    public void updateSkill(SkillDTO skillDTO) {
        Skills skills = new Skills();
        BeanUtils.copyProperties(skillDTO, skills);
        skillMapper.updateSkill(skills);
    }

    /**
     * 绠€鍘嗙鑾峰彇鎶€鑳戒俊鎭?     * @return
     */
    @Cacheable(value = "skills", key = "'visible'")
    public List<SkillVO> getSkillVO() {
        List<Skills> skills = skillMapper.getVisibleSkill();
        if(skills!=null && !skills.isEmpty()){
            List<SkillVO> skillVOList = skills.stream().map(skill -> SkillVO.builder()
                    .id(skill.getId())
                    .name(skill.getName())
                    .description(skill.getDescription())
                    .icon(skill.getIcon())
                    .sort(skill.getSort())
                    .build()
            ).toList();
            return skillVOList;
        }
        return Collections.emptyList();
    }
}

