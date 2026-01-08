package com.eys.app.config;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器
 * 用于验证 Token 和提取连接参数
 *
 * @author EYS
 */
@Slf4j
@Component
public class GameHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * 握手前拦截
     * 验证 Token 有效性，提取 gameId 和 userId
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            // 获取请求参数
            String token = servletRequest.getServletRequest().getParameter("token");
            String gameIdStr = servletRequest.getServletRequest().getParameter("gameId");

            if (token == null || token.isEmpty()) {
                log.warn("WebSocket 握手失败: 缺少 token 参数");
                return false;
            }

            if (gameIdStr == null || gameIdStr.isEmpty()) {
                log.warn("WebSocket 握手失败: 缺少 gameId 参数");
                return false;
            }

            try {
                // 验证 Token 并获取用户ID
                Object loginId = StpUtil.getLoginIdByToken(token);
                if (loginId == null) {
                    log.warn("WebSocket 握手失败: Token 无效");
                    return false;
                }

                Long userId = Long.parseLong(loginId.toString());
                Long gameId = Long.parseLong(gameIdStr);

                // 将用户信息存入 WebSocket Session 属性
                attributes.put("userId", userId);
                attributes.put("gameId", gameId);

                log.info("WebSocket 握手成功: userId={}, gameId={}", userId, gameId);
                return true;
            } catch (Exception e) {
                log.error("WebSocket 握手异常: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
        // 握手后处理（可选）
    }
}
