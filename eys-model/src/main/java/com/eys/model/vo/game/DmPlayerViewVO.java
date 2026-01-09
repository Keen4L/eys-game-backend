package com.eys.model.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DM 视角玩家信息 VO
 * 包含完整的玩家身份信息（仅 DM 可见）
 *
 * @author EYS
 */
@Data
@Builder
@Schema(description = "DM视角玩家信息")
public class DmPlayerViewVO {

    @Schema(description = "对局玩家ID")
    private Long gamePlayerId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "座位号")
    private Integer seatNo;

    @Schema(description = "初始角色ID")
    private Long initRoleId;

    @Schema(description = "初始角色名")
    private String initRoleName;

    @Schema(description = "当前角色ID")
    private Long currRoleId;

    @Schema(description = "当前角色名")
    private String currRoleName;

    @Schema(description = "阵营类型: 0-鹅, 1-鸭, 2-中立")
    private Integer campType;

    @Schema(description = "是否存活")
    private Boolean alive;

    @Schema(description = "当前生效的 Tag 列表")
    private List<String> activeTags;
}
