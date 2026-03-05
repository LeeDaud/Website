package cc.leedaud.service.impl;

import cc.leedaud.service.VerifyCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {

    // Redis key
    private static final String KEY_VERIFY_CODE = "verify_code";
    private static final String KEY_RATE_LIMIT = "rate_limit";
    private static final String KEY_ATTEMPT_COUNT = "attempt_count";
    private static final String KEY_LOCK = "lock";

    // 鏃堕棿甯搁噺
    private static final int RATE_LIMIT_SECONDS = 60; // 鍙戦€侀鐜囬檺鍒?0绉?    private static final int CODE_TTL_MINUTES = 5;    // 楠岃瘉鐮佹湁鏁堟湡5鍒嗛挓
    private static final int MAX_ATTEMPTS = 5;        // 鏈€澶у皾璇曟鏁?    private static final int LOCK_MINUTES = 30;       // 閿佸畾30鍒嗛挓

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redis;

    // 鐢熸垚楠岃瘉鐮?    public String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    // 淇濆瓨楠岃瘉鐮佸苟璁剧疆鍙戦€侀鐜?    public void saveCode(String code) {
        // 淇濆瓨楠岃瘉鐮?        redis.opsForValue().set(KEY_VERIFY_CODE, code, CODE_TTL_MINUTES, TimeUnit.MINUTES);
        // 璁剧疆鍙戦€侀鐜囬檺鍒?        redis.opsForValue().set(KEY_RATE_LIMIT, "1", RATE_LIMIT_SECONDS, TimeUnit.SECONDS);
        // 閲嶇疆灏濊瘯璁℃暟鍜岄攣瀹氱姸鎬?        redis.delete(KEY_ATTEMPT_COUNT);
        redis.delete(KEY_LOCK);
    }

    // 閭鏄惁鍙互鍙戦€侀獙璇佺爜锛堥鐜囬檺鍒讹級
    public boolean canSendCode() {
        return redis.opsForValue().get(KEY_RATE_LIMIT) == null;
    }

    // 鑾峰彇鍓╀綑楠岃瘉鐮佸喎鍗存椂闂?绉?
    public Long getRemainingCooldown() {
        Long ttl = redis.getExpire(KEY_RATE_LIMIT, TimeUnit.SECONDS);
        return ttl != null ? Math.max(ttl, 0) : 0;
    }

    // 鏄惁琚攣瀹?    public boolean isLocked() {
        return Boolean.TRUE.equals(redis.hasKey(KEY_LOCK));
    }

    // 鑾峰彇閿佸畾鍓╀綑鏃堕棿锛堝垎閽燂級
    public Long getLockRemainingMinutes() {
        Long ttl = redis.getExpire(KEY_LOCK, TimeUnit.MINUTES);
        return ttl != null ? Math.max(ttl, 0) : 0;
    }

    // 鏄惁鍏佽灏濊瘯楠岃瘉
    public boolean canAttempt() {
        // 妫€鏌ユ槸鍚﹁閿佸畾
        if (isLocked()) {
            return false;
        }
        return true;
    }

    // 楠岃瘉楠岃瘉鐮?    public boolean verifyCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        // 妫€鏌ユ槸鍚﹁閿佸畾
        if (isLocked()) {
            return false;
        }

        // 妫€鏌ラ獙璇佺爜鏄惁瀛樺湪
        String savedCode = (String) redis.opsForValue().get(KEY_VERIFY_CODE);
        if (savedCode == null) {
            // 楠岃瘉澶辫触锛岃褰曞皾璇?            recordFailedAttempt();
            return false;
        }

        // 楠岃瘉
        boolean success = savedCode.equals(code.trim());

        if (success) {
            // 楠岃瘉鎴愬姛
            clearAll(); // 娓呯┖鎵€鏈夐獙璇佺浉鍏虫暟鎹?            return true;
        } else {
            // 楠岃瘉澶辫触锛岃褰曞皾璇?            recordFailedAttempt();
            return false;
        }
    }

    // 璁板綍澶辫触灏濊瘯
    private void recordFailedAttempt() {
        // 澧炲姞澶辫触璁℃暟
        Long attemptCount = redis.opsForValue().increment(KEY_ATTEMPT_COUNT, 1L);
        if (attemptCount == null) {
            attemptCount = 1L;
        }

        // 濡傛灉鏄涓€娆″け璐ワ紝璁剧疆杩囨湡鏃堕棿
        if (attemptCount == 1) {
            Long codeTtl = redis.getExpire(KEY_VERIFY_CODE, TimeUnit.SECONDS);
            if (codeTtl > 0) {
                redis.expire(KEY_ATTEMPT_COUNT, codeTtl, TimeUnit.SECONDS);
            }
        }

        // 杈惧埌鏈€澶у皾璇曟鏁帮紝閿佸畾
        if (attemptCount >= MAX_ATTEMPTS) {
            redis.opsForValue().set(KEY_LOCK, "1", LOCK_MINUTES, TimeUnit.MINUTES);
        }
    }

    // 鑾峰彇褰撳墠灏濊瘯娆℃暟
    public Long getAttemptCount() {
        try {
            Object value = redis.opsForValue().get(KEY_ATTEMPT_COUNT);
            if (value == null) {
                return 0L;
            }
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    // 鑾峰彇鍓╀綑灏濊瘯娆℃暟
    public Long getRemainingAttempts() {
        if (isLocked()) {
            return 0L;
        }
        Long attemptCount = getAttemptCount();
        return Math.max(MAX_ATTEMPTS - attemptCount, 0);
    }

    // 閲嶇疆鐘舵€?    public void clearAll() {
        redis.delete(KEY_VERIFY_CODE);
        redis.delete(KEY_RATE_LIMIT);
        redis.delete(KEY_ATTEMPT_COUNT);
        redis.delete(KEY_LOCK);
    }
}

