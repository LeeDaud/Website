package cc.leedaud.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    /**
     * 鐢熸垚jwt
     * 绉佸寵浣跨敤鍥哄畾瀵嗛挜
     *
     * @param secretKey jwt绉橀挜
     * @param ttlMillis jwt杩囨湡鏃堕棿(姣)
     * @param claims    璁剧疆鐨勪俊鎭?     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 鎸囧畾绛惧悕鐨勬椂鍊欎娇鐢ㄧ殑绛惧悕绠楁硶锛屼篃灏辨槸header閭ｉ儴鍒?        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 鐢熸垚JWT鐨勬椂闂?        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 璁剧疆jwt鐨刡ody
        JwtBuilder builder = Jwts.builder()
                // 濡傛灉鏈夌鏈夊０鏄庯紝涓€瀹氳鍏堣缃繖涓嚜宸卞垱寤虹殑绉佹湁鐨勫０鏄庯紝杩欎釜鏄粰builder鐨刢laim璧嬪€硷紝涓€鏃﹀啓鍦ㄦ爣鍑嗙殑澹版槑璧嬪€间箣鍚庯紝灏辨槸瑕嗙洊浜嗛偅浜涙爣鍑嗙殑澹版槑鐨?                .claims(claims)
                // 璁剧疆绛惧悕浣跨敤鐨勭鍚嶇畻娉曞拰绛惧悕浣跨敤鐨勭閽?                .signWith(key)
                // 璁剧疆杩囨湡鏃堕棿
                .expiration(exp);

        return builder.compact();
    }

    /**
     * Token瑙ｅ瘑
     *
     * @param secretKey jwt绉橀挜 姝ょ閽ヤ竴瀹氳淇濈暀濂藉湪鏈嶅姟绔? 涓嶈兘鏆撮湶鍑哄幓, 鍚﹀垯sign灏卞彲浠ヨ浼€? 濡傛灉瀵规帴澶氫釜瀹㈡埛绔缓璁敼閫犳垚澶氫釜
     * @param token     鍔犲瘑鍚庣殑token
     * @return
     */
    public static Claims parseJWT(String secretKey, String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                            // 璁剧疆绛惧悕鐨勭閽?                            .verifyWith(key)
                            .build()
                            // 璁剧疆闇€瑕佽В鏋愮殑jwt
                            .parseSignedClaims(token)
                            .getPayload();
        return claims;
    }
}

