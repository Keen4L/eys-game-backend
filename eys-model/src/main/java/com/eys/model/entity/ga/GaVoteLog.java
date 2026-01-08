package com.eys.model.entity.ga;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票记录实体
 *
 * @author EYS
 */
@Data
@TableName("ga_vote_log")
@Schema(description = "投票记录")
public class GaVoteLog implements Serializable {

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
     * 投票者ID
     */
    @Schema(description = "投票者ID")
    private Long voterId;

    /**
     * 被投者ID（NULL为弃票）
     */
    @Schema(description = "被投者ID，NULL为弃票")
    private Long targetId;

    /**
     * 是否跳过: 0-正常投票/弃票, 1-被技能跳过
     */
    @Schema(description = "是否跳过: 0-正常, 1-被技能跳过")
    private Integer isSkipped;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
