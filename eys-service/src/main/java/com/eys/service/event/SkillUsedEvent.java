package com.eys.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 技能使用事件
 * 当玩家使用技能时触发，用于 WebSocket 通知
 *
 * @author EYS
 */
@Getter
public class SkillUsedEvent extends ApplicationEvent {

    /**
     * 游戏ID
     */
    private final Long gameId;

    /**
     * 使用者对局玩家ID
     */
    private final Long actorPlayerId;

    /**
     * 技能ID
     */
    private final Long skillId;

    /**
     * 技能名称
     */
    private final String skillName;

    /**
     * 结果备注
     */
    private final String resultNote;

    public SkillUsedEvent(Object source, Long gameId, Long actorPlayerId, Long skillId, String skillName,
            String resultNote) {
        super(source);
        this.gameId = gameId;
        this.actorPlayerId = actorPlayerId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.resultNote = resultNote;
    }
}
