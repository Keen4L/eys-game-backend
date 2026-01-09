package com.eys.service.event;

import org.springframework.context.ApplicationEvent;
import lombok.Getter;

/**
 * 玩家加入房间事件
 *
 * @author EYS
 */
@Getter
public class PlayerJoinedEvent extends ApplicationEvent {

    private final Long gameId;
    private final Long userId;
    private final String nickname;
    private final String avatarUrl;
    private final Integer seatNo;

    public PlayerJoinedEvent(Object source, Long gameId, Long userId,
            String nickname, String avatarUrl, Integer seatNo) {
        super(source);
        this.gameId = gameId;
        this.userId = userId;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.seatNo = seatNo;
    }
}
