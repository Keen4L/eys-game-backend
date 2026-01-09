package com.eys.model.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 阶段切换请求DTO
 * DM 点击"下一阶段"，系统自动计算目标阶段
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
    @Schema(description = "游戏ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long gameId;
}
