package com.eys.model.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 投票请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "投票请求")
public class VoteDTO {

    /**
     * 游戏ID
     */
    @NotNull(message = "游戏ID不能为空")
    @Schema(description = "游戏ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long gameId;

    /**
     * 被投票的玩家ID（NULL表示弃票）
     */
    @Schema(description = "被投票的对局玩家ID，NULL表示弃票")
    private Long targetPlayerId;
}
