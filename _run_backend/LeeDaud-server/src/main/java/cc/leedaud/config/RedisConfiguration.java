package cc.leedaud.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class RedisConfiguration {

    /**
     * 鍒涘缓鑷畾涔夌殑 Jackson2JsonRedisSerializer锛岃В鍐抽泦鍚堢被鍨嬪簭鍒楀寲闂
     */
    @Bean
    public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 鍚敤榛樿绫诲瀷淇℃伅
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
        );

        // 閰嶇疆鍙鎬?        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 閰嶇疆搴忓垪鍖栫壒鎬?        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 娉ㄥ唽 JavaTimeModule 浠ュ鐞?Java 8 鏃堕棿绫诲瀷
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
                                                       GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 璁剧疆redis杩炴帴宸ュ巶瀵硅薄
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 璁剧疆key鐨勫簭鍒楀寲鍣?        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 璁剧疆value鐨勫簭鍒楀寲鍣?- 浣跨敤鑷畾涔夌殑搴忓垪鍖栧櫒
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 鍒濆鍖朢edisTemplate
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * 閰嶇疆Spring Cache浣跨敤Redis浣滀负缂撳瓨鍚庣
     * 涓嶅悓缂撳瓨绌洪棿浣跨敤涓嶅悓鐨凾TL绛栫暐
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
                                     GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {

        // 榛樿缂撳瓨閰嶇疆锛?0鍒嗛挓杩囨湡锛屼娇鐢ㄨ嚜瀹氫箟鐨勫簭鍒楀寲鍣?        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        // 涓嶅悓缂撳瓨绌洪棿鐨凾TL绛栫暐
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 闈欐€佹暟鎹細寰堝皯鍙樺寲锛岀紦瀛?灏忔椂
        cacheConfigurations.put("personalInfo", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("socialMedia", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("skills", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("experiences", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("friendLinks", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 鏂囩珷鐩稿叧锛氶€備腑鍙樺寲棰戠巼锛岀紦瀛?0鍒嗛挓
        cacheConfigurations.put("articleCategories", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("articleTags", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("articleList", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("articleDetail", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("articleArchive", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 缁熻鏁版嵁锛氬彉鍖栭绻侊紝鐭椂闂寸紦瀛?        cacheConfigurations.put("blogReport", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // 闊充箰鍒楄〃锛氬緢灏戝彉鍖栵紝缂撳瓨1灏忔椂
        cacheConfigurations.put("musicList", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 绯荤粺閰嶇疆锛氭瀬灏戝彉鍖栵紝缂撳瓨1灏忔椂
        cacheConfigurations.put("systemConfig", defaultConfig.entryTtl(Duration.ofHours(1)));

        // Sitemap/RSS Feed锛氱紦瀛?0鍒嗛挓
        cacheConfigurations.put("sitemap", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("rssFeed", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
