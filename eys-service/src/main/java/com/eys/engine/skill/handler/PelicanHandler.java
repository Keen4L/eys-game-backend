package com.eys.engine.skill.handler;

import com.eys.common.constant.GameEffectConstant;
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

import java.util.List;

/**
 * 鹈鹕处理器（特殊角色专用）
 * 角色：鹈鹕(18)
 *
 * 逻辑：每天晚上可以最多吃两个人，累计吃四个人胜利
 * 被吞者不死但消失（SWALLOWED 状态）
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PelicanHandler implements SkillHandler {

    private final GaGamePlayerService gamePlayerService;
    private final GaPlayerStatusService playerStatusService;

    @Override
    public String getHandlerKey() {
        return "PelicanHandler";
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.PLAYER;
    }

    @Override
    public SkillResult execute(SkillContext context) {
        List<Long> targetIds = context.getTargetPlayerIds();
        if (targetIds == null || targetIds.isEmpty()) {
            return SkillResult.fail("未选择目标");
        }

        // 鹈鹕最多选两个目标
        if (targetIds.size() > 2) {
            return SkillResult.fail("最多选择两个目标");
        }

        GaGamePlayer actor = context.getActor();
        StringBuilder dmNoteBuilder = new StringBuilder();
        dmNoteBuilder.append(String.format("鹈鹕(玩家%d) 吞噬了：", actor.getSeatNo()));

        int swallowCount = 0;

        for (Long targetId : targetIds) {
            GaGamePlayer targetPlayer = gamePlayerService.getById(targetId);
            if (targetPlayer == null) {
                continue;
            }

            GaPlayerStatus targetStatus = playerStatusService.getById(targetId);
            if (targetStatus == null || targetStatus.getIsAlive() != 1) {
                continue;
            }

            // 检查是否已被吞
            if (playerStatusService.hasEffect(targetId, GameEffectConstant.SWALLOWED)) {
                continue;
            }

            // 施加 SWALLOWED 状态（永久，直到鹈鹕死）
            playerStatusService.addEffect(targetId, GameEffectConstant.SWALLOWED, -1);
            swallowCount++;

            dmNoteBuilder.append(String.format("玩家%d ", targetPlayer.getSeatNo()));
        }

        if (swallowCount == 0) {
            return SkillResult.fail("没有有效目标");
        }

        String dmNote = dmNoteBuilder.toString();
        String publicNote = String.format("玩家%d 使用了技能", actor.getSeatNo());

        log.info("PelicanHandler 执行: actor={}, swallowed={} 人",
                actor.getId(), swallowCount);

        // TODO: 检查累计吞噬数量是否达到4人，触发鹈鹕胜利

        return SkillResult.status(targetIds.get(0), GameEffectConstant.SWALLOWED, dmNote, publicNote);
    }
}
