package com.eys.model.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 技能释放请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "技能释放请求")
public class SkillUseDTO {

    /**
     * 游戏ID
     */
    @NotNull(message = "游戏ID不能为空")
    @Schema(description = "游戏ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long gameId;

    /**
     * 技能实例ID
     */
    @NotNull(message = "技能实例ID不能为空")
    @Schema(description = "技能实例ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long skillInstanceId;

    /**
     * 目标玩家ID列表
     */
    @Schema(description = "目标玩家ID列表（对局玩家ID）")
    private List<Long> targetPlayerIds;

    /**
     * 猜测角色ID（用于需要猜测角色的技能）
     */
    @Schema(description = "猜测角色ID（用于PLAYER_ROLE类型技能）")
    private Long guessRoleId;
}
