package com.eys.engine.skill;

import lombok.Builder;
import lombok.Data;

/**
 * 技能执行结果
 *
 * @author EYS
 */
@Data
@Builder
public class SkillResult {

    /**
     * 是否执行成功
     */
    private boolean success;

    /**
     * 结果日志（给 DM 看的详细信息）
     */
    private String dmNote;

    /**
     * 公开日志（给其他玩家看的脱敏信息）
     */
    private String publicNote;

    /**
     * 主要目标玩家ID
     */
    private Long targetPlayerId;

    /**
     * 目标角色ID（查验类技能使用）
     */
    private Long targetRoleId;

    /**
     * 效果类型: NONE, KILL, STATUS, REVEAL, ACTOR_DEATH 等
     */
    private String effectType;

    /**
     * 释放者是否死亡（如刺客猜错）
     */
    private boolean actorDeath;

    /**
     * 附加数据（如查验结果）
     */
    private Object extraData;

    // ==================== 快捷构造方法 ====================

    public static SkillResult success(String dmNote, String publicNote) {
        return SkillResult.builder()
                .success(true)
                .dmNote(dmNote)
                .publicNote(publicNote)
                .effectType("NONE")
                .actorDeath(false)
                .build();
    }

    public static SkillResult kill(Long targetId, String dmNote, String publicNote) {
        return SkillResult.builder()
                .success(true)
                .targetPlayerId(targetId)
                .dmNote(dmNote)
                .publicNote(publicNote)
                .effectType("KILL")
                .actorDeath(false)
                .build();
    }

    public static SkillResult actorDeath(String dmNote, String publicNote) {
        return SkillResult.builder()
                .success(false)
                .dmNote(dmNote)
                .publicNote(publicNote)
                .effectType("ACTOR_DEATH")
                .actorDeath(true)
                .build();
    }

    public static SkillResult status(Long targetId, String effectKey, String dmNote, String publicNote) {
        return SkillResult.builder()
                .success(true)
                .targetPlayerId(targetId)
                .dmNote(dmNote)
                .publicNote(publicNote)
                .effectType("STATUS")
                .extraData(effectKey)
                .actorDeath(false)
                .build();
    }

    public static SkillResult reveal(Long targetId, Long roleId, String dmNote, String publicNote) {
        return SkillResult.builder()
                .success(true)
                .targetPlayerId(targetId)
                .targetRoleId(roleId)
                .dmNote(dmNote)
                .publicNote(publicNote)
                .effectType("REVEAL")
                .actorDeath(false)
                .build();
    }

    public static SkillResult fail(String reason) {
        return SkillResult.builder()
                .success(false)
                .dmNote(reason)
                .publicNote("技能释放失败")
                .effectType("NONE")
                .actorDeath(false)
                .build();
    }
}
