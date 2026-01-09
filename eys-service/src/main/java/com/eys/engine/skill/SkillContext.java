package com.eys.engine.skill;

import com.eys.model.entity.cfg.CfgSkill;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.model.entity.ga.GaGameRecord;
import com.eys.model.entity.ga.GaPlayerStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 技能执行上下文
 * 包含技能执行所需的所有信息
 *
 * @author EYS
 */
@Data
@Builder
public class SkillContext {

    /**
     * 技能配置
     */
    private CfgSkill skill;

    /**
     * 发起者对局玩家
     */
    private GaGamePlayer actor;

    /**
     * 发起者状态
     */
    private GaPlayerStatus actorStatus;

    /**
     * 目标玩家ID列表
     */
    private List<Long> targetPlayerIds;

    /**
     * 猜测的角色ID（刺客专用）
     */
    private Long guessRoleId;

    /**
     * 当前对局记录
     */
    private GaGameRecord gameRecord;

    /**
     * 当前轮次
     */
    private Integer currentRound;

    /**
     * 当前阶段
     */
    private String currentStage;
}
