package com.eys.service.cfg.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.cfg.CfgMapSpawnPointMapper;
import com.eys.model.entity.cfg.CfgMapSpawnPoint;
import com.eys.service.cfg.CfgMapSpawnPointService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地图出生点 Service 实现
 *
 * @author EYS
 */
@Service
public class CfgMapSpawnPointServiceImpl extends ServiceImpl<CfgMapSpawnPointMapper, CfgMapSpawnPoint>
        implements CfgMapSpawnPointService {

    @Override
    public List<CfgMapSpawnPoint> listByMapId(Long mapId) {
        return list(new LambdaQueryWrapper<CfgMapSpawnPoint>()
                .eq(CfgMapSpawnPoint::getMapId, mapId));
    }

    @Override
    public void deleteByMapId(Long mapId) {
        remove(new LambdaQueryWrapper<CfgMapSpawnPoint>()
                .eq(CfgMapSpawnPoint::getMapId, mapId));
    }
}
