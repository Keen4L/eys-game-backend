package com.eys.service.ga;

import com.eys.model.dto.game.CreateRoomDTO;
import com.eys.model.dto.game.JoinRoomDTO;
import com.eys.model.vo.game.RoomVO;

/**
 * 房间管理 Service 接口
 *
 * @author EYS
 */
public interface RoomService {

    /**
     * DM创建房间
     *
     * @param dmUserId DM用户ID
     * @param dto      创建房间请求
     * @return 房间信息
     */
    RoomVO createRoom(Long dmUserId, CreateRoomDTO dto);

    /**
     * 玩家加入房间
     *
     * @param userId 用户ID
     * @param dto    加入房间请求
     * @return 房间信息
     */
    RoomVO joinRoom(Long userId, JoinRoomDTO dto);

    /**
     * 玩家退出房间
     *
     * @param userId 用户ID
     * @param gameId 游戏ID
     */
    void leaveRoom(Long userId, Long gameId);

    /**
     * 获取房间信息
     *
     * @param gameId 游戏ID
     * @param userId 请求用户ID（用于判断是否为DM）
     * @return 房间信息
     */
    RoomVO getRoomInfo(Long gameId, Long userId);

    /**
     * 根据房间码获取房间信息
     *
     * @param roomCode 房间码
     * @return 房间信息
     */
    RoomVO getRoomByCode(String roomCode);
}
