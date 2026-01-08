package com.eys.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阵营类型枚举
 *
 * @author EYS
 */
@Getter
@AllArgsConstructor
public enum CampType {

    /**
     * 鹅阵营
     */
    GOOSE(0, "鹅阵营"),

    /**
     * 鸭阵营
     */
    DUCK(1, "鸭阵营"),

    /**
     * 中立
     */
    NEUTRAL(2, "中立");

    /**
     * 阵营代码
     */
    private final Integer code;

    /**
     * 阵营描述
     */
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static CampType fromCode(Integer code) {
        for (CampType camp : values()) {
            if (camp.getCode().equals(code)) {
                return camp;
            }
        }
        throw new IllegalArgumentException("未知的阵营类型: " + code);
    }
}
