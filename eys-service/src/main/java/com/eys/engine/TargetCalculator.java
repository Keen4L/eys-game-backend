package com.eys.engine;

import com.eys.model.entity.cfg.CfgSkill;
import com.eys.model.entity.ga.GaActionLog;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.model.entity.ga.GaPlayerStatus;
import com.eys.service.ga.GaActionLogService;
import com.eys.service.ga.GaGamePlayerService;
import com.eys.service.ga.GaPlayerStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 可选目标计算器
 * 根据技能配置和当前状态计算玩家可选的目标列表
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TargetCalculator {

    private final GaGamePlayerService gamePlayerService;
    private final GaPlayerStatusService playerStatusService;
    private final GaActionLogService actionLogService;

    /**
     * 计算技能可选目标
     *
     * @param gameId        游戏ID
     * @param actorPlayerId 释放者对局玩家ID
     * @param skill         技能配置
     * @param currentRound  当前轮次
     * @return 可选目标玩家ID列表
     */
    public List<Long> calculateValidTargets(Long gameId, Long actorPlayerId, CfgSkill skill, int currentRound) {
        // 1. 获取所有玩家
        List<GaGamePlayer> allPlayers = gamePlayerService.listByGameId(gameId);

        return allPlayers.stream()
                .filter(player -> {
                    // 2. 排除自己（如果配置要求）
                    if (skill.getExcludeSelf() == 1 && player.getId().equals(actorPlayerId)) {
                        return false;
                    }

                    // 3. 根据存活状态过滤
                    GaPlayerStatus status = playerStatusService.getById(player.getId());
                    boolean isAlive = status != null && status.getIsAlive() == 1;
                    int targetAliveState = skill.getTargetAliveState() != null ? skill.getTargetAliveState() : 1;

                    if (targetAliveState == 1 && !isAlive) {
                        return false; // 需要活人但目标已死
                    }
                    if (targetAliveState == 2 && isAlive) {
                        return false; // 需要死人但目标还活着
                    }

                    // 4. 检查目标是否有 BLOCK_SKILL 限制（被禁闭/被吞噬等）
                    String tagRestriction = skill.getTagRestriction();
                    if ("BLOCK_SKILL".equals(tagRestriction)) {
                        // 该技能会给目标添加 BLOCK_SKILL，不影响目标选择
                    } else {
                        // 检查目标是否被封禁技能
                        if (playerStatusService.hasEffect(player.getId(), "禁闭") ||
                                playerStatusService.hasEffect(player.getId(), "吞噬")) {
                            // 被禁闭/吞噬的玩家不能作为目标（可选：根据业务调整）
                        }
                    }

                    // 5. 防止连续两轮对同一目标使用（如保镖）
                    if (shouldExcludeLastRoundTarget(skill.getName())) {
                        if (wasTargetedLastRound(gameId, actorPlayerId, player.getId(), skill.getId(), currentRound)) {
                            return false;
                        }
                    }

                    return true;
                })
                .map(GaGamePlayer::getId)
                .collect(Collectors.toList());
    }

    /**
     * 判断该技能是否需要排除上轮目标
     */
    private boolean shouldExcludeLastRoundTarget(String skillName) {
        // 保镖、恋爱脑、梦魇等技能不能连续两轮对同一人使用
        return "贴身保护".equals(skillName) ||
                "绑定恋人".equals(skillName) ||
                "梦魇缠绕".equals(skillName);
    }

    /**
     * 检查上轮是否对该目标使用过此技能
     */
    private boolean wasTargetedLastRound(Long gameId, Long actorPlayerId, Long targetPlayerId,
            Long skillId, int currentRound) {
        if (currentRound <= 1) {
            return false; // 第一轮没有上轮
        }

        List<GaActionLog> lastRoundActions = actionLogService.listByGameAndRound(gameId, currentRound - 1);
        return lastRoundActions.stream().anyMatch(log -> log.getActorId().equals(actorPlayerId) &&
                log.getSkillId() != null && log.getSkillId().equals(skillId) &&
                log.getActionData() != null && log.getActionData().contains(String.valueOf(targetPlayerId)));
    }
}
