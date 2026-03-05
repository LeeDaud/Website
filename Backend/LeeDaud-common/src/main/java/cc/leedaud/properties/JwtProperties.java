package cc.leedaud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "leedaud.jwt")
@Data
public class JwtProperties {
    /**
     * jwt浠ょ墝鐩稿叧閰嶇疆
     */
    private String secretKey;
    private Long ttl;
    private String tokenName;
}

