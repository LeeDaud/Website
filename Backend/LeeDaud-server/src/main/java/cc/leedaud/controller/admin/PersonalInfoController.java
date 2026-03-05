package cc.leedaud.controller.admin;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.dto.PersonalInfoDTO;
import cc.leedaud.entity.PersonalInfo;
import cc.leedaud.enumeration.OperationType;
import cc.leedaud.result.Result;
import cc.leedaud.service.PersonalInfoService;
import cc.leedaud.vo.PersonalInfoVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 绠＄悊绔釜浜轰俊鎭帴鍙? */
@RestController("adminPersonalInfoController")
@RequestMapping("/admin/personalInfo")
@Slf4j
public class PersonalInfoController {

    @Autowired
    private PersonalInfoService personalInfoService;

    /**
     * 鑾峰彇涓汉淇℃伅
     */
    @GetMapping
    public Result<PersonalInfo> getPersonalInfo() {
        PersonalInfo personalInfo = personalInfoService.getAllPersonalInfo();
        return Result.success(personalInfo);
    }

    /**
     * 鏇存柊涓汉淇℃伅
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "personalInfo", targetId = "#personalInfoDTO.id")
    public Result updatePersonalInfo(@Valid @RequestBody PersonalInfoDTO personalInfoDTO) {
        log.info("鏇存柊涓汉淇℃伅: {}", personalInfoDTO);
        personalInfoService.updatePersonalInfo(personalInfoDTO);
        return Result.success();
    }
}

