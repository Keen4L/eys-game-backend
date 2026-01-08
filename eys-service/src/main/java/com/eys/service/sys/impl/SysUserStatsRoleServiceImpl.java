package com.eys.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.sys.SysUserStatsRoleMapper;
import com.eys.model.entity.sys.SysUserStatsRole;
import com.eys.service.sys.SysUserStatsRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户角色战绩明细 Service 实现
 *
 * @author EYS
 */
@Service
public class SysUserStatsRoleServiceImpl extends ServiceImpl<SysUserStatsRoleMapper, SysUserStatsRole>
        implements SysUserStatsRoleService {
}
