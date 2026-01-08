package com.eys.model.entity.ga;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 玩家实时状态实体
 *
 * @author EYS
 */
@Data
@TableName(value = "ga_player_status", autoResultMap = true)
@Schema(description = "玩家实时状态")
public class GaPlayerStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联 ga_game_player.id
     */
    @TableId
    @Schema(description = "对局玩家ID")
    private Long gamePlayerId;

    /**
     * 是否存活: 1-存活, 0-死亡
     */
    @Schema(description = "是否存活: 1-存活, 0-死亡")
    private Integer isAlive;

    /**
     * 死亡轮次
     */
    @Schema(description = "死亡轮次")
    private Integer deathRound;

    /**
     * 死亡阶段
     */
    @Schema(description = "死亡阶段")
    private String deathStage;

    /**
     * 当前生效的Buff/Debuff集合（JSON）
     * 示例: {"MUTED": {"source_id": 5, "round": 2}, "PROTECTED": {"source_id": 3,
     * "round": 2}}
     */
    @Schema(description = "当前生效的效果集合(JSON)")
    private String activeEffects;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
