package com.eys.model.entity.cfg;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 地图出生点配置实体
 *
 * @author EYS
 */
@Data
@TableName("cfg_map_spawn_point")
@Schema(description = "地图出生点配置")
public class CfgMapSpawnPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 所属地图ID
     */
    @Schema(description = "所属地图ID")
    private Long mapId;

    /**
     * 区域名称（如: 实验室）
     */
    @Schema(description = "区域名称")
    private String areaName;

    /**
     * X坐标
     */
    @Schema(description = "X坐标")
    private Integer posX;

    /**
     * Y坐标
     */
    @Schema(description = "Y坐标")
    private Integer posY;

    /**
     * 交互生效宽度
     */
    @Schema(description = "交互生效宽度")
    private Integer activeWidth;

    /**
     * 交互生效高度
     */
    @Schema(description = "交互生效高度")
    private Integer activeHeight;
}
