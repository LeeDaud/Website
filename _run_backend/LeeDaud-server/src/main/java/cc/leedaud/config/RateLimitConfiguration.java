package cc.leedaud.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 闄愭祦閰嶇疆绫伙紙鍩轰簬Bucket4j鏈湴浠ょ墝妗讹級
 */
@Data
@Slf4j
@Configuration
public class RateLimitConfiguration {

    /**
     * 鏈湴Bucket缂撳瓨: key -> Bucket
     */
    private final ConcurrentHashMap<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    /**
     * 鑾峰彇鎴栧垱寤轰护鐗屾《
     * @param key 闄愭祦key
     * @param burstCapacity 绐佸彂瀹归噺锛堟《瀹归噺锛?     * @param tokens 姣忎釜鏃堕棿绐楀彛琛ュ厖鐨勪护鐗屾暟
     * @param duration 鏃堕棿绐楀彛
     * @return Bucket
     */
    public Bucket resolveBucket(String key, int burstCapacity, long tokens, Duration duration) {
        return bucketCache.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.classic(
                    burstCapacity,
                    Refill.greedy(tokens, duration)
            );
            return Bucket.builder().addLimit(limit).build();
        });
    }

    /**
     * 灏濊瘯娑堣垂涓€涓护鐗?     * @param key 闄愭祦key
     * @param burstCapacity 绐佸彂瀹归噺
     * @param tokens 姣忎釜鏃堕棿绐楀彛琛ュ厖鐨勪护鐗屾暟
     * @param duration 鏃堕棿绐楀彛
     * @return true=鍏佽閫氳繃, false=琚檺娴?     */
    public boolean tryConsume(String key, int burstCapacity, long tokens, Duration duration) {
        Bucket bucket = resolveBucket(key, burstCapacity, tokens, duration);
        return bucket.tryConsume(1);
    }
}
