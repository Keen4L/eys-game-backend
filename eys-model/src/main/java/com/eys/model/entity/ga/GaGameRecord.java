package com.eys.model.entity.ga;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对局主记录实体
 *
 * @author EYS
 */
@Data
@TableName("ga_game_record")
@Schema(description = "对局记录")
public class GaGameRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 房间邀请码
     */
    @Schema(description = "房间邀请码")
    private String roomCode;

    /**
     * 带本DM用户ID
     */
    @Schema(description = "DM用户ID")
    private Long dmUserId;

    /**
     * 地图ID
     */
    @Schema(description = "地图ID")
    private Long mapId;

    /**
     * 本局使用的角色ID列表 (JSON 格式)
     */
    @Schema(description = "本局使用的角色ID列表")
    private String roleIds;

    /**
     * 游戏状态: PREPARING, PLAYING, FINISHED
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
    @Schema(description = "当前阶段: START, PRE_VOTE, VOTE, POST_VOTE, END")
    private String currentStage;

    /**
     * 胜利类型: 1-鹅胜, 2-鸭胜, 3-中立个人胜
     */
    @Schema(description = "胜利类型: 1-鹅胜, 2-鸭胜, 3-中立个人胜")
    private Integer victoryType;

    /**
     * 中立获胜者用户ID（仅 victoryType=3 时有效）
     */
    @Schema(description = "中立获胜者用户ID")
    private Long winnerUserId;

    /**
     * 游戏开始时间
     */
    @Schema(description = "游戏开始时间")
    private LocalDateTime startedAt;

    /**
     * 游戏结束时间
     */
    @Schema(description = "游戏结束时间")
    private LocalDateTime finishedAt;
}
