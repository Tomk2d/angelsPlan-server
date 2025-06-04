package org.tomkidWorld.angelsPlan.domain.game.timeAuction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "time_auction_rooms")
@Getter
@Setter
public class TimeAuctionRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", unique = true, nullable = false)
    private String roomId;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "current_round")
    private Integer currentRound = 0;

    @Column(name = "total_rounds")
    private Integer totalRounds = 10;

    @Column(name = "max_players")
    private Integer maxPlayers = 4;

    @Column(name = "min_players")
    private Integer minPlayers = 2;

    @ElementCollection
    @CollectionTable(name = "time_auction_room_players", joinColumns = @JoinColumn(name = "room_table_id"))
    @Column(name = "player_id")
    private Set<String> playerIds = new HashSet<>();

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addPlayer(String playerId) {
        if (playerIds.size() >= maxPlayers) {
            throw new IllegalStateException("방이 가득 찼습니다.");
        }
        playerIds.add(playerId);
    }

    public void removePlayer(String playerId) {
        playerIds.remove(playerId);
    }

    public boolean isFull() {
        return playerIds.size() >= maxPlayers;
    }
} 