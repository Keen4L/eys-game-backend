package com.eys.model.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 技能实例VO
 *
 * @author EYS
 */
@Data
@Builder
@Schema(description = "技能实例")
public class SkillInstanceVO {

    /**
     * 技能实例ID
     */
    @Schema(description = "技能实例ID")
    private Long id;

    /**
     * 技能配置ID
     */
    @Schema(description = "技能配置ID")
    private Long skillId;

    /**
     * 技能名称
     */
    @Schema(description = "技能名称")
    private String skillName;

    /**
     * 技能描述
     */
    @Schema(description = "技能描述")
    private String description;

    /**
     * 交互类型
     */
    @Schema(description = "交互类型: 0-无需选择, 1-选择玩家, 2-选择玩家+角色")
    private Integer interactionType;

    /**
     * 剩余使用次数
     */
    @Schema(description = "剩余使用次数，-1表示无限")
    private Integer remainCount;

    /**
     * 是否可在当前阶段使用
     */
    @Schema(description = "是否可在当前阶段使用")
    private Boolean canUseNow;

    /**
     * 可选目标玩家ID列表（后端计算好直接返回）
     */
    @Schema(description = "可选目标玩家ID列表")
    private List<Long> validTargetIds;
}
