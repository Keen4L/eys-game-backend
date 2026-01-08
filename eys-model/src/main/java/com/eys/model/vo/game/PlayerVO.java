package com.eys.model.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 玩家信息VO
 *
 * @author EYS
 */
@Data
@Builder
@Schema(description = "玩家信息")
public class PlayerVO {

    /**
     * 对局玩家ID
     */
    @Schema(description = "对局玩家ID")
    private Long gamePlayerId;

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
     * 座位号
     */
    @Schema(description = "座位号")
    private Integer seatNo;

    /**
     * 是否存活
     */
    @Schema(description = "是否存活")
    private Boolean alive;

    /**
     * 当前角色ID（仅DM可见或游戏结束后可见）
     */
    @Schema(description = "当前角色ID（仅DM或游戏结束后可见）")
    private Long roleId;

    /**
     * 当前角色名称（仅DM可见或游戏结束后可见）
     */
    @Schema(description = "当前角色名称（仅DM或游戏结束后可见）")
    private String roleName;
}
