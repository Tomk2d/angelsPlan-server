package org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeAuctionMessage {
    private String type;        // 메시지 타입 (JOIN, BET, ROUND_END, GAME_END 등)
    private String roomId;      // 게임룸 ID
    private String roomName;    // 게임룸 이름
    private String playerId;    // 플레이어 ID
    private Integer betTime;    // 베팅한 시간
    private Integer remainingTime; // 남은 시간
    private Integer currentRound;  // 현재 라운드
    private String message;     // 추가 메시지
} 