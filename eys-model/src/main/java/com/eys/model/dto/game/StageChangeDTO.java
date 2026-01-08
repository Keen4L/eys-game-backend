package com.eys.model.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 阶段切换请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "阶段切换请求")
public class StageChangeDTO {

    /**
     * 游戏ID
     */
    @NotNull(message = "游戏ID不能为空")
    @Schema(description = "游戏ID", required = true)
    private Long gameId;

    /**
     * 目标阶段
     */
    @NotBlank(message = "目标阶段不能为空")
    @Schema(description = "目标阶段: START, PRE_VOTE, VOTE, POST_VOTE, END", required = true)
    private String targetStage;

    /**
     * 是否进入下一轮（仅 END -> START 时需要）
     */
    @Schema(description = "是否进入下一轮")
    private Boolean nextRound;
}
