package com.eys.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eys.common.exception.BizException;
import com.eys.common.result.PageResult;
import com.eys.common.result.R;
import com.eys.common.result.ResultCode;
import com.eys.model.dto.sys.SysUserQueryDTO;
import com.eys.model.dto.sys.SysUserSaveDTO;
import com.eys.model.entity.sys.SysUser;
import com.eys.model.entity.sys.SysUserStats;
import com.eys.model.vo.sys.SysUserVO;
import com.eys.service.sys.SysUserService;
import com.eys.service.sys.SysUserStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 *
 * @author EYS
 */
@Tag(name = "用户管理", description = "用户CRUD接口")
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class SysUserController {

    private final SysUserService sysUserService;
    private final SysUserStatsService sysUserStatsService;

    /**
     * 分页查询用户列表
     */
    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public R<PageResult<SysUserVO>> page(SysUserQueryDTO query) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(query.getNickname()), SysUser::getNickname, query.getNickname())
                .eq(query.getRoleType() != null, SysUser::getRoleType, query.getRoleType())
                .eq(query.getIsEnabled() != null, SysUser::getIsEnabled, query.getIsEnabled())
                .orderByDesc(SysUser::getCreatedAt);

        // 分页查询
        Page<SysUser> page = sysUserService.page(
                new Page<>(query.getPageNum(), query.getPageSize()),
                wrapper);

        // 获取用户战绩
        List<Long> userIds = page.getRecords().stream().map(SysUser::getId).toList();
        Map<Long, SysUserStats> statsMap = sysUserStatsService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUserStats::getUserId, s -> s));

        // 转换 VO
        List<SysUserVO> voList = page.getRecords().stream().map(user -> {
            SysUserVO vo = new SysUserVO();
            BeanUtils.copyProperties(user, vo);
            SysUserStats stats = statsMap.get(user.getId());
            if (stats != null) {
                vo.setTotalMatches(stats.getTotalMatches());
                vo.setTotalWins(stats.getTotalWins());
            }
            return vo;
        }).toList();

        return R.ok(PageResult.of(voList, page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public R<SysUserVO> detail(@Parameter(description = "用户ID") @PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(user, vo);

        // 获取战绩
        SysUserStats stats = sysUserStatsService.getById(id);
        if (stats != null) {
            vo.setTotalMatches(stats.getTotalMatches());
            vo.setTotalWins(stats.getTotalWins());
        }

        return R.ok(vo);
    }

    /**
     * 新增/编辑用户
     */
    @Operation(summary = "新增/编辑用户")
    @PostMapping("/save")
    public R<Long> save(@Valid @RequestBody SysUserSaveDTO dto) {
        SysUser user;

        if (dto.getId() != null) {
            // 编辑
            user = sysUserService.getById(dto.getId());
            if (user == null) {
                throw new BizException(ResultCode.USER_NOT_FOUND);
            }

            // 更新字段
            user.setNickname(dto.getNickname());
            user.setAvatarUrl(dto.getAvatarUrl());
            user.setRoleType(dto.getRoleType());
            user.setIsEnabled(dto.getIsEnabled());

            // 如果传了密码则更新
            if (StringUtils.hasText(dto.getPassword())) {
                user.setPassword(BCrypt.hashpw(dto.getPassword()));
            }

            sysUserService.updateById(user);
        } else {
            // 新增
            user = new SysUser();
            BeanUtils.copyProperties(dto, user);

            // 检查用户名是否重复
            if (StringUtils.hasText(dto.getUsername())) {
                SysUser exist = sysUserService.getByUsername(dto.getUsername());
                if (exist != null) {
                    throw new BizException(ResultCode.USERNAME_EXISTS);
                }
            }

            // 加密密码
            if (StringUtils.hasText(dto.getPassword())) {
                user.setPassword(BCrypt.hashpw(dto.getPassword()));
            }

            if (user.getRoleType() == null) {
                user.setRoleType(0);
            }
            if (user.getIsEnabled() == null) {
                user.setIsEnabled(1);
            }

            sysUserService.save(user);

            // 初始化战绩
            SysUserStats stats = new SysUserStats();
            stats.setUserId(user.getId());
            stats.setTotalMatches(0);
            stats.setTotalWins(0);
            stats.setGooseWins(0);
            stats.setDuckWins(0);
            stats.setNeutralWins(0);
            sysUserStatsService.save(stats);
        }

        return R.ok(user.getId());
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "用户ID") @PathVariable Long id) {
        sysUserService.removeById(id);
        sysUserStatsService.removeById(id);
        return R.ok();
    }

    /**
     * 启用/禁用用户
     */
    @Operation(summary = "启用/禁用用户")
    @PostMapping("/{id}/toggle")
    public R<Void> toggle(@Parameter(description = "用户ID") @PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        user.setIsEnabled(user.getIsEnabled() == 1 ? 0 : 1);
        sysUserService.updateById(user);

        return R.ok();
    }
}
