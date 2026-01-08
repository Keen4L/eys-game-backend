package com.eys.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.sys.SysUserStatsMapper;
import com.eys.model.entity.sys.SysUserStats;
import com.eys.service.sys.SysUserStatsService;
import org.springframework.stereotype.Service;

/**
 * 用户总战绩 Service 实现
 *
 * @author EYS
 */
@Service
public class SysUserStatsServiceImpl extends ServiceImpl<SysUserStatsMapper, SysUserStats>
        implements SysUserStatsService {
}
