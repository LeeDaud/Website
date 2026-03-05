package cc.leedaud.controller.cv;

import cc.leedaud.entity.Experiences;
import cc.leedaud.result.Result;
import cc.leedaud.service.ExperienceService;
import cc.leedaud.vo.ExperienceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 绠€鍘嗙缁忓巻鎺ュ彛
 */
@RestController("cvExperienceController")
@RequestMapping("/cv/experience")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    /**
     * 鑾峰彇鍏ㄩ儴缁忓巻淇℃伅
     */
    @GetMapping
    public Result<List<ExperienceVO>> getAllExperience() {
        List<ExperienceVO> experienceList = experienceService.getAllExperience();
        return Result.success(experienceList);
    }
}

