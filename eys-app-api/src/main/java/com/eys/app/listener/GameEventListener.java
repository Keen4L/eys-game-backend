package com.eys.app.listener;

import com.eys.app.config.GameWebSocketHandler;
import com.eys.common.constant.WsMessageType;
import com.eys.service.event.GameStageChangeEvent;
import com.eys.service.event.PlayerStatusChangeEvent;
import com.eys.service.event.SkillUsedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 游戏事件监听器
 * 监听 Service 层发布的事件，通过 WebSocket 广播给房间内玩家
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GameEventListener {

        private final GameWebSocketHandler webSocketHandler;

        /**
         * 监听阶段变更事件
         */
        @Async
        @EventListener
        public void handleStageChange(GameStageChangeEvent event) {
                log.info("收到阶段变更事件: gameId={}, {} -> {}, round={}",
                                event.getGameId(), event.getOldStage(), event.getNewStage(), event.getCurrentRound());

                Map<String, Object> data = Map.of(
                                "oldStage", event.getOldStage() != null ? event.getOldStage() : "",
                                "newStage", event.getNewStage(),
                                "currentRound", event.getCurrentRound());

                webSocketHandler.broadcastToGame(event.getGameId(), WsMessageType.STAGE_CHANGE, data);
        }

        /**
         * 监听玩家状态变更事件
         */
        @Async
        @EventListener
        public void handlePlayerStatusChange(PlayerStatusChangeEvent event) {
                log.info("收到玩家状态变更事件: gameId={}, playerId={}, alive={}, type={}",
                                event.getGameId(), event.getGamePlayerId(), event.getAlive(), event.getChangeType());

                Map<String, Object> data = Map.of(
                                "gamePlayerId", event.getGamePlayerId(),
                                "userId", event.getUserId(),
                                "alive", event.getAlive(),
                                "changeType", event.getChangeType());

                webSocketHandler.broadcastToGame(event.getGameId(), WsMessageType.PLAYER_STATUS, data);
        }

        /**
         * 监听技能使用事件（分级广播）
         */
        @Async
        @EventListener
        public void handleSkillUsed(SkillUsedEvent event) {
                log.info("收到技能使用事件: gameId={}, actorId={}, skillId={}",
                                event.getGameId(), event.getActorPlayerId(), event.getSkillId());

                // 1. 给 DM 发送完整信息（包含身份判定结果）
                Map<String, Object> dmData = Map.of(
                                "actorPlayerId", event.getActorPlayerId(),
                                "skillId", event.getSkillId(),
                                "skillName", event.getSkillName(),
                                "dmNote", event.getDmNote() != null ? event.getDmNote() : "");

                webSocketHandler.sendToUser(event.getGameId(), event.getDmUserId(),
                                WsMessageType.SKILL_USED, dmData);

                // 2. 给房间内其他人发送脱敏信息
                Map<String, Object> publicData = Map.of(
                                "actorPlayerId", event.getActorPlayerId(),
                                "skillId", event.getSkillId(),
                                "skillName", event.getSkillName(),
                                "publicNote", event.getPublicNote() != null ? event.getPublicNote() : "");

                webSocketHandler.broadcastToGameExclude(event.getGameId(), event.getDmUserId(),
                                WsMessageType.SKILL_USED, publicData);
        }
}
