package com.eys.service.ga.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.ga.GaVoteLogMapper;
import com.eys.model.entity.ga.GaVoteLog;
import com.eys.service.ga.GaVoteLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 投票记录 Service 实现
 *
 * @author EYS
 */
@Service
public class GaVoteLogServiceImpl extends ServiceImpl<GaVoteLogMapper, GaVoteLog>
        implements GaVoteLogService {

    @Override
    public List<GaVoteLog> listByGameAndRound(Long gameId, Integer roundNo) {
        return list(new LambdaQueryWrapper<GaVoteLog>()
                .eq(GaVoteLog::getGameId, gameId)
                .eq(GaVoteLog::getRoundNo, roundNo));
    }

    @Override
    public boolean hasVoted(Long gameId, Integer roundNo, Long voterId) {
        return count(new LambdaQueryWrapper<GaVoteLog>()
                .eq(GaVoteLog::getGameId, gameId)
                .eq(GaVoteLog::getRoundNo, roundNo)
                .eq(GaVoteLog::getVoterId, voterId)) > 0;
    }

    @Override
    public int countByGameAndRound(Long gameId, Integer roundNo) {
        return (int) count(new LambdaQueryWrapper<GaVoteLog>()
                .eq(GaVoteLog::getGameId, gameId)
                .eq(GaVoteLog::getRoundNo, roundNo));
    }
}
