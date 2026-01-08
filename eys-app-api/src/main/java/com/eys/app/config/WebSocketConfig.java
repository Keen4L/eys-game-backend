package com.eys.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * WebSocket 配置类
 *
 * @author EYS
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private GameWebSocketHandler gameWebSocketHandler;

    @Autowired
    private GameHandshakeInterceptor gameHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册 WebSocket 处理器
        // 路径: /ws/game?token=xxx&gameId=xxx
        registry.addHandler(gameWebSocketHandler, "/ws/game")
                .addInterceptors(gameHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
