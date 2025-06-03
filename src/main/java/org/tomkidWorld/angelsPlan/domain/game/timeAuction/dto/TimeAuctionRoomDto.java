package org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class TimeAuctionRoomDto {
    private String roomId;
    private String roomName;
    private Integer currentRound;
    private Integer totalRounds;
    private Integer maxPlayers;
    private Set<String> playerIds;
    private Boolean isActive;
} 