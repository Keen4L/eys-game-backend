package com.eys.service.ga.impl;

import com.eys.common.constant.GameStage;
import com.eys.common.exception.BizException;
import com.eys.common.result.ResultCode;
import com.eys.model.dto.game.VoteDTO;
import com.eys.model.entity.ga.GaGamePlayer;
import com.eys.model.entity.ga.GaGameRecord;
import com.eys.model.entity.ga.GaPlayerStatus;
import com.eys.model.entity.ga.GaVoteLog;
import com.eys.model.entity.sys.SysUser;
import com.eys.model.vo.game.VoteResultVO;
import com.eys.service.ga.*;
import com.eys.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 投票 Service 实现
 *
 * @author EYS
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final GaGameRecordService gameRecordService;
    private final GaGamePlayerService gamePlayerService;
    private final GaPlayerStatusService playerStatusService;
    private final GaVoteLogService voteLogService;
    private final SysUserService userService;
    private final ApplicationEventPublisher eventPublisher;

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

        // 检查是否被禁言
        if (playerStatusService.hasEffect(player.getId(), "禁言")) {
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

        // 统计投票进度（优化：使用 COUNT 替代全量加载）
        int totalVoters = playerStatusService.countAliveByGameId(dto.getGameId());
        int votedCount = voteLogService.countByGameAndRound(dto.getGameId(), record.getCurrentRound());

        eventPublisher.publishEvent(new com.eys.service.event.VoteSubmittedEvent(
                this, dto.getGameId(), record.getDmUserId(), votedCount, totalVoters));
    }

    @Override
    public VoteResultVO getVoteResult(Long gameId, Integer roundNo) {
        GaGameRecord record = gameRecordService.getById(gameId);
        if (record == null) {
            throw new BizException(ResultCode.ROOM_NOT_FOUND);
        }

        int targetRound = roundNo != null ? roundNo : record.getCurrentRound();

        // 获取存活玩家列表
        List<GaGamePlayer> allPlayers = gamePlayerService.listByGameId(gameId);
        List<GaGamePlayer> aliveVoters = allPlayers.stream().filter(p -> {
            GaPlayerStatus status = playerStatusService.getById(p.getId());
            return status != null && status.getIsAlive() == 1;
        }).toList();

        // 获取本轮投票记录
        List<GaVoteLog> votes = voteLogService.listByGameAndRound(gameId, targetRound);

        // 统计弃票数
        int abstainCount = (int) votes.stream()
                .filter(v -> (v.getTargetId() == null || v.getTargetId() == 0) && v.getIsSkipped() != 1).count();

        // 统计跳过数
        int skippedCount = (int) votes.stream().filter(v -> v.getIsSkipped() == 1).count();

        // 统计得票
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
        }).sorted((a, b) -> b.getCount() - a.getCount()).collect(Collectors.toList());

        // 判定最高票
        Long topVotedPlayerId = null;
        int topVoteCount = 0;
        boolean isTie = false;

        if (!voteCounts.isEmpty()) {
            topVoteCount = voteCounts.get(0).getCount();
            topVotedPlayerId = voteCounts.get(0).getTargetPlayerId();

            final int finalTopVoteCount = topVoteCount;
            long tieCount = voteCounts.stream().filter(item -> item.getCount() == finalTopVoteCount).count();
            isTie = tieCount > 1;
        }

        return VoteResultVO.builder().gameId(gameId).roundNo(targetRound).votedCount(votes.size())
                .totalVoters(aliveVoters.size()).completed(votes.size() >= aliveVoters.size()).voteCounts(voteCounts)
                .topVotedPlayerId(topVotedPlayerId).topVoteCount(topVoteCount).isTie(isTie).abstainCount(abstainCount)
                .skippedCount(skippedCount).build();
    }
}
