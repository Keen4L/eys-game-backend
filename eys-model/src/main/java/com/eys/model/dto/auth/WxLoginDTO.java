package com.eys.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "微信登录请求")
public class WxLoginDTO {

    /**
     * 微信登录凭证 code
     */
    @NotBlank(message = "登录凭证不能为空")
    @Schema(description = "微信登录凭证code", required = true)
    private String code;

    /**
     * 用户昵称（首次登录时传入）
     */
    @Schema(description = "用户昵称（首次登录时传入）")
    private String nickname;

    /**
     * 用户头像URL（首次登录时传入）
     */
    @Schema(description = "用户头像URL（首次登录时传入）")
    private String avatarUrl;
}
