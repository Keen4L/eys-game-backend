package com.eys.service.ga;

import com.eys.model.dto.game.VoteDTO;
import com.eys.model.vo.game.VoteResultVO;

/**
 * 投票 Service 接口
 *
 * @author EYS
 */
public interface VoteService {

    /**
     * 玩家投票
     *
     * @param userId 用户ID
     * @param dto    投票请求
     */
    void vote(Long userId, VoteDTO dto);

    /**
     * 获取投票结果统计
     *
     * @param gameId  游戏ID
     * @param roundNo 轮次（可选，为 null 时取当前轮次）
     * @return 投票结果
     */
    VoteResultVO getVoteResult(Long gameId, Integer roundNo);
}
