package com.eys.service.ga.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.ga.GaActionLogMapper;
import com.eys.model.entity.ga.GaActionLog;
import com.eys.service.ga.GaActionLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 动作流水 Service 实现
 *
 * @author EYS
 */
@Service
public class GaActionLogServiceImpl extends ServiceImpl<GaActionLogMapper, GaActionLog>
                implements GaActionLogService {

        @Override
        public List<GaActionLog> listByGameAndRound(Long gameId, int roundNo) {
                return list(new LambdaQueryWrapper<GaActionLog>()
                                .eq(GaActionLog::getGameId, gameId)
                                .eq(GaActionLog::getRoundNo, roundNo));
        }
}
