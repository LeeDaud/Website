package cc.leedaud.controller.admin;

import cc.leedaud.annotation.RateLimit;
import cc.leedaud.dto.*;
import cc.leedaud.result.Result;
import cc.leedaud.service.AdminService;
import cc.leedaud.vo.AdminLoginVO;
import cc.leedaud.vo.AdminVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 绠＄悊绔鐞嗗憳鎺ュ彛
 */
@RestController
@RequestMapping("/admin/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 鍙戦€侀獙璇佺爜
     */
    @PostMapping("/sendCode")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
            timeWindow = 60, message = "鎿嶄綔杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result sendCode(@RequestBody SendCodeDTO sendCodeDTO) {
        log.info("鍙戦€侀獙璇佺爜,{}", sendCodeDTO);
        adminService.sendVerifyCode(sendCodeDTO.getUsername());
        return Result.success();
    }

    /**
     * 绠＄悊鍛樼櫥褰?     */
    @PostMapping("/login")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
            timeWindow = 60, message = "鎿嶄綔杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯")
    public Result<AdminLoginVO> AdminLogin(@Valid @RequestBody AdminLoginDTO adminLoginDTO) throws Exception {
        log.info("绠＄悊鍛樼櫥褰曪細{}", adminLoginDTO);
        AdminLoginVO adminLoginVO = adminService.login(adminLoginDTO);
        return Result.success(adminLoginVO);
    }

    /**
     * 鑾峰彇绠＄悊鍛樹俊鎭?     */
    @GetMapping
    public Result<AdminVO> getAdminInfo() {
        AdminVO adminVO = adminService.getAdminById();
        return Result.success(adminVO);
    }

    /**
     * 绠＄悊鍛橀€€鍑虹櫥褰?     */
    @PostMapping("/logout")
    public Result logout(@RequestBody AdminLogoutDTO adminLogoutDTO) {
        log.info("绠＄悊鍛橀€€鍑虹櫥褰曪細{}", adminLogoutDTO);
        adminService.logout(adminLogoutDTO);
        return Result.success();
    }

    /**
     * 绠＄悊鍛樹慨鏀瑰瘑鐮?     */
    @PutMapping("/changePassword")
    public Result changePassword(@Valid @RequestBody AdminChangePasswordDTO adminChangePasswordDTO) throws Exception {
        log.info("绠＄悊鍛樹慨鏀瑰瘑鐮侊細{}", adminChangePasswordDTO);
        adminService.changePassword(adminChangePasswordDTO);
        return Result.success();
    }

    /**
     * 绠＄悊鍛樻洿鏀规樀绉?     */
    @PutMapping("/changeNickname")
    public Result changeNickname(@Valid @RequestBody AdminChangeNicknameDTO adminChangeNicknameDTO) {
        log.info("绠＄悊鍛樻洿鏀规樀绉帮細{}", adminChangeNicknameDTO);
        adminService.changeNickname(adminChangeNicknameDTO);
        return Result.success();
    }

    /**
     * 绠＄悊鍛樻崲缁戦偖绠?     */
    @PutMapping("/changeEmail")
    public Result changeEmail(@Valid @RequestBody AdminChangeEmailDTO adminChangeEmailDTO) {
        log.info("绠＄悊鍛樻崲缁戦偖绠憋細{}", adminChangeEmailDTO);
        adminService.changeEmail(adminChangeEmailDTO);
        return Result.success();
    }
}

