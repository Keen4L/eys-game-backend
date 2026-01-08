package com.eys.model.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 登录响应VO
 *
 * @author EYS
 */
@Data
@Builder
@Schema(description = "登录响应")
public class LoginVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatarUrl;

    /**
     * 角色类型
     */
    @Schema(description = "角色类型: 0-玩家, 1-DM, 2-管理员")
    private Integer roleType;

    /**
     * Token
     */
    @Schema(description = "登录Token")
    private String token;
}
