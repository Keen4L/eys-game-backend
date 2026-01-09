package com.eys.model.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 加入房间请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "加入房间请求")
public class JoinRoomDTO {

    /**
     * 房间邀请码
     */
    @NotBlank(message = "房间码不能为空")
    @Schema(description = "房间邀请码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roomCode;
}
