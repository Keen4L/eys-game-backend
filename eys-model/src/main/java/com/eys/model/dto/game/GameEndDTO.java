package com.eys.model.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 游戏结束请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "游戏结束请求")
public class GameEndDTO {

    /**
     * 游戏ID
     */
    @NotNull(message = "游戏ID不能为空")
    @Schema(description = "游戏ID", required = true)
    private Long gameId;

    /**
     * 胜利类型: 1-鹅胜, 2-鸭胜, 3-中立个人胜
     */
    @NotNull(message = "胜利类型不能为空")
    @Schema(description = "胜利类型: 1-鹅胜, 2-鸭胜, 3-中立个人胜", required = true)
    private Integer victoryType;

    /**
     * 中立获胜者用户ID（仅 victoryType=3 时需要）
     */
    @Schema(description = "中立获胜者用户ID（仅中立胜利时需要）")
    private Long winnerUserId;
}
