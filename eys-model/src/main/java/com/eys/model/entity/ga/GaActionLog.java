package com.eys.model.entity.ga;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 全量动作流水实体
 *
 * @author EYS
 */
@Data
@TableName(value = "ga_action_log", autoResultMap = true)
@Schema(description = "动作流水")
public class GaActionLog implements Serializable {

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
     * 阶段
     */
    @Schema(description = "阶段")
    private String stage;

    /**
     * 动作来源: 0-玩家发起, 1-DM录入/干预
     */
    @Schema(description = "动作来源: 0-玩家发起, 1-DM录入")
    private Integer sourceType;

    /**
     * 动作类型: SKILL, KILL, REVIVE, VOTE, SYSTEM
     */
    @Schema(description = "动作类型")
    private String actionType;

    /**
     * 执行者ID（玩家ID，DM操作则为0）
     */
    @Schema(description = "执行者ID")
    private Long actorId;

    /**
     * 关联技能ID
     */
    @Schema(description = "关联技能ID")
    private Long skillId;

    /**
     * 动作载荷数据（JSON）
     * 示例: {"target_ids": [5], "guess_role_id": 10}
     */
    @Schema(description = "动作载荷数据(JSON)")
    private String actionData;

    /**
     * 结果备注
     */
    @Schema(description = "结果备注")
    private String resultNote;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
