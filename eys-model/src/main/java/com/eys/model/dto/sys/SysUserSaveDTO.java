package com.eys.model.dto.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户保存请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "用户保存请求")
public class SysUserSaveDTO {

    /**
     * 用户ID（编辑时传入）
     */
    @Schema(description = "用户ID，编辑时传入")
    private Long id;

    /**
     * 管理员账号
     */
    @Schema(description = "管理员账号")
    private String username;

    /**
     * 密码（新增或修改密码时传入）
     */
    @Schema(description = "密码，新增必填，编辑时不传则不修改")
    private String password;

    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空")
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
     * 是否启用
     */
    @Schema(description = "是否启用: 1-启用, 0-禁用")
    private Integer isEnabled;
}
