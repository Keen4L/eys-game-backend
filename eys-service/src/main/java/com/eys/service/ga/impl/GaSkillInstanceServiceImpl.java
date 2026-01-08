package com.eys.service.ga.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.ga.GaSkillInstanceMapper;
import com.eys.model.entity.ga.GaSkillInstance;
import com.eys.service.ga.GaSkillInstanceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 技能实例 Service 实现
 *
 * @author EYS
 */
@Service
public class GaSkillInstanceServiceImpl extends ServiceImpl<GaSkillInstanceMapper, GaSkillInstance>
        implements GaSkillInstanceService {

    @Override
    public List<GaSkillInstance> listByGamePlayerId(Long gamePlayerId) {
        return list(new LambdaQueryWrapper<GaSkillInstance>()
                .eq(GaSkillInstance::getGamePlayerId, gamePlayerId));
    }

    @Override
    public void deductUsage(Long instanceId) {
        GaSkillInstance current = getById(instanceId);
        if (current == null || current.getRemainCount() <= 0) {
            return;
        }

        if (current.getGroupId() != null) {
            // 技能组共享次数：必须更新全组
            lambdaUpdate()
                    .eq(GaSkillInstance::getGamePlayerId, current.getGamePlayerId())
                    .eq(GaSkillInstance::getGroupId, current.getGroupId())
                    .gt(GaSkillInstance::getRemainCount, 0)
                    .setSql("remain_count = remain_count - 1")
                    .update();
        } else {
            // 只更新自己
            current.setRemainCount(current.getRemainCount() - 1);
            updateById(current);
        }
    }
}
