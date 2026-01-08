package com.eys.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 技能触发模式枚举
 *
 * @author EYS
 */
@Getter
@AllArgsConstructor
public enum TriggerMode {

    /**
     * 玩家主动释放
     * 系统自动推送通知，玩家在对应阶段可主动使用
     */
    PLAYER_ACTIVE(0, "玩家主动释放"),

    /**
     * DM发起请求
     * DM点击后系统向目标玩家推送技能使用通知，玩家响应
     */
    DM_REQUEST(1, "DM发起请求"),

    /**
     * DM直接录入
     * 敏感操作如晚上杀人，DM直接记录结果，不经过玩家
     */
    DM_INPUT(2, "DM直接录入");

    /**
     * 触发模式代码
     */
    private final Integer code;

    /**
     * 触发模式描述
     */
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static TriggerMode fromCode(Integer code) {
        for (TriggerMode mode : values()) {
            if (mode.getCode().equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("未知的触发模式: " + code);
    }
}
