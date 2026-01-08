package com.eys.model.dto.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询请求DTO
 *
 * @author EYS
 */
@Data
@Schema(description = "用户查询请求")
public class SysUserQueryDTO {

    /**
     * 用户昵称（模糊查询）
     */
    @Schema(description = "用户昵称（模糊查询）")
    private String nickname;

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

    /**
     * 页码
     */
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}
