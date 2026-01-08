package com.eys.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eys.common.result.PageResult;
import com.eys.common.result.R;
import com.eys.model.entity.ga.GaGameRecord;
import com.eys.service.ga.GaGameRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 游戏记录管理控制器
 *
 * @author EYS
 */
@Tag(name = "游戏记录", description = "游戏记录查询接口")
@RestController
@RequestMapping("/ga/record")
@RequiredArgsConstructor
public class GaGameRecordController {

    private final GaGameRecordService gaGameRecordService;

    /**
     * 分页查询游戏记录
     */
    @Operation(summary = "分页查询游戏记录")
    @GetMapping("/page")
    public R<PageResult<GaGameRecord>> page(
            @Parameter(description = "房间码") @RequestParam(required = false) String roomCode,
            @Parameter(description = "游戏状态") @RequestParam(required = false) String status,
            @Parameter(description = "DM用户ID") @RequestParam(required = false) Long dmUserId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<GaGameRecord> wrapper = new LambdaQueryWrapper<GaGameRecord>()
                .eq(StringUtils.hasText(roomCode), GaGameRecord::getRoomCode, roomCode)
                .eq(StringUtils.hasText(status), GaGameRecord::getStatus, status)
                .eq(dmUserId != null, GaGameRecord::getDmUserId, dmUserId)
                .orderByDesc(GaGameRecord::getId);

        Page<GaGameRecord> page = gaGameRecordService.page(new Page<>(pageNum, pageSize), wrapper);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 获取游戏详情
     */
    @Operation(summary = "获取游戏详情")
    @GetMapping("/{id}")
    public R<GaGameRecord> detail(@Parameter(description = "游戏ID") @PathVariable Long id) {
        return R.ok(gaGameRecordService.getById(id));
    }

    /**
     * 获取进行中的游戏列表
     */
    @Operation(summary = "获取进行中的游戏列表")
    @GetMapping("/playing")
    public R<PageResult<GaGameRecord>> playingGames(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {

        Page<GaGameRecord> page = gaGameRecordService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<GaGameRecord>()
                        .eq(GaGameRecord::getStatus, "PLAYING")
                        .orderByDesc(GaGameRecord::getStartedAt));

        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }
}
