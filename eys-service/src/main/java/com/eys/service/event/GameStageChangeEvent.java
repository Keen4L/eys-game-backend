package com.eys.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 游戏阶段变更事件
 * 当 DM 切换游戏阶段时触发，用于 WebSocket 广播
 *
 * @author EYS
 */
@Getter
public class GameStageChangeEvent extends ApplicationEvent {

    /**
     * 游戏ID
     */
    private final Long gameId;

    /**
     * 旧阶段
     */
    private final String oldStage;

    /**
     * 新阶段
     */
    private final String newStage;

    /**
     * 当前轮次
     */
    private final Integer currentRound;

    public GameStageChangeEvent(Object source, Long gameId, String oldStage, String newStage, Integer currentRound) {
        super(source);
        this.gameId = gameId;
        this.oldStage = oldStage;
        this.newStage = newStage;
        this.currentRound = currentRound;
    }
}
