package com.eys.model.entity.ga;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 对局玩家绑定实体
 *
 * @author EYS
 */
@Data
@TableName("ga_game_player")
@Schema(description = "对局玩家")
public class GaGamePlayer implements Serializable {

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
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 座位号
     */
    @Schema(description = "座位号")
    private Integer seatNo;

    /**
     * 初始角色ID
     */
    @Schema(description = "初始角色ID")
    private Long initRoleId;

    /**
     * 当前角色ID（鹦鹉/继承后会变）
     */
    @Schema(description = "当前角色ID")
    private Long currRoleId;

    /**
     * 是否获胜: 1-胜, 0-负
     */
    @Schema(description = "是否获胜: 1-胜, 0-负")
    private Integer isWinner;
}
