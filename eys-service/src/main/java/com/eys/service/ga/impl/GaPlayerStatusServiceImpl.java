package com.eys.service.ga.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.ga.GaPlayerStatusMapper;
import com.eys.model.entity.ga.GaPlayerStatus;
import com.eys.service.ga.GaPlayerStatusService;
import org.springframework.stereotype.Service;

/**
 * 玩家状态 Service 实现
 *
 * @author EYS
 */
@Service
public class GaPlayerStatusServiceImpl extends ServiceImpl<GaPlayerStatusMapper, GaPlayerStatus>
        implements GaPlayerStatusService {
}
