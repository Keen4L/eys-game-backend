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
 * 魔术鹅处理器
 * 角色：魔术鹅(12)
 *
 * 逻辑：夜间置换两个人的生命状态。
 * 例如：置换 A 和 B。
 * 如果 A 本回合死亡，则 A 复活，B 替死。
 * 如果 B 本回合死亡，则 B 复活，A 替死。
 *
 * 实现方式：
 * 施加 LIFE_SWAPPED 状态标记，记录置换对象。
 * 结算阶段（Stage Change or Player Death Event）需要处理这个标记。
 * 本 Handler 只负责施加标记。
 *
 * @author EYS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MagicHandler implements SkillHandler {

    private final GaGamePlayerService gamePlayerService;
    private final GaPlayerStatusService playerStatusService;

    @Override
    public String getHandlerKey() {
        return "MagicHandler";
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.PLAYER;
    }

    @Override
    public SkillResult execute(SkillContext context) {
        List<Long> targetIds = context.getTargetPlayerIds();
        if (targetIds == null || targetIds.size() != 2) {
            return SkillResult.fail("必须选择两名玩家进行置换");
        }

        Long targetId1 = targetIds.get(0);
        Long targetId2 = targetIds.get(1);

        GaGamePlayer target1 = gamePlayerService.getById(targetId1);
        GaGamePlayer target2 = gamePlayerService.getById(targetId2);

        if (target1 == null || target2 == null) {
            return SkillResult.fail("目标玩家不存在");
        }

        // 施加置换状态
        // 这里的逻辑是：给两个玩家都加上 LIFE_SWAPPED 状态，且 Value 指向对方 ID
        // 需要 GaPlayerStatusService 支持存储 Extra Data 或者通过 Effect Value 存储
        // 假设 activeEffects 是 Map<String, Object>，Value 可以是对方 ID

        // 目前 hasEffect 只检查 Key。我们需要 addEffect 支持 value。
        // 假设 addEffect 的 duration 参数被重载或扩展，或者我们存 string value。
        // 由于接口限制，我们可能需暂存在 Redis 或 扩展 status 字段。
        // 简化版本：只标记 LIFE_SWAPPED。结算逻辑需要去查 ActionLog 知道换了谁？
        // 或者：User A status has "LIFE_SWAPPED:UserB_ID"

        // 既然 activeEffects 是 JSON，我们可以存 complex object。
        // 但 addEffect(Long playerId, String effectKey, int duration) 只能存 duration。
        // 让我们扩展一下，或者暂时利用 ActionLog 在结算时回溯？
        // 不，结算时查状态最快。
        // 我们可以构造一个特殊的 Key： "LIFE_SWAPPED_" + targetId

        playerStatusService.addEffect(targetId1, GameEffectConstant.LIFE_SWAPPED + "_" + targetId2, 1);
        playerStatusService.addEffect(targetId2, GameEffectConstant.LIFE_SWAPPED + "_" + targetId1, 1);

        GaGamePlayer actor = context.getActor();
        String dmNote = String.format("魔术鹅(玩家%d) 置换了 玩家%d 和 玩家%d 的生命",
                actor.getSeatNo(), target1.getSeatNo(), target2.getSeatNo());
        String publicNote = String.format("玩家%d 使用了技能", actor.getSeatNo());

        log.info("MagicHandler 执行: actor={}, target1={}, target2={}",
                actor.getId(), targetId1, targetId2);

        return SkillResult.success(dmNote, publicNote);
    }
}
