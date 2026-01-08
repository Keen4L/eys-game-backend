package com.eys.service.ga;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eys.model.entity.ga.GaSkillInstance;

import java.util.List;

/**
 * 技能实例 Service 接口
 *
 * @author EYS
 */
public interface GaSkillInstanceService extends IService<GaSkillInstance> {

    /**
     * 获取玩家的所有技能实例
     */
    List<GaSkillInstance> listByGamePlayerId(Long gamePlayerId);

    /**
     * 扣减技能使用次数（支持技能组共享扣减）
     *
     * @param instanceId 技能实例ID
     */
    void deductUsage(Long instanceId);
}
