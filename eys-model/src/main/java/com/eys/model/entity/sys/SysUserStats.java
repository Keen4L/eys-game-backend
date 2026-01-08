package com.eys.model.entity.sys;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户总战绩实体
 *
 * @author EYS
 */
@Data
@TableName("sys_user_stats")
@Schema(description = "用户总战绩")
public class SysUserStats implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（关联 sys_user.id）
     */
    @TableId
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 总对局数
     */
    @Schema(description = "总对局数")
    private Integer totalMatches;

    /**
     * 总胜场
     */
    @Schema(description = "总胜场")
    private Integer totalWins;

    /**
     * 鹅阵营胜场
     */
    @Schema(description = "鹅阵营胜场")
    private Integer gooseWins;

    /**
     * 鸭阵营胜场
     */
    @Schema(description = "鸭阵营胜场")
    private Integer duckWins;

    /**
     * 中立胜场
     */
    @Schema(description = "中立胜场")
    private Integer neutralWins;
}
