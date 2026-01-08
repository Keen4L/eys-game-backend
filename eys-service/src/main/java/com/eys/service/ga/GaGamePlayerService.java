package com.eys.service.ga;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eys.model.entity.ga.GaGamePlayer;

import java.util.List;

/**
 * 对局玩家 Service 接口
 *
 * @author EYS
 */
public interface GaGamePlayerService extends IService<GaGamePlayer> {

    /**
     * 获取游戏的所有玩家
     */
    List<GaGamePlayer> listByGameId(Long gameId);

    /**
     * 根据游戏ID和用户ID获取对局玩家
     */
    GaGamePlayer getByGameAndUser(Long gameId, Long userId);

    /**
     * 获取游戏玩家数量
     */
    int countByGameId(Long gameId);

    /**
     * 获取下一个可用座位号
     */
    int getNextSeatNo(Long gameId);
}
