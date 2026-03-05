package cc.leedaud.service.impl;

import cc.leedaud.constant.JwtClaimsConstant;
import cc.leedaud.properties.JwtProperties;
import cc.leedaud.service.TokenService;
import cc.leedaud.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redis;
    @Autowired
    private JwtProperties jwtProperties;

    private static final String TOKEN_PREFIX = "token:active:";

    /**
     * 鍒涘缓骞朵繚瀛榯oken
     */
    public String createAndStoreToken(Long userId, Integer role) {
        // 鐢熸垚token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID,userId);
        claims.put(JwtClaimsConstant.ADMIN_ROLE,role);
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);

        // 灏唗oken瀛樺偍鑷砇edis,鐢╯et鍙互澶氱鐧诲綍
        String tokenKey = TOKEN_PREFIX + userId;
        redis.opsForSet().add(tokenKey, token);
        redis.expire(tokenKey, jwtProperties.getTtl(), TimeUnit.MILLISECONDS);

        return token;
    }

    /**
     * 楠岃瘉token鏈夋晥鎬?     * @param userId
     * @param token
     * @return
     */
    public boolean isValidToken(Long userId, String token) {
        String key = TOKEN_PREFIX + userId;
        return Boolean.TRUE.equals(redis.opsForSet().isMember(key, token));
    }

    /**
     * 閫€鍑虹櫥褰?- 鍒犻櫎token
     */
    public void logout(Long userId, String token) {
        String key = TOKEN_PREFIX + userId;
        redis.opsForSet().remove(key, token);
    }

    /**
     * 閫€鍑虹櫥褰?- 鍒犻櫎鎵€鏈塼oken
     */
    public void logoutAll(Long userId) {
        String key = TOKEN_PREFIX + userId;
        redis.delete(key);
    }
}

