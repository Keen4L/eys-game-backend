package com.eys.service.event;

import org.springframework.context.ApplicationEvent;
import lombok.Getter;

/**
 * 玩家退出房间事件
 *
 * @author EYS
 */
@Getter
public class PlayerLeftEvent extends ApplicationEvent {

    private final Long gameId;
    private final Long userId;

    public PlayerLeftEvent(Object source, Long gameId, Long userId) {
        super(source);
        this.gameId = gameId;
        this.userId = userId;
    }
}
