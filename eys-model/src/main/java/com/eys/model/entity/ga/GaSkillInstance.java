package com.eys.model.entity.ga;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 技能运行时实例实体
 *
 * @author EYS
 */
@Data
@TableName("ga_skill_instance")
@Schema(description = "技能运行时实例")
public class GaSkillInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 对局玩家ID
     */
    @Schema(description = "对局玩家ID")
    private Long gamePlayerId;

    /**
     * 技能配置ID
     */
    @Schema(description = "技能配置ID")
    private Long skillId;

    /**
     * 剩余使用次数
     */
    @Schema(description = "剩余使用次数")
    private Integer remainCount;

    /**
     * 是否激活: 1-激活, 0-失效
     */
    @Schema(description = "是否激活: 1-激活, 0-失效")
    private Integer isActive;

    /**
     * 技能组ID（用于同组技能共享次数）
     */
    @Schema(description = "技能组ID")
    private Long groupId;
}
