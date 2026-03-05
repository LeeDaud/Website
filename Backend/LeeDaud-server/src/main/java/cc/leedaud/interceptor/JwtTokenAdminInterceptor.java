package cc.leedaud.interceptor;

import cc.leedaud.constant.JwtClaimsConstant;
import cc.leedaud.constant.MessageConstant;
import cc.leedaud.constant.StatusConstant;
import cc.leedaud.context.BaseContext;
import cc.leedaud.exception.GuestReadOnlyException;
import cc.leedaud.exception.NotLoginException;
import cc.leedaud.exception.UnauthorizedException;
import cc.leedaud.properties.JwtProperties;
import cc.leedaud.service.TokenService;
import cc.leedaud.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * jwt浠ょ墝鏍￠獙鐨勬嫤鎴櫒
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private TokenService tokenService;

    /**
     * 鏍￠獙jwt
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //鍒ゆ柇褰撳墠鎷︽埅鍒扮殑鏄疌ontroller鐨勬柟娉曡繕鏄叾浠栬祫婧?        if (!(handler instanceof HandlerMethod)) {
            //褰撳墠鎷︽埅鍒扮殑涓嶆槸鍔ㄦ€佹柟娉曪紝鐩存帴鏀捐
            return true;
        }

        // 浠庤姹傚ご涓幏鍙栦护鐗?        String token = request.getHeader(jwtProperties.getTokenName());

        // 濡傛灉浠ょ墝涓虹┖锛屾姏鍑烘湭鐧诲綍寮傚父
        if(StringUtils.isEmpty(token)){
            throw new NotLoginException(MessageConstant.NOT_LOGIN);
        }

        // 鏍￠獙浠ょ墝
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);
            Long adminId = Long.valueOf(claims.get(JwtClaimsConstant.ADMIN_ID).toString());
            Integer role = Integer.valueOf(claims.get(JwtClaimsConstant.ADMIN_ROLE).toString());
            log.info("jwt鏍￠獙,褰撳墠绠＄悊鍛榠d锛歿}, role: {}", adminId, role);

            // 妫€娴嬩护鐗屾槸鍚﹀湪鏈嶅姟绔瓨鍦?            if(!tokenService.isValidToken(adminId, token)){
                throw new UnauthorizedException(MessageConstant.NOT_AUTHORIZED);
            }

            // 娓稿璐﹀彿(role=0)鍙厑璁窯ET鏌ヨ鎿嶄綔锛岀姝㈠鍒犳敼
            if(role.equals(StatusConstant.DISABLE) && !"GET".equalsIgnoreCase(request.getMethod())){
                throw new GuestReadOnlyException(MessageConstant.GUEST_READ_ONLY);
            }

            BaseContext.setCurrentId(adminId);
            BaseContext.setCurrentRole(role);
            // 閫氳繃锛屾斁琛?            return true;
        }catch (GuestReadOnlyException ex){
            throw ex;
        }
        catch (Exception ex) {
            // 鏍￠獙澶辫触锛屾姏鍑烘湭鎺堟潈寮傚父
            throw new UnauthorizedException(MessageConstant.NOT_AUTHORIZED);
        }
    }

    /**
     * 鍚庣疆澶勭悊 - 娓呯悊ThreadLocal
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        try {
            // 娓呯悊ThreadLocal锛岄槻姝㈣櫄鎷熺嚎绋嬪鐢ㄥ鑷碼dminId涓茬敤
            BaseContext.removeCurrentId();
            BaseContext.removeCurrentRole();
        } catch (Exception e) {
            log.error("娓呯悊ThreadLocal澶辫触", e);
        }
    }
}

