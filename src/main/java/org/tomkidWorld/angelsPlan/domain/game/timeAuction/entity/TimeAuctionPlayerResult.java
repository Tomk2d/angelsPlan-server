package org.tomkidWorld.angelsPlan.domain.game.timeAuction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "time_auction_player_results")
public class TimeAuctionPlayerResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_result_id", nullable = false)
    private TimeAuctionGameResult gameResult;

    @Column(nullable = false)
    private String playerId;

    @Column(nullable = false)
    private Integer finalScore;

    @Column(nullable = false)
    private Integer totalWins;  // 이긴 라운드 수

    @Column(nullable = false)
    private Integer totalBets;  // 총 베팅 횟수

    @Column(nullable = false)
    private Integer averageBetTime;  // 평균 베팅 시간
} 