package com.eys.engine.skill;

import com.eys.common.constant.InteractionType;

/**
 * 技能处理器接口（策略模式）
 * 所有技能都通过此接口实现：校验、执行、反馈
 *
 * @author EYS
 */
public interface SkillHandler {

    /**
     * 获取技能交互类型（决定前端弹什么窗）
     *
     * @return InteractionType (PLAYER, PLAYER_ROLE, NONE...)
     */
    InteractionType getInteractionType();

    /**
     * 执行技能核心逻辑
     *
     * @param context 技能上下文（发起者、目标、配置参数、当前局势）
     * @return 技能执行结果
     */
    SkillResult execute(SkillContext context);

    /**
     * 获取处理器标识（用于工厂模式查找 Bean）
     *
     * @return handler_key，如 "StandardKillHandler"
     */
    String getHandlerKey();
}
