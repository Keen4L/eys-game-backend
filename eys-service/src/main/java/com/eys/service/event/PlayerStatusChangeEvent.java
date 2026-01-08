package com.eys.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 玩家状态变更事件
 * 当玩家死亡/复活时触发，用于 WebSocket 广播
 *
 * @author EYS
 */
@Getter
public class PlayerStatusChangeEvent extends ApplicationEvent {

    /**
     * 游戏ID
     */
    private final Long gameId;

    /**
     * 对局玩家ID
     */
    private final Long gamePlayerId;

    /**
     * 用户ID
     */
    private final Long userId;

    /**
     * 是否存活
     */
    private final Boolean alive;

    /**
     * 变更类型: KILL-死亡, REVIVE-复活
     */
    private final String changeType;

    public PlayerStatusChangeEvent(Object source, Long gameId, Long gamePlayerId, Long userId, Boolean alive,
            String changeType) {
        super(source);
        this.gameId = gameId;
        this.gamePlayerId = gamePlayerId;
        this.userId = userId;
        this.alive = alive;
        this.changeType = changeType;
    }
}
