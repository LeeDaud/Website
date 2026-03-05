package cc.leedaud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "leedaud.website")
@Data
public class WebsiteProperties {
    // 缃戠珯鏍囬
    private String title;
    // 涓婚〉鍦板潃
    private String home;
    // 绠＄悊绔湴鍧€
    private String admin;
    // 绠€鍘嗗湴鍧€
    private String cv;
    // 鍗氬鍦板潃
    private String blog;
}

