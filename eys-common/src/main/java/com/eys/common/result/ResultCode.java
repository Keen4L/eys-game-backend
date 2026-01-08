package com.eys.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务错误码枚举
 *
 * @author EYS
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // ==================== 通用错误码 ====================
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败
     */
    FAILURE(500, "操作失败"),

    /**
     * 参数校验失败
     */
    PARAM_ERROR(400, "参数校验失败"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 权限不足
     */
    FORBIDDEN(403, "权限不足"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    // ==================== 业务错误码 (1xxx) ====================
    /**
     * 用户相关
     */
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    USERNAME_EXISTS(1003, "用户名已存在"),
    PASSWORD_ERROR(1004, "密码错误"),

    // ==================== 游戏错误码 (2xxx) ====================
    /**
     * 房间相关
     */
    ROOM_NOT_FOUND(2001, "房间不存在"),
    ROOM_FULL(2002, "房间已满"),
    ROOM_PLAYING(2003, "游戏已开始，无法加入"),
    ROOM_CODE_INVALID(2004, "房间码无效"),

    /**
     * 游戏流程相关
     */
    GAME_NOT_STARTED(2101, "游戏尚未开始"),
    GAME_ALREADY_ENDED(2102, "游戏已结束"),
    STAGE_TRANSITION_ERROR(2103, "阶段切换错误"),
    NOT_YOUR_TURN(2104, "不是您的操作回合"),

    /**
     * 技能相关
     */
    SKILL_NOT_AVAILABLE(2201, "技能不可用"),
    SKILL_NO_REMAINING(2202, "技能次数已用尽"),
    SKILL_STAGE_MISMATCH(2203, "当前阶段不可释放该技能"),
    SKILL_TARGET_INVALID(2204, "技能目标无效"),

    /**
     * 投票相关
     */
    VOTE_NOT_ALLOWED(2301, "当前不可投票"),
    VOTE_ALREADY_SUBMITTED(2302, "您已投票"),
    VOTE_TARGET_DEAD(2303, "投票目标已死亡"),

    /**
     * 玩家状态相关
     */
    PLAYER_DEAD(2401, "玩家已死亡"),
    PLAYER_NOT_IN_GAME(2402, "玩家不在游戏中"),
    PLAYER_ALREADY_JOINED(2403, "玩家已在房间中"),

    // ==================== 配置错误码 (3xxx) ====================
    /**
     * 地图相关
     */
    MAP_NOT_FOUND(3001, "地图不存在"),
    MAP_IN_USE(3002, "地图正在使用中，无法删除"),

    /**
     * 角色相关
     */
    ROLE_NOT_FOUND(3101, "角色不存在"),
    ROLE_IN_USE(3102, "角色正在使用中，无法删除"),

    /**
     * 牌组相关
     */
    DECK_NOT_FOUND(3201, "牌组不存在"),
    DECK_PLAYER_COUNT_MISMATCH(3202, "牌组人数与玩家数不匹配");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;
}
