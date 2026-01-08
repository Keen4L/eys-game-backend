package com.eys.model.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 玩家自己的游戏视角VO
 *
 * @author EYS
 */
@Data
@Builder
@Schema(description = "玩家游戏视角")
public class PlayerGameVO {

    /**
     * 游戏ID
     */
    @Schema(description = "游戏ID")
    private Long gameId;

    /**
     * 我的对局玩家ID
     */
    @Schema(description = "我的对局玩家ID")
    private Long myGamePlayerId;

    /**
     * 我的座位号
     */
    @Schema(description = "我的座位号")
    private Integer mySeatNo;

    /**
     * 我的角色ID
     */
    @Schema(description = "我的角色ID")
    private Long myRoleId;

    /**
     * 我的角色名称
     */
    @Schema(description = "我的角色名称")
    private String myRoleName;

    /**
     * 我的角色阵营
     */
    @Schema(description = "我的角色阵营: 0-鹅, 1-鸭, 2-中立")
    private Integer myCampType;

    /**
     * 我是否存活
     */
    @Schema(description = "我是否存活")
    private Boolean alive;

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
     * 我的技能列表
     */
    @Schema(description = "我的技能列表")
    private List<SkillInstanceVO> skills;

    /**
     * 场上所有玩家（脱敏后，不含角色信息）
     */
    @Schema(description = "场上所有玩家（脱敏）")
    private List<PlayerSafeVO> players;
}
