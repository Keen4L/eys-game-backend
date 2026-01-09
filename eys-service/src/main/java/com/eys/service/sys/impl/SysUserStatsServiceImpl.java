package com.eys.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.common.constant.CampType;
import com.eys.mapper.sys.SysUserStatsMapper;
import com.eys.model.entity.sys.SysUserStats;
import com.eys.service.sys.SysUserStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户总战绩 Service 实现
 *
 * @author EYS
 */
@Service
public class SysUserStatsServiceImpl extends ServiceImpl<SysUserStatsMapper, SysUserStats>
                implements SysUserStatsService {

        @Override
        @Transactional
        public void updateStats(Long userId, int campType, boolean isWinner) {
                // 获取或初始化用户战绩
                SysUserStats stats = getById(userId);
                if (stats == null) {
                        stats = new SysUserStats();
                        stats.setUserId(userId);
                        stats.setTotalMatches(0);
                        stats.setTotalWins(0);
                        stats.setGooseWins(0);
                        stats.setDuckWins(0);
                        stats.setNeutralWins(0);
                }

                // 更新对局数
                stats.setTotalMatches(stats.getTotalMatches() + 1);

                // 更新胜场
                if (isWinner) {
                        stats.setTotalWins(stats.getTotalWins() + 1);
                        if (campType == CampType.GOOSE.getCode()) {
                                stats.setGooseWins(stats.getGooseWins() + 1);
                        } else if (campType == CampType.DUCK.getCode()) {
                                stats.setDuckWins(stats.getDuckWins() + 1);
                        } else if (campType == CampType.NEUTRAL.getCode()) {
                                stats.setNeutralWins(stats.getNeutralWins() + 1);
                        }
                }

                saveOrUpdate(stats);
        }
}
