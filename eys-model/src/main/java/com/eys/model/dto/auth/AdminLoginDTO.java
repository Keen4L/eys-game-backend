package com.eys.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员登录请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "管理员登录请求")
public class AdminLoginDTO {

    /**
     * 管理员账号
     */
    @NotBlank(message = "账号不能为空")
    @Schema(description = "管理员账号", example = "admin")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "admin123")
    private String password;
}
