package com.eys.model.entity.ga;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 玩家每轮次出生位置记录实体
 *
 * @author EYS
 */
@Data
@TableName("ga_player_spawn")
@Schema(description = "玩家出生点记录")
public class GaPlayerSpawn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 对局ID
     */
    @Schema(description = "对局ID")
    private Long gameId;

    /**
     * 轮次
     */
    @Schema(description = "轮次")
    private Integer roundNo;

    /**
     * 对局玩家ID
     */
    @Schema(description = "对局玩家ID")
    private Long gamePlayerId;

    /**
     * 出生点ID
     */
    @Schema(description = "出生点ID")
    private Long spawnPointId;
}
