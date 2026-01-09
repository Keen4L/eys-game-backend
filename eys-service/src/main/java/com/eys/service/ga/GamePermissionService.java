package com.eys.service.ga;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eys.common.constant.RoleType;
import com.eys.common.exception.BizException;
import com.eys.common.result.ResultCode;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.model.entity.ga.GaGameRecord;
import com.eys.model.entity.sys.SysUser;
import com.eys.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 游戏权限校验服务
 * 提供统一的房间级权限校验
 *
 * @author EYS
 */
@Service
@RequiredArgsConstructor
public class GamePermissionService {

    private final GaGameRecordService gameRecordService;
    private final GaGamePlayerService gamePlayerService;
    private final SysUserService userService;

    /**
     * 校验当前用户是否具有 DM 权限
     * 1. 检查房间存在
     * 2. 检查用户是否是该房间的 DM（dmUserId）
     * 3. 或者用户 roleType = ADMIN（管理员可以操作任何房间）
     *
     * @param gameId 游戏ID
     * @throws BizException 如果不是该房间的 DM 则抛出异常
     */
    public void assertDm(Long gameId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 校验房间存在
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        // 管理员可以操作任何房间
        SysUser user = userService.getById(userId);
        if (user != null && RoleType.ADMIN.getCode().equals(user.getRoleType())) {
            return;
        }

        // 校验是否是该房间的 DM
        if (!record.getDmUserId().equals(userId)) {
            throw new BizException(ResultCode.FORBIDDEN, "只有该房间的 DM 可以执行此操作");
        }
    }

    /**
     * 校验当前用户是否是该房间的玩家
     *
     * @param gameId 游戏ID
     * @return 对局玩家信息
     * @throws BizException 如果不是玩家则抛出异常
     */
    public GaGamePlayer assertPlayer(Long gameId) {
        Long userId = StpUtil.getLoginIdAsLong();
        GaGamePlayer player = gamePlayerService.getOne(
                new LambdaQueryWrapper<GaGamePlayer>()
                        .eq(GaGamePlayer::getGameId, gameId)
                        .eq(GaGamePlayer::getUserId, userId));
        if (player == null) {
            throw new BizException(ResultCode.PLAYER_NOT_IN_GAME);
        }
        return player;
    }

    /**
     * 校验当前用户是否在该房间内（DM 或玩家）
     *
     * @param gameId 游戏ID
     * @throws BizException 如果不在房间内则抛出异常
     */
    public void assertInGame(Long gameId) {
        Long userId = StpUtil.getLoginIdAsLong();
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        // 检查是否是 DM（通过 roleType）
        SysUser user = userService.getById(userId);
        if (user != null && (RoleType.DM.getCode().equals(user.getRoleType())
                || RoleType.ADMIN.getCode().equals(user.getRoleType()))) {
            return;
        }

        // 检查是否是玩家
        GaGamePlayer player = gamePlayerService.getOne(
                new LambdaQueryWrapper<GaGamePlayer>()
                        .eq(GaGamePlayer::getGameId, gameId)
                        .eq(GaGamePlayer::getUserId, userId));
        if (player == null) {
            throw new BizException(ResultCode.FORBIDDEN, "你不在该房间内");
        }
    }

    /**
     * 获取房间信息（已校验存在性）
     *
     * @param gameId 游戏ID
     * @return 房间记录
     */
    public GaGameRecord getGameRecord(Long gameId) {
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }
        return record;
    }
}
