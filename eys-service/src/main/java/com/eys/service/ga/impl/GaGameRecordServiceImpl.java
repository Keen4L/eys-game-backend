package com.eys.service.ga.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.mapper.ga.GaGameRecordMapper;
import com.eys.model.entity.ga.GaGameRecord;
import com.eys.service.ga.GaGameRecordService;
import org.springframework.stereotype.Service;

/**
 * 对局记录 Service 实现
 *
 * @author EYS
 */
@Service
public class GaGameRecordServiceImpl extends ServiceImpl<GaGameRecordMapper, GaGameRecord>
        implements GaGameRecordService {
}
