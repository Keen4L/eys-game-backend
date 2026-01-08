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

        // 检查目标是否是明牌状态（决斗鹅发动技能后明牌，不可被刺客刺）
        if (playerStatusService.hasEffect(targetId, GameEffectConstant.REVEALED)) {
            return SkillResult.fail("目标已明牌，无法刺杀");
        }

        GaGamePlayer actor = context.getActor();

        // 获取实际角色
        Long actualRoleId = targetPlayer.getCurrRoleId();
        Long guessRoleId = context.getGuessRoleId();

        CfgRole actualRole = roleService.getById(actualRoleId);
        CfgRole guessRole = roleService.getById(guessRoleId);

        String actualRoleName = actualRole != null ? actualRole.getName() : "未知";
        String guessRoleName = guessRole != null ? guessRole.getName() : "未知";

        // 判定是否猜中
        boolean isCorrect = guessRoleId.equals(actualRoleId);

        if (isCorrect) {
            // 猜对：目标死亡
            targetStatus.setIsAlive(0);
            targetStatus.setDeathRound(context.getCurrentRound());
            targetStatus.setDeathStage(context.getCurrentStage());
            playerStatusService.updateById(targetStatus);

            String dmNote = String.format("刺客(玩家%d) 刺杀成功！目标(玩家%d) 是【%s】",
                    actor.getSeatNo(), targetPlayer.getSeatNo(), actualRoleName);
            String publicNote = String.format("玩家%d 使用了技能【刺杀】", actor.getSeatNo());

            log.info("AssassinHandler 刺杀成功: actor={}, target={}, role={}",
                    actor.getId(), targetId, actualRoleName);

            return SkillResult.kill(targetId, dmNote, publicNote);

        } else {
            // 猜错：刺客自己死亡
            GaPlayerStatus actorStatus = playerStatusService.getById(actor.getId());
            if (actorStatus != null) {
                actorStatus.setIsAlive(0);
                actorStatus.setDeathRound(context.getCurrentRound());
                actorStatus.setDeathStage(context.getCurrentStage());
                playerStatusService.updateById(actorStatus);
            }

            String dmNote = String.format("刺客(玩家%d) 刺杀失败！猜测【%s】，实际是【%s】，刺客自己死亡",
                    actor.getSeatNo(), guessRoleName, actualRoleName);
            String publicNote = String.format("玩家%d 使用了技能【刺杀】，失败", actor.getSeatNo());

            log.info("AssassinHandler 刺杀失败: actor={}, target={}, guess={}, actual={}",
                    actor.getId(), targetId, guessRoleName, actualRoleName);

            return SkillResult.actorDeath(dmNote, publicNote);
        }
    }
}
