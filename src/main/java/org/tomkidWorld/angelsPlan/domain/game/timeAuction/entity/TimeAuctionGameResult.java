package org.tomkidWorld.angelsPlan.domain.game.timeAuction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "time_auction_game_results")
public class TimeAuctionGameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private Integer totalRounds;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @OneToMany(mappedBy = "gameResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeAuctionPlayerResult> playerResults = new ArrayList<>();

    @Column(nullable = false)
    private String winnerId;  // 승자 ID

    @Column(nullable = false)
    private Integer winnerScore;  // 승자 점수
} 