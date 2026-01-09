package com.eys.service.ga;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eys.model.entity.ga.GaActionLog;

import java.util.List;

/**
 * 动作流水 Service 接口
 *
 * @author EYS
 */
public interface GaActionLogService extends IService<GaActionLog> {

    /**
     * 根据对局和轮次获取动作日志
     *
     * @param gameId  对局ID
     * @param roundNo 轮次
     * @return 动作日志列表
     */
    List<GaActionLog> listByGameAndRound(Long gameId, int roundNo);
}
