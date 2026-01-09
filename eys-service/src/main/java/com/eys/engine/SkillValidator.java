package com.eys.engine;

import com.eys.common.constant.GameEffectConstant;
import com.eys.common.exception.BizException;
import com.eys.common.result.ResultCode;
import com.eys.model.entity.cfg.CfgSkill;
import com.eys.model.entity.ga.GaGameRecord;
import com.eys.model.entity.ga.GaPlayerStatus;
import com.eys.model.entity.ga.GaSkillInstance;
import com.eys.service.ga.GaPlayerStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 技能校验器 (重构版)
 * 负责校验技能使用的合法性，包括阶段、次数、目标等规则
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SkillValidator {

    private final GaPlayerStatusService playerStatusService;

    /**
     * 校验技能是否可以使用
     *
     * @param skill          技能配置
     * @param instance       技能实例
     * @param record         游戏记录
     * @param actorStatus    释放者状态
     * @param targetIds      目标玩家ID列表
     * @param targetStatuses 目标玩家状态列表
     * @throws BizException 校验失败时抛出异常
     */
    public void validate(CfgSkill skill, GaSkillInstance instance, GaGameRecord record,
            GaPlayerStatus actorStatus, List<Long> targetIds,
            List<GaPlayerStatus> targetStatuses) {

        // 1. 校验释放者是否存活
        if (actorStatus != null && actorStatus.getIsAlive() != 1) {
            throw new BizException(ResultCode.PLAYER_DEAD);
        }

        // 2. 校验状态效果（Tag 拦截）
        if (actorStatus != null) {
            if (playerStatusService.hasEffect(actorStatus.getGamePlayerId(), GameEffectConstant.NIGHTMARED) ||
                    playerStatusService.hasEffect(actorStatus.getGamePlayerId(), GameEffectConstant.SWALLOWED) ||
                    playerStatusService.hasEffect(actorStatus.getGamePlayerId(), GameEffectConstant.DETAINED)) {
                throw new BizException(ResultCode.SKILL_NOT_AVAILABLE, "当前状态无法使用技能");
            }
        }

        // 3. 校验技能次数
        if (instance != null && instance.getRemainCount() <= 0) {
            throw new BizException(ResultCode.SKILL_NO_REMAINING);
        }

        // 4. 校验阶段
        validatePhase(skill, record.getCurrentStage());

        // 5. 校验目标
        if (targetIds != null && !targetIds.isEmpty()) {
            validateTargets(skill, targetIds, targetStatuses, instance != null ? instance.getGamePlayerId() : null);
        }

        // 6. 校验约束条件（第一轮等）
        validateConstraints(skill, record);
    }

    /**
     * 判断技能当前是否可用（用于前端展示）
     */
    public boolean canUseNow(CfgSkill skill, String currentStage, int remainCount) {
        if (skill == null || remainCount <= 0) {
            return false;
        }

        // 没有阶段限制的技能（空字符串或null），仅DM手动推送
        if (skill.getTriggerPhases() == null || skill.getTriggerPhases().isBlank()) {
            return false;
        }

        // 检查当前阶段是否在允许的阶段列表中
        List<String> phases = Arrays.asList(skill.getTriggerPhases().split(","));
        return phases.contains(currentStage);
    }

    /**
     * 获取技能初始次数
     */
    public int getInitialCount(CfgSkill skill) {
        if (skill.getMaxUsageTotal() != null && skill.getMaxUsageTotal() > 0) {
            return skill.getMaxUsageTotal();
        }
        return -1; // 无限次数
    }

    // ==================== 私有方法 ====================

    /**
     * 校验阶段
     */
    private void validatePhase(CfgSkill skill, String currentStage) {
        // 没有阶段限制的技能跳过校验
        if (skill.getTriggerPhases() == null || skill.getTriggerPhases().isBlank()) {
            return;
        }

        List<String> phases = Arrays.asList(skill.getTriggerPhases().split(","));
        if (!phases.contains(currentStage)) {
            throw new BizException(ResultCode.SKILL_STAGE_MISMATCH);
        }
    }

    /**
     * 校验目标
     */
    private void validateTargets(CfgSkill skill, List<Long> targetIds,
            List<GaPlayerStatus> targetStatuses, Long actorPlayerId) {

        // 校验目标数量
        Integer targetCount = skill.getTargetCount();
        if (targetCount != null && targetIds.size() != targetCount) {
            throw new BizException(ResultCode.SKILL_TARGET_INVALID,
                    String.format("需要选择 %d 个目标", targetCount));
        }

        // 校验排除自己
        if (skill.getExcludeSelf() != null && skill.getExcludeSelf() == 1) {
            if (targetIds.contains(actorPlayerId)) {
                throw new BizException(ResultCode.SKILL_TARGET_INVALID, "不能选择自己");
            }
        }

        // 校验目标存活状态
        if (targetStatuses != null && skill.getTargetAliveState() != null) {
            Integer requiredState = skill.getTargetAliveState();
            if (requiredState == 1) { // 必须是活人
                for (GaPlayerStatus status : targetStatuses) {
                    if (status.getIsAlive() != 1) {
                        throw new BizException(ResultCode.SKILL_TARGET_INVALID, "目标必须是存活玩家");
                    }
                }
            } else if (requiredState == 2) { // 必须是死人
                for (GaPlayerStatus status : targetStatuses) {
                    if (status.getIsAlive() == 1) {
                        throw new BizException(ResultCode.SKILL_TARGET_INVALID, "目标必须是死亡玩家");
                    }
                }
            }
            // 0 = 不限，不做校验
        }
    }

    /**
     * 校验约束条件
     */
    private void validateConstraints(CfgSkill skill, GaGameRecord record) {
        // 目前没有额外的约束条件需要校验
        // 如果未来需要添加"第一轮不可用"等规则，可以在这里扩展
    }
}
