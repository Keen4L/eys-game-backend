package com.eys.app.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eys.common.result.R;
import com.eys.model.dto.game.CreateRoomDTO;
import com.eys.model.dto.game.JoinRoomDTO;
import com.eys.model.vo.game.RoomVO;
import com.eys.service.ga.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 房间管理控制器
 *
 * @author EYS
 */
@Tag(name = "房间管理", description = "房间创建/加入/退出接口")
@RestController
@RequestMapping("/app/v1/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * DM创建房间
     */
    @Operation(summary = "创建房间", description = "DM创建游戏房间")
    @PostMapping("/create")
    public R<RoomVO> create(@Valid @RequestBody CreateRoomDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        RoomVO room = roomService.createRoom(userId, dto);
        return R.ok(room);
    }

    /**
     * 加入房间
     */
    @Operation(summary = "加入房间", description = "玩家通过房间码加入房间")
    @PostMapping("/join")
    public R<RoomVO> join(@Valid @RequestBody JoinRoomDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        RoomVO room = roomService.joinRoom(userId, dto);
        return R.ok(room);
    }

    /**
     * 退出房间
     */
    @Operation(summary = "退出房间")
    @PostMapping("/{gameId}/leave")
    public R<Void> leave(@Parameter(description = "游戏ID") @PathVariable Long gameId) {
        Long userId = StpUtil.getLoginIdAsLong();
        roomService.leaveRoom(userId, gameId);
        return R.ok();
    }

    /**
     * 获取房间信息
     */
    @Operation(summary = "获取房间信息")
    @GetMapping("/{gameId}")
    public R<RoomVO> info(@Parameter(description = "游戏ID") @PathVariable Long gameId) {
        Long userId = StpUtil.getLoginIdAsLong();
        RoomVO room = roomService.getRoomInfo(gameId, userId);
        return R.ok(room);
    }

    /**
     * 根据房间码查询房间
     */
    @Operation(summary = "根据房间码查询房间信息")
    @GetMapping("/code/{roomCode}")
    public R<RoomVO> getByCode(@Parameter(description = "房间码") @PathVariable String roomCode) {
        RoomVO room = roomService.getRoomByCode(roomCode);
        return R.ok(room);
    }
}
