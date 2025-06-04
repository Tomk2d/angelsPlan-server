package org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("timeAuctionRoom")
public class TimeAuctionRoomDto {
    @Id
    private String roomId;
    private String roomName;
    private String hostId;
    private int currentRound;
    private int totalRounds;
    private int maxPlayers;
    private int minPlayers;
    private List<String> playerIds;
    private Map<String, Integer> currentBets;
    private int remainingTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean full;
} 