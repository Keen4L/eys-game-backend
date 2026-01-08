package com.eys.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 游戏状态枚举
 *
 * @author EYS
 */
@Getter
@AllArgsConstructor
public enum GameStatus {

    /**
     * 准备中（等待玩家加入）
     */
    PREPARING("PREPARING", "准备中"),

    /**
     * 游戏进行中
     */
    PLAYING("PLAYING", "游戏中"),

    /**
     * 游戏已结束
     */
    FINISHED("FINISHED", "已结束");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static GameStatus fromCode(String code) {
        for (GameStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的游戏状态: " + code);
    }
}
