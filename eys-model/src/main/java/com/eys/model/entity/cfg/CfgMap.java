package com.eys.model.entity.cfg;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 地图基础配置实体
 *
 * @author EYS
 */
@Data
@TableName("cfg_map")
@Schema(description = "地图配置")
public class CfgMap implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 地图名称
     */
    @Schema(description = "地图名称")
    private String name;

    /**
     * 底图资源URL
     */
    @Schema(description = "底图资源URL")
    private String backgroundUrl;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
