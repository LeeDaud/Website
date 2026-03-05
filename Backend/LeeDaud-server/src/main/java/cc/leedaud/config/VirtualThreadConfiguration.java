package cc.leedaud.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
@EnableAsync
public class VirtualThreadConfiguration implements WebMvcConfigurer {

    /**
     * 閰嶇疆Tomcat浣跨敤铏氭嫙绾跨▼
     */
    @Bean
    public TomcatProtocolHandlerCustomizer<?> tomcatVirtualThreadExecutor() {
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }

    /**
     * 閰嶇疆Spring寮傛浠诲姟浣跨敤铏氭嫙绾跨▼
     */
    @Bean(name = "taskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * 閰嶇疆閭欢鍙戦€佺殑寮傛鎵ц鍣?     */
    @Bean(name = "mailTaskExecutor")
    public AsyncTaskExecutor mailTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * 閰嶇疆MVC寮傛璇锋眰鏀寔
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(new TaskExecutorAdapter(
                Executors.newVirtualThreadPerTaskExecutor()
        ));
        configurer.setDefaultTimeout(30000L);  // 30绉掕秴鏃?    }

    /**
     * 閰嶇疆鏁版嵁搴撹繛鎺ユ睜铏氭嫙绾跨▼鏀寔
     */
    @Bean
    @Qualifier("dataSourceExecutor")
    public Executor dataSourceExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
