package com.eys.app.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eys.common.result.R;
import com.eys.model.dto.game.GameEndDTO;
import com.eys.model.dto.game.StageChangeDTO;
import com.eys.model.dto.game.StartGameDTO;
import com.eys.model.vo.game.PlayerGameVO;
import com.eys.service.ga.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 游戏流程控制器
 *
 * @author EYS
 */
@Tag(name = "游戏流程", description = "游戏开始/阶段/结束接口")
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * DM开始游戏
     */
    @Operation(summary = "开始游戏", description = "DM开始游戏，分配角色")
    @PostMapping("/start")
    public R<Void> start(@Valid @RequestBody StartGameDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        gameService.startGame(userId, dto);
        return R.ok();
    }

    /**
     * DM切换阶段
     */
    @Operation(summary = "切换阶段", description = "DM手动推进游戏阶段")
    @PostMapping("/stage")
    public R<Void> changeStage(@Valid @RequestBody StageChangeDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        gameService.changeStage(userId, dto);
        return R.ok();
    }

    /**
     * DM结束游戏
     */
    @Operation(summary = "结束游戏", description = "DM结束游戏并判定胜负")
    @PostMapping("/end")
    public R<Void> end(@Valid @RequestBody GameEndDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        gameService.endGame(userId, dto);
        return R.ok();
    }

    /**
     * 获取玩家游戏视角
     */
    @Operation(summary = "获取玩家游戏视角", description = "获取玩家自己的游戏状态、角色、技能等")
    @GetMapping("/{gameId}/view")
    public R<PlayerGameVO> getPlayerView(@Parameter(description = "游戏ID") @PathVariable Long gameId) {
        Long userId = StpUtil.getLoginIdAsLong();
        PlayerGameVO view = gameService.getPlayerGameView(userId, gameId);
        return R.ok(view);
    }

    /**
     * DM判定玩家死亡
     */
    @Operation(summary = "DM判定玩家死亡")
    @PostMapping("/{gameId}/kill/{playerId}")
    public R<Void> killPlayer(
            @Parameter(description = "游戏ID") @PathVariable Long gameId,
            @Parameter(description = "对局玩家ID") @PathVariable Long playerId) {
        Long userId = StpUtil.getLoginIdAsLong();
        gameService.killPlayer(userId, gameId, playerId);
        return R.ok();
    }

    /**
     * DM复活玩家
     */
    @Operation(summary = "DM复活玩家")
    @PostMapping("/{gameId}/revive/{playerId}")
    public R<Void> revivePlayer(
            @Parameter(description = "游戏ID") @PathVariable Long gameId,
            @Parameter(description = "对局玩家ID") @PathVariable Long playerId) {
        Long userId = StpUtil.getLoginIdAsLong();
        gameService.revivePlayer(userId, gameId, playerId);
        return R.ok();
    }
}
