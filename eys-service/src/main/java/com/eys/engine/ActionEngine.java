package com.eys.engine;

import com.eys.common.constant.InteractionType;
import com.eys.model.config.SkillLogicConfig;
import com.eys.model.entity.cfg.CfgRole;
import com.eys.model.entity.cfg.CfgSkill;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.service.cfg.CfgRoleService;
import com.eys.service.ga.GaGamePlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 动作引擎
 * 负责执行技能效果判定，如刺客刺杀比对身份等
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActionEngine {

    private final CfgRoleService roleService;
    private final GaGamePlayerService gamePlayerService;

    /**
     * 技能执行结果
     */
    public record SkillResult(
            boolean success, // 是否成功（如刺客刺中）
            String resultNote, // 结果描述
            Long targetPlayerId, // 主要目标ID
            Long targetRoleId, // 目标角色ID
            String effectType // 效果类型: NONE, KILL, REVEAL 等
    ) {
    }

    /**
     * 执行技能效果判定
     *
     * @param skill       技能配置
     * @param actorPlayer 释放者
     * @param targetIds   目标玩家ID列表
     * @param guessRoleId 猜测的角色ID（仅部分技能需要）
     * @return 技能执行结果
     */
    public SkillResult execute(CfgSkill skill, GaGamePlayer actorPlayer,
            List<Long> targetIds, Long guessRoleId) {

        String skillName = skill.getName();
        Integer interactionType = skill.getInteractionType();

        // 无交互类型技能
        if (InteractionType.NONE.getCode().equals(interactionType)) {
            return new SkillResult(true, "技能释放: " + skillName, null, null, "NONE");
        }

        // 选择玩家类型技能
        if (InteractionType.PLAYER.getCode().equals(interactionType)) {
            if (targetIds == null || targetIds.isEmpty()) {
                return new SkillResult(false, "未选择目标", null, null, "NONE");
            }
            Long targetId = targetIds.get(0);
            return new SkillResult(true,
                    "技能释放: " + skillName + " -> 玩家" + targetId,
                    targetId, null, "TARGET");
        }

        // 选择玩家并猜测角色类型技能（如刺客）
        if (InteractionType.PLAYER_ROLE.getCode().equals(interactionType)) {
            return executePlayerRoleSkill(skill, actorPlayer, targetIds, guessRoleId);
        }

        return new SkillResult(true, "技能释放: " + skillName, null, null, "NONE");
    }

    /**
     * 执行猜测角色类技能（如刺客刺杀）
     */
    private SkillResult executePlayerRoleSkill(CfgSkill skill, GaGamePlayer actorPlayer,
            List<Long> targetIds, Long guessRoleId) {
        if (targetIds == null || targetIds.isEmpty()) {
            return new SkillResult(false, "未选择目标", null, null, "NONE");
        }

        Long targetPlayerId = targetIds.get(0);
        GaGamePlayer targetPlayer = gamePlayerService.getById(targetPlayerId);

        if (targetPlayer == null) {
            return new SkillResult(false, "目标玩家不存在", null, null, "NONE");
        }

        Long actualRoleId = targetPlayer.getCurrRoleId();
        CfgRole actualRole = roleService.getById(actualRoleId);
        CfgRole guessRole = guessRoleId != null ? roleService.getById(guessRoleId) : null;

        String actualRoleName = actualRole != null ? actualRole.getName() : "未知";
        String guessRoleName = guessRole != null ? guessRole.getName() : "未知";

        // 判定是否猜中
        boolean isCorrect = guessRoleId != null && guessRoleId.equals(actualRoleId);

        if (isCorrect) {
            log.info("技能判定成功: skill={}, actor={}, target={}, role={}",
                    skill.getName(), actorPlayer.getId(), targetPlayerId, actualRoleName);
            return new SkillResult(true,
                    "【" + skill.getName() + "】命中！目标角色: " + actualRoleName,
                    targetPlayerId, actualRoleId, "KILL");
        } else {
            log.info("技能判定失败: skill={}, actor={}, target={}, guess={}, actual={}",
                    skill.getName(), actorPlayer.getId(), targetPlayerId, guessRoleName, actualRoleName);
            return new SkillResult(false,
                    "【" + skill.getName() + "】未命中。猜测: " + guessRoleName,
                    targetPlayerId, null, "NONE");
        }
    }
}
