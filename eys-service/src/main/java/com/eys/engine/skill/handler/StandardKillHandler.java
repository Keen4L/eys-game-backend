package com.eys.engine.skill.handler;

import com.eys.common.constant.InteractionType;
import com.eys.engine.skill.SkillContext;
import com.eys.engine.skill.SkillHandler;
import com.eys.engine.skill.SkillResult;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.model.entity.ga.GaPlayerStatus;
import com.eys.service.ga.GaGamePlayerService;
import com.eys.service.ga.GaPlayerStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 通用击杀处理器
 * 适用角色：警长鹅、正义鹅、猎鹰、鸭王、火种鸭 等
 *
 * JSON 配置参数：
 * - penalty_on_good: boolean - 杀好人自己死（警长）
 * - limit: int - 全局限制次数
 * - bring_on_death: boolean - 死亡带走目标（鸭王）
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StandardKillHandler implements SkillHandler {

    private final GaGamePlayerService gamePlayerService;
    private final GaPlayerStatusService playerStatusService;

    @Override
    public String getHandlerKey() {
        return "StandardKillHandler";
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

        GaPlayerStatus targetStatus = playerStatusService.getById(targetId);
        if (targetStatus == null || targetStatus.getIsAlive() != 1) {
            return SkillResult.fail("目标已死亡");
        }

        Map<String, Object> config = context.getConfig();
        GaGamePlayer actor = context.getActor();

        // 读取配置
        boolean penaltyOnGood = Boolean.TRUE.equals(config.get("penalty_on_good"));

        // 检查是否杀好人（警长逻辑）
        if (penaltyOnGood) {
            // 获取目标阵营
            Long targetRoleId = targetPlayer.getCurrRoleId();
            // TODO: 查询角色阵营，如果是好人则自己也死
            // 暂时简化处理：设置标记，由 Service 层判断
        }

        // 执行击杀
        targetStatus.setIsAlive(0);
        targetStatus.setDeathRound(context.getCurrentRound());
        targetStatus.setDeathStage(context.getCurrentStage());
        playerStatusService.updateById(targetStatus);

        String dmNote = String.format("玩家%d 使用【%s】击杀了 玩家%d",
                actor.getSeatNo(), context.getSkill().getName(), targetPlayer.getSeatNo());
        String publicNote = String.format("玩家%d 使用了技能【%s】",
                actor.getSeatNo(), context.getSkill().getName());

        log.info("StandardKillHandler 执行: actor={}, target={}, skill={}",
                actor.getId(), targetId, context.getSkill().getName());

        return SkillResult.kill(targetId, dmNote, publicNote);
    }
}
