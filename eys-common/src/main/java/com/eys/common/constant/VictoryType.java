package com.eys.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 胜利类型枚举
 *
 * @author EYS
 */
@Getter
@AllArgsConstructor
public enum VictoryType {

    /**
     * 鹅阵营胜利
     */
    GOOSE_WIN(1, "鹅阵营胜利"),

    /**
     * 鸭阵营胜利
     */
    DUCK_WIN(2, "鸭阵营胜利"),

    /**
     * 中立角色个人胜利
     */
    NEUTRAL_WIN(3, "中立角色胜利");

    /**
     * 胜利类型代码
     */
    private final Integer code;

    /**
     * 胜利类型描述
     */
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static VictoryType fromCode(Integer code) {
        for (VictoryType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的胜利类型: " + code);
    }
}
