package com.eys.model.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 开始游戏请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "开始游戏请求")
public class StartGameDTO {

    /**
     * 游戏ID
     */
    @NotNull(message = "游戏ID不能为空")
    @Schema(description = "游戏ID", required = true)
    private Long gameId;

    /**
     * 内定角色分配（可选）
     * Key: 用户ID, Value: 角色ID
     */
    @Schema(description = "内定角色分配，Key为用户ID，Value为角色ID")
    private Map<Long, Long> fixedRoles;
}
