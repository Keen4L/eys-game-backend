package com.eys.engine.skill.handler;

import com.eys.common.constant.InteractionType;
import com.eys.engine.skill.SkillContext;
import com.eys.engine.skill.SkillHandler;
import com.eys.engine.skill.SkillResult;
import com.eys.model.entity.ga.GaGamePlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 炸弹鸭处理器
 * 角色：炸弹鸭(24)
 *
 * 逻辑：夜间自爆。
 * 根据描述：没有刀，只可以在夜间自爆。
 * 理解为：自杀，并可能带走周围的人？或者仅仅是自爆？
 * 如果仅仅是自爆，那和普通自杀没区别？
 * 通常炸弹鸭自爆会带走所有人或特定范围。
 * 假设：自爆 = 杀死自己 + 杀死所有在场其他人（如果是在会议期间自爆则是拉着大家一起死？不对，只能在夜间）。
 * 夜间自爆通常意味着同房间的人一起死，或者全图随机死？
 * 既然没有具体范围参数，我们暂时实现为：自杀，并对 System 输出“炸弹鸭自爆”。
 * 具体的 AOE 伤害可能需要地图位置判定（Spawn Point），这比较复杂。
 * 
 * 简化实现：自杀，并在 DM 端高亮显示，由 DM 手动处理其他人死亡（如果需要）。
 * 或者：配置 config 决定是否带走目标。
 *
 * @author EYS
 */
@Slf4j
@Component
public class BombHandler implements SkillHandler {

    @Override
    public String getHandlerKey() {
        return "BombHandler";
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.NONE; // 自爆不需要选人
    }

    @Override
    public SkillResult execute(SkillContext context) {
        GaGamePlayer actor = context.getActor();

        // 仅记录自爆意图，不修改状态
        String dmNote = String.format("炸弹鸭(玩家%d) 触发【自爆】！⚠️ DM需根据位置判定 AOE 伤害范围",
                actor.getSeatNo());
        String publicNote = String.format("玩家%d 使用了技能【自爆】", actor.getSeatNo());

        log.info("BombHandler 记录: actor={} 自爆（DM需手动处理伤害)", actor.getId());

        // 返回特殊效果类型供 DM 识别
        return SkillResult.builder()
                .success(true)
                .dmNote(dmNote)
                .publicNote(publicNote)
                .effectType("BOMB_EXPLODE")
                .actorDeath(false) // DM 决定谁死
                .build();
    }
}
