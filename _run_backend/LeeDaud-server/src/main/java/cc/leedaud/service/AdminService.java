package cc.leedaud.service;

import cc.leedaud.dto.*;
import cc.leedaud.vo.AdminLoginVO;
import cc.leedaud.vo.AdminVO;

public interface AdminService {

    /**
     * 鍙戦€侀獙璇佺爜
     */
    void sendVerifyCode(String username);

    /**
     * 绠＄悊鍛樼櫥褰?     * @param adminLoginDTO
     * @return
     */
    AdminLoginVO login(AdminLoginDTO adminLoginDTO) throws Exception;

    /**
     * 鑾峰彇绠＄悊鍛樹俊鎭?     * @return
     */
    AdminVO getAdminById();

    /**
     * 绠＄悊鍛橀€€鍑虹櫥褰?     * @param adminLogoutDTO
     */
    void logout(AdminLogoutDTO adminLogoutDTO);

    /**
     * 绠＄悊鍛樹慨鏀瑰瘑鐮?     * @param adminChangePasswordDTO
     */
    void changePassword(AdminChangePasswordDTO adminChangePasswordDTO) throws Exception;

    /**
     * 绠＄悊鍛樻洿鏀规樀绉?     * @param adminChangeNicknameDTO
     */
    void changeNickname(AdminChangeNicknameDTO adminChangeNicknameDTO);

    /**
     * 绠＄悊鍛樻崲缁戦偖绠?     * @param adminChangeEmailDTO
     */
    void changeEmail(AdminChangeEmailDTO adminChangeEmailDTO);
}

