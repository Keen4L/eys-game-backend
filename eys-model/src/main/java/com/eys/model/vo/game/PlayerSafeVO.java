package com.eys.model.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 玩家安全信息VO（不含身份信息）
 * 用于返回给普通玩家查看其他玩家信息
 *
 * @author EYS
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "玩家安全信息（无身份）")
public class PlayerSafeVO {

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
}
