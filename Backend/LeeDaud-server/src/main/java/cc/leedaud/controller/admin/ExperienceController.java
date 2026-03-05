package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.ExperienceDTO;
import cc.leedaud.entity.Experiences;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.Result;
import cc.leedaud.service.ExperienceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  绠＄悊绔粡鍘嗘帴鍙? */
@RestController("adminExperienceController")
@RequestMapping("/admin/experience")
@Slf4j
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    /**
     * 鏍规嵁鍒嗙被鑾峰彇缁忓巻淇℃伅
     */
    @GetMapping
    public Result<List<Experiences>> getExperience(@RequestParam(required = false) Integer type) {
        log.info("鏍规嵁鍒嗙被鑾峰彇缁忓巻淇℃伅,{}", type);
        List<Experiences> experienceList = experienceService.getExperience(type);
        return Result.success(experienceList);
    }

    /**
     * 娣诲姞缁忓巻淇℃伅
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "experience")
    public Result addExperience(@Valid @RequestBody ExperienceDTO experienceDTO) {
        log.info("娣诲姞缁忓巻淇℃伅,{}", experienceDTO);
        experienceService.addExperience(experienceDTO);
        return Result.success();
    }

    /**
     * 淇敼缁忓巻淇℃伅
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "experience", targetId = "#experienceDTO.id")
    public Result updateExperience(@Valid @RequestBody ExperienceDTO experienceDTO) {
        log.info("淇敼缁忓巻淇℃伅,{}", experienceDTO);
        experienceService.updateExperience(experienceDTO);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎缁忓巻淇℃伅
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "experience", targetId = "#ids")
    public Result deleteExperience(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎缁忓巻淇℃伅,{}", ids);
        experienceService.batchDelete(ids);
        return Result.success();
    }

}

