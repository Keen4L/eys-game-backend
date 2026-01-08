package com.eys.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eys.common.exception.BizException;
import com.eys.common.result.PageResult;
import com.eys.common.result.R;
import com.eys.common.result.ResultCode;
import com.eys.model.entity.cfg.CfgMap;
import com.eys.model.entity.cfg.CfgMapSpawnPoint;
import com.eys.service.cfg.CfgMapService;
import com.eys.service.cfg.CfgMapSpawnPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地图管理控制器
 *
 * @author EYS
 */
@Tag(name = "地图管理", description = "地图及出生点CRUD接口")
@RestController
@RequestMapping("/cfg/map")
@RequiredArgsConstructor
public class CfgMapController {

    private final CfgMapService cfgMapService;
    private final CfgMapSpawnPointService cfgMapSpawnPointService;

    // ==================== 地图管理 ====================

    /**
     * 分页查询地图列表
     */
    @Operation(summary = "分页查询地图列表")
    @GetMapping("/page")
    public R<PageResult<CfgMap>> page(
            @Parameter(description = "地图名称") @RequestParam(required = false) String name,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<CfgMap> wrapper = new LambdaQueryWrapper<CfgMap>()
                .like(StringUtils.hasText(name), CfgMap::getName, name)
                .orderByDesc(CfgMap::getCreatedAt);

        Page<CfgMap> page = cfgMapService.page(new Page<>(pageNum, pageSize), wrapper);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 获取所有地图（下拉选择用）
     */
    @Operation(summary = "获取所有地图列表")
    @GetMapping("/list")
    public R<List<CfgMap>> list() {
        return R.ok(cfgMapService.list());
    }

    /**
     * 获取地图详情
     */
    @Operation(summary = "获取地图详情")
    @GetMapping("/{id}")
    public R<CfgMap> detail(@Parameter(description = "地图ID") @PathVariable Long id) {
        CfgMap map = cfgMapService.getById(id);
        if (map == null) {
            throw new BizException(ResultCode.MAP_NOT_FOUND);
        }
        return R.ok(map);
    }

    /**
     * 新增/编辑地图
     */
    @Operation(summary = "新增/编辑地图")
    @PostMapping("/save")
    public R<Long> save(@Valid @RequestBody CfgMap map) {
        cfgMapService.saveOrUpdate(map);
        return R.ok(map.getId());
    }

    /**
     * 删除地图
     */
    @Operation(summary = "删除地图")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "地图ID") @PathVariable Long id) {
        // 同时删除出生点
        cfgMapSpawnPointService.deleteByMapId(id);
        cfgMapService.removeById(id);
        return R.ok();
    }

    // ==================== 出生点管理 ====================

    /**
     * 获取地图出生点列表
     */
    @Operation(summary = "获取地图出生点列表")
    @GetMapping("/{mapId}/spawn-points")
    public R<List<CfgMapSpawnPoint>> listSpawnPoints(@Parameter(description = "地图ID") @PathVariable Long mapId) {
        return R.ok(cfgMapSpawnPointService.listByMapId(mapId));
    }

    /**
     * 新增/编辑出生点
     */
    @Operation(summary = "新增/编辑出生点")
    @PostMapping("/spawn-point/save")
    public R<Long> saveSpawnPoint(@Valid @RequestBody CfgMapSpawnPoint spawnPoint) {
        cfgMapSpawnPointService.saveOrUpdate(spawnPoint);
        return R.ok(spawnPoint.getId());
    }

    /**
     * 删除出生点
     */
    @Operation(summary = "删除出生点")
    @DeleteMapping("/spawn-point/{id}")
    public R<Void> deleteSpawnPoint(@Parameter(description = "出生点ID") @PathVariable Long id) {
        cfgMapSpawnPointService.removeById(id);
        return R.ok();
    }

    /**
     * 批量保存出生点
     */
    @Operation(summary = "批量保存出生点")
    @PostMapping("/{mapId}/spawn-points/batch")
    public R<Void> batchSaveSpawnPoints(
            @Parameter(description = "地图ID") @PathVariable Long mapId,
            @RequestBody List<CfgMapSpawnPoint> spawnPoints) {
        // 删除原有出生点
        cfgMapSpawnPointService.deleteByMapId(mapId);
        // 设置地图ID并批量保存
        spawnPoints.forEach(sp -> sp.setMapId(mapId));
        cfgMapSpawnPointService.saveBatch(spawnPoints);
        return R.ok();
    }
}
