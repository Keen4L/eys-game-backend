package com.eys.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eys.model.entity.sys.SysUserStats;

/**
 * 用户总战绩 Service 接口
 *
 * @author EYS
 */
public interface SysUserStatsService extends IService<SysUserStats> {

    /**
     * 更新用户战绩
     *
     * @param userId   用户ID
     * @param campType 本局阵营: 0-鹅, 1-鸭, 2-中立
     * @param isWinner 是否获胜
     */
    void updateStats(Long userId, int campType, boolean isWinner);
}
