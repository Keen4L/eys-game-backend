package com.eys.app.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eys.common.result.R;
import com.eys.model.dto.game.SkillUseDTO;
import com.eys.model.dto.game.VoteDTO;
import com.eys.model.vo.game.PlayerGameVO;
import com.eys.model.vo.game.VoteResultVO;
import com.eys.service.ga.GameService;
import com.eys.service.ga.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 游戏控制器（玩家端）
 * 包含玩家视角、技能使用、投票等操作
 * DM 操作请使用 DmGameController
 *
 * @author EYS
 */
@Tag(name = "游戏操作", description = "玩家游戏接口")
@RestController
@RequestMapping("/app/v1/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final VoteService voteService;

    // ==================== 玩家视角 ====================

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

    // ==================== 技能操作 ====================

    /**
     * 玩家使用技能
     */
    @Operation(summary = "使用技能", description = "玩家主动释放技能")
    @PostMapping("/skill/use")
    public R<Void> useSkill(@Valid @RequestBody SkillUseDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        gameService.useSkill(userId, dto);
        return R.ok();
    }

    // ==================== 投票操作 ====================

    /**
     * 玩家投票
     */
    @Operation(summary = "投票", description = "玩家在投票阶段进行投票")
    @PostMapping("/vote")
    public R<Void> vote(@Valid @RequestBody VoteDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        voteService.vote(userId, dto);
        return R.ok();
    }

    /**
     * 获取投票结果
     */
    @Operation(summary = "获取投票结果", description = "获取指定轮次的投票结果统计")
    @GetMapping("/{gameId}/vote/result")
    public R<VoteResultVO> getVoteResult(
            @Parameter(description = "游戏ID") @PathVariable Long gameId,
            @Parameter(description = "轮次，不传则取当前轮次") @RequestParam(required = false) Integer roundNo) {
        return R.ok(voteService.getVoteResult(gameId, roundNo));
    }
}
