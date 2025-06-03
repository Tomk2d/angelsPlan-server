package org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveRoomMessage {
    private String roomId;
    private String playerId;
} 