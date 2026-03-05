package cc.leedaud.service.impl;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.constant.StatusConstant;
import cc.leedaud.context.BaseContext;
import cc.leedaud.dto.*;
import cc.leedaud.entity.Admin;
import cc.leedaud.exception.*;
import cc.leedaud.mapper.AdminMapper;
import cc.leedaud.properties.VisitorProperties;
import cc.leedaud.service.*;
import cc.leedaud.vo.AdminLoginVO;
import cc.leedaud.vo.AdminVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private VerifyCodeService verifyCodeService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private VisitorProperties visitorProperties;
    @Autowired
    private EncryptPasswordService encryptPasswordService;

    /**
     * 鍙戦€侀獙璇佺爜
     * @param username
     */
    public void sendVerifyCode(String username) {
        // 楠岃瘉鐢ㄦ埛鏄惁瀛樺湪
        Admin admin = adminMapper.getByUsername(username);
        if(admin == null){
            // 璐﹀彿涓嶅瓨鍦?            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if(admin.getRole() == StatusConstant.DISABLE){
            // 娓稿鏃犻』閭楠岃瘉鐮?            throw new VisitorSendCodeException(MessageConstant.VISITOR_VERIFY_CODE_ERROR
                    +visitorProperties.getVerifyCode());
        }
        // 妫€鏌ユ槸鍚﹀彲浠ュ彂閫侀獙璇佺爜
        if(!verifyCodeService.canSendCode()){
            Long cooldown = verifyCodeService.getRemainingCooldown();
            throw new VerifyCodeCoolDownException("楠岃瘉鐮佸喎鍗翠腑,璇风瓑寰?+cooldown+"绉掑悗閲嶈瘯");
        }
        // 鐢熸垚骞朵繚瀛橀獙璇佺爜
        String code = verifyCodeService.generateCode();
        String email = admin.getEmail();

        verifyCodeService.saveCode(code);

        // 鍙戦€侀獙璇佺爜
        emailService.sendVerifyCode(email,code);
    }

    /**
     * 绠＄悊鍛樼櫥褰?     * @param adminLoginDTO
     * @return
     */
    public AdminLoginVO login(AdminLoginDTO adminLoginDTO) throws Exception {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();
        // 楠岃瘉鐢ㄦ埛鏄惁瀛樺湪
        Admin admin = adminMapper.getByUsername(username);
        if(admin == null){
            // 璐﹀彿涓嶅瓨鍦?            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 瀵瑰瘑鐮佽繘琛屽姞瀵?        String hashedPassword = encryptPasswordService.hashPassword(password, admin.getSalt());
        // 楠岃瘉瀵嗙爜鏄惁姝ｇ‘
        if(!hashedPassword.equals(admin.getPassword())){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 鍖哄垎娓稿鍜岀鐞嗗憳鏍￠獙楠岃瘉鐮?        if(admin.getRole() == StatusConstant.ENABLE){
            // 绠＄悊鍛橀渶瑕佹牎楠岄偖绠遍獙璇佺爜

            // 妫€鏌ユ槸鍚﹀彲浠ユ牎楠岄獙璇佺爜
            if(!verifyCodeService.canAttempt()){
                Long lockRemainingMinutes = verifyCodeService.getLockRemainingMinutes();
                throw new VerifyCodeLockException(MessageConstant.VERIFY_CODE_LOCK+lockRemainingMinutes+"鍒嗛挓");
            }

            // 鏍￠獙楠岃瘉鐮佹槸鍚︽纭?            boolean isValid = verifyCodeService.verifyCode(adminLoginDTO.getCode());
            if(!isValid){
                Long remainingAttempts = verifyCodeService.getRemainingAttempts();
                throw new VerifyCodeErrorException(MessageConstant.VERIFY_CODE_ERROR
                        +",杩樺彲浠ヨ瘯"+remainingAttempts+"娆?);
            }

        }else{
            // 娓稿鐩存帴鏍￠獙鍥哄畾楠岃瘉鐮?            if(!adminLoginDTO.getCode().equals(visitorProperties.getVerifyCode())){
                throw new VerifyCodeErrorException(MessageConstant.VERIFY_CODE_ERROR
                        +",璇疯緭鍏?"+visitorProperties.getVerifyCode());
            }
        }

        // 鐢熸垚骞跺瓨鍌╰oken
        String token = tokenService.createAndStoreToken(admin.getId(), admin.getRole());

        return AdminLoginVO.builder()
                .id(admin.getId())
                .token(token)
                .build();
    }

    /**
     * 鑾峰彇绠＄悊鍛樹俊鎭?     * @return
     */
    public AdminVO getAdminById() {
        Long adminId = BaseContext.getCurrentId();
        Admin admin = adminMapper.getById(adminId);
        if(admin == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 鏋勯€犵鐞嗗憳淇℃伅
        return AdminVO.builder()
                .id(adminId)
                .nickname(admin.getNickname())
                .email(admin.getEmail())
                .build();
    }

    /**
     * 绠＄悊鍛橀€€鍑虹櫥褰?     * @param adminLogoutDTO
     */
    public void logout(AdminLogoutDTO adminLogoutDTO) {
        // 鍒犻櫎Redis涓殑token
        tokenService.logout(adminLogoutDTO.getId(), adminLogoutDTO.getToken());
    }

    /**
     * 绠＄悊鍛樹慨鏀瑰瘑鐮?     * @param adminChangePasswordDTO
     */
    public void changePassword(AdminChangePasswordDTO adminChangePasswordDTO) throws Exception {
        Long adminId = BaseContext.getCurrentId();
        Admin admin = adminMapper.getById(adminId);
        if(admin == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        // 楠岃瘉涓ゆ杈撳叆鐨勬柊瀵嗙爜鏄惁涓€鑷?        if(!adminChangePasswordDTO.getNewPassword().equals(adminChangePasswordDTO.getConfirmNewPassword())){
            throw new PasswordErrorException(MessageConstant.NEW_PASSWORD_NOT_MATCH);
        }
        // 楠岃瘉鏃у瘑鐮佹槸鍚︽纭?        String hashedOldPassword = encryptPasswordService.hashPassword(adminChangePasswordDTO.getOldPassword(), admin.getSalt());
        if(!hashedOldPassword.equals(admin.getPassword())){
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        }
        // 鑾峰彇鍔犲瘑鍚庣殑鏂板瘑鐮?        String hashedNewPassword = encryptPasswordService.hashPassword(adminChangePasswordDTO.getNewPassword(), admin.getSalt());
        // 楠岃瘉鏂板瘑鐮佹槸鍚︿笌鏃у瘑鐮佷竴鑷?        if(hashedNewPassword.equals(admin.getPassword())){
            throw new PasswordErrorException(MessageConstant.NEW_PASSWORD_NOT_CHANGE);
        }
        // 鏇存柊绠＄悊鍛樹俊鎭?        admin.setPassword(hashedNewPassword);
        adminMapper.update(admin);
        // 鐧诲嚭鎵€鏈夎澶?        tokenService.logoutAll(adminId);
    }

    /**
     * 绠＄悊鍛樻洿鏀规樀绉?     * @param adminChangeNicknameDTO
     */
    public void changeNickname(AdminChangeNicknameDTO adminChangeNicknameDTO) {
        Long adminId = BaseContext.getCurrentId();
        Admin admin = adminMapper.getById(adminId);
        if(admin == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        // 鏇存柊鏄电О
        admin.setNickname(adminChangeNicknameDTO.getNickname());
        adminMapper.update(admin);
    }

    /**
     * 绠＄悊鍛樻崲缁戦偖绠?     * @param adminChangeEmailDTO
     */
    public void changeEmail(AdminChangeEmailDTO adminChangeEmailDTO) {
        Long adminId = BaseContext.getCurrentId();
        Admin admin = adminMapper.getById(adminId);
        if(admin == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 鏁堥獙閭楠岃瘉鐮?        // 妫€鏌ユ槸鍚﹀彲浠ユ牎楠岄獙璇佺爜
        if(!verifyCodeService.canAttempt()){
            Long lockRemainingMinutes = verifyCodeService.getLockRemainingMinutes();
            throw new VerifyCodeLockException(MessageConstant.VERIFY_CODE_LOCK+lockRemainingMinutes+"鍒嗛挓");
        }

        // 鏍￠獙楠岃瘉鐮佹槸鍚︽纭?        boolean isValid = verifyCodeService.verifyCode(adminChangeEmailDTO.getCode());
        if(!isValid){
            Long remainingAttempts = verifyCodeService.getRemainingAttempts();
            throw new VerifyCodeErrorException(MessageConstant.VERIFY_CODE_ERROR
                    +",杩樺彲浠ヨ瘯"+remainingAttempts+"娆?);
        }
        // 鏇存柊閭
        admin.setEmail(adminChangeEmailDTO.getEmail());
        adminMapper.update(admin);
    }
}

