package com.eys.model.vo.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户列表VO
 *
 * @author EYS
 */
@Data
@Schema(description = "用户列表")
public class SysUserVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 微信OpenID
     */
    @Schema(description = "微信OpenID")
    private String openid;

    /**
     * 管理员账号
     */
    @Schema(description = "管理员账号")
    private String username;

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
     * 是否启用
     */
    @Schema(description = "是否启用: 1-启用, 0-禁用")
    private Integer isEnabled;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间")
    private LocalDateTime createdAt;

    /**
     * 总对局数
     */
    @Schema(description = "总对局数")
    private Integer totalMatches;

    /**
     * 总胜场
     */
    @Schema(description = "总胜场")
    private Integer totalWins;
}
