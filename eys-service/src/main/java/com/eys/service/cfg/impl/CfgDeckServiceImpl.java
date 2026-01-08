package com.eys.service.cfg.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.cfg.CfgDeckMapper;
import com.eys.model.entity.cfg.CfgDeck;
import com.eys.service.cfg.CfgDeckService;
import org.springframework.stereotype.Service;

/**
 * 预设牌组 Service 实现
 *
 * @author EYS
 */
@Service
public class CfgDeckServiceImpl extends ServiceImpl<CfgDeckMapper, CfgDeck> implements CfgDeckService {
}
