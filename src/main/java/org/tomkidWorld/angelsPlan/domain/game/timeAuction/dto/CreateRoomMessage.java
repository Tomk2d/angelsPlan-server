package org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomMessage {
    private String roomName;
    private String playerId;
} 