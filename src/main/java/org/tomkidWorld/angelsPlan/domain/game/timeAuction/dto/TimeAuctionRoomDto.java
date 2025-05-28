package org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto;

import lombok.Getter;
import lombok.Setter;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.entity.TimeAuctionRoom;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class TimeAuctionRoomDto {
    private String roomId;
    private String roomName;
    private boolean isGameStarted;
    private int currentRound;
    private int maxPlayers;
    private int currentPlayers;
    private Set<String> playerNicknames;

    public static TimeAuctionRoomDto from(TimeAuctionRoom room) {
        TimeAuctionRoomDto dto = new TimeAuctionRoomDto();
        dto.setRoomId(room.getRoomId());
        dto.setRoomName(room.getRoomName());
        dto.setGameStarted(room.isGameStarted());
        dto.setCurrentRound(room.getCurrentRound());
        dto.setMaxPlayers(room.getMaxPlayers());
        dto.setCurrentPlayers(room.getCurrentPlayers());
        dto.setPlayerNicknames(room.getPlayers().stream()
                .map(player -> player.getNickname())
                .collect(Collectors.toSet()));
        return dto;
    }
} 