package com.eys.service.ga.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.ga.GaPlayerStatusMapper;
import com.eys.model.entity.ga.GaPlayerStatus;
import com.eys.service.ga.GaPlayerStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 玩家状态 Service 实现
 *
 * @author EYS
 */
@Slf4j
@Service
public class GaPlayerStatusServiceImpl extends ServiceImpl<GaPlayerStatusMapper, GaPlayerStatus>
                implements GaPlayerStatusService {

        @Override
        public boolean hasEffect(Long gamePlayerId, String effectType) {
                GaPlayerStatus status = getById(gamePlayerId);
                if (status == null || status.getActiveEffects() == null) {
                        return false;
                }

                try {
                        JSONArray effects = JSON.parseArray(status.getActiveEffects());
                        for (int i = 0; i < effects.size(); i++) {
                                JSONObject effect = effects.getJSONObject(i);
                                if (effectType.equals(effect.getString("type"))) {
                                        return true;
                                }
                        }
                } catch (Exception e) {
                        log.warn("解析玩家状态效果失败: {}", e.getMessage());
                }
                return false;
        }

        @Override
        public void addEffect(Long gamePlayerId, String effectType, int duration) {
                GaPlayerStatus status = getById(gamePlayerId);
                if (status == null) {
                        return;
                }

                JSONArray effects;
                try {
                        effects = status.getActiveEffects() != null
                                        ? JSON.parseArray(status.getActiveEffects())
                                        : new JSONArray();
                } catch (Exception e) {
                        effects = new JSONArray();
                }

                // 检查是否已存在
                for (int i = 0; i < effects.size(); i++) {
                        JSONObject effect = effects.getJSONObject(i);
                        if (effectType.equals(effect.getString("type"))) {
                                // 已存在，更新持续时间
                                effect.put("duration", duration);
                                status.setActiveEffects(effects.toJSONString());
                                updateById(status);
                                return;
                        }
                }

                // 添加新效果
                JSONObject newEffect = new JSONObject();
                newEffect.put("type", effectType);
                newEffect.put("duration", duration);
                effects.add(newEffect);

                status.setActiveEffects(effects.toJSONString());
                updateById(status);
        }

        @Override
        public void removeEffect(Long gamePlayerId, String effectType) {
                GaPlayerStatus status = getById(gamePlayerId);
                if (status == null || status.getActiveEffects() == null) {
                        return;
                }

                try {
                        JSONArray effects = JSON.parseArray(status.getActiveEffects());
                        JSONArray newEffects = new JSONArray();
                        for (int i = 0; i < effects.size(); i++) {
                                JSONObject effect = effects.getJSONObject(i);
                                if (!effectType.equals(effect.getString("type"))) {
                                        newEffects.add(effect);
                                }
                        }

                        status.setActiveEffects(newEffects.isEmpty() ? null : newEffects.toJSONString());
                        updateById(status);
                } catch (Exception e) {
                        log.warn("移除玩家状态效果失败: {}", e.getMessage());
                }
        }
}
