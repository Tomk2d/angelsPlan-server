package org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartGameMessage {
    private String roomId;
    private String playerId;  // 방장 ID
} 