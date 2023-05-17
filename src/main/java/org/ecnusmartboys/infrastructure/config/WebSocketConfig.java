package org.ecnusmartboys.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.api.interceptor.WebSocketInterceptor;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketInterceptor webSocketInterceptor;

    private final WebSocketHandler webSocketHandler;

//    @Bean
//    public ServerEndpointExporter serverEndpoint() {
//        return new ServerEndpointExporter();
//    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws")
                //设置允许跨域访问
                .setAllowedOrigins("*")
                .addInterceptors(webSocketInterceptor);
    }
}
