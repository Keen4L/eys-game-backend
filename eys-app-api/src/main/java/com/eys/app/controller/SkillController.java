package com.eys.app.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eys.common.result.R;
import com.eys.model.dto.game.SkillUseDTO;
import com.eys.service.ga.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 技能控制器
 *
 * @author EYS
 */
@Tag(name = "技能操作", description = "技能释放接口")
@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
public class SkillController {

    private final GameService gameService;

    /**
     * 玩家使用技能
     */
    @Operation(summary = "使用技能", description = "玩家主动释放技能")
    @PostMapping("/use")
    public R<Void> useSkill(@Valid @RequestBody SkillUseDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        gameService.useSkill(userId, dto);
        return R.ok();
    }

    /**
     * DM录入技能
     */
    @Operation(summary = "DM录入技能", description = "DM直接录入敏感技能结果")
    @PostMapping("/dmInput")
    public R<Void> dmInputSkill(@Valid @RequestBody SkillUseDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        gameService.dmInputSkill(userId, dto);
        return R.ok();
    }

    /**
     * DM请求玩家使用技能
     */
    @Operation(summary = "DM请求技能", description = "DM发起技能使用请求，通知目标玩家")
    @PostMapping("/{gameId}/request/{playerId}/{skillInstanceId}")
    public R<Void> dmRequestSkill(
            @Parameter(description = "游戏ID") @PathVariable Long gameId,
            @Parameter(description = "目标玩家ID") @PathVariable Long playerId,
            @Parameter(description = "技能实例ID") @PathVariable Long skillInstanceId) {
        Long userId = StpUtil.getLoginIdAsLong();
        gameService.dmRequestSkill(userId, gameId, playerId, skillInstanceId);
        return R.ok();
    }
}
