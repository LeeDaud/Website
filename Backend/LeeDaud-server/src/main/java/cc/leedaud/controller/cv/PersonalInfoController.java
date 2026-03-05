package cc.leedaud.controller.cv;

import cc.leedaud.result.Result;
import cc.leedaud.service.PersonalInfoService;
import cc.leedaud.vo.PersonalInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绠€鍘嗙涓汉淇℃伅鎺ュ彛
 */
@RestController("cvPersonalInfoController")
@RequestMapping("/cv/personalInfo")
public class PersonalInfoController {

    @Autowired
    private PersonalInfoService personalInfoService;

    /**
     * 鑾峰彇涓汉淇℃伅
     */
    @GetMapping
    public Result<PersonalInfoVO> getPersonalInfo() {
        PersonalInfoVO personalInfoVO = personalInfoService.getPersonalInfo();
        return Result.success(personalInfoVO);
    }
}

