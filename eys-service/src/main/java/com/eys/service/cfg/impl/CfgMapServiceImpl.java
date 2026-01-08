package com.eys.service.cfg.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.cfg.CfgMapMapper;
import com.eys.model.entity.cfg.CfgMap;
import com.eys.service.cfg.CfgMapService;
import org.springframework.stereotype.Service;

/**
 * 地图配置 Service 实现
 *
 * @author EYS
 */
@Service
public class CfgMapServiceImpl extends ServiceImpl<CfgMapMapper, CfgMap> implements CfgMapService {
}
