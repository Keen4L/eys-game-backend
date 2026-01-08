package com.eys.model.entity.cfg;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预设牌组实体
 *
 * @author EYS
 */
@Data
@TableName(value = "cfg_deck", autoResultMap = true)
@Schema(description = "预设牌组")
public class CfgDeck implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 牌组名称
     */
    @Schema(description = "牌组名称")
    private String name;

    /**
     * 适用玩家人数
     */
    @Schema(description = "适用玩家人数")
    private Integer playerCount;

    /**
     * 角色ID数组（JSON）
     * 示例: [1, 2, 5, 8, 10]
     */
    @Schema(description = "角色ID数组(JSON)")
    private String roleIds;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
