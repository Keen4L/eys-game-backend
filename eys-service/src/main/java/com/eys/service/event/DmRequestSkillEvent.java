package com.eys.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * DM 请求玩家使用技能事件
 * 当 DM 请求某个玩家使用技能时发布此事件
 *
 * @author EYS
 */
@Getter
public class DmRequestSkillEvent extends ApplicationEvent {

    private final Long gameId;
    private final Long dmUserId;
    private final Long targetPlayerId;
    private final Long targetUserId;
    private final Long skillInstanceId;
    private final String skillName;

    public DmRequestSkillEvent(Object source, Long gameId, Long dmUserId, Long targetPlayerId,
            Long targetUserId, Long skillInstanceId, String skillName) {
        super(source);
        this.gameId = gameId;
        this.dmUserId = dmUserId;
        this.targetPlayerId = targetPlayerId;
        this.targetUserId = targetUserId;
        this.skillInstanceId = skillInstanceId;
        this.skillName = skillName;
    }
}
