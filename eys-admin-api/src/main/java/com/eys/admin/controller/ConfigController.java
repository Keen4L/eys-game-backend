package com.eys.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eys.common.exception.BizException;
import com.eys.common.result.PageResult;
import com.eys.common.result.R;
import com.eys.common.result.ResultCode;
import com.eys.model.entity.cfg.*;
import com.eys.service.cfg.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配置管理控制器
 * 统一管理角色、技能、地图、出生点、牌组等配置
 *
 * @author EYS
 */
@Tag(name = "配置管理", description = "角色/技能/地图/牌组配置管理")
@RestController
@RequestMapping("/cfg")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class ConfigController {

    private final CfgRoleService roleService;
    private final CfgSkillService skillService;
    private final CfgMapService mapService;
    private final CfgMapSpawnPointService spawnPointService;
    private final CfgDeckService deckService;

    // ==================== 角色管理 ====================

    @Operation(summary = "分页查询角色")
    @GetMapping("/role/page")
    public R<PageResult<CfgRole>> rolePage(
            @Parameter(description = "角色名称") @RequestParam(required = false) String name,
            @Parameter(description = "阵营类型") @RequestParam(required = false) Integer campType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CfgRole> page = roleService.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<CfgRole>()
                        .like(StringUtils.hasText(name), CfgRole::getName, name)
                        .eq(campType != null, CfgRole::getCampType, campType)
                        .orderByAsc(CfgRole::getCampType, CfgRole::getId));
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    @Operation(summary = "获取所有角色")
    @GetMapping("/role/list")
    public R<List<CfgRole>> roleList(
            @Parameter(description = "阵营类型") @RequestParam(required = false) Integer campType,
            @Parameter(description = "是否启用") @RequestParam(required = false) Integer isEnabled) {
        return R.ok(roleService.list(new LambdaQueryWrapper<CfgRole>()
                .eq(campType != null, CfgRole::getCampType, campType)
                .eq(isEnabled != null, CfgRole::getIsEnabled, isEnabled)
                .orderByAsc(CfgRole::getCampType, CfgRole::getId)));
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/role/{id}")
    public R<CfgRole> roleDetail(@PathVariable Long id) {
        CfgRole role = roleService.getById(id);
        if (role == null)
            throw new BizException(ResultCode.ROLE_NOT_FOUND);
        return R.ok(role);
    }

    @Operation(summary = "新增/编辑角色")
    @PostMapping("/role/save")
    public R<Long> roleSave(@Valid @RequestBody CfgRole role) {
        roleService.saveOrUpdate(role);
        return R.ok(role.getId());
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/role/{id}")
    public R<Void> roleDelete(@PathVariable Long id) {
        skillService.deleteByRoleId(id);
        roleService.removeById(id);
        return R.ok();
    }

    @Operation(summary = "启用/禁用角色")
    @PostMapping("/role/{id}/toggle")
    public R<Void> roleToggle(@PathVariable Long id) {
        CfgRole role = roleService.getById(id);
        if (role == null)
            throw new BizException(ResultCode.ROLE_NOT_FOUND);
        role.setIsEnabled(role.getIsEnabled() == 1 ? 0 : 1);
        roleService.updateById(role);
        return R.ok();
    }

    // ==================== 技能管理 ====================

    @Operation(summary = "获取角色技能")
    @GetMapping("/role/{roleId}/skills")
    public R<List<CfgSkill>> roleSkills(@PathVariable Long roleId) {
        return R.ok(skillService.listByRoleId(roleId));
    }

    @Operation(summary = "新增/编辑技能")
    @PostMapping("/skill/save")
    public R<Long> skillSave(@Valid @RequestBody CfgSkill skill) {
        skillService.saveOrUpdate(skill);
        return R.ok(skill.getId());
    }

    @Operation(summary = "删除技能")
    @DeleteMapping("/skill/{id}")
    public R<Void> skillDelete(@PathVariable Long id) {
        skillService.removeById(id);
        return R.ok();
    }

    // ==================== 地图管理 ====================

    @Operation(summary = "分页查询地图")
    @GetMapping("/map/page")
    public R<PageResult<CfgMap>> mapPage(
            @Parameter(description = "地图名称") @RequestParam(required = false) String name,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CfgMap> page = mapService.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<CfgMap>()
                        .like(StringUtils.hasText(name), CfgMap::getName, name)
                        .orderByDesc(CfgMap::getCreatedAt));
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    @Operation(summary = "获取所有地图")
    @GetMapping("/map/list")
    public R<List<CfgMap>> mapList() {
        return R.ok(mapService.list());
    }

    @Operation(summary = "获取地图详情")
    @GetMapping("/map/{id}")
    public R<CfgMap> mapDetail(@PathVariable Long id) {
        CfgMap map = mapService.getById(id);
        if (map == null)
            throw new BizException(ResultCode.MAP_NOT_FOUND);
        return R.ok(map);
    }

    @Operation(summary = "新增/编辑地图")
    @PostMapping("/map/save")
    public R<Long> mapSave(@Valid @RequestBody CfgMap map) {
        mapService.saveOrUpdate(map);
        return R.ok(map.getId());
    }

    @Operation(summary = "删除地图")
    @DeleteMapping("/map/{id}")
    public R<Void> mapDelete(@PathVariable Long id) {
        spawnPointService.deleteByMapId(id);
        mapService.removeById(id);
        return R.ok();
    }

    // ==================== 出生点管理 ====================

    @Operation(summary = "获取地图出生点")
    @GetMapping("/map/{mapId}/spawn-points")
    public R<List<CfgMapSpawnPoint>> spawnPoints(@PathVariable Long mapId) {
        return R.ok(spawnPointService.listByMapId(mapId));
    }

    @Operation(summary = "新增/编辑出生点")
    @PostMapping("/map/spawn-point/save")
    public R<Long> spawnPointSave(@Valid @RequestBody CfgMapSpawnPoint sp) {
        spawnPointService.saveOrUpdate(sp);
        return R.ok(sp.getId());
    }

    @Operation(summary = "删除出生点")
    @DeleteMapping("/map/spawn-point/{id}")
    public R<Void> spawnPointDelete(@PathVariable Long id) {
        spawnPointService.removeById(id);
        return R.ok();
    }

    @Operation(summary = "批量保存出生点")
    @PostMapping("/map/{mapId}/spawn-points/batch")
    public R<Void> spawnPointsBatch(@PathVariable Long mapId,
            @RequestBody List<CfgMapSpawnPoint> spawnPoints) {
        spawnPointService.deleteByMapId(mapId);
        spawnPoints.forEach(sp -> sp.setMapId(mapId));
        spawnPointService.saveBatch(spawnPoints);
        return R.ok();
    }

    // ==================== 牌组管理 ====================

    @Operation(summary = "分页查询牌组")
    @GetMapping("/deck/page")
    public R<PageResult<CfgDeck>> deckPage(
            @Parameter(description = "牌组名称") @RequestParam(required = false) String name,
            @Parameter(description = "玩家人数") @RequestParam(required = false) Integer playerCount,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CfgDeck> page = deckService.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<CfgDeck>()
                        .like(StringUtils.hasText(name), CfgDeck::getName, name)
                        .eq(playerCount != null, CfgDeck::getPlayerCount, playerCount)
                        .orderByDesc(CfgDeck::getCreatedAt));
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    @Operation(summary = "获取所有牌组")
    @GetMapping("/deck/list")
    public R<List<CfgDeck>> deckList(
            @Parameter(description = "玩家人数") @RequestParam(required = false) Integer playerCount) {
        return R.ok(deckService.list(new LambdaQueryWrapper<CfgDeck>()
                .eq(playerCount != null, CfgDeck::getPlayerCount, playerCount)
                .orderByAsc(CfgDeck::getPlayerCount)));
    }

    @Operation(summary = "获取牌组详情")
    @GetMapping("/deck/{id}")
    public R<CfgDeck> deckDetail(@PathVariable Long id) {
        CfgDeck deck = deckService.getById(id);
        if (deck == null)
            throw new BizException("牌组不存在");
        return R.ok(deck);
    }

    @Operation(summary = "新增/编辑牌组")
    @PostMapping("/deck/save")
    public R<Long> deckSave(@Valid @RequestBody CfgDeck deck) {
        deckService.saveOrUpdate(deck);
        return R.ok(deck.getId());
    }

    @Operation(summary = "删除牌组")
    @DeleteMapping("/deck/{id}")
    public R<Void> deckDelete(@PathVariable Long id) {
        deckService.removeById(id);
        return R.ok();
    }
}
