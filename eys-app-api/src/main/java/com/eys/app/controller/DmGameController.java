package com.eys.app.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eys.common.result.R;
import com.eys.model.dto.game.GameEndDTO;
import com.eys.model.dto.game.StageChangeDTO;
import com.eys.model.dto.game.StartGameDTO;
import com.eys.model.entity.ga.GaActionLog;
import com.eys.model.vo.game.DmPlayerViewVO;
import com.eys.service.ga.GamePermissionService;
import com.eys.service.ga.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DM 控场接口
 * 只有 DM/房主 可以调用的接口
 * 所有接口通过 GamePermissionService.assertDm() 统一校验权限
 *
 * @author EYS
 */
@RestController
@RequestMapping("/app/v1/dm")
@RequiredArgsConstructor
@Tag(name = "DM控场", description = "DM 专用控场接口")
public class DmGameController {

    private final GameService gameService;
    private final GamePermissionService permissionService;

    /**
     * DM 开始游戏
     */
    @PostMapping("/start")
    @Operation(summary = "开始游戏", description = "DM 开始游戏，分配角色")
    public R<Void> startGame(@RequestBody StartGameDTO dto) {
        permissionService.assertDm(dto.getGameId());
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.startGame(dmUserId, dto);
        return R.ok();
    }

    /**
     * DM 推进阶段
     */
    @PostMapping("/stage/next")
    @Operation(summary = "推进游戏阶段", description = "DM 切换游戏阶段（如 PRE_VOTE -> VOTE）")
    public R<Void> nextStage(@RequestBody StageChangeDTO dto) {
        permissionService.assertDm(dto.getGameId());
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.changeStage(dmUserId, dto);
        return R.ok();
    }

    // ==================== 技能操作 ====================

    /**
     * DM录入技能
     */
    @Operation(summary = "DM录入技能", description = "DM直接录入敏感技能结果")
    @PostMapping("/skill/input")
    public R<Void> dmInputSkill(@jakarta.validation.Valid @RequestBody com.eys.model.dto.game.SkillUseDTO dto) {
        permissionService.assertDm(dto.getGameId());
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.dmInputSkill(dmUserId, dto);
        return R.ok();
    }

    /**
     * DM请求玩家使用技能
     */
    @Operation(summary = "DM请求技能", description = "DM发起技能使用请求，通知目标玩家")
    @PostMapping("/{gameId}/skill/request/{playerId}/{skillInstanceId}")
    public R<Void> dmRequestSkill(
            @io.swagger.v3.oas.annotations.Parameter(description = "游戏ID") @PathVariable Long gameId,
            @io.swagger.v3.oas.annotations.Parameter(description = "目标玩家ID") @PathVariable Long playerId,
            @io.swagger.v3.oas.annotations.Parameter(description = "技能实例ID") @PathVariable Long skillInstanceId) {
        permissionService.assertDm(gameId);
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.dmRequestSkill(dmUserId, gameId, playerId, skillInstanceId);
        return R.ok();
    }

    // ==================== 游戏管理 ====================

    /**
     * DM 手动判死
     */
    @PostMapping("/admin/kill")
    @Operation(summary = "判定玩家死亡", description = "DM 手动将玩家判定为死亡")
    public R<Void> killPlayer(@RequestParam Long gameId, @RequestParam Long targetPlayerId) {
        permissionService.assertDm(gameId);
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
        permissionService.assertDm(gameId);
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
        permissionService.assertDm(dto.getGameId());
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.endGame(dmUserId, dto);
        return R.ok();
    }

    /**
     * DM 移除玩家标签
     */
    @PostMapping("/admin/removeTag")
    @Operation(summary = "移除状态标签", description = "DM 手动移除玩家身上的状态标签")
    public R<Void> removeTag(@RequestParam Long gameId, @RequestParam Long targetPlayerId,
            @RequestParam String tagName) {
        permissionService.assertDm(gameId);
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.removeTag(dmUserId, gameId, targetPlayerId, tagName);
        return R.ok();
    }

    /**
     * DM 代替玩家释放技能
     */
    @PostMapping("/admin/proxyCast")
    @Operation(summary = "代替玩家释放技能", description = "DM 帮玩家点技能（玩家掉线或不会玩时）")
    public R<Void> proxyCastSkill(@RequestParam Long gameId, @RequestParam Long actorPlayerId,
            @RequestParam Long skillInstanceId, @RequestParam(required = false) Long targetPlayerId) {
        permissionService.assertDm(gameId);
        Long dmUserId = StpUtil.getLoginIdAsLong();
        gameService.proxyCastSkill(dmUserId, gameId, actorPlayerId, skillInstanceId, targetPlayerId);
        return R.ok();
    }

    /**
     * DM 获取全局视角（所有玩家身份）
     */
    @GetMapping("/{gameId}/overview")
    @Operation(summary = "DM全局视角", description = "获取所有玩家的身份信息和状态")
    public R<List<DmPlayerViewVO>> getDmView(@PathVariable Long gameId) {
        permissionService.assertDm(gameId);
        Long dmUserId = StpUtil.getLoginIdAsLong();
        return R.ok(gameService.getDmFullView(dmUserId, gameId));
    }

    /**
     * DM 获取动作日志
     */
    @GetMapping("/{gameId}/logs")
    @Operation(summary = "获取动作日志", description = "获取指定轮次的所有动作日志")
    public R<List<GaActionLog>> getActionLogs(@PathVariable Long gameId,
            @RequestParam(required = false) Integer roundNo) {
        permissionService.assertDm(gameId);
        Long dmUserId = StpUtil.getLoginIdAsLong();
        return R.ok(gameService.getActionLogs(dmUserId, gameId, roundNo));
    }
}
