package com.eys.engine.skill.handler;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 刺客处理器
 * 适用角色：刺客鸭(23)
 *
 * 逻辑：猜对对方死，猜错自己死
 * 注意：决斗鹅明牌后不可被刺客刺
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AssassinHandler implements SkillHandler {

    private final GaGamePlayerService gamePlayerService;
    private final GaPlayerStatusService playerStatusService;
    private final CfgRoleService roleService;

    @Override
    public String getHandlerKey() {
        return "AssassinHandler";
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.PLAYER_ROLE;
    }

    @Override
    public SkillResult execute(SkillContext context) {
        // 校验目标
        if (context.getTargetPlayerIds() == null || context.getTargetPlayerIds().isEmpty()) {
            return SkillResult.fail("未选择目标");
        }

        if (context.getGuessRoleId() == null) {
            return SkillResult.fail("未猜测角色");
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

        // 检查目标是否是明牌状态（决斗鹅明牌后不可刺）
        if (playerStatusService.hasEffect(targetId, GameEffectConstant.REVEALED)) {
            return SkillResult.fail("目标已明牌，无法刺杀");
        }

        GaGamePlayer actor = context.getActor();
        Long guessRoleId = context.getGuessRoleId();
        Long actualRoleId = targetPlayer.getCurrRoleId();

        CfgRole actualRole = roleService.getById(actualRoleId);
        CfgRole guessRole = roleService.getById(guessRoleId);

        String actualRoleName = actualRole != null ? actualRole.getName() : "未知";
        String guessRoleName = guessRole != null ? guessRole.getName() : "未知";
        boolean isCorrect = guessRoleId.equals(actualRoleId);

        // 仅记录猜测结果，不执行生死判定
        String dmNote = String.format("刺客(玩家%d) 猜测 玩家%d 是【%s】，实际是【%s】 —— %s",
                actor.getSeatNo(), targetPlayer.getSeatNo(), guessRoleName, actualRoleName,
                isCorrect ? "✅ 猜对" : "❌ 猜错（刺客应死亡）");
        String publicNote = String.format("玩家%d 使用了技能【刺杀】", actor.getSeatNo());

        log.info("AssassinHandler 记录: actor={}, target={}, guess={}, actual={}, correct={}",
                actor.getId(), targetId, guessRoleName, actualRoleName, isCorrect);

        // 返回结果（包含猜测信息供 DM 裁决）
        SkillResult result = SkillResult.builder()
                .success(true)
                .targetPlayerId(targetId)
                .targetRoleId(actualRoleId)
                .dmNote(dmNote)
                .publicNote(publicNote)
                .effectType("ASSASSINATE")
                .actorDeath(false) // DM 根据日志自行决定谁死
                .build();

        // 在 extraData 中附加猜测结果供前端展示
        result.setExtraData(java.util.Map.of(
                "guess_role_id", guessRoleId,
                "actual_role_id", actualRoleId,
                "is_correct", isCorrect));

        return result;
    }
}
