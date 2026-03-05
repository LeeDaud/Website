package cc.leedaud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "leedaud.email")
@Data
public class EmailProperties {
    /**
     * 閭鏈嶅姟鍣ㄩ偖绠?     */
    private String personal;

    private String from;
}

