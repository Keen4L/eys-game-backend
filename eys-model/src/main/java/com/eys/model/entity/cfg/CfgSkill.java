package com.eys.model.entity.cfg;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 技能逻辑配置实体 (重构版)
 *
 * @author EYS
 */
@Data
@TableName(value = "cfg_skill", autoResultMap = true)
@Schema(description = "技能配置")
public class CfgSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 所属角色ID
     */
    @Schema(description = "所属角色ID")
    private Long roleId;

    /**
     * 技能名称 (同时也是Tag名称)
     */
    @Schema(description = "技能名称")
    private String name;

    /**
     * 自动分发阶段 (逗号分隔)
     * 示例: "ROAMING" 或 "PRE_VOTE,POST_VOTE"
     * 空字符串表示仅DM手动推送
     */
    @Schema(description = "自动分发阶段(逗号分隔), 空则仅DM手动推")
    private String triggerPhases;

    /**
     * 交互类型
     * 0: 直接触发(无需选人)
     * 1: 选人
     * 2: 选人+猜身份
     */
    @Schema(description = "交互类型: 0-直接触发, 1-选人, 2-选人+猜身份")
    private Integer interactionType;

    /**
     * 目标数量
     */
    @Schema(description = "选几个人")
    private Integer targetCount;

    /**
     * 全局总次数
     * -1 表示无限
     */
    @Schema(description = "全局总次数, -1无限")
    private Integer maxUsageTotal;

    /**
     * 每轮次数
     */
    @Schema(description = "每轮次数")
    private Integer maxUsageRound;

    /**
     * 目标存活状态
     * 0: 不限
     * 1: 活人
     * 2: 死人
     */
    @Schema(description = "目标存活状态: 0-不限, 1-活人, 2-死人")
    private Integer targetAliveState;

    /**
     * 是否排除自己
     * 0: 可选自己
     * 1: 排除自己
     */
    @Schema(description = "是否排除自己: 0-可选自己, 1-排除自己")
    private Integer excludeSelf;

    /**
     * 行为类型
     * LOG: 纯记录
     * TAG: 记录+贴标签
     * QUERY: 记录+查验反馈
     */
    @Schema(description = "行为类型: LOG/TAG/QUERY")
    private String behaviorType;

    /**
     * 标签过期规则 (仅 TAG 类型有效)
     * NEXT_ROUND: 下轮失效
     * PELICAN: 鹈鹕存活则在
     * PERMANENT: 永久
     */
    @Schema(description = "标签过期规则: NEXT_ROUND/PELICAN/PERMANENT")
    private String tagExpiryRule;

    /**
     * 标签限制 (仅 TAG 类型有效)
     * NONE: 无限制
     * BLOCK_SKILL: 禁止接收技能推送
     */
    @Schema(description = "标签限制: BLOCK_SKILL/NONE")
    private String tagRestriction;

    /**
     * 查验字段 (仅 QUERY 类型有效)
     * ROLE_ID: 角色ID
     * CAMP_TYPE: 阵营
     * IS_DUCK: 是否是鸭
     * DUCK_COUNT: 鸭子数量
     */
    @Schema(description = "查验字段: ROLE_ID/CAMP_TYPE/IS_DUCK/DUCK_COUNT")
    private String queryField;

    /**
     * 技能图标 URL
     */
    @Schema(description = "技能图标URL")
    private String imgUrl;

    /**
     * 技能描述
     */
    @Schema(description = "技能描述")
    private String description;
}
