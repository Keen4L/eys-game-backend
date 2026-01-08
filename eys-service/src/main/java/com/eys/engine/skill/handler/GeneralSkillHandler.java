package com.eys.engine.skill.handler;

import com.alibaba.fastjson2.JSON;
import com.eys.common.constant.GameEffectConstant;
import com.eys.common.constant.InteractionType;
import com.eys.engine.skill.SkillContext;
import com.eys.engine.skill.SkillHandler;
import com.eys.engine.skill.SkillResult;
import com.eys.model.entity.cfg.CfgRole;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.model.entity.ga.GaPlayerStatus;
import com.eys.service.cfg.CfgRoleService;
import com.eys.service.ga.GaGamePlayerService;
import com.eys.service.ga.GaPlayerStatusService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用技能处理器
 * 替代 StandardKillHandler, StatusEffectHandler, InvestigationHandler
 * 支持通过 JSON 动作列表配置多种效果
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeneralSkillHandler implements SkillHandler {

    private final GaGamePlayerService gamePlayerService;
    private final GaPlayerStatusService playerStatusService;
    private final CfgRoleService roleService;

    @Override
    public String getHandlerKey() {
        return "GeneralSkillHandler";
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.PLAYER;
    }

    @Override
    public SkillResult execute(SkillContext context) {
        Object actionsObj = context.getConfig().get("actions");
        if (actionsObj == null) {
            return SkillResult.fail("配置错误：缺少 actions");
        }

        List<ActionConfig> actions = JSON.parseArray(JSON.toJSONString(actionsObj), ActionConfig.class);
        if (actions == null || actions.isEmpty()) {
            return SkillResult.fail("配置错误：actions 为空");
        }

        SkillResult finalResult = null;
        StringBuilder dmNoteBuilder = new StringBuilder();
        StringBuilder publicNoteBuilder = new StringBuilder();

        // 默认目标
        Long targetId = context.getTargetPlayerIds() != null && !context.getTargetPlayerIds().isEmpty()
                ? context.getTargetPlayerIds().get(0)
                : null;

        for (ActionConfig action : actions) {
            SkillResult stepResult = null;
            switch (action.getType()) {
                case "KILL":
                    stepResult = handleKill(context, action, targetId);
                    break;
                case "ADD_STATUS":
                    stepResult = handleAddStatus(context, action, targetId);
                    break;
                case "CHECK_ROLE":
                    stepResult = handleCheckRole(context, action, targetId);
                    break;
                default:
                    log.warn("未知的动作类型: {}", action.getType());
            }

            if (stepResult != null) {
                // 如果任意一步失败，可以中断或者继续？通常如果是组合技，一步失败可能都失败。
                // 这里暂简单处理：如果返回失败，立即返回。
                if (!stepResult.isSuccess()) {
                    return stepResult;
                }

                // 累加 Note
                if (stepResult.getDmNote() != null) {
                    dmNoteBuilder.append(stepResult.getDmNote()).append("; ");
                }
                if (stepResult.getPublicNote() != null) {
                    publicNoteBuilder.append(stepResult.getPublicNote()).append("; ");
                }

                // 采用最后一个非空的 result 作为主体（主要是 effect_type, target_id 等）
                finalResult = stepResult;
            }
        }

        if (finalResult != null) {
            return SkillResult.builder()
                    .success(true)
                    .dmNote(dmNoteBuilder.toString())
                    .publicNote(publicNoteBuilder.toString())
                    .targetPlayerId(finalResult.getTargetPlayerId())
                    .targetRoleId(finalResult.getTargetRoleId())
                    .effectType(finalResult.getEffectType())
                    .actorDeath(finalResult.isActorDeath())
                    .extraData(finalResult.getExtraData())
                    .build();
        }

        return SkillResult.success("技能执行完成", "技能已使用");
    }

    // ==================== 内部逻辑实现 ====================

    /**
     * 处理击杀逻辑
     */
    /**
     * 处理击杀逻辑（纯记录）
     * 不修改任何玩家状态，仅生成日志供 DM 裁决
     */
    private SkillResult handleKill(SkillContext context, ActionConfig config, Long targetId) {
        if (targetId == null)
            return SkillResult.fail("未选择目标");

        GaGamePlayer actor = context.getActor();
        GaGamePlayer target = gamePlayerService.getById(targetId);
        GaPlayerStatus targetStatus = playerStatusService.getById(targetId);

        if (target == null || targetStatus == null || targetStatus.getIsAlive() != 1) {
            return SkillResult.fail("目标无效或已死亡");
        }

        // 检查是否有杀错惩罚风险（仅记录，不执行）
        String warningNote = "";
        if (Boolean.TRUE.equals(config.getPenaltyOnGood())) {
            Long targetRoleId = target.getCurrRoleId();
            CfgRole targetRole = roleService.getById(targetRoleId);
            // 鹅阵营(0) 为好人
            if (targetRole != null && targetRole.getCampType() == 0) {
                warningNote = " ⚠️【警告：目标是好人阵营，存在反噬风险】";
            }
        }

        // 生成日志描述（不修改状态）
        String dmNote = String.format("玩家%d 对 玩家%d 释放击杀技能%s",
                actor.getSeatNo(), target.getSeatNo(), warningNote);
        String publicNote = String.format("玩家%d 使用了技能", actor.getSeatNo());

        log.info("GeneralHandler.KILL 记录: actor={}, target={}", actor.getId(), targetId);
        return SkillResult.kill(targetId, dmNote, publicNote);
    }

    /**
     * 处理状态附加逻辑
     */
    private SkillResult handleAddStatus(SkillContext context, ActionConfig config, Long targetId) {
        if (targetId == null)
            return SkillResult.fail("未选择目标");

        String effectKey = config.getEffectKey();
        Integer duration = config.getDuration();

        if (effectKey == null || duration == null) {
            return SkillResult.fail("状态配置缺失");
        }

        playerStatusService.addEffect(targetId, effectKey, duration);

        GaGamePlayer actor = context.getActor();
        GaGamePlayer target = gamePlayerService.getById(targetId);

        String dmNote = String.format("玩家%d 对 玩家%d 施加了状态【%s】", actor.getSeatNo(), target.getSeatNo(), effectKey);
        String publicNote = String.format("玩家%d 使用了技能", actor.getSeatNo());

        return SkillResult.status(targetId, effectKey, dmNote, publicNote);
    }

    /**
     * 处理查验反馈逻辑
     */
    private SkillResult handleCheckRole(SkillContext context, ActionConfig config, Long targetId) {
        if (targetId == null)
            return SkillResult.fail("未选择目标");

        GaGamePlayer actor = context.getActor();
        GaGamePlayer target = gamePlayerService.getById(targetId);
        Long targetRoleId = target.getCurrRoleId();
        CfgRole targetRole = roleService.getById(targetRoleId);

        String returnType = config.getReturnType() != null ? config.getReturnType() : "ROLE";
        String resultDesc;
        Map<String, Object> extra = new HashMap<>();

        if (targetRole == null) {
            resultDesc = "无法获取角色信息";
        } else {
            String targetRoleName = targetRole.getName();
            Integer campType = targetRole.getCampType();

            if ("ROLE".equals(returnType)) {
                resultDesc = "身份是【" + targetRoleName + "】";
                extra.put("role_name", targetRoleName);
            } else if ("CAMP".equals(returnType)) {
                String campName = getCampName(campType);
                resultDesc = "阵营是【" + campName + "】";
                extra.put("camp", campName);
            } else if ("DUCK_CHECK".equals(returnType)) {
                boolean isDuck = campType != null && campType == 1; // 1-鸭
                resultDesc = isDuck ? "是鸭子！" : "不是鸭子";
                extra.put("is_duck", isDuck);
            } else {
                resultDesc = "未知查验类型";
            }
        }

        String dmNote = String.format("玩家%d 查验 玩家%d：%s", actor.getSeatNo(), target.getSeatNo(), resultDesc);
        String publicNote = String.format("玩家%d 使用了技能", actor.getSeatNo());

        SkillResult result = SkillResult.reveal(targetId, targetRoleId, dmNote, publicNote);
        result.setExtraData(extra);
        return result;
    }

    private String getCampName(Integer campType) {
        if (campType == null)
            return "未知";
        return switch (campType) {
            case 0 -> "鹅(好人)";
            case 1 -> "鸭(坏人)";
            case 2 -> "中立";
            default -> "未知";
        };
    }

    /**
     * 动作配置 DTO
     */
    @Data
    public static class ActionConfig {
        private String type; // KILL, ADD_STATUS, CHECK_ROLE

        // KILL config
        private Boolean penaltyOnGood; // penalty_on_good

        // ADD_STATUS config
        private String effectKey; // effect_key
        private Integer duration;

        // CHECK_ROLE config
        private String returnType; // return_type
    }
}
