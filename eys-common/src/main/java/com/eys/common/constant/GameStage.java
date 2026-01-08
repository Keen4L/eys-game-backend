package com.eys.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 游戏阶段枚举
 * 定义游戏流程中的各个阶段
 *
 * @author EYS
 */
@Getter
@AllArgsConstructor
public enum GameStage {

    /**
     * 开始阶段：分发身份牌，开局技能释放
     */
    START("START", "开始阶段"),

    /**
     * 投票前阶段：黑夜/技能释放
     */
    PRE_VOTE("PRE_VOTE", "投票前阶段"),

    /**
     * 投票阶段：存活玩家投票
     */
    VOTE("VOTE", "投票阶段"),

    /**
     * 投票后阶段：白昼/技能释放
     */
    POST_VOTE("POST_VOTE", "投票后阶段"),

    /**
     * 结束阶段：DM判定胜负
     */
    END("END", "结束阶段");

    /**
     * 阶段代码
     */
    private final String code;

    /**
     * 阶段描述
     */
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static GameStage fromCode(String code) {
        for (GameStage stage : values()) {
            if (stage.getCode().equals(code)) {
                return stage;
            }
        }
        throw new IllegalArgumentException("未知的游戏阶段: " + code);
    }
}
