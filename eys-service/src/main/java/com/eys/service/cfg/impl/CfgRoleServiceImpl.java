package com.eys.service.cfg.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.cfg.CfgRoleMapper;
import com.eys.model.entity.cfg.CfgRole;
import com.eys.service.cfg.CfgRoleService;
import org.springframework.stereotype.Service;

/**
 * 角色配置 Service 实现
 *
 * @author EYS
 */
@Service
public class CfgRoleServiceImpl extends ServiceImpl<CfgRoleMapper, CfgRole> implements CfgRoleService {
}
