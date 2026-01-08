package com.eys.model.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 投票结果 VO
 *
 * @author EYS
 */
@Data
@Builder
@Schema(description = "投票结果")
public class VoteResultVO {

    /**
     * 游戏ID
     */
    @Schema(description = "游戏ID")
    private Long gameId;

    /**
     * 轮次
     */
    @Schema(description = "轮次")
    private Integer roundNo;

    /**
     * 已投票人数
     */
    @Schema(description = "已投票人数")
    private Integer votedCount;

    /**
     * 应投票人数（存活玩家数）
     */
    @Schema(description = "应投票人数")
    private Integer totalVoters;

    /**
     * 是否投票完成
     */
    @Schema(description = "是否投票完成")
    private Boolean completed;

    /**
     * 得票统计列表
     */
    @Schema(description = "得票统计列表")
    private List<VoteCountItem> voteCounts;

    /**
     * 最高票玩家ID（可能有多个平票，暂取第一个）
     */
    @Schema(description = "最高票玩家ID")
    private Long topVotedPlayerId;

    /**
     * 最高票数
     */
    @Schema(description = "最高票数")
    private Integer topVoteCount;

    /**
     * 是否平票
     */
    @Schema(description = "是否平票")
    private Boolean isTie;

    /**
     * 弃票数（主动弃权：targetId=0 且 isSkipped=0）
     */
    @Schema(description = "弃票数")
    private Integer abstainCount;

    /**
     * 跳过数（被禁言/超时未投：isSkipped=1）
     */
    @Schema(description = "跳过/未投票数")
    private Integer skippedCount;

    /**
     * 单个玩家得票统计
     */
    @Data
    @Builder
    @Schema(description = "得票统计项")
    public static class VoteCountItem {
        /**
         * 被投票玩家ID
         */
        @Schema(description = "被投票玩家ID")
        private Long targetPlayerId;

        /**
         * 被投票玩家昵称
         */
        @Schema(description = "被投票玩家昵称")
        private String targetNickname;

        /**
         * 被投票玩家座位号
         */
        @Schema(description = "被投票玩家座位号")
        private Integer seatNo;

        /**
         * 得票数
         */
        @Schema(description = "得票数")
        private Integer count;
    }
}
