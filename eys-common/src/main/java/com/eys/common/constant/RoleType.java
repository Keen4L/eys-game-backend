package com.eys.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色类型枚举
 *
 * @author EYS
 */
@Getter
@AllArgsConstructor
public enum RoleType {

    /**
     * 普通玩家
     */
    PLAYER(0, "玩家"),

    /**
     * DM（主持人）
     */
    DM(1, "DM"),

    /**
     * 管理员
     */
    ADMIN(2, "管理员");

    /**
     * 角色类型代码
     */
    private final Integer code;

    /**
     * 角色类型描述
     */
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static RoleType fromCode(Integer code) {
        for (RoleType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的角色类型: " + code);
    }
}
