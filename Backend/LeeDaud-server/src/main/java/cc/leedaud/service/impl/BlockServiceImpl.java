package cc.leedaud.service.impl;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.entity.Visitors;
import cc.leedaud.exception.BlockedException;
import cc.leedaud.mapper.VisitorMapper;
import cc.leedaud.service.BlockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BlockServiceImpl implements BlockService {

    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String RATE_LIMIT_KEY = "visitor:rate:";
    private static final String BLOCKED_KEY = "visitor:blocked:";

    /**
     * 妫€鏌ョ紦瀛樻槸鍚︽湁琚皝绂佽褰?     * @param fingerprint
     */
    public void checkIfBlocked(String fingerprint) {
        // 鍏堟鏌edis缂撳瓨
        String blockedKey = BLOCKED_KEY + fingerprint;
        Boolean isBlocked = redisTemplate.hasKey(blockedKey);

        if(Boolean.TRUE.equals(isBlocked)){
            throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
        }
        // 妫€鏌ユ暟鎹簱
        Visitors visitor = visitorMapper.findVisitorByFingerprint(fingerprint);
        if (visitor != null && visitor.getIsBlocked() == 1) {
            if (visitor.getExpiresAt() == null || visitor.getExpiresAt().isAfter(LocalDateTime.now())) {
                // 灏佺鏈夋晥锛屾洿鏂癛edis缂撳瓨
                redisTemplate.opsForValue().set(blockedKey, "1", 1, TimeUnit.DAYS);
                throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
            } else {
                // 灏佺宸茶繃鏈燂紝瑙ｉ櫎灏佺
                log.info("銆愯瀹㈣拷韪€戝皝绂佽繃鏈熻嚜鍔ㄨВ灏? id={}, fingerprint={}, expiresAt={}",
                        visitor.getId(), fingerprint, visitor.getExpiresAt());
                visitor.setIsBlocked(0);
                visitor.setExpiresAt(null);
                visitorMapper.updateById(visitor);
            }
        }
    }

    /**
     * 妫€鏌ヨ姹傞鐜?     * @param fingerprint
     * @param ip
     */
    public void checkRateLimit(String fingerprint, String ip) {
        // IP绾у埆闄愬埗锛氭瘡鍒嗛挓60娆?        String ipKey = RATE_LIMIT_KEY + "ip:" + ip;
        Long ipCount = redisTemplate.opsForValue().increment(ipKey, 1);
        if (ipCount == 1) {
            redisTemplate.expire(ipKey, 1, TimeUnit.MINUTES);
        }
        if (ipCount > 60) {
            // 鑷姩灏佺
            blockVisitor(fingerprint);
            throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
        }

        // 鎸囩汗绾у埆闄愬埗锛氭瘡灏忔椂1000娆?        String fpKey = RATE_LIMIT_KEY + "fp:" + fingerprint;
        Long fpCount = redisTemplate.opsForValue().increment(fpKey, 1);
        if (fpCount == 1) {
            redisTemplate.expire(fpKey, 1, TimeUnit.HOURS);
        }
        if (fpCount > 1000) {
            // 鑷姩灏佺
            blockVisitor(fingerprint);
            throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
        }
    }

    /**
     * 灏佺璁垮
     * @param fingerprint
     */
    private void blockVisitor(String fingerprint) {
        Visitors visitor = visitorMapper.findVisitorByFingerprint(fingerprint);
        if (visitor != null) {
            visitor.setIsBlocked(1);
            // 灏?澶?            visitor.setExpiresAt(LocalDateTime.now().plusDays(1));
            visitorMapper.updateById(visitor);

            // 鏇存柊Redis缂撳瓨
            String blockedKey = BLOCKED_KEY + fingerprint;
            redisTemplate.opsForValue().set(blockedKey, "1", 1, TimeUnit.DAYS);
            log.warn("灏佺璁垮: fingerprint={}",
                    fingerprint);
        }
    }
}

