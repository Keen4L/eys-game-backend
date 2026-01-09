package com.eys.service.ga;

import com.eys.model.dto.game.*;
import com.eys.model.vo.game.PlayerGameVO;

/**
 * 游戏核心 Service 接口
 * 处理游戏流程、技能释放、DM 操作等核心逻辑
 * 
 * 注：房间管理由 RoomService 处理，投票由 VoteService 处理
 *
 * @author EYS
 */
public interface GameService {

    // ==================== 游戏流程 ====================

    /**
     * DM开始游戏
     *
     * @param dmUserId DM用户ID
     * @param dto      开始游戏请求
     */
    void startGame(Long dmUserId, StartGameDTO dto);

    /**
     * DM切换游戏阶段
     *
     * @param dmUserId DM用户ID
     * @param dto      阶段切换请求
     */
    void changeStage(Long dmUserId, StageChangeDTO dto);

    /**
     * DM结束游戏
     *
     * @param dmUserId DM用户ID
     * @param dto      游戏结束请求
     */
    void endGame(Long dmUserId, GameEndDTO dto);

    /**
     * 获取玩家游戏视角
     *
     * @param userId 用户ID
     * @param gameId 游戏ID
     * @return 玩家视角
     */
    PlayerGameVO getPlayerGameView(Long userId, Long gameId);

    // ==================== 技能 ====================

    /**
     * 玩家使用技能
     *
     * @param userId 用户ID
     * @param dto    技能使用请求
     */
    void useSkill(Long userId, SkillUseDTO dto);

    /**
     * DM录入技能（DM_INPUT类型）
     *
     * @param dmUserId DM用户ID
     * @param dto      技能使用请求
     */
    void dmInputSkill(Long dmUserId, SkillUseDTO dto);

    /**
     * DM发起技能请求（DM_REQUEST类型）
     *
     * @param dmUserId        DM用户ID
     * @param gameId          游戏ID
     * @param targetPlayerId  目标玩家ID
     * @param skillInstanceId 技能实例ID
     */
    void dmRequestSkill(Long dmUserId, Long gameId, Long targetPlayerId, Long skillInstanceId);

    // ==================== DM 操作 ====================

    /**
     * DM判定玩家死亡
     *
     * @param dmUserId       DM用户ID
     * @param gameId         游戏ID
     * @param targetPlayerId 目标对局玩家ID
     */
    void killPlayer(Long dmUserId, Long gameId, Long targetPlayerId);

    /**
     * DM复活玩家
     *
     * @param dmUserId       DM用户ID
     * @param gameId         游戏ID
     * @param targetPlayerId 目标对局玩家ID
     */
    void revivePlayer(Long dmUserId, Long gameId, Long targetPlayerId);

    /**
     * DM移除玩家标签
     *
     * @param dmUserId       DM用户ID
     * @param gameId         游戏ID
     * @param targetPlayerId 目标对局玩家ID
     * @param tagName        标签名称
     */
    void removeTag(Long dmUserId, Long gameId, Long targetPlayerId, String tagName);

    /**
     * DM代替玩家释放技能
     *
     * @param dmUserId        DM用户ID
     * @param gameId          游戏ID
     * @param actorPlayerId   释放者对局玩家ID
     * @param skillInstanceId 技能实例ID
     * @param targetPlayerId  目标对局玩家ID (可选)
     */
    void proxyCastSkill(Long dmUserId, Long gameId, Long actorPlayerId, Long skillInstanceId, Long targetPlayerId);

    /**
     * DM获取全局视角（所有玩家身份）
     *
     * @param dmUserId DM用户ID
     * @param gameId   游戏ID
     * @return 所有玩家的完整信息
     */
    java.util.List<com.eys.model.vo.game.DmPlayerViewVO> getDmFullView(Long dmUserId, Long gameId);

    /**
     * DM获取动作日志
     *
     * @param dmUserId DM用户ID
     * @param gameId   游戏ID
     * @param roundNo  轮次（可选，为 null 时取所有轮次）
     * @return 动作日志列表
     */
    java.util.List<com.eys.model.entity.ga.GaActionLog> getActionLogs(Long dmUserId, Long gameId, Integer roundNo);
}
