package com.eys.engine.skill.handler;

import com.eys.common.constant.InteractionType;
import com.eys.engine.skill.SkillContext;
import com.eys.engine.skill.SkillHandler;
import com.eys.engine.skill.SkillResult;
import com.eys.model.entity.cfg.CfgRole;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.service.cfg.CfgRoleService;
import com.eys.service.ga.GaGamePlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 查验反馈处理器
 * 适用角色：先知鹅、大白鹅、等式鹅 等
 *
 * JSON 配置参数：
 * - return_type: string - 返回类型 "ROLE"(查身份) / "CAMP"(查阵营) / "DUCK_CHECK"(是否是鸭)
 * - cost_gold: int - 消耗金币（先知鹅 10金币）
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InvestigationHandler implements SkillHandler {

    private final GaGamePlayerService gamePlayerService;
    private final CfgRoleService roleService;

    @Override
    public String getHandlerKey() {
        return "InvestigationHandler";
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.PLAYER;
    }

    @Override
    public SkillResult execute(SkillContext context) {
        // 校验目标
        if (context.getTargetPlayerIds() == null || context.getTargetPlayerIds().isEmpty()) {
            return SkillResult.fail("未选择目标");
        }

        Long targetId = context.getTargetPlayerIds().get(0);
        GaGamePlayer targetPlayer = gamePlayerService.getById(targetId);
        if (targetPlayer == null) {
            return SkillResult.fail("目标玩家不存在");
        }

        Map<String, Object> config = context.getConfig();
        GaGamePlayer actor = context.getActor();

        // 读取配置
        String returnType = (String) config.getOrDefault("return_type", "ROLE");

        // 获取目标角色信息
        Long targetRoleId = targetPlayer.getCurrRoleId();
        CfgRole targetRole = roleService.getById(targetRoleId);
        String targetRoleName = targetRole != null ? targetRole.getName() : "未知";
        Integer targetCampType = targetRole != null ? targetRole.getCampType() : null;
        String targetCamp = getCampName(targetCampType);

        // 构建查验结果
        Map<String, Object> revealData = new HashMap<>();
        String resultDesc;

        switch (returnType) {
            case "ROLE":
                // 查身份：返回具体角色名
                revealData.put("role_id", targetRoleId);
                revealData.put("role_name", targetRoleName);
                resultDesc = "身份是【" + targetRoleName + "】";
                break;

            case "CAMP":
                // 查阵营：只返回阵营
                revealData.put("camp", targetCamp);
                resultDesc = "阵营是【" + targetCamp + "】";
                break;

            case "DUCK_CHECK":
                // 大白鹅：只判断是否是鸭（坏人阵营 campType=1）
                boolean isDuck = targetCampType != null && targetCampType == 1;
                revealData.put("is_duck", isDuck);
                resultDesc = isDuck ? "是鸭子！" : "不是鸭子";
                break;

            default:
                resultDesc = "未知查验类型";
        }

        String dmNote = String.format("玩家%d 查验 玩家%d：%s",
                actor.getSeatNo(), targetPlayer.getSeatNo(), resultDesc);
        String publicNote = String.format("玩家%d 使用了技能【%s】",
                actor.getSeatNo(), context.getSkill().getName());

        log.info("InvestigationHandler 执行: actor={}, target={}, result={}",
                actor.getId(), targetId, resultDesc);

        SkillResult result = SkillResult.reveal(targetId, targetRoleId, dmNote, publicNote);
        result.setExtraData(revealData);
        return result;
    }

    /**
     * 阵营类型转名称
     * 0-鹅(好人), 1-鸭(坏人), 2-中立
     */
    private String getCampName(Integer campType) {
        if (campType == null) {
            return "未知";
        }
        return switch (campType) {
            case 0 -> "鹅(好人)";
            case 1 -> "鸭(坏人)";
            case 2 -> "中立";
            default -> "未知";
        };
    }
}
