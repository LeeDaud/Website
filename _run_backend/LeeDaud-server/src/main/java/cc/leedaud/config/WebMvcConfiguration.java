package cc.leedaud.config;

import cc.leedaud.interceptor.JwtTokenAdminInterceptor;
import cc.leedaud.json.JacksonObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 閰嶇疆绫伙紝娉ㄥ唽web灞傜浉鍏崇粍浠? */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 娉ㄥ唽鑷畾涔夋嫤鎴櫒
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/admin/login")
                .excludePathPatterns("/admin/admin/sendCode")
                .excludePathPatterns("/admin/admin/logout");

        // API 鍝嶅簲绂佹 CDN/娴忚鍣ㄧ紦瀛橈紝闃叉 GET 璇锋眰杩斿洖杩囨湡鏁版嵁
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.setHeader("Pragma", "no-cache");
                return true;
            }
        }).addPathPatterns("/admin/**", "/blog/**","/cv/**","/home/**");
    }

    /**
     * 閰嶇疆璺ㄥ煙鏀寔
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 鍏佽鎵€鏈夋簮锛屾垨鎸囧畾鍩熷悕
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 鍏佽鐨凥TTP鏂规硶
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);  // 棰勬璇锋眰缂撳瓨鏃堕棿
    }

    /**
     * 鎵╁睍娑堟伅杞崲鍣? 灏咼ava瀵硅薄杞崲涓篔SON鏍煎紡鐨勫搷搴旀暟鎹?     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 鍒涘缓娑堟伅杞崲鍣ㄥ璞?        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 璁剧疆瀵硅薄杞崲鍣紝搴曞眰浣跨敤FastJSON灏咼ava瀵硅薄杞负JSON
        converter.setObjectMapper(new JacksonObjectMapper());
        // 灏嗘秷鎭浆鎹㈠櫒鍔犲叆鍒板鍣ㄤ腑
        converters.add(0, converter);
    }
}

