package com.eys.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * WebSocket消息类型枚举
 *
 * @author EYS
 */
@Getter
@AllArgsConstructor
public enum WsMessageType {

    /**
     * 阶段变更通知
     */
    STAGE_CHANGE("STAGE_CHANGE", "阶段变更"),

    /**
     * 玩家存活状态变更
     */
    PLAYER_STATUS("PLAYER_STATUS", "存活状态变更"),

    /**
     * DM请求玩家释放技能
     */
    SKILL_REQUEST("SKILL_REQUEST", "技能释放请求"),

    /**
     * 投票结果统计
     */
    VOTE_RESULT("VOTE_RESULT", "投票结果"),

    /**
     * 游戏结束通知
     */
    GAME_END("GAME_END", "游戏结束"),

    /**
     * 断线重连后同步状态
     */
    RECONNECT_SYNC("RECONNECT_SYNC", "重连同步"),

    /**
     * 玩家加入房间
     */
    PLAYER_JOIN("PLAYER_JOIN", "玩家加入"),

    /**
     * 玩家离开房间
     */
    PLAYER_LEAVE("PLAYER_LEAVE", "玩家离开"),

    /**
     * 身份牌分发
     */
    ROLE_ASSIGNED("ROLE_ASSIGNED", "身份分发"),

    /**
     * 技能使用通知
     */
    SKILL_USED("SKILL_USED", "技能使用"),

    /**
     * 游戏开始
     */
    GAME_START("GAME_START", "游戏开始"),

    /**
     * DM 请求玩家使用技能
     */
    DM_REQUEST_SKILL("DM_REQUEST_SKILL", "DM请求技能");

    /**
     * 消息类型代码
     */
    private final String code;

    /**
     * 消息类型描述
     */
    private final String desc;
}
