package com.eys.engine.skill;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.eys.common.exception.BizException;
import com.eys.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 技能处理器工厂
 * 根据 JSON 配置中的 handler_key 查找对应的 Spring Bean
 *
 * @author EYS
 */
@Slf4j
@Component
public class SkillHandlerFactory {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 处理器缓存：handler_key -> SkillHandler
     */
    private final Map<String, SkillHandler> handlerMap = new HashMap<>();

    /**
     * 初始化：扫描所有 SkillHandler Bean 并注册
     */
    @PostConstruct
    public void init() {
        Map<String, SkillHandler> beans = applicationContext.getBeansOfType(SkillHandler.class);
        beans.forEach((beanName, handler) -> {
            String key = handler.getHandlerKey();
            handlerMap.put(key, handler);
            log.info("注册技能处理器: {} -> {}", key, handler.getClass().getSimpleName());
        });
        log.info("技能处理器工厂初始化完成，共注册 {} 个处理器", handlerMap.size());
    }

    /**
     * 根据 handler_key 获取处理器
     *
     * @param handlerKey 处理器标识
     * @return SkillHandler 实例
     */
    public SkillHandler getHandler(String handlerKey) {
        SkillHandler handler = handlerMap.get(handlerKey);
        if (handler == null) {
            throw new BizException(ResultCode.SKILL_NOT_AVAILABLE, "未找到技能处理器: " + handlerKey);
        }
        return handler;
    }

    /**
     * 从技能 JSON 配置中解析 handler_key 并获取处理器
     *
     * @param skillLogicJson 技能逻辑 JSON 字符串
     * @return SkillHandler 实例
     */
    public SkillHandler getHandlerFromJson(String skillLogicJson) {
        if (skillLogicJson == null || skillLogicJson.isBlank()) {
            throw new BizException(ResultCode.SKILL_NOT_AVAILABLE, "技能配置为空");
        }

        try {
            JSONObject json = JSON.parseObject(skillLogicJson);
            String handlerKey = json.getString("handler_key");
            if (handlerKey == null || handlerKey.isBlank()) {
                throw new BizException(ResultCode.SKILL_NOT_AVAILABLE, "缺少 handler_key 配置");
            }
            return getHandler(handlerKey);
        } catch (Exception e) {
            log.error("解析技能配置失败: {}", e.getMessage());
            throw new BizException(ResultCode.SKILL_NOT_AVAILABLE, "技能配置解析失败");
        }
    }

    /**
     * 从技能 JSON 配置中提取 config 部分
     *
     * @param skillLogicJson 技能逻辑 JSON 字符串
     * @return config Map
     */
    public Map<String, Object> extractConfig(String skillLogicJson) {
        if (skillLogicJson == null || skillLogicJson.isBlank()) {
            return new HashMap<>();
        }

        try {
            JSONObject json = JSON.parseObject(skillLogicJson);
            JSONObject config = json.getJSONObject("config");
            if (config != null) {
                return config.to(new com.alibaba.fastjson2.TypeReference<Map<String, Object>>() {
                });
            }
            return new HashMap<>();
        } catch (Exception e) {
            log.warn("提取技能 config 失败: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 检查是否存在指定处理器
     */
    public boolean hasHandler(String handlerKey) {
        return handlerMap.containsKey(handlerKey);
    }
}
