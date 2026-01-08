package com.eys.model.entity.sys;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户实体
 *
 * @author EYS
 */
@Data
@TableName("sys_user")
@Schema(description = "系统用户")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 微信小程序OpenID
     */
    @Schema(description = "微信OpenID")
    private String openid;

    /**
     * 后台管理员账号
     */
    @Schema(description = "管理员账号")
    private String username;

    /**
     * 加密密码
     */
    @Schema(description = "加密密码")
    private String password;

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
     * 权限: 0-玩家, 1-DM, 2-管理员
     */
    @Schema(description = "权限类型: 0-玩家, 1-DM, 2-管理员")
    private Integer roleType;

    /**
     * 状态: 1-启用, 0-禁用
     */
    @Schema(description = "状态: 1-启用, 0-禁用")
    private Integer isEnabled;

    /**
     * 注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "注册时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
