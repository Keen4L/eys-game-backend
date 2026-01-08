package com.eys.engine;

import com.alibaba.fastjson2.JSON;
import com.eys.common.constant.GameStage;
import com.eys.common.constant.TriggerMode;
import com.eys.common.exception.BizException;
import com.eys.common.result.ResultCode;
import com.eys.model.config.SkillLogicConfig;
import com.eys.model.entity.cfg.CfgSkill;
import com.eys.model.entity.ga.GaGameRecord;
import com.eys.model.entity.ga.GaPlayerStatus;
import com.eys.model.entity.ga.GaSkillInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 技能校验器
 * 负责校验技能使用的合法性，包括阶段、次数、目标等规则
 *
 * @author EYS
 */
@Slf4j
@Component
public class SkillValidator {

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

        // 2. 校验技能次数
        if (instance.getRemainCount() <= 0) {
            throw new BizException(ResultCode.SKILL_NO_REMAINING);
        }

        // 3. 解析技能逻辑配置
        SkillLogicConfig config = parseSkillLogic(skill.getSkillLogic());

        // 4. 校验阶段（仅 PLAYER_ACTIVE 类型需要校验）
        if (TriggerMode.PLAYER_ACTIVE.getCode().equals(skill.getTriggerMode())) {
            validatePhase(config, record.getCurrentStage());
        }

        // 5. 校验目标
        if (targetIds != null && !targetIds.isEmpty()) {
            validateTargets(config, targetIds, targetStatuses, instance.getGamePlayerId());
        }

        // 6. 校验约束条件
        validateConstraints(config, record);
    }

    /**
     * 判断技能当前是否可用（用于前端展示）
     */
    public boolean canUseNow(CfgSkill skill, String currentStage, int remainCount) {
        if (skill == null || remainCount <= 0) {
            return false;
        }

        // 只有 PLAYER_ACTIVE 类型的技能需要检查阶段
        if (!TriggerMode.PLAYER_ACTIVE.getCode().equals(skill.getTriggerMode())) {
            return false;
        }

        SkillLogicConfig config = parseSkillLogic(skill.getSkillLogic());
        if (config == null || config.getPhases() == null) {
            return false;
        }

        return config.getPhases().contains(currentStage);
    }

    /**
     * 获取技能初始次数
     */
    public int getInitialCount(CfgSkill skill) {
        SkillLogicConfig config = parseSkillLogic(skill.getSkillLogic());
        if (config != null && config.getUsage() != null && config.getUsage().getInitial() != null) {
            return config.getUsage().getInitial();
        }
        return 1;
    }

    /**
     * 获取技能组ID
     */
    public Long getGroupId(CfgSkill skill) {
        SkillLogicConfig config = parseSkillLogic(skill.getSkillLogic());
        if (config != null && config.getUsage() != null) {
            return config.getUsage().getGroupId();
        }
        return null;
    }

    // ==================== 私有方法 ====================

    /**
     * 解析技能逻辑 JSON
     */
    private SkillLogicConfig parseSkillLogic(String skillLogic) {
        if (skillLogic == null || skillLogic.isBlank()) {
            return null;
        }
        try {
            return JSON.parseObject(skillLogic, SkillLogicConfig.class);
        } catch (Exception e) {
            log.warn("解析技能逻辑失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 校验阶段
     */
    private void validatePhase(SkillLogicConfig config, String currentStage) {
        if (config == null || config.getPhases() == null) {
            return; // 无限制
        }
        if (!config.getPhases().contains(currentStage)) {
            throw new BizException(ResultCode.SKILL_STAGE_MISMATCH);
        }
    }

    /**
     * 校验目标
     */
    private void validateTargets(SkillLogicConfig config, List<Long> targetIds,
            List<GaPlayerStatus> targetStatuses, Long actorPlayerId) {
        if (config == null || config.getTarget() == null) {
            return; // 无限制
        }

        SkillLogicConfig.TargetConfig targetConfig = config.getTarget();

        // 校验目标数量
        if (targetConfig.getMin() != null && targetIds.size() < targetConfig.getMin()) {
            throw new BizException(ResultCode.SKILL_TARGET_INVALID, "目标数量不足");
        }
        if (targetConfig.getMax() != null && targetIds.size() > targetConfig.getMax()) {
            throw new BizException(ResultCode.SKILL_TARGET_INVALID, "目标数量过多");
        }

        // 校验排除自己
        if (Boolean.TRUE.equals(targetConfig.getExcludeSelf()) && targetIds.contains(actorPlayerId)) {
            throw new BizException(ResultCode.SKILL_TARGET_INVALID, "不能选择自己");
        }

        // 校验目标范围
        if (targetStatuses != null && "ALIVE".equals(targetConfig.getScope())) {
            for (GaPlayerStatus status : targetStatuses) {
                if (status.getIsAlive() != 1) {
                    throw new BizException(ResultCode.SKILL_TARGET_INVALID, "目标必须是存活玩家");
                }
            }
        }
    }

    /**
     * 校验约束条件
     */
    private void validateConstraints(SkillLogicConfig config, GaGameRecord record) {
        if (config == null || config.getConstraints() == null) {
            return;
        }

        SkillLogicConfig.ConstraintsConfig constraints = config.getConstraints();

        // 第一轮不可用
        if (Boolean.TRUE.equals(constraints.getDisableFirstRound()) && record.getCurrentRound() == 1) {
            throw new BizException(ResultCode.SKILL_STAGE_MISMATCH, "第一轮不可使用该技能");
        }

        // TODO: 不能连续对同一目标使用（需要查询 ActionLog）
    }
}
