package com.eys.engine.skill;

/**
 * 技能处理器接口（策略模式）
 * 所有技能都通过此接口实现执行逻辑
 *
 * @author EYS
 */
public interface SkillHandler {

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
     * @return handler_key，如 "GeneralSkillHandler"
     */
    String getHandlerKey();
}
