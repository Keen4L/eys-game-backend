package com.eys.model.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 房间信息VO
 *
 * @author EYS
 */
@Data
@Builder
@Schema(description = "房间信息")
public class RoomVO {

    /**
     * 游戏ID
     */
    @Schema(description = "游戏ID")
    private Long gameId;

    /**
     * 房间邀请码
     */
    @Schema(description = "房间邀请码")
    private String roomCode;

    /**
     * DM用户ID
     */
    @Schema(description = "DM用户ID")
    private Long dmUserId;

    /**
     * DM昵称
     */
    @Schema(description = "DM昵称")
    private String dmNickname;

    /**
     * 地图ID
     */
    @Schema(description = "地图ID")
    private Long mapId;

    /**
     * 地图名称
     */
    @Schema(description = "地图名称")
    private String mapName;

    /**
     * 游戏状态
     */
    @Schema(description = "游戏状态: PREPARING, PLAYING, FINISHED")
    private String status;

    /**
     * 当前轮次
     */
    @Schema(description = "当前轮次")
    private Integer currentRound;

    /**
     * 当前阶段
     */
    @Schema(description = "当前阶段")
    private String currentStage;

    /**
     * 房间内玩家列表
     * - 普通玩家/游戏进行中: 返回 PlayerSafeVO（无身份）
     * - DM/游戏结束: 返回 PlayerFullVO（含身份）
     */
    @Schema(description = "房间内玩家列表")
    private List<? extends PlayerSafeVO> players;

    /**
     * 本局牌组角色ID列表
     */
    @Schema(description = "本局牌组角色ID列表")
    private List<Long> roleIds;
}
