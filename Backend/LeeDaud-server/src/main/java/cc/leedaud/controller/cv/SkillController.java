package cc.leedaud.controller.cv;

import cc.leedaud.entity.Skills;
import cc.leedaud.result.Result;
import cc.leedaud.service.SkillService;
import cc.leedaud.vo.SkillVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 简历端技能接口
 */
@RestController("cvSkillController")
@RequestMapping("/cv/skill")
public class SkillController {

    @Autowired
    private SkillService skillService;

    /**
     * 获取技能信息
     */
    @GetMapping
    public Result<List<SkillVO>> getSkill() {
        List<SkillVO> skillList = skillService.getSkillVO();
        return Result.success(skillList);
    }
}

