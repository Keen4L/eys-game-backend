package com.eys.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eys.common.exception.BizException;
import com.eys.common.result.PageResult;
import com.eys.common.result.R;
import com.eys.model.entity.cfg.CfgDeck;
import com.eys.service.cfg.CfgDeckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预设牌组管理控制器
 *
 * @author EYS
 */
@Tag(name = "牌组管理", description = "预设牌组CRUD接口")
@RestController
@RequestMapping("/cfg/deck")
@RequiredArgsConstructor
public class CfgDeckController {

    private final CfgDeckService cfgDeckService;

    /**
     * 分页查询牌组列表
     */
    @Operation(summary = "分页查询牌组列表")
    @GetMapping("/page")
    public R<PageResult<CfgDeck>> page(
            @Parameter(description = "牌组名称") @RequestParam(required = false) String name,
            @Parameter(description = "玩家人数") @RequestParam(required = false) Integer playerCount,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<CfgDeck> wrapper = new LambdaQueryWrapper<CfgDeck>()
                .like(StringUtils.hasText(name), CfgDeck::getName, name)
                .eq(playerCount != null, CfgDeck::getPlayerCount, playerCount)
                .orderByDesc(CfgDeck::getCreatedAt);

        Page<CfgDeck> page = cfgDeckService.page(new Page<>(pageNum, pageSize), wrapper);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 获取所有牌组（下拉选择用）
     */
    @Operation(summary = "获取所有牌组列表")
    @GetMapping("/list")
    public R<List<CfgDeck>> list(
            @Parameter(description = "玩家人数") @RequestParam(required = false) Integer playerCount) {
        LambdaQueryWrapper<CfgDeck> wrapper = new LambdaQueryWrapper<CfgDeck>()
                .eq(playerCount != null, CfgDeck::getPlayerCount, playerCount)
                .orderByAsc(CfgDeck::getPlayerCount);
        return R.ok(cfgDeckService.list(wrapper));
    }

    /**
     * 获取牌组详情
     */
    @Operation(summary = "获取牌组详情")
    @GetMapping("/{id}")
    public R<CfgDeck> detail(@Parameter(description = "牌组ID") @PathVariable Long id) {
        CfgDeck deck = cfgDeckService.getById(id);
        if (deck == null) {
            throw new BizException("牌组不存在");
        }
        return R.ok(deck);
    }

    /**
     * 新增/编辑牌组
     */
    @Operation(summary = "新增/编辑牌组")
    @PostMapping("/save")
    public R<Long> save(@Valid @RequestBody CfgDeck deck) {
        cfgDeckService.saveOrUpdate(deck);
        return R.ok(deck.getId());
    }

    /**
     * 删除牌组
     */
    @Operation(summary = "删除牌组")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "牌组ID") @PathVariable Long id) {
        cfgDeckService.removeById(id);
        return R.ok();
    }
}
