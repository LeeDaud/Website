package cc.leedaud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching // 寮€鍚紦瀛樻敞瑙ｅ姛鑳?@EnableTransactionManagement // 寮€鍚簨鍔＄鐞?@EnableScheduling // 寮€鍚畾鏃朵换鍔?@EnableAsync // 寮€鍚紓姝ユ柟娉曟墽琛?@Slf4j
public class LeeDaudBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeeDaudBackendApplication.class, args);
        log.info("LeeDaud Backend server started");
    }
}

