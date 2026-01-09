package com.eys.service.ga.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eys.common.constant.GameStage;
import com.eys.common.constant.GameStatus;
import com.eys.common.exception.BizException;
import com.eys.common.result.ResultCode;
import com.eys.model.dto.game.CreateRoomDTO;
import com.eys.model.dto.game.JoinRoomDTO;
import com.eys.model.entity.cfg.CfgDeck;
import com.eys.model.entity.cfg.CfgMap;
import com.eys.model.entity.cfg.CfgRole;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.model.entity.ga.GaGameRecord;
import com.eys.model.entity.ga.GaPlayerStatus;
import com.eys.model.entity.sys.SysUser;
import com.eys.model.vo.game.PlayerFullVO;
import com.eys.model.vo.game.PlayerSafeVO;
import com.eys.model.vo.game.RoomVO;
import com.eys.service.cfg.CfgDeckService;
import com.eys.service.cfg.CfgMapService;
import com.eys.service.cfg.CfgRoleService;
import com.eys.service.ga.*;
import com.eys.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房间管理 Service 实现
 *
 * @author EYS
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final GaGameRecordService gameRecordService;
    private final GaGamePlayerService gamePlayerService;
    private final GaPlayerStatusService playerStatusService;
    private final CfgMapService mapService;
    private final CfgRoleService roleService;
    private final CfgDeckService deckService;
    private final SysUserService userService;
    private final ApplicationEventPublisher eventPublisher;

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

        // 生成唯一房间码
        String roomCode = generateUniqueRoomCode();

        // 创建对局记录
        GaGameRecord record = new GaGameRecord();
        record.setRoomCode(roomCode);
        record.setDmUserId(dmUserId);
        record.setMapId(dto.getMapId());
        record.setRoleIds(JSON.toJSONString(roleIds));
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
        GaGameRecord record = gameRecordService.getOne(
                new LambdaQueryWrapper<GaGameRecord>().eq(GaGameRecord::getRoomCode, dto.getRoomCode().toUpperCase()));

        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        if (!GameStatus.PREPARING.getCode().equals(record.getStatus())) {
            throw new BizException(ResultCode.ROOM_PLAYING);
        }

        GaGamePlayer existPlayer = gamePlayerService.getByGameAndUser(record.getId(), userId);
        if (existPlayer != null) {
            throw new BizException(ResultCode.PLAYER_ALREADY_JOINED);
        }

        GaGamePlayer player = new GaGamePlayer();
        player.setGameId(record.getId());
        player.setUserId(userId);
        player.setSeatNo(gamePlayerService.getNextSeatNo(record.getId()));
        player.setIsWinner(0);
        gamePlayerService.save(player);

        SysUser user = userService.getById(userId);

        log.info("玩家加入房间: userId={}, gameId={}", userId, record.getId());

        eventPublisher.publishEvent(new com.eys.service.event.PlayerJoinedEvent(
                this, record.getId(), userId,
                user != null ? user.getNickname() : "",
                user != null ? user.getAvatarUrl() : "",
                player.getSeatNo()));

        return getRoomInfo(record.getId(), userId);
    }

    @Override
    @Transactional
    public void leaveRoom(Long userId, Long gameId) {
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        if (!GameStatus.PREPARING.getCode().equals(record.getStatus())) {
            throw new BizException("游戏已开始，无法退出");
        }

        GaGamePlayer player = gamePlayerService.getByGameAndUser(gameId, userId);
        if (player != null) {
            gamePlayerService.removeById(player.getId());
            log.info("玩家退出房间: userId={}, gameId={}", userId, gameId);

            eventPublisher.publishEvent(new com.eys.service.event.PlayerLeftEvent(this, gameId, userId));
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

    // ==================== 私有方法 ====================

    private String generateRoomCode() {
        return RandomUtil.randomString("ABCDEFGHJKLMNPQRSTUVWXYZ23456789", 6);
    }

    private String generateUniqueRoomCode() {
        for (int i = 0; i < 10; i++) {
            String code = generateRoomCode();
            boolean exists = gameRecordService.count(
                    new LambdaQueryWrapper<GaGameRecord>()
                            .eq(GaGameRecord::getRoomCode, code)
                            .eq(GaGameRecord::getStatus, GameStatus.PREPARING.getCode())) > 0;
            if (!exists) {
                return code;
            }
            log.warn("房间码冲突，重试生成: attempt={}", i + 1);
        }
        throw new BizException("生成房间码失败，请重试");
    }

    private RoomVO buildRoomVO(GaGameRecord record, Long viewerUserId, List<Long> roleIds) {
        SysUser dm = userService.getById(record.getDmUserId());
        CfgMap map = mapService.getById(record.getMapId());

        List<GaGamePlayer> players = gamePlayerService.listByGameId(record.getId());
        boolean isDm = viewerUserId != null && viewerUserId.equals(record.getDmUserId());
        boolean isFinished = GameStatus.FINISHED.getCode().equals(record.getStatus());

        List<? extends PlayerSafeVO> playerVOs = players.stream().map(p -> {
            SysUser user = userService.getById(p.getUserId());
            GaPlayerStatus status = playerStatusService.getById(p.getId());
            boolean isAlive = status == null || status.getIsAlive() == 1;

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
}
