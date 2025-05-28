package org.tomkidWorld.angelsPlan.domain.game.timeAuction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.TimeAuctionMessage;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.TimeAuctionRoomDto;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.service.TimeAuctionRoomService;

@Controller
@RequiredArgsConstructor
public class TimeAuctionController {
    private final TimeAuctionRoomService roomService;

    @MessageMapping("/timeAuction/create")
    @SendTo("/topic/game/rooms")
    public TimeAuctionMessage createRoom(TimeAuctionMessage message) {
        TimeAuctionRoomDto room = roomService.createRoom(message.getRoomName(), Long.parseLong(message.getPlayerId()));
        message.setType("ROOM_CREATED");
        message.setRoomId(room.getRoomId());
        return message;
    }

    @MessageMapping("/timeAuction/join/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public TimeAuctionMessage joinRoom(@DestinationVariable String roomId, TimeAuctionMessage message) {
        TimeAuctionRoomDto room = roomService.joinRoom(roomId, Long.parseLong(message.getPlayerId()));
        message.setType("PLAYER_JOINED");
        return message;
    }

    @MessageMapping("/timeAuction/leave/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public TimeAuctionMessage leaveRoom(@DestinationVariable String roomId, TimeAuctionMessage message) {
        roomService.leaveRoom(roomId, Long.parseLong(message.getPlayerId()));
        message.setType("PLAYER_LEFT");
        return message;
    }

    @MessageMapping("/timeAuction/rooms")
    @SendTo("/topic/game/rooms")
    public TimeAuctionMessage getRooms(TimeAuctionMessage message) {
        message.setType("ROOM_LIST");
        return message;
    }
} 