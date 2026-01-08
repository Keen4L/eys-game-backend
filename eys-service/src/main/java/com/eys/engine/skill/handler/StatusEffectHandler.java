package com.eys.engine.skill.handler;

import com.eys.common.constant.InteractionType;
import com.eys.engine.skill.SkillContext;
import com.eys.engine.skill.SkillHandler;
import com.eys.engine.skill.SkillResult;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.service.ga.GaGamePlayerService;
import com.eys.service.ga.GaPlayerStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 状态效果处理器
 * 适用角色：禁言鸭、保镖鹅、忍者鸭、恋爱脑鹅、梦魇鸭 等
 *
 * JSON 配置参数：
 * - effect_key: string - 状态效果 Key（SILENCED, PROTECTED, NIGHTMARED 等）
 * - duration: int - 持续轮数（-1 表示永久）
 * - can_stack: boolean - 是否可叠加屯起来（禁言鸭）
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatusEffectHandler implements SkillHandler {

    private final GaGamePlayerService gamePlayerService;
    private final GaPlayerStatusService playerStatusService;

    @Override
    public String getHandlerKey() {
        return "StatusEffectHandler";
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
        String effectKey = (String) config.get("effect_key");
        if (effectKey == null || effectKey.isBlank()) {
            return SkillResult.fail("未配置 effect_key");
        }

        int duration = config.get("duration") != null
                ? ((Number) config.get("duration")).intValue()
                : 1;

        // 给目标添加状态效果
        playerStatusService.addEffect(targetId, effectKey, duration);

        String dmNote = String.format("玩家%d 对 玩家%d 施加了【%s】效果",
                actor.getSeatNo(), targetPlayer.getSeatNo(), effectKey);
        String publicNote = String.format("玩家%d 使用了技能【%s】",
                actor.getSeatNo(), context.getSkill().getName());

        log.info("StatusEffectHandler 执行: actor={}, target={}, effect={}, duration={}",
                actor.getId(), targetId, effectKey, duration);

        return SkillResult.status(targetId, effectKey, dmNote, publicNote);
    }
}
