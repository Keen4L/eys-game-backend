package com.eys.app.config;

import com.alibaba.fastjson2.JSON;
import com.eys.common.constant.WsMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 游戏 WebSocket 处理器
 * 处理 WebSocket 连接、断开、消息收发
 *
 * @author EYS
 */
@Slf4j
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    /**
     * 存储所有连接的 Session
     * 结构: gameId -> (userId -> session)
     */
    private static final Map<Long, Map<Long, WebSocketSession>> GAME_SESSIONS = new ConcurrentHashMap<>();

    /**
     * 连接建立
     */
    @Override
    @SuppressWarnings("null")
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        Long gameId = (Long) session.getAttributes().get("gameId");

        // 将 Session 加入到对应的游戏房间
        GAME_SESSIONS.computeIfAbsent(gameId, k -> new ConcurrentHashMap<>())
                .put(userId, session);

        log.info("WebSocket 连接建立: userId={}, gameId={}, sessionId={}", userId, gameId, session.getId());

        // 发送重连同步消息（如果是断线重连）
        sendReconnectSync(session, gameId, userId);
    }

    /**
     * 连接关闭
     */
    @Override
    @SuppressWarnings("null")
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        Long gameId = (Long) session.getAttributes().get("gameId");

        // 从房间中移除 Session
        Map<Long, WebSocketSession> gameSessions = GAME_SESSIONS.get(gameId);
        if (gameSessions != null) {
            gameSessions.remove(userId);
            if (gameSessions.isEmpty()) {
                GAME_SESSIONS.remove(gameId);
            }
        }

        log.info("WebSocket 连接关闭: userId={}, gameId={}, status={}", userId, gameId, status);
    }

    /**
     * 收到消息
     */
    @Override
    @SuppressWarnings("null")
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        Long userId = (Long) session.getAttributes().get("userId");
        Long gameId = (Long) session.getAttributes().get("gameId");

        log.debug("收到 WebSocket 消息: userId={}, gameId={}, payload={}", userId, gameId, message.getPayload());

        // 心跳消息处理
        if ("ping".equals(message.getPayload())) {
            try {
                session.sendMessage(new TextMessage("pong"));
            } catch (IOException e) {
                log.error("发送心跳响应失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 发送异常
     */
    @Override
    @SuppressWarnings("null")
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        Long userId = (Long) session.getAttributes().get("userId");
        Long gameId = (Long) session.getAttributes().get("gameId");
        log.error("WebSocket 传输异常: userId={}, gameId={}, error={}", userId, gameId, exception.getMessage());
    }

    // ==================== 消息推送方法 ====================

    /**
     * 向房间内所有玩家推送消息
     */
    public void broadcastToGame(Long gameId, WsMessageType type, Object data) {
        Map<Long, WebSocketSession> gameSessions = GAME_SESSIONS.get(gameId);
        if (gameSessions == null || gameSessions.isEmpty()) {
            log.warn("游戏房间无连接: gameId={}", gameId);
            return;
        }

        WsMessage wsMessage = new WsMessage(type.getCode(), data);
        String payload = JSON.toJSONString(wsMessage);

        gameSessions.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(payload));
                } catch (IOException e) {
                    log.error("推送消息失败: userId={}, error={}", userId, e.getMessage());
                }
            }
        });

        log.debug("广播消息: gameId={}, type={}, recipients={}", gameId, type.getCode(), gameSessions.size());
    }

    /**
     * 向指定玩家推送消息
     */
    public void sendToUser(Long gameId, Long userId, WsMessageType type, Object data) {
        Map<Long, WebSocketSession> gameSessions = GAME_SESSIONS.get(gameId);
        if (gameSessions == null) {
            log.warn("游戏房间不存在: gameId={}", gameId);
            return;
        }

        WebSocketSession session = gameSessions.get(userId);
        if (session == null || !session.isOpen()) {
            log.warn("用户未连接: gameId={}, userId={}", gameId, userId);
            return;
        }

        WsMessage wsMessage = new WsMessage(type.getCode(), data);
        String payload = JSON.toJSONString(wsMessage);

        try {
            session.sendMessage(new TextMessage(payload));
            log.debug("推送消息给用户: gameId={}, userId={}, type={}", gameId, userId, type.getCode());
        } catch (IOException e) {
            log.error("推送消息失败: userId={}, error={}", userId, e.getMessage());
        }
    }

    /**
     * 向房间内所有玩家推送消息（排除指定用户）
     *
     * @param gameId        游戏ID
     * @param excludeUserId 排除的用户ID
     * @param type          消息类型
     * @param data          消息内容
     */
    public void broadcastToGameExclude(Long gameId, Long excludeUserId, WsMessageType type, Object data) {
        Map<Long, WebSocketSession> gameSessions = GAME_SESSIONS.get(gameId);
        if (gameSessions == null || gameSessions.isEmpty()) {
            log.warn("游戏房间无连接: gameId={}", gameId);
            return;
        }

        WsMessage wsMessage = new WsMessage(type.getCode(), data);
        String payload = JSON.toJSONString(wsMessage);

        gameSessions.forEach((userId, session) -> {
            // 排除指定用户
            if (!userId.equals(excludeUserId) && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(payload));
                } catch (IOException e) {
                    log.error("推送消息失败: userId={}, error={}", userId, e.getMessage());
                }
            }
        });

        log.debug("广播消息(排除{}): gameId={}, type={}", excludeUserId, gameId, type.getCode());
    }

    /**
     * 断线重连后同步当前游戏状态
     * 注：重连同步由客户端主动请求当前游戏状态，无需服务端推送
     */
    private void sendReconnectSync(WebSocketSession session, Long gameId, Long userId) {
        log.info("WebSocket 重连: gameId={}, userId={}", gameId, userId);
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long gameId, Long userId) {
        Map<Long, WebSocketSession> gameSessions = GAME_SESSIONS.get(gameId);
        if (gameSessions == null) {
            return false;
        }
        WebSocketSession session = gameSessions.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 获取房间在线人数
     */
    public int getOnlineCount(Long gameId) {
        Map<Long, WebSocketSession> gameSessions = GAME_SESSIONS.get(gameId);
        return gameSessions == null ? 0
                : (int) gameSessions.values().stream()
                        .filter(WebSocketSession::isOpen).count();
    }

    /**
     * WebSocket 消息封装
     */
    public record WsMessage(String type, Object data) {
    }
}
