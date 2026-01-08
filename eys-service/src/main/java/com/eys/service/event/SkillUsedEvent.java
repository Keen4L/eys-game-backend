package com.eys.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 技能使用事件
 * 当玩家使用技能时触发，用于 WebSocket 通知
 * 支持消息分级：DM 收到完整信息，其他人收到脱敏信息
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
     * DM 用户ID（用于精准推送）
     */
    private final Long dmUserId;

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
     * DM 专用详细结果（包含身份信息）
     */
    private final String dmNote;

    /**
     * 全员公开通知（脱敏后）
     */
    private final String publicNote;

    public SkillUsedEvent(Object source, Long gameId, Long dmUserId, Long actorPlayerId,
            Long skillId, String skillName, String dmNote, String publicNote) {
        super(source);
        this.gameId = gameId;
        this.dmUserId = dmUserId;
        this.actorPlayerId = actorPlayerId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.dmNote = dmNote;
        this.publicNote = publicNote;
    }
}
