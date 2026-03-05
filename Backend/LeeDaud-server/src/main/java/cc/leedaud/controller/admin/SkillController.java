package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.SkillDTO;
import cc.leedaud.entity.Skills;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.Result;
import cc.leedaud.service.SkillService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绠＄悊绔妧鑳芥帴鍙? */
@RestController("adminSkillController")
@RequestMapping("/admin/skill")
@Slf4j
public class SkillController {

    @Autowired
    private SkillService skillService;

    /**
     * 鑾峰彇鎵€鏈夋妧鑳戒俊鎭?     */
    @GetMapping
    public Result<List<Skills>> getAllSkill() {
        return Result.success(skillService.getAllSkill());
    }

    /**
     * 娣诲姞鎶€鑳戒俊鎭?     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "skill")
    public Result addSkill(@Valid @RequestBody SkillDTO skillDTO) {
        log.info("娣诲姞鎶€鑳戒俊鎭?{}", skillDTO);
        skillService.addSkill(skillDTO);
        return Result.success();
    }

    /**
     * 鎵归噺鍒犻櫎鎶€鑳戒俊鎭?     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "skill", targetId = "#ids")
    public Result<String> deleteSkill(@RequestParam List<Long> ids) {
        log.info("鎵归噺鍒犻櫎鎶€鑳戒俊鎭?{}", ids);
        skillService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 淇敼鎶€鑳戒俊鎭?     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "skill", targetId = "#skillDTO.id")
    public Result updateSkill(@Valid @RequestBody SkillDTO skillDTO) {
        log.info("淇敼鎶€鑳戒俊鎭?{}", skillDTO);
        skillService.updateSkill(skillDTO);
        return Result.success();
    }

}

