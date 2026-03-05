package cc.leedaud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket閰嶇疆绫伙紝鐢ㄤ簬娉ㄥ唽WebSocket鐨凚ean
 */
@Configuration
public class WebSocketConfiguration {

    /**
     * 鑷姩娉ㄥ唽浣跨敤浜咢ServerEndpoint娉ㄨВ澹版槑鐨刉ebSocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}


