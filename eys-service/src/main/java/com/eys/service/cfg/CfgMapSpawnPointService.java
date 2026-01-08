package com.eys.service.cfg;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eys.model.entity.cfg.CfgMapSpawnPoint;

import java.util.List;

/**
 * 地图出生点 Service 接口
 *
 * @author EYS
 */
public interface CfgMapSpawnPointService extends IService<CfgMapSpawnPoint> {

    /**
     * 根据地图ID获取出生点列表
     */
    List<CfgMapSpawnPoint> listByMapId(Long mapId);

    /**
     * 删除地图下的所有出生点
     */
    void deleteByMapId(Long mapId);
}
