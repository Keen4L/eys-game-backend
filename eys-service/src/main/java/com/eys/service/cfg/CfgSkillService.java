package com.eys.service.cfg;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eys.model.entity.cfg.CfgSkill;

import java.util.List;

/**
 * 技能配置 Service 接口
 *
 * @author EYS
 */
public interface CfgSkillService extends IService<CfgSkill> {

    /**
     * 根据角色ID获取技能列表
     */
    List<CfgSkill> listByRoleId(Long roleId);

    /**
     * 删除角色下的所有技能
     */
    void deleteByRoleId(Long roleId);
}
