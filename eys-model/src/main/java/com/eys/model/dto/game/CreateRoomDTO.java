package com.eys.model.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建房间请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "创建房间请求")
public class CreateRoomDTO {

    /**
     * 地图ID
     */
    @NotNull(message = "地图不能为空")
    @Schema(description = "地图ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long mapId;

    /**
     * 预设牌组ID（可选，不传则完全自定义）
     */
    @Schema(description = "预设牌组ID（可选）")
    private Long deckId;

    /**
     * 自定义角色ID列表（如果不使用预设牌组或需要修改）
     */
    @Schema(description = "自定义角色ID列表")
    private List<Long> roleIds;
}
