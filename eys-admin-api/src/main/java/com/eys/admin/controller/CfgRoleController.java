package com.eys.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eys.common.exception.BizException;
import com.eys.common.result.PageResult;
import com.eys.common.result.R;
import com.eys.common.result.ResultCode;
import com.eys.model.entity.cfg.CfgRole;
import com.eys.model.entity.cfg.CfgSkill;
import com.eys.service.cfg.CfgRoleService;
import com.eys.service.cfg.CfgSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author EYS
 */
@Tag(name = "角色管理", description = "角色及技能CRUD接口")
@RestController
@RequestMapping("/cfg/role")
@RequiredArgsConstructor
public class CfgRoleController {

    private final CfgRoleService cfgRoleService;
    private final CfgSkillService cfgSkillService;

    // ==================== 角色管理 ====================

    /**
     * 分页查询角色列表
     */
    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    public R<PageResult<CfgRole>> page(
            @Parameter(description = "角色名称") @RequestParam(required = false) String name,
            @Parameter(description = "阵营类型") @RequestParam(required = false) Integer campType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<CfgRole> wrapper = new LambdaQueryWrapper<CfgRole>()
                .like(StringUtils.hasText(name), CfgRole::getName, name)
                .eq(campType != null, CfgRole::getCampType, campType)
                .orderByAsc(CfgRole::getCampType)
                .orderByAsc(CfgRole::getId);

        Page<CfgRole> page = cfgRoleService.page(new Page<>(pageNum, pageSize), wrapper);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 获取所有角色（下拉选择用）
     */
    @Operation(summary = "获取所有角色列表")
    @GetMapping("/list")
    public R<List<CfgRole>> list(
            @Parameter(description = "阵营类型") @RequestParam(required = false) Integer campType,
            @Parameter(description = "是否启用") @RequestParam(required = false) Integer isEnabled) {
        LambdaQueryWrapper<CfgRole> wrapper = new LambdaQueryWrapper<CfgRole>()
                .eq(campType != null, CfgRole::getCampType, campType)
                .eq(isEnabled != null, CfgRole::getIsEnabled, isEnabled)
                .orderByAsc(CfgRole::getCampType)
                .orderByAsc(CfgRole::getId);
        return R.ok(cfgRoleService.list(wrapper));
    }

    /**
     * 获取角色详情
     */
    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public R<CfgRole> detail(@Parameter(description = "角色ID") @PathVariable Long id) {
        CfgRole role = cfgRoleService.getById(id);
        if (role == null) {
            throw new BizException(ResultCode.ROLE_NOT_FOUND);
        }
        return R.ok(role);
    }

    /**
     * 新增/编辑角色
     */
    @Operation(summary = "新增/编辑角色")
    @PostMapping("/save")
    public R<Long> save(@Valid @RequestBody CfgRole role) {
        cfgRoleService.saveOrUpdate(role);
        return R.ok(role.getId());
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "角色ID") @PathVariable Long id) {
        // 同时删除技能
        cfgSkillService.deleteByRoleId(id);
        cfgRoleService.removeById(id);
        return R.ok();
    }

    /**
     * 启用/禁用角色
     */
    @Operation(summary = "启用/禁用角色")
    @PostMapping("/{id}/toggle")
    public R<Void> toggle(@Parameter(description = "角色ID") @PathVariable Long id) {
        CfgRole role = cfgRoleService.getById(id);
        if (role == null) {
            throw new BizException(ResultCode.ROLE_NOT_FOUND);
        }
        role.setIsEnabled(role.getIsEnabled() == 1 ? 0 : 1);
        cfgRoleService.updateById(role);
        return R.ok();
    }

    // ==================== 技能管理 ====================

    /**
     * 获取角色技能列表
     */
    @Operation(summary = "获取角色技能列表")
    @GetMapping("/{roleId}/skills")
    public R<List<CfgSkill>> listSkills(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        return R.ok(cfgSkillService.listByRoleId(roleId));
    }

    /**
     * 新增/编辑技能
     */
    @Operation(summary = "新增/编辑技能")
    @PostMapping("/skill/save")
    public R<Long> saveSkill(@Valid @RequestBody CfgSkill skill) {
        cfgSkillService.saveOrUpdate(skill);
        return R.ok(skill.getId());
    }

    /**
     * 删除技能
     */
    @Operation(summary = "删除技能")
    @DeleteMapping("/skill/{id}")
    public R<Void> deleteSkill(@Parameter(description = "技能ID") @PathVariable Long id) {
        cfgSkillService.removeById(id);
        return R.ok();
    }
}
