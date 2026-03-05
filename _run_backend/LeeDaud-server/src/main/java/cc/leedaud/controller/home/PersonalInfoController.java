package cc.leedaud.controller.home;

import cc.leedaud.result.Result;
import cc.leedaud.service.PersonalInfoService;
import cc.leedaud.vo.PersonalInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  棣栭〉绔釜浜轰俊鎭帴鍙? */
@RestController("homePersonalInfoController")
@RequestMapping("/home/personalInfo")
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

