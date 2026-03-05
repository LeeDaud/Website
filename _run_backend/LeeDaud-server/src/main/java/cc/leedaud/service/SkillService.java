package cc.leedaud.service;

import cc.leedaud.dto.SkillDTO;
import cc.leedaud.entity.Skills;
import cc.leedaud.vo.SkillVO;

import java.util.List;

public interface SkillService {
    /**
     * 鑾峰彇鎵€鏈夋妧鑳戒俊鎭?     * @return
     */
    List<Skills> getAllSkill();

    /**
     * 娣诲姞鎶€鑳?     * @param skillDTO
     */
    void addSkill(SkillDTO skillDTO);

    /**
     * 鎵归噺鍒犻櫎鎶€鑳?     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 淇敼鎶€鑳?     * @param skillDTO
     */
    void updateSkill(SkillDTO skillDTO);

    /**
     * 绠€鍘嗙鑾峰彇鎶€鑳戒俊鎭?     * @return
     */
    List<SkillVO> getSkillVO();
}

