package com.eys.common.constant;

/**
 * 游戏效果常量
 * 定义玩家可能被挂载的状态效果类型
 * 根据角色表设计
 *
 * @author EYS
 */
public final class GameEffectConstant {

    private GameEffectConstant() {
    }

    // ==================== 禁言鸭(22) ====================

    /**
     * 禁言 - 不能说话/投票（禁言鸭技能）
     */
    public static final String SILENCED = "SILENCED";

    // ==================== 梦魇鸭(25) ====================

    /**
     * 被梦魇 - 封刀封技能（梦魇鸭技能）
     */
    public static final String NIGHTMARED = "NIGHTMARED";

    // ==================== 鹈鹕(18) ====================

    /**
     * 被吞噬 - 完全失效（鹈鹕技能，累计吃四人胜利）
     */
    public static final String SWALLOWED = "SWALLOWED";

    // ==================== 保镖鹅(5) ====================

    /**
     * 被保护 - 免疫一次击杀（保镖鹅技能）
     */
    public static final String PROTECTED = "PROTECTED";

    // ==================== 医生鹅(7) ====================

    /**
     * 中毒 - 延迟死亡（医生鹅毒药）
     */
    public static final String POISONED = "POISONED";

    /**
     * 被解救 - 免死一次（医生鹅解药）
     */
    public static final String HEALED = "HEALED";

    // ==================== 恋爱脑鹅(11) ====================

    /**
     * 被恋爱脑连接 - 同两天连同一人则那人被点杀
     */
    public static final String LOVE_LINKED = "LOVE_LINKED";

    // ==================== 美女鸭(20) ====================

    /**
     * 被美女鸭连接 - 自己死对方死
     */
    public static final String BEAUTY_LINKED = "BEAUTY_LINKED";

    // ==================== 忍者鸭(21) ====================

    /**
     * 被忍者鸭连接 - 忍者被投出去时一起死
     */
    public static final String NINJA_LINKED = "NINJA_LINKED";

    // ==================== 警长鹅(4) ====================

    /**
     * 被禁闭 - 无法行动（警长鹅全局一次禁闭）
     */
    public static final String DETAINED = "DETAINED";

    // ==================== 加拿大鹅(6) ====================

    /**
     * 加拿大效果 - 本回合鸭子不可发动任何技能
     */
    public static final String CANADA_EFFECT = "CANADA_EFFECT";

    // ==================== 魔术鹅(12) ====================

    /**
     * 生命置换 - 被魔术鹅置换生命
     */
    public static final String LIFE_SWAPPED = "LIFE_SWAPPED";

    // ==================== 决斗鹅(13) ====================

    /**
     * 明牌状态 - 决斗后身份公开，不可被刺客刺
     */
    public static final String REVEALED = "REVEALED";
}
