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
}
