package com.eys.service.event;

import org.springframework.context.ApplicationEvent;
import lombok.Getter;

/**
 * 游戏开始事件
 *
 * @author EYS
 */
@Getter
public class GameStartedEvent extends ApplicationEvent {

    private final Long gameId;
    private final String stage;
    private final Integer round;

    public GameStartedEvent(Object source, Long gameId, String stage, Integer round) {
        super(source);
        this.gameId = gameId;
        this.stage = stage;
        this.round = round;
    }
}
