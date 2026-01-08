package com.eys.service.cfg.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.cfg.CfgSkillMapper;
import com.eys.model.entity.cfg.CfgSkill;
import com.eys.service.cfg.CfgSkillService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 技能配置 Service 实现
 *
 * @author EYS
 */
@Service
public class CfgSkillServiceImpl extends ServiceImpl<CfgSkillMapper, CfgSkill> implements CfgSkillService {

    @Override
    public List<CfgSkill> listByRoleId(Long roleId) {
        return list(new LambdaQueryWrapper<CfgSkill>()
                .eq(CfgSkill::getRoleId, roleId));
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        remove(new LambdaQueryWrapper<CfgSkill>()
                .eq(CfgSkill::getRoleId, roleId));
    }
}
