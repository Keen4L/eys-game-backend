package com.eys.model.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 玩家完整信息VO（含身份信息）
 * 仅用于 DM 视角或游戏结束后的复盘
 *
 * @author EYS
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "玩家完整信息（含身份）")
public class PlayerFullVO extends PlayerSafeVO {

    /**
     * 当前角色ID
     */
    @Schema(description = "当前角色ID")
    private Long roleId;

    /**
     * 当前角色名称
     */
    @Schema(description = "当前角色名称")
    private String roleName;

    /**
     * 角色阵营: 0-鹅, 1-鸭, 2-中立
     */
    @Schema(description = "角色阵营: 0-鹅, 1-鸭, 2-中立")
    private Integer campType;

    /**
     * 初始角色ID（用于复盘查看身份变化）
     */
    @Schema(description = "初始角色ID")
    private Long initRoleId;

    /**
     * 初始角色名称
     */
    @Schema(description = "初始角色名称")
    private String initRoleName;
}
