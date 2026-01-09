package com.eys.engine.skill.handler;

import com.eys.common.constant.InteractionType;
import com.eys.engine.skill.SkillContext;
import com.eys.engine.skill.SkillHandler;
import com.eys.engine.skill.SkillResult;
import com.eys.model.entity.cfg.CfgRole;
import com.eys.model.entity.cfg.CfgSkill;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.service.cfg.CfgRoleService;
import com.eys.service.ga.GaGamePlayerService;
import com.eys.service.ga.GaPlayerStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用技能处理器 (重构版)
 * 处理所有技能，根据 behaviorType 分支执行：
 * - LOG: 纯记录日志
 * - TAG: 记录+贴标签
 * - QUERY: 记录+查验反馈
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
        CfgSkill skill = context.getSkill();
        String behaviorType = skill.getBehaviorType();

        if (behaviorType == null || behaviorType.isBlank()) {
            behaviorType = "LOG"; // 默认纯记录
        }

        GaGamePlayer actor = context.getActor();
        List<Long> targetIds = context.getTargetPlayerIds();
        Long targetId = (targetIds != null && !targetIds.isEmpty()) ? targetIds.get(0) : null;

        // 构建日志
        StringBuilder dmNoteBuilder = new StringBuilder();
        StringBuilder publicNoteBuilder = new StringBuilder();

        dmNoteBuilder.append(String.format("玩家%d 使用了 [%s]", actor.getSeatNo(), skill.getName()));
        publicNoteBuilder.append(String.format("玩家%d 使用了技能", actor.getSeatNo()));

        // 分支处理
        switch (behaviorType.toUpperCase()) {
            case "TAG":
                return handleTag(context, skill, targetId, dmNoteBuilder, publicNoteBuilder);
            case "QUERY":
                return handleQuery(context, skill, targetId, dmNoteBuilder, publicNoteBuilder);
            case "LOG":
            default:
                return handleLog(context, skill, targetId, dmNoteBuilder, publicNoteBuilder);
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 处理 LOG 类型 (纯记录)
     */
    private SkillResult handleLog(SkillContext context, CfgSkill skill, Long targetId,
            StringBuilder dmNoteBuilder, StringBuilder publicNoteBuilder) {

        if (targetId != null) {
            GaGamePlayer target = gamePlayerService.getById(targetId);
            if (target != null) {
                dmNoteBuilder.append(String.format(" -> 玩家%d", target.getSeatNo()));
            }
        }

        // 刺客猜身份特殊处理
        if (context.getGuessRoleId() != null) {
            CfgRole guessRole = roleService.getById(context.getGuessRoleId());
            String roleName = guessRole != null ? guessRole.getName() : "未知";
            dmNoteBuilder.append(String.format(" (猜测身份: %s)", roleName));
        }

        log.info("GeneralHandler.LOG: actor={}, target={}, skill={}",
                context.getActor().getId(), targetId, skill.getName());

        return SkillResult.success(dmNoteBuilder.toString(), publicNoteBuilder.toString());
    }

    /**
     * 处理 TAG 类型 (记录+贴标签)
     */
    private SkillResult handleTag(SkillContext context, CfgSkill skill, Long targetId,
            StringBuilder dmNoteBuilder, StringBuilder publicNoteBuilder) {

        if (targetId == null) {
            return SkillResult.fail("未选择目标");
        }

        GaGamePlayer target = gamePlayerService.getById(targetId);
        if (target == null) {
            return SkillResult.fail("目标无效");
        }

        // 获取标签配置
        String tagName = skill.getName(); // 技能名即为标签名
        String expiryRule = skill.getTagExpiryRule() != null ? skill.getTagExpiryRule() : "NEXT_ROUND";
        String restriction = skill.getTagRestriction() != null ? skill.getTagRestriction() : "NONE";

        // 计算 duration (轮次)
        int duration = "PERMANENT".equals(expiryRule) ? -1 : 1;
        if ("PELICAN".equals(expiryRule)) {
            duration = -1; // 鹈鹕存活则在，由系统特殊处理
        }

        // 添加状态效果
        playerStatusService.addEffect(targetId, tagName, duration);

        dmNoteBuilder.append(String.format(" -> 玩家%d 被施加 [%s] 状态", target.getSeatNo(), tagName));

        log.info("GeneralHandler.TAG: actor={}, target={}, tag={}, expiry={}",
                context.getActor().getId(), targetId, tagName, expiryRule);

        return SkillResult.status(targetId, tagName, dmNoteBuilder.toString(), publicNoteBuilder.toString());
    }

    /**
     * 处理 QUERY 类型 (记录+查验反馈)
     */
    private SkillResult handleQuery(SkillContext context, CfgSkill skill, Long targetId,
            StringBuilder dmNoteBuilder, StringBuilder publicNoteBuilder) {

        String queryField = skill.getQueryField();
        if (queryField == null || queryField.isBlank()) {
            queryField = "ROLE_ID"; // 默认查角色
        }

        Map<String, Object> extraData = new HashMap<>();
        String resultDesc;

        if (targetId == null) {
            // 无目标查询 (如询问鸭数)
            if ("DUCK_COUNT".equals(queryField)) {
                // TODO: 实现鸭子数量查询
                resultDesc = "场上鸭子数量由DM告知";
                extraData.put("query_type", "DUCK_COUNT");
            } else {
                resultDesc = "无目标查询";
            }
        } else {
            GaGamePlayer target = gamePlayerService.getById(targetId);
            if (target == null) {
                return SkillResult.fail("目标无效");
            }

            CfgRole targetRole = roleService.getById(target.getCurrRoleId());

            switch (queryField.toUpperCase()) {
                case "ROLE_ID":
                    resultDesc = targetRole != null ? "身份是【" + targetRole.getName() + "】" : "未知身份";
                    extraData.put("role_name", targetRole != null ? targetRole.getName() : null);
                    break;
                case "CAMP_TYPE":
                    String campName = getCampName(targetRole != null ? targetRole.getCampType() : null);
                    resultDesc = "阵营是【" + campName + "】";
                    extraData.put("camp", campName);
                    break;
                case "IS_DUCK":
                    boolean isDuck = targetRole != null && targetRole.getCampType() == 1;
                    resultDesc = isDuck ? "是鸭子！" : "不是鸭子";
                    extraData.put("is_duck", isDuck);
                    break;
                default:
                    resultDesc = "未知查验类型";
            }

            dmNoteBuilder.append(String.format(" -> 玩家%d: %s", target.getSeatNo(), resultDesc));
        }

        log.info("GeneralHandler.QUERY: actor={}, target={}, queryField={}",
                context.getActor().getId(), targetId, queryField);

        SkillResult result = SkillResult.reveal(targetId,
                targetId != null ? gamePlayerService.getById(targetId).getCurrRoleId() : null,
                dmNoteBuilder.toString(), publicNoteBuilder.toString());
        result.setExtraData(extraData);
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
}
