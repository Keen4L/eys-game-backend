package com.eys.app.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eys.common.result.R;
import com.eys.model.dto.game.VoteDTO;
import com.eys.service.ga.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 投票控制器
 *
 * @author EYS
 */
@Tag(name = "投票操作", description = "投票接口")
@RestController
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VoteController {

    private final GameService gameService;

    /**
     * 玩家投票
     */
    @Operation(summary = "投票", description = "玩家在投票阶段进行投票")
    @PostMapping
    public R<Void> vote(@Valid @RequestBody VoteDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        gameService.vote(userId, dto);
        return R.ok();
    }
}
