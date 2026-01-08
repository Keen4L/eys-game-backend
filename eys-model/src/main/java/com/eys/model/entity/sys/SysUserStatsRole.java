package com.eys.model.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户角色战绩明细实体
 *
 * @author EYS
 */
@Data
@TableName("sys_user_stats_role")
@Schema(description = "用户角色战绩明细")
public class SysUserStatsRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 角色ID（关联 cfg_role.id）
     */
    @Schema(description = "角色ID")
    private Long roleId;

    /**
     * 该角色总场次
     */
    @Schema(description = "该角色总场次")
    private Integer matchCount;

    /**
     * 该角色胜场次
     */
    @Schema(description = "该角色胜场次")
    private Integer winCount;

    /**
     * 最后一次游玩该角色的时间
     */
    @Schema(description = "最后游玩时间")
    private LocalDateTime lastPlayedAt;
}
