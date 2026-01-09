package com.eys.service.ga;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eys.model.entity.ga.GaPlayerStatus;

/**
 * 玩家状态 Service 接口
 *
 * @author EYS
 */
public interface GaPlayerStatusService extends IService<GaPlayerStatus> {

    /**
     * 检查玩家是否有指定状态效果
     *
     * @param gamePlayerId 对局玩家ID
     * @param effectType   效果类型（如 MUTED, PROTECTED 等）
     * @return 是否有该效果
     */
    boolean hasEffect(Long gamePlayerId, String effectType);

    /**
     * 给玩家添加状态效果
     *
     * @param gamePlayerId 对局玩家ID
     * @param effectType   效果类型
     * @param duration     持续轮数（-1 表示永久）
     */
    void addEffect(Long gamePlayerId, String effectType, int duration);

    /**
     * 移除玩家的状态效果
     *
     * @param gamePlayerId 对局玩家ID
     * @param effectType   效果类型
     */
    void removeEffect(Long gamePlayerId, String effectType);

    /**
     * 清理指定对局中所有过期的 Tag (duration=1 的自动扣减，=0 的移除)
     *
     * @param gameId 对局ID
     */
    void tickDownAndClearExpiredEffects(Long gameId);

    /**
     * 统计存活玩家数量（优化：使用 COUNT 替代 list + filter）
     *
     * @param gameId 游戏ID
     * @return 存活玩家数量
     */
    int countAliveByGameId(Long gameId);
}
