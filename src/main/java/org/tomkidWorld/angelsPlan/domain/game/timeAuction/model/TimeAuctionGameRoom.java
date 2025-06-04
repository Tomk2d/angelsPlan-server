package org.tomkidWorld.angelsPlan.domain.game.timeAuction.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeAuctionGameRoom implements Serializable {
    private String roomId;
    private String roomName;
    private String hostId;  // 방장 ID
    private Integer currentRound;
    private Integer totalRounds;
    private Integer maxPlayers;
    private Integer minPlayers;  // 최소 필요 인원
    private Set<String> playerIds;
    private Map<String, Integer> currentBets;  // 플레이어별 현재 베팅
    private Integer remainingTime;
    private GameStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TimeAuctionGameRoom() {
        this.playerIds = new HashSet<>();
        this.currentBets = new ConcurrentHashMap<>();
        this.status = GameStatus.WAITING;
        this.currentRound = 0;
        this.totalRounds = 10;
        this.maxPlayers = 4;
        this.minPlayers = 2;  // 최소 2명 필요
        this.remainingTime = 60; // 기본 60초
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isFull() {
        return playerIds.size() >= maxPlayers;
    }

    public boolean canStartGame() {
        return playerIds.size() >= minPlayers && status == GameStatus.WAITING;
    }

    public boolean isHost(String playerId) {
        return hostId != null && hostId.equals(playerId);
    }

    public void addPlayer(String playerId) {
        if (!isFull()) {
            playerIds.add(playerId);
            if (hostId == null) {  // 첫 플레이어는 방장
                hostId = playerId;
            }
            updatedAt = LocalDateTime.now();
        }
    }

    public void removePlayer(String playerId) {
        playerIds.remove(playerId);
        currentBets.remove(playerId);
        
        // 방장이 나가면 다음 플레이어를 방장으로 지정
        if (isHost(playerId) && !playerIds.isEmpty()) {
            hostId = playerIds.iterator().next();
        }
        
        updatedAt = LocalDateTime.now();
    }

    public void placeBet(String playerId, Integer betTime) {
        if (status == GameStatus.PLAYING) {
            currentBets.put(playerId, betTime);
            updatedAt = LocalDateTime.now();
        }
    }

    public void startRound() {
        currentRound++;
        status = GameStatus.PLAYING;
        remainingTime = 60;
        currentBets.clear();
        updatedAt = LocalDateTime.now();
    }

    public void endRound() {
        status = GameStatus.ROUND_END;
        updatedAt = LocalDateTime.now();
    }

    public void endGame() {
        status = GameStatus.GAME_END;
        updatedAt = LocalDateTime.now();
    }
} 