package com.eys.service.ga.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eys.common.constant.*;
import com.eys.common.exception.BizException;
import com.eys.common.result.ResultCode;
import com.eys.model.dto.game.*;
import com.eys.model.entity.cfg.CfgDeck;
import com.eys.model.entity.cfg.CfgMap;
import com.eys.model.entity.cfg.CfgRole;
import com.eys.model.entity.cfg.CfgSkill;
import com.eys.model.entity.ga.*;
import com.eys.model.entity.sys.SysUser;
import com.eys.model.vo.game.*;
import com.eys.service.cfg.CfgDeckService;
import com.eys.service.cfg.CfgMapService;
import com.eys.service.cfg.CfgRoleService;
import com.eys.service.cfg.CfgSkillService;
import com.eys.service.ga.*;
import com.eys.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import com.eys.service.event.GameStageChangeEvent;
import com.eys.service.event.PlayerStatusChangeEvent;
import com.eys.service.event.SkillUsedEvent;
import com.eys.engine.SkillValidator;

/**
 * 游戏核心 Service 实现
 *
 * @author EYS
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GaGameRecordService gameRecordService;
    private final GaGamePlayerService gamePlayerService;
    private final GaPlayerStatusService playerStatusService;
    private final GaSkillInstanceService skillInstanceService;
    private final GaActionLogService actionLogService;
    private final GaVoteLogService voteLogService;
    private final CfgMapService mapService;
    private final CfgRoleService roleService;
    private final CfgSkillService skillService;
    private final CfgDeckService deckService;
    private final SysUserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final SkillValidator skillValidator;
    private final com.eys.engine.skill.SkillHandlerFactory skillHandlerFactory;

    // ==================== 房间管理 ====================

    @Override
    @Transactional
    public RoomVO createRoom(Long dmUserId, CreateRoomDTO dto) {
        // 验证地图存在
        CfgMap map = mapService.getById(dto.getMapId());
        if (map == null) {
            throw new BizException(ResultCode.MAP_NOT_FOUND);
        }

        // 确定角色列表
        List<Long> roleIds;
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            roleIds = dto.getRoleIds();
        } else if (dto.getDeckId() != null) {
            CfgDeck deck = deckService.getById(dto.getDeckId());
            if (deck == null) {
                throw new BizException(ResultCode.DECK_NOT_FOUND);
            }
            roleIds = JSONArray.parseArray(deck.getRoleIds(), Long.class);
        } else {
            throw new BizException("请选择预设牌组或自定义角色列表");
        }

        // 生成房间码（6位大写字母+数字）
        String roomCode = generateRoomCode();

        // 创建对局记录
        GaGameRecord record = new GaGameRecord();
        record.setRoomCode(roomCode);
        record.setDmUserId(dmUserId);
        record.setMapId(dto.getMapId());
        record.setStatus(GameStatus.PREPARING.getCode());
        record.setCurrentRound(1);
        record.setCurrentStage(GameStage.START.getCode());
        gameRecordService.save(record);

        log.info("DM创建房间: dmUserId={}, gameId={}, roomCode={}", dmUserId, record.getId(), roomCode);

        return buildRoomVO(record, dmUserId, roleIds);
    }

    @Override
    @Transactional
    public RoomVO joinRoom(Long userId, JoinRoomDTO dto) {
        // 根据房间码查找游戏
        GaGameRecord record = gameRecordService.getOne(
                new LambdaQueryWrapper<GaGameRecord>().eq(GaGameRecord::getRoomCode, dto.getRoomCode().toUpperCase()));

        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        // 检查游戏状态
        if (!GameStatus.PREPARING.getCode().equals(record.getStatus())) {
            throw new BizException(ResultCode.ROOM_PLAYING);
        }

        // 检查是否已在房间中
        GaGamePlayer existPlayer = gamePlayerService.getByGameAndUser(record.getId(), userId);
        if (existPlayer != null) {
            throw new BizException(ResultCode.PLAYER_ALREADY_JOINED);
        }

        // 加入房间
        GaGamePlayer player = new GaGamePlayer();
        player.setGameId(record.getId());
        player.setUserId(userId);
        player.setSeatNo(gamePlayerService.getNextSeatNo(record.getId()));
        player.setIsWinner(0);
        gamePlayerService.save(player);

        log.info("玩家加入房间: userId={}, gameId={}", userId, record.getId());

        return getRoomInfo(record.getId(), userId);
    }

    @Override
    @Transactional
    public void leaveRoom(Long userId, Long gameId) {
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        // 只能在准备阶段退出
        if (!GameStatus.PREPARING.getCode().equals(record.getStatus())) {
            throw new BizException("游戏已开始，无法退出");
        }

        GaGamePlayer player = gamePlayerService.getByGameAndUser(gameId, userId);
        if (player != null) {
            gamePlayerService.removeById(player.getId());
            log.info("玩家退出房间: userId={}, gameId={}", userId, gameId);
        }
    }

    @Override
    public RoomVO getRoomInfo(Long gameId, Long userId) {
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }
        return buildRoomVO(record, userId, null);
    }

    @Override
    public RoomVO getRoomByCode(String roomCode) {
        GaGameRecord record = gameRecordService
                .getOne(new LambdaQueryWrapper<GaGameRecord>().eq(GaGameRecord::getRoomCode, roomCode.toUpperCase()));
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }
        return buildRoomVO(record, null, null);
    }

    // ==================== 游戏流程 ====================

    @Override
    @Transactional
    public void startGame(Long dmUserId, StartGameDTO dto) {
        GaGameRecord record = gameRecordService.getById(dto.getGameId());
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        // 验证DM
        if (!record.getDmUserId().equals(dmUserId)) {
            throw new BizException(ResultCode.FORBIDDEN, "只有DM可以开始游戏");
        }

        // 验证状态
        if (!GameStatus.PREPARING.getCode().equals(record.getStatus())) {
            throw new BizException("游戏已开始或已结束");
        }

        // 获取玩家列表
        List<GaGamePlayer> players = gamePlayerService.listByGameId(dto.getGameId());
        if (players.isEmpty()) {
            throw new BizException("房间内没有玩家");
        }

        // TODO: 从数据库获取牌组角色ID列表，这里暂时用固定数据
        // 实际应该在 CreateRoom 时保存牌组信息到对局记录中

        // 分配角色（内定 + 随机）
        Map<Long, Long> fixedRoles = dto.getFixedRoles() != null ? dto.getFixedRoles() : new HashMap<>();

        // 获取所有可用角色
        List<CfgRole> allRoles = roleService.list(new LambdaQueryWrapper<CfgRole>().eq(CfgRole::getIsEnabled, 1));

        if (allRoles.size() < players.size()) {
            throw new BizException("角色数量不足，无法开始游戏");
        }

        // 随机分配
        List<Long> availableRoleIds = allRoles.stream().map(CfgRole::getId)
                .filter(roleId -> !fixedRoles.containsValue(roleId)).collect(Collectors.toList());
        Collections.shuffle(availableRoleIds);

        int randomIndex = 0;
        for (GaGamePlayer player : players) {
            Long roleId;
            if (fixedRoles.containsKey(player.getUserId())) {
                roleId = fixedRoles.get(player.getUserId());
            } else {
                if (randomIndex >= availableRoleIds.size()) {
                    throw new BizException("可用角色不足");
                }
                roleId = availableRoleIds.get(randomIndex++);
            }

            // 设置角色
            player.setInitRoleId(roleId);
            player.setCurrRoleId(roleId);
            gamePlayerService.updateById(player);

            // 创建玩家状态
            GaPlayerStatus status = new GaPlayerStatus();
            status.setGamePlayerId(player.getId());
            status.setIsAlive(1);
            playerStatusService.save(status);

            // 创建技能实例
            List<CfgSkill> skills = skillService.listByRoleId(roleId);
            for (CfgSkill skill : skills) {
                GaSkillInstance instance = new GaSkillInstance();
                instance.setGamePlayerId(player.getId());
                instance.setSkillId(skill.getId());

                // 使用 SkillValidator 解析技能配置（替代手动 JSON 解析）
                int initialCount = skillValidator.getInitialCount(skill);
                instance.setRemainCount(initialCount);
                instance.setIsActive(1);

                // 使用 SkillValidator 解析技能组ID
                Long groupId = skillValidator.getGroupId(skill);
                instance.setGroupId(groupId);

                skillInstanceService.save(instance);
            }
        }

        // 更新游戏状态
        record.setStatus(GameStatus.PLAYING.getCode());
        record.setStartedAt(LocalDateTime.now());
        gameRecordService.updateById(record);

        log.info("游戏开始: gameId={}, playerCount={}", dto.getGameId(), players.size());
    }

    @Override
    @Transactional
    public void changeStage(Long dmUserId, StageChangeDTO dto) {
        GaGameRecord record = gameRecordService.getById(dto.getGameId());
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        // 验证DM
        if (!record.getDmUserId().equals(dmUserId)) {
            throw new BizException(ResultCode.FORBIDDEN, "只有DM可以切换阶段");
        }

        // 验证游戏状态
        if (!GameStatus.PLAYING.getCode().equals(record.getStatus())) {
            throw new BizException(ResultCode.GAME_NOT_STARTED);
        }

        // 如果是进入下一轮
        if (Boolean.TRUE.equals(dto.getNextRound())) {
            record.setCurrentRound(record.getCurrentRound() + 1);
        }

        record.setCurrentStage(dto.getTargetStage());
        gameRecordService.updateById(record);

        log.info("阶段切换: gameId={}, stage={}, round={}", dto.getGameId(), dto.getTargetStage(),
                record.getCurrentRound());

        // 发布阶段变更事件，触发 WebSocket 广播
        String oldStage = record.getCurrentStage();
        eventPublisher.publishEvent(new GameStageChangeEvent(this, dto.getGameId(), oldStage, dto.getTargetStage(),
                record.getCurrentRound()));
    }

    @Override
    @Transactional
    public void endGame(Long dmUserId, GameEndDTO dto) {
        GaGameRecord record = gameRecordService.getById(dto.getGameId());
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        // 验证DM
        if (!record.getDmUserId().equals(dmUserId)) {
            throw new BizException(ResultCode.FORBIDDEN, "只有DM可以结束游戏");
        }

        // 更新游戏记录
        record.setStatus(GameStatus.FINISHED.getCode());
        record.setCurrentStage(GameStage.END.getCode());
        record.setVictoryType(dto.getVictoryType());
        record.setWinnerUserId(dto.getWinnerUserId());
        record.setFinishedAt(LocalDateTime.now());
        gameRecordService.updateById(record);

        // 更新玩家胜负状态并统计战绩
        List<GaGamePlayer> players = gamePlayerService.listByGameId(dto.getGameId());
        for (GaGamePlayer player : players) {
            CfgRole role = roleService.getById(player.getCurrRoleId());
            if (role == null)
                continue;

            boolean isWinner = false;
            if (dto.getVictoryType() == 1 && role.getCampType() == CampType.GOOSE.getCode()) {
                isWinner = true;
            } else if (dto.getVictoryType() == 2 && role.getCampType() == CampType.DUCK.getCode()) {
                isWinner = true;
            } else if (dto.getVictoryType() == 3 && player.getUserId().equals(dto.getWinnerUserId())) {
                isWinner = true;
            }

            player.setIsWinner(isWinner ? 1 : 0);
            gamePlayerService.updateById(player);

            // TODO: 更新用户战绩统计
        }

        log.info("游戏结束: gameId={}, victoryType={}", dto.getGameId(), dto.getVictoryType());
    }

    @Override
    public PlayerGameVO getPlayerGameView(Long userId, Long gameId) {
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        GaGamePlayer myPlayer = gamePlayerService.getByGameAndUser(gameId, userId);
        if (myPlayer == null) {
            throw new BizException(ResultCode.PLAYER_NOT_IN_GAME);
        }

        // 获取我的角色信息
        CfgRole myRole = roleService.getById(myPlayer.getCurrRoleId());

        // 获取我的状态
        GaPlayerStatus myStatus = playerStatusService.getById(myPlayer.getId());

        // 获取我的技能
        List<GaSkillInstance> mySkillInstances = skillInstanceService.listByGamePlayerId(myPlayer.getId());
        List<SkillInstanceVO> skillVOs = mySkillInstances.stream().map(inst -> {
            CfgSkill skill = skillService.getById(inst.getSkillId());
            boolean canUse = canUseSkill(skill, record.getCurrentStage(), inst.getRemainCount());
            return SkillInstanceVO.builder().id(inst.getId()).skillId(inst.getSkillId())
                    .skillName(skill != null ? skill.getName() : "")
                    .description(skill != null ? skill.getDescription() : "")
                    .triggerMode(skill != null ? skill.getTriggerMode() : 0)
                    .interactionType(skill != null ? skill.getInteractionType() : 0).remainCount(inst.getRemainCount())
                    .canUseNow(canUse).build();
        }).toList();

        // 获取场上玩家（使用 PlayerSafeVO，不暴露其他玩家身份）
        List<GaGamePlayer> allPlayers = gamePlayerService.listByGameId(gameId);
        List<PlayerSafeVO> playerVOs = allPlayers.stream().<PlayerSafeVO>map(p -> {
            SysUser user = userService.getById(p.getUserId());
            GaPlayerStatus status = playerStatusService.getById(p.getId());
            return PlayerSafeVO.builder().gamePlayerId(p.getId()).userId(p.getUserId())
                    .nickname(user != null ? user.getNickname() : "").avatarUrl(user != null ? user.getAvatarUrl() : "")
                    .seatNo(p.getSeatNo()).alive(status != null && status.getIsAlive() == 1).build();
        }).collect(Collectors.toList());

        return PlayerGameVO.builder().gameId(gameId).myGamePlayerId(myPlayer.getId()).mySeatNo(myPlayer.getSeatNo())
                .myRoleId(myPlayer.getCurrRoleId()).myRoleName(myRole != null ? myRole.getName() : "")
                .myCampType(myRole != null ? myRole.getCampType() : null)
                .alive(myStatus != null && myStatus.getIsAlive() == 1).currentRound(record.getCurrentRound())
                .currentStage(record.getCurrentStage()).skills(skillVOs).players(playerVOs).build();
    }

    // ==================== 技能 ====================

    @Override
    @Transactional
    public void useSkill(Long userId, SkillUseDTO dto) {
        GaGameRecord record = gameRecordService.getById(dto.getGameId());
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        GaGamePlayer player = gamePlayerService.getByGameAndUser(dto.getGameId(), userId);
        if (player == null) {
            throw new BizException(ResultCode.PLAYER_NOT_IN_GAME);
        }

        // 获取技能实例
        GaSkillInstance instance = skillInstanceService.getById(dto.getSkillInstanceId());
        if (instance == null || !instance.getGamePlayerId().equals(player.getId())) {
            throw new BizException(ResultCode.SKILL_NOT_AVAILABLE);
        }

        CfgSkill skill = skillService.getById(instance.getSkillId());
        if (skill == null) {
            throw new BizException(ResultCode.SKILL_NOT_AVAILABLE);
        }

        // 获取释放者状态
        GaPlayerStatus actorStatus = playerStatusService.getById(player.getId());

        // 使用 SkillValidator 校验（阶段、次数、目标）
        skillValidator.validate(skill, instance, record, actorStatus, dto.getTargetPlayerIds(), null);

        // 【策略模式】根据技能配置获取对应的 Handler
        String skillLogicJson = skill.getSkillLogic();
        com.eys.engine.skill.SkillHandler handler = skillHandlerFactory.getHandlerFromJson(skillLogicJson);
        Map<String, Object> config = skillHandlerFactory.extractConfig(skillLogicJson);

        // 构建技能执行上下文
        com.eys.engine.skill.SkillContext context = com.eys.engine.skill.SkillContext.builder().skill(skill)
                .config(config).actor(player).actorStatus(actorStatus).targetPlayerIds(dto.getTargetPlayerIds())
                .guessRoleId(dto.getGuessRoleId()).gameRecord(record).currentRound(record.getCurrentRound())
                .currentStage(record.getCurrentStage()).build();

        // 执行技能
        com.eys.engine.skill.SkillResult result = handler.execute(context);

        // 扣减技能次数（使用技能组共享扣减）
        skillInstanceService.deductUsage(instance.getId());

        // 记录动作流水
        GaActionLog actionLog = new GaActionLog();
        actionLog.setGameId(dto.getGameId());
        actionLog.setRoundNo(record.getCurrentRound());
        actionLog.setStage(record.getCurrentStage());
        actionLog.setSourceType(0); // 玩家发起
        actionLog.setActionType("SKILL");
        actionLog.setActorId(player.getId());
        actionLog.setSkillId(skill.getId());
        actionLog.setActionData(JSON.toJSONString(
                Map.of("target_ids", dto.getTargetPlayerIds() != null ? dto.getTargetPlayerIds() : List.of(),
                        "guess_role_id", dto.getGuessRoleId() != null ? dto.getGuessRoleId() : 0, "success",
                        result.isSuccess(), "effect_type", result.getEffectType())));
        actionLog.setResultNote(result.getDmNote());
        actionLogService.save(actionLog);

        // 发布技能使用事件（分级推送：DM 收详细信息，其他人收脱敏信息）
        eventPublisher.publishEvent(new SkillUsedEvent(this, dto.getGameId(), record.getDmUserId(), player.getId(),
                skill.getId(), skill.getName(), result.getDmNote(), result.getPublicNote()));

        log.info("玩家使用技能: userId={}, gameId={}, skillId={}, handler={}, success={}", userId, dto.getGameId(),
                skill.getId(), handler.getHandlerKey(), result.isSuccess());
    }

    @Override
    @Transactional
    public void dmInputSkill(Long dmUserId, SkillUseDTO dto) {
        GaGameRecord record = gameRecordService.getById(dto.getGameId());
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        // 验证DM
        if (!record.getDmUserId().equals(dmUserId)) {
            throw new BizException(ResultCode.FORBIDDEN, "只有DM可以录入技能");
        }

        // 获取技能实例
        GaSkillInstance instance = skillInstanceService.getById(dto.getSkillInstanceId());
        if (instance == null) {
            throw new BizException(ResultCode.SKILL_NOT_AVAILABLE);
        }

        CfgSkill skill = skillService.getById(instance.getSkillId());
        if (skill == null) {
            throw new BizException(ResultCode.SKILL_NOT_AVAILABLE);
        }

        // DM录入不校验阶段和次数，直接记录
        // 记录动作流水
        GaActionLog actionLog = new GaActionLog();
        actionLog.setGameId(dto.getGameId());
        actionLog.setRoundNo(record.getCurrentRound());
        actionLog.setStage(record.getCurrentStage());
        actionLog.setSourceType(1); // DM录入
        actionLog.setActionType("SKILL");
        actionLog.setActorId(instance.getGamePlayerId());
        actionLog.setSkillId(skill.getId());
        actionLog.setActionData(JSON.toJSONString(
                Map.of("target_ids", dto.getTargetPlayerIds() != null ? dto.getTargetPlayerIds() : List.of(),
                        "guess_role_id", dto.getGuessRoleId() != null ? dto.getGuessRoleId() : 0)));
        actionLog.setResultNote("[DM录入] 技能: " + skill.getName());
        actionLogService.save(actionLog);

        log.info("DM录入技能: dmUserId={}, gameId={}, skillId={}", dmUserId, dto.getGameId(), skill.getId());
    }

    @Override
    public void dmRequestSkill(Long dmUserId, Long gameId, Long targetPlayerId, Long skillInstanceId) {
        // TODO: 通过 WebSocket 向目标玩家推送技能使用请求
        log.info("DM请求玩家使用技能: dmUserId={}, gameId={}, targetPlayerId={}, skillInstanceId={}", dmUserId, gameId,
                targetPlayerId, skillInstanceId);
    }

    // ==================== 投票 ====================

    @Override
    @Transactional
    public void vote(Long userId, VoteDTO dto) {
        GaGameRecord record = gameRecordService.getById(dto.getGameId());
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        // 验证阶段
        if (!GameStage.VOTE.getCode().equals(record.getCurrentStage())) {
            throw new BizException(ResultCode.VOTE_NOT_ALLOWED);
        }

        GaGamePlayer player = gamePlayerService.getByGameAndUser(dto.getGameId(), userId);
        if (player == null) {
            throw new BizException(ResultCode.PLAYER_NOT_IN_GAME);
        }

        // 检查是否存活
        GaPlayerStatus status = playerStatusService.getById(player.getId());
        if (status == null || status.getIsAlive() != 1) {
            throw new BizException(ResultCode.PLAYER_DEAD);
        }

        // 检查是否被禁言（状态效果）
        if (playerStatusService.hasEffect(player.getId(), "MUTED")) {
            throw new BizException(ResultCode.FORBIDDEN, "你已被禁言，无法投票");
        }

        // 检查是否已投票
        if (voteLogService.hasVoted(dto.getGameId(), record.getCurrentRound(), player.getId())) {
            throw new BizException(ResultCode.VOTE_ALREADY_SUBMITTED);
        }

        // 记录投票
        GaVoteLog voteLog = new GaVoteLog();
        voteLog.setGameId(dto.getGameId());
        voteLog.setRoundNo(record.getCurrentRound());
        voteLog.setVoterId(player.getId());
        voteLog.setTargetId(dto.getTargetPlayerId());
        voteLog.setIsSkipped(0);
        voteLogService.save(voteLog);

        log.info("玩家投票: userId={}, gameId={}, targetId={}", userId, dto.getGameId(), dto.getTargetPlayerId());
    }

    @Override
    public VoteResultVO getVoteResult(Long gameId, Integer roundNo) {
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        // 如果未指定轮次，使用当前轮次
        int targetRound = roundNo != null ? roundNo : record.getCurrentRound();

        // 获取存活玩家列表（应投票人数）
        List<GaGamePlayer> allPlayers = gamePlayerService.listByGameId(gameId);
        List<GaGamePlayer> aliveVoters = allPlayers.stream().filter(p -> {
            GaPlayerStatus status = playerStatusService.getById(p.getId());
            return status != null && status.getIsAlive() == 1;
        }).toList();

        // 获取本轮投票记录
        List<GaVoteLog> votes = voteLogService.listByGameAndRound(gameId, targetRound);

        // 统计弃票数（targetId=0 或 NULL 且 isSkipped=0）
        int abstainCount = (int) votes.stream()
                .filter(v -> (v.getTargetId() == null || v.getTargetId() == 0) && v.getIsSkipped() != 1).count();

        // 统计跳过数（isSkipped=1）
        int skippedCount = (int) votes.stream().filter(v -> v.getIsSkipped() == 1).count();

        // 统计得票（排除弃票和跳过）
        Map<Long, Long> voteCountMap = votes.stream()
                .filter(v -> v.getTargetId() != null && v.getTargetId() != 0 && v.getIsSkipped() != 1)
                .collect(Collectors.groupingBy(GaVoteLog::getTargetId, Collectors.counting()));

        // 构建得票统计列表
        List<VoteResultVO.VoteCountItem> voteCounts = voteCountMap.entrySet().stream().map(entry -> {
            Long targetId = entry.getKey();
            GaGamePlayer targetPlayer = gamePlayerService.getById(targetId);
            SysUser user = targetPlayer != null ? userService.getById(targetPlayer.getUserId()) : null;
            return VoteResultVO.VoteCountItem.builder().targetPlayerId(targetId)
                    .targetNickname(user != null ? user.getNickname() : "")
                    .seatNo(targetPlayer != null ? targetPlayer.getSeatNo() : 0).count(entry.getValue().intValue())
                    .build();
        }).sorted((a, b) -> b.getCount() - a.getCount()) // 按得票数降序
                .collect(Collectors.toList());

        // 判定最高票
        Long topVotedPlayerId = null;
        int topVoteCount = 0;
        boolean isTie = false;

        if (!voteCounts.isEmpty()) {
            topVoteCount = voteCounts.get(0).getCount();
            topVotedPlayerId = voteCounts.get(0).getTargetPlayerId();

            // 检查是否有平票
            final int finalTopVoteCount = topVoteCount;
            long tieCount = voteCounts.stream().filter(item -> item.getCount() == finalTopVoteCount).count();
            isTie = tieCount > 1;
        }

        return VoteResultVO.builder().gameId(gameId).roundNo(targetRound).votedCount(votes.size())
                .totalVoters(aliveVoters.size()).completed(votes.size() >= aliveVoters.size()).voteCounts(voteCounts)
                .topVotedPlayerId(topVotedPlayerId).topVoteCount(topVoteCount).isTie(isTie).abstainCount(abstainCount)
                .skippedCount(skippedCount).build();
    }

    // ==================== DM 操作 ====================

    @Override
    @Transactional
    public void killPlayer(Long dmUserId, Long gameId, Long targetPlayerId) {
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        if (!record.getDmUserId().equals(dmUserId)) {
            throw new BizException(ResultCode.FORBIDDEN, "只有DM可以判定死亡");
        }

        GaPlayerStatus status = playerStatusService.getById(targetPlayerId);
        if (status == null) {
            throw new BizException(ResultCode.PLAYER_NOT_IN_GAME);
        }

        status.setIsAlive(0);
        status.setDeathRound(record.getCurrentRound());
        status.setDeathStage(record.getCurrentStage());
        playerStatusService.updateById(status);

        // 记录动作
        GaActionLog actionLog = new GaActionLog();
        actionLog.setGameId(gameId);
        actionLog.setRoundNo(record.getCurrentRound());
        actionLog.setStage(record.getCurrentStage());
        actionLog.setSourceType(1);
        actionLog.setActionType("KILL");
        actionLog.setActorId(0L);
        actionLog.setActionData(JSON.toJSONString(Map.of("target_id", targetPlayerId)));
        actionLog.setResultNote("DM判定死亡");
        actionLogService.save(actionLog);

        log.info("DM判定玩家死亡: gameId={}, targetPlayerId={}", gameId, targetPlayerId);

        // 查找玩家 userId 并发布状态变更事件
        GaGamePlayer player = gamePlayerService.getById(targetPlayerId);
        if (player != null) {
            eventPublisher.publishEvent(
                    new PlayerStatusChangeEvent(this, gameId, targetPlayerId, player.getUserId(), false, "KILL"));
        }
    }

    @Override
    @Transactional
    public void revivePlayer(Long dmUserId, Long gameId, Long targetPlayerId) {
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        if (!record.getDmUserId().equals(dmUserId)) {
            throw new BizException(ResultCode.FORBIDDEN, "只有DM可以复活玩家");
        }

        GaPlayerStatus status = playerStatusService.getById(targetPlayerId);
        if (status == null) {
            throw new BizException(ResultCode.PLAYER_NOT_IN_GAME);
        }

        status.setIsAlive(1);
        status.setDeathRound(null);
        status.setDeathStage(null);
        playerStatusService.updateById(status);

        // 记录动作
        GaActionLog actionLog = new GaActionLog();
        actionLog.setGameId(gameId);
        actionLog.setRoundNo(record.getCurrentRound());
        actionLog.setStage(record.getCurrentStage());
        actionLog.setSourceType(1);
        actionLog.setActionType("REVIVE");
        actionLog.setActorId(0L);
        actionLog.setActionData(JSON.toJSONString(Map.of("target_id", targetPlayerId)));
        actionLog.setResultNote("DM复活玩家");
        actionLogService.save(actionLog);

        log.info("DM复活玩家: gameId={}, targetPlayerId={}", gameId, targetPlayerId);

        // 查找玩家 userId 并发布状态变更事件
        GaGamePlayer player = gamePlayerService.getById(targetPlayerId);
        if (player != null) {
            eventPublisher.publishEvent(
                    new PlayerStatusChangeEvent(this, gameId, targetPlayerId, player.getUserId(), true, "REVIVE"));
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 生成房间码
     */
    private String generateRoomCode() {
        return RandomUtil.randomString("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 6);
    }

    /**
     * 构建房间VO
     */
    private RoomVO buildRoomVO(GaGameRecord record, Long viewerUserId, List<Long> roleIds) {
        SysUser dm = userService.getById(record.getDmUserId());
        CfgMap map = mapService.getById(record.getMapId());

        List<GaGamePlayer> players = gamePlayerService.listByGameId(record.getId());
        boolean isDm = viewerUserId != null && viewerUserId.equals(record.getDmUserId());
        boolean isFinished = GameStatus.FINISHED.getCode().equals(record.getStatus());

        // 根据查看者权限决定返回 PlayerSafeVO 还是 PlayerFullVO
        List<? extends PlayerSafeVO> playerVOs = players.stream().map(p -> {
            SysUser user = userService.getById(p.getUserId());
            GaPlayerStatus status = playerStatusService.getById(p.getId());
            boolean isAlive = status == null || status.getIsAlive() == 1;

            // DM 或游戏结束时返回完整信息
            if (isDm || isFinished) {
                CfgRole role = p.getCurrRoleId() != null ? roleService.getById(p.getCurrRoleId()) : null;
                CfgRole initRole = p.getInitRoleId() != null ? roleService.getById(p.getInitRoleId()) : null;
                return PlayerFullVO.builder().gamePlayerId(p.getId()).userId(p.getUserId())
                        .nickname(user != null ? user.getNickname() : "")
                        .avatarUrl(user != null ? user.getAvatarUrl() : "").seatNo(p.getSeatNo()).alive(isAlive)
                        .roleId(role != null ? role.getId() : null).roleName(role != null ? role.getName() : null)
                        .campType(role != null ? role.getCampType() : null)
                        .initRoleId(initRole != null ? initRole.getId() : null)
                        .initRoleName(initRole != null ? initRole.getName() : null).build();
            }

            // 普通玩家只能看到脱敏信息
            return PlayerSafeVO.builder().gamePlayerId(p.getId()).userId(p.getUserId())
                    .nickname(user != null ? user.getNickname() : "").avatarUrl(user != null ? user.getAvatarUrl() : "")
                    .seatNo(p.getSeatNo()).alive(isAlive).build();
        }).toList();

        return RoomVO.builder().gameId(record.getId()).roomCode(record.getRoomCode()).dmUserId(record.getDmUserId())
                .dmNickname(dm != null ? dm.getNickname() : "").mapId(record.getMapId())
                .mapName(map != null ? map.getName() : "").status(record.getStatus())
                .currentRound(record.getCurrentRound()).currentStage(record.getCurrentStage()).players(playerVOs)
                .roleIds(roleIds).build();
    }

    /**
     * 判断技能是否可以在当前阶段使用
     * 
     * @deprecated 使用 skillValidator.canUseNow() 替代
     */
    private boolean canUseSkill(CfgSkill skill, String currentStage, int remainCount) {
        return skillValidator.canUseNow(skill, currentStage, remainCount);
    }
}
