package com.eys.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 技能交互类型枚举
 *
 * @author EYS
 */
@Getter
@AllArgsConstructor
public enum InteractionType {

    /**
     * 无需选择目标
     */
    NONE(0, "无需选择目标"),

    /**
     * 选择玩家
     */
    PLAYER(1, "选择玩家"),

    /**
     * 选择玩家 + 猜测角色
     */
    PLAYER_ROLE(2, "选择玩家+猜测角色");

    /**
     * 交互类型代码
     */
    private final Integer code;

    /**
     * 交互类型描述
     */
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static InteractionType fromCode(Integer code) {
        for (InteractionType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的交互类型: " + code);
    }
}
