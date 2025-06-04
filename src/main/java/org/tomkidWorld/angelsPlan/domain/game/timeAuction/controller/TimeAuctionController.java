package org.tomkidWorld.angelsPlan.domain.game.timeAuction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.*;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.service.TimeAuctionRoomService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TimeAuctionController {

    private final TimeAuctionRoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/time-auction/create")
    public void createRoom(@Payload CreateRoomMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getUser().getName();
        log.info("=== WebSocket create room 요청 받음, playerId: {}, roomName: {} ===", playerId, message.getRoomName());
        
        TimeAuctionRoomDto roomDto = TimeAuctionRoomDto.builder()
                .roomName(message.getRoomName())
                .hostId(playerId)
                .currentRound(0)
                .totalRounds(10)
                .maxPlayers(4)
                .minPlayers(2)
                .status("WAITING")
                .build();
        
        TimeAuctionRoomDto createdRoom = roomService.updateRoom(roomDto);
        messagingTemplate.convertAndSend("/topic/time-auction/rooms", createdRoom);
    }

    @MessageMapping("/time-auction/join")
    public void joinRoom(@Payload JoinRoomMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getUser().getName();
        log.info("=== WebSocket join 요청 받음, playerId: {}, roomId: {} ===", playerId, message.getRoomId());
        
        TimeAuctionRoomDto updatedRoom = roomService.joinRoom(message.getRoomId(), playerId);
        messagingTemplate.convertAndSend("/topic/time-auction/rooms", updatedRoom);
    }

    @MessageMapping("/time-auction/leave")
    public void leaveRoom(@Payload LeaveRoomMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getUser().getName();
        log.info("=== WebSocket leave 요청 받음, playerId: {}, roomId: {} ===", playerId, message.getRoomId());
        
        TimeAuctionRoomDto updatedRoom = roomService.leaveRoom(message.getRoomId(), playerId);
        messagingTemplate.convertAndSend("/topic/time-auction/rooms", updatedRoom);
    }

    @MessageMapping("/time-auction/start")
    public void startGame(@Payload StartGameMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getUser().getName();
        log.info("=== WebSocket start game 요청 받음, playerId: {}, roomId: {} ===", playerId, message.getRoomId());
        
        TimeAuctionRoomDto updatedRoom = roomService.startGame(message.getRoomId(), playerId);
        messagingTemplate.convertAndSend("/topic/time-auction/rooms", updatedRoom);
    }

    @MessageMapping("/time-auction/bet")
    public void placeBet(@Payload BetMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getUser().getName();
        log.info("=== WebSocket bet 요청 받음, playerId: {}, roomId: {}, amount: {} ===", 
                playerId, message.getRoomId(), message.getAmount());
        
        TimeAuctionRoomDto updatedRoom = roomService.placeBet(message.getRoomId(), playerId, message.getAmount());
        messagingTemplate.convertAndSend("/topic/time-auction/rooms", updatedRoom);
    }
} 