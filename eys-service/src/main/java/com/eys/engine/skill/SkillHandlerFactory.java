package com.eys.engine.skill;

import com.eys.engine.skill.handler.GeneralSkillHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 技能处理器工厂 (重构版)
 * 简化为直接返回 GeneralSkillHandler
 *
 * @author EYS
 */
@Slf4j
@Component
public class SkillHandlerFactory {

    @Autowired
    private GeneralSkillHandler generalSkillHandler;

    /**
     * 初始化日志
     */
    @PostConstruct
    public void init() {
        log.info("技能处理器工厂初始化完成（重构版）：仅使用 GeneralSkillHandler");
    }

    /**
     * 根据 behaviorType 获取处理器
     * 重构后统一使用 GeneralSkillHandler
     *
     * @param behaviorType 行为类型 (LOG/TAG/QUERY)
     * @return SkillHandler 实例
     */
    public SkillHandler getHandler(String behaviorType) {
        // 重构后所有技能都由 GeneralSkillHandler 处理
        // behaviorType 仅用于日志和调试
        log.debug("获取技能处理器: behaviorType={}", behaviorType);
        return generalSkillHandler;
    }

    /**
     * 获取默认处理器
     */
    public SkillHandler getDefaultHandler() {
        return generalSkillHandler;
    }
}
