package com.eys.service.ga;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eys.model.entity.ga.GaVoteLog;

import java.util.List;

/**
 * 投票记录 Service 接口
 *
 * @author EYS
 */
public interface GaVoteLogService extends IService<GaVoteLog> {

    /**
     * 获取某轮次的投票记录
     */
    List<GaVoteLog> listByGameAndRound(Long gameId, Integer roundNo);

    /**
     * 检查玩家是否已投票
     */
    boolean hasVoted(Long gameId, Integer roundNo, Long voterId);

    /**
     * 统计某轮次的投票数量（优化：使用 COUNT 替代 list.size()）
     *
     * @param gameId  游戏ID
     * @param roundNo 轮次
     * @return 投票数量
     */
    int countByGameAndRound(Long gameId, Integer roundNo);
}
