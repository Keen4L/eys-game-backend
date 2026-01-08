package com.eys.app.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eys.common.result.R;
import com.eys.model.dto.game.GameEndDTO;
import com.eys.model.dto.game.StageChangeDTO;
import com.eys.service.ga.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * DM 控场接口
 * 只有 DM/房主 可以调用的接口
 *
 * @author EYS
 */
@RestController
@RequestMapping("/app/game/dm")
@RequiredArgsConstructor
@Tag(name = "DM控场", description = "DM 专用控场接口")
public class DmGameController {

    private final GameService gameService;

    /**
     * DM 推进阶段
     */
    @PostMapping("/stage/next")
    @Operation(summary = "推进游戏阶段", description = "DM 切换游戏阶段（如 DAY -> VOTE）")
    public R<Void> nextStage(@RequestBody StageChangeDTO dto) {
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.changeStage(dmUserId, dto);
        return R.ok();
    }

    /**
     * DM 手动判死
     */
    @PostMapping("/admin/kill")
    @Operation(summary = "判定玩家死亡", description = "DM 手动将玩家判定为死亡")
    public R<Void> killPlayer(@RequestParam Long gameId, @RequestParam Long targetPlayerId) {
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.killPlayer(dmUserId, gameId, targetPlayerId);
        return R.ok();
    }

    /**
     * DM 复活玩家
     */
    @PostMapping("/admin/revive")
    @Operation(summary = "复活玩家", description = "DM 将死亡玩家复活")
    public R<Void> revivePlayer(@RequestParam Long gameId, @RequestParam Long targetPlayerId) {
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.revivePlayer(dmUserId, gameId, targetPlayerId);
        return R.ok();
    }

    /**
     * DM 结束游戏
     */
    @PostMapping("/game/end")
    @Operation(summary = "结束游戏", description = "DM 结束游戏并判定胜负")
    public R<Void> endGame(@RequestBody GameEndDTO dto) {
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.endGame(dmUserId, dto);
        return R.ok();
    }
}
