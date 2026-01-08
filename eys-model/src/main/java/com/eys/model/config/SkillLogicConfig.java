package com.eys.model.config;

import lombok.Data;
import java.util.List;

/**
 * 技能逻辑配置（JSON 反序列化目标类）
 * 对应 CfgSkill.skillLogic 字段的 JSON 结构
 *
 * 示例 JSON:
 * {
 * "phases": ["PRE_VOTE", "POST_VOTE"],
 * "usage": { "initial": 1, "group_id": 1001 },
 * "target": { "scope": "ALIVE", "min": 1, "max": 1, "exclude_self": true },
 * "constraints": { "no_consecutive": true }
 * }
 *
 * @author EYS
 */
@Data
public class SkillLogicConfig {

    /**
     * 技能可用阶段列表
     * 例如: ["PRE_VOTE", "POST_VOTE"]
     */
    private List<String> phases;

    /**
     * 技能使用次数配置
     */
    private UsageConfig usage;

    /**
     * 技能目标配置
     */
    private TargetConfig target;

    /**
     * 技能约束条件
     */
    private ConstraintsConfig constraints;

    /**
     * 使用次数配置
     */
    @Data
    public static class UsageConfig {
        /**
         * 初始次数
         */
        private Integer initial = 1;

        /**
         * 技能组ID（同组技能共享次数）
         */
        private Long groupId;

        /**
         * 每轮可用次数（-1 表示无限制）
         */
        private Integer perRound = -1;
    }

    /**
     * 目标配置
     */
    @Data
    public static class TargetConfig {
        /**
         * 目标范围: ALIVE-存活玩家, ALL-所有玩家, DEAD-死亡玩家
         */
        private String scope = "ALIVE";

        /**
         * 最少选择目标数
         */
        private Integer min = 1;

        /**
         * 最多选择目标数
         */
        private Integer max = 1;

        /**
         * 是否排除自己
         */
        private Boolean excludeSelf = false;
    }

    /**
     * 约束条件配置
     */
    @Data
    public static class ConstraintsConfig {
        /**
         * 不能连续对同一目标使用
         */
        private Boolean noConsecutive = false;

        /**
         * 需要猜测角色
         */
        private Boolean requireGuessRole = false;

        /**
         * 第一轮不可用
         */
        private Boolean disableFirstRound = false;
    }
}
