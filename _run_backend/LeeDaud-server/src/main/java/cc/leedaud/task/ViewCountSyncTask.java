package cc.leedaud.task;

import cc.leedaud.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

/**
 * 鏂囩珷娴忚閲忓畾鏃跺悓姝ヤ换鍔? * 灏哛edis涓疮绉殑娴忚閲忓閲忔壒閲忓悓姝ュ埌MySQL
 */
@Slf4j
@Component
public class ViewCountSyncTask {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ArticleMapper articleMapper;

    private static final String VIEW_COUNT_KEY = "article:viewCount";
    private static final String SYNC_LOCK_KEY = "lock:viewCountSync";

    /**
     * 姣?鍒嗛挓灏哛edis涓殑娴忚閲忓閲忓悓姝ュ埌MySQL
     * 浣跨敤Redis鍒嗗竷寮忛攣闃叉澶氬疄渚嬪苟鍙戞墽琛?     */
    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 60 * 1000)
    public void syncViewCountToMySQL() {
        // 鑾峰彇鍒嗗竷寮忛攣锛?鍒嗛挓杩囨湡锛堥槻姝㈡閿侊級
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(SYNC_LOCK_KEY, "1", Duration.ofMinutes(4));
        if (!Boolean.TRUE.equals(locked)) {
            log.debug("娴忚閲忓悓姝ヤ换鍔℃湭鑾峰彇鍒伴攣锛岃烦杩囨湰娆℃墽琛?);
            return;
        }

        try {
            Map<Object, Object> viewCounts = redisTemplate.opsForHash().entries(VIEW_COUNT_KEY);
            if (viewCounts == null || viewCounts.isEmpty()) {
                return;
            }

            int syncCount = 0;
            for (Map.Entry<Object, Object> entry : viewCounts.entrySet()) {
                try {
                    Long articleId = Long.parseLong(entry.getKey().toString());
                    int increment = ((Number) entry.getValue()).intValue();
                    if (increment <= 0) {
                        // 娓呯悊鏃犳晥鎴栦负闆剁殑key
                        redisTemplate.opsForHash().delete(VIEW_COUNT_KEY, entry.getKey());
                        continue;
                    }

                    // 鎵归噺绱姞鍒癕ySQL
                    articleMapper.addViewCount(articleId, increment);
                    // 浣跨敤鍘熷瓙鎿嶄綔鍑忓幓宸插悓姝ョ殑鍊硷紝閬垮厤涓㈠け鍚屾鏈熼棿鏂板鐨勬祻瑙堥噺
                    Long remaining = redisTemplate.opsForHash().increment(VIEW_COUNT_KEY, entry.getKey(), -increment);
                    // 濡傛灉鍓╀綑鍊?=0锛屾竻鐞嗚key
                    if (remaining != null && remaining <= 0) {
                        redisTemplate.opsForHash().delete(VIEW_COUNT_KEY, entry.getKey());
                    }
                    syncCount++;
                } catch (Exception e) {
                    log.error("鍚屾鏂囩珷 {} 娴忚閲忓紓甯? {}", entry.getKey(), e.getMessage());
                }
            }

            if (syncCount > 0) {
                log.info("娴忚閲忓悓姝ュ畬鎴愶紝鍏卞悓姝?{} 绡囨枃绔?, syncCount);
            }
        } catch (Exception e) {
            log.error("娴忚閲忓悓姝ュ紓甯? {}", e.getMessage());
        } finally {
            redisTemplate.delete(SYNC_LOCK_KEY);
        }
    }
}

