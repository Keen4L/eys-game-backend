package com.eys.service.event;

import org.springframework.context.ApplicationEvent;
import lombok.Getter;

/**
 * 投票提交事件（通知 DM 投票进度）
 *
 * @author EYS
 */
@Getter
public class VoteSubmittedEvent extends ApplicationEvent {

    private final Long gameId;
    private final Long dmUserId;
    private final Integer votedCount;
    private final Integer totalVoters;

    public VoteSubmittedEvent(Object source, Long gameId, Long dmUserId,
            Integer votedCount, Integer totalVoters) {
        super(source);
        this.gameId = gameId;
        this.dmUserId = dmUserId;
        this.votedCount = votedCount;
        this.totalVoters = totalVoters;
    }
}
