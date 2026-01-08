package com.eys.model.entity.cfg;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色基础信息实体
 *
 * @author EYS
 */
@Data
@TableName("cfg_role")
@Schema(description = "角色配置")
public class CfgRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String name;

    /**
     * 阵营: 0-鹅, 1-鸭, 2-中立
     */
    @Schema(description = "阵营类型: 0-鹅, 1-鸭, 2-中立")
    private Integer campType;

    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    private String description;

    /**
     * 角色卡牌/头像URL
     */
    @Schema(description = "角色图片URL")
    private String imgUrl;

    /**
     * 是否启用: 1-启用, 0-禁用
     */
    @Schema(description = "是否启用: 1-启用, 0-禁用")
    private Integer isEnabled;
}
