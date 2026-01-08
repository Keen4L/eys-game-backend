package com.eys.model.entity.cfg;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 技能逻辑配置实体
 *
 * @author EYS
 */
@Data
@TableName(value = "cfg_skill", autoResultMap = true)
@Schema(description = "技能配置")
public class CfgSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 所属角色ID
     */
    @Schema(description = "所属角色ID")
    private Long roleId;

    /**
     * 技能名称
     */
    @Schema(description = "技能名称")
    private String name;

    /**
     * 交互类型: 0-NONE, 1-PLAYER, 2-PLAYER_ROLE
     */
    @Schema(description = "交互类型: 0-NONE, 1-PLAYER, 2-PLAYER_ROLE")
    private Integer interactionType;

    /**
     * 触发模式: 0-PLAYER_ACTIVE, 1-DM_REQUEST, 2-DM_INPUT
     */
    @Schema(description = "触发模式: 0-PLAYER_ACTIVE, 1-DM_REQUEST, 2-DM_INPUT")
    private Integer triggerMode;

    /**
     * 技能逻辑规则集（JSON）
     * 示例:
     * {
     * "phases": ["PRE_VOTE", "POST_VOTE"],
     * "usage": { "initial": 1, "group_id": 1001 },
     * "target": { "scope": "ALIVE", "min": 1, "max": 1, "exclude_self": true },
     * "constraints": { "no_consecutive": true }
     * }
     */
    @Schema(description = "技能逻辑规则集(JSON)")
    private String skillLogic;

    /**
     * 技能图标 URL
     */
    @Schema(description = "技能图标URL")
    private String imgUrl;

    /**
     * 技能描述
     */
    @Schema(description = "技能描述")
    private String description;
}
