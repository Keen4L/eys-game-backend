package com.eys.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eys.common.result.PageResult;
import com.eys.common.result.R;
import com.eys.model.entity.sys.SysUser;
import com.eys.model.entity.sys.SysUserStats;
import com.eys.model.entity.sys.SysUserStatsRole;
import com.eys.model.vo.sys.SysUserVO;
import com.eys.service.sys.SysUserService;
import com.eys.service.sys.SysUserStatsService;
import com.eys.service.sys.SysUserStatsRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 排行榜控制器
 *
 * @author EYS
 */
@Tag(name = "排行榜", description = "排行榜查询接口")
@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class RankController {

    private final SysUserService sysUserService;
    private final SysUserStatsService sysUserStatsService;
    private final SysUserStatsRoleService sysUserStatsRoleService;

    /**
     * 总胜场排行榜
     */
    @Operation(summary = "总胜场排行榜")
    @GetMapping("/total-wins")
    public R<PageResult<SysUserVO>> totalWinsRank(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {

        // 查询排行数据
        Page<SysUserStats> page = sysUserStatsService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysUserStats>()
                        .orderByDesc(SysUserStats::getTotalWins));

        // 获取用户信息
        List<Long> userIds = page.getRecords().stream().map(SysUserStats::getUserId).toList();
        Map<Long, SysUser> userMap = sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        // 组装VO
        List<SysUserVO> voList = page.getRecords().stream().map(stats -> {
            SysUserVO vo = new SysUserVO();
            SysUser user = userMap.get(stats.getUserId());
            if (user != null) {
                BeanUtils.copyProperties(user, vo);
            }
            vo.setTotalMatches(stats.getTotalMatches());
            vo.setTotalWins(stats.getTotalWins());
            return vo;
        }).toList();

        return R.ok(PageResult.of(voList, page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 鹅阵营胜场排行榜
     */
    @Operation(summary = "鹅阵营胜场排行榜")
    @GetMapping("/goose-wins")
    public R<PageResult<SysUserVO>> gooseWinsRank(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {

        Page<SysUserStats> page = sysUserStatsService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysUserStats>()
                        .orderByDesc(SysUserStats::getGooseWins));

        List<Long> userIds = page.getRecords().stream().map(SysUserStats::getUserId).toList();
        Map<Long, SysUser> userMap = sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        List<SysUserVO> voList = page.getRecords().stream().map(stats -> {
            SysUserVO vo = new SysUserVO();
            SysUser user = userMap.get(stats.getUserId());
            if (user != null) {
                BeanUtils.copyProperties(user, vo);
            }
            vo.setTotalMatches(stats.getTotalMatches());
            vo.setTotalWins(stats.getGooseWins());
            return vo;
        }).toList();

        return R.ok(PageResult.of(voList, page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 鸭阵营胜场排行榜
     */
    @Operation(summary = "鸭阵营胜场排行榜")
    @GetMapping("/duck-wins")
    public R<PageResult<SysUserVO>> duckWinsRank(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {

        Page<SysUserStats> page = sysUserStatsService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysUserStats>()
                        .orderByDesc(SysUserStats::getDuckWins));

        List<Long> userIds = page.getRecords().stream().map(SysUserStats::getUserId).toList();
        Map<Long, SysUser> userMap = sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        List<SysUserVO> voList = page.getRecords().stream().map(stats -> {
            SysUserVO vo = new SysUserVO();
            SysUser user = userMap.get(stats.getUserId());
            if (user != null) {
                BeanUtils.copyProperties(user, vo);
            }
            vo.setTotalMatches(stats.getTotalMatches());
            vo.setTotalWins(stats.getDuckWins());
            return vo;
        }).toList();

        return R.ok(PageResult.of(voList, page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 角色胜场排行榜
     */
    @Operation(summary = "角色胜场排行榜")
    @GetMapping("/role/{roleId}")
    public R<List<SysUserStatsRole>> roleRank(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "50") Integer limit) {

        List<SysUserStatsRole> list = sysUserStatsRoleService.list(
                new LambdaQueryWrapper<SysUserStatsRole>()
                        .eq(SysUserStatsRole::getRoleId, roleId)
                        .orderByDesc(SysUserStatsRole::getWinCount)
                        .last("LIMIT " + limit));

        return R.ok(list);
    }
}
