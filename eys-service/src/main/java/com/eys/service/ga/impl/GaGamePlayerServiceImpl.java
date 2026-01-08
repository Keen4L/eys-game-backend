package com.eys.service.ga.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.ga.GaGamePlayerMapper;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.service.ga.GaGamePlayerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 对局玩家 Service 实现
 *
 * @author EYS
 */
@Service
public class GaGamePlayerServiceImpl extends ServiceImpl<GaGamePlayerMapper, GaGamePlayer>
        implements GaGamePlayerService {

    @Override
    public List<GaGamePlayer> listByGameId(Long gameId) {
        return list(new LambdaQueryWrapper<GaGamePlayer>()
                .eq(GaGamePlayer::getGameId, gameId)
                .orderByAsc(GaGamePlayer::getSeatNo));
    }

    @Override
    public GaGamePlayer getByGameAndUser(Long gameId, Long userId) {
        return getOne(new LambdaQueryWrapper<GaGamePlayer>()
                .eq(GaGamePlayer::getGameId, gameId)
                .eq(GaGamePlayer::getUserId, userId));
    }

    @Override
    public int countByGameId(Long gameId) {
        return (int) count(new LambdaQueryWrapper<GaGamePlayer>()
                .eq(GaGamePlayer::getGameId, gameId));
    }

    @Override
    public int getNextSeatNo(Long gameId) {
        Integer maxSeat = baseMapper.selectOne(new LambdaQueryWrapper<GaGamePlayer>()
                .eq(GaGamePlayer::getGameId, gameId)
                .orderByDesc(GaGamePlayer::getSeatNo)
                .last("LIMIT 1")) == null ? 0
                        : baseMapper.selectOne(new LambdaQueryWrapper<GaGamePlayer>()
                                .eq(GaGamePlayer::getGameId, gameId)
                                .orderByDesc(GaGamePlayer::getSeatNo)
                                .last("LIMIT 1")).getSeatNo();
        return maxSeat + 1;
    }
}
