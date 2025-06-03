package org.tomkidWorld.angelsPlan.domain.game.timeAuction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.CreateRoomMessage;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.JoinRoomMessage;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.LeaveRoomMessage;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.TimeAuctionRoomDto;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.service.TimeAuctionRoomService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TimeAuctionController {

    private final TimeAuctionRoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/time-auction/create")
    public void createRoom(@Payload CreateRoomMessage message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("방 생성 요청 받음: {}", message);
        String playerId = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : message.getPlayerId();
        log.info("사용자 ID: {}", playerId);
        
        TimeAuctionRoomDto roomDto = new TimeAuctionRoomDto();
        roomDto.setRoomName(message.getRoomName());
        TimeAuctionRoomDto room = roomService.createRoom(roomDto, playerId);
        log.info("방 생성 완료: {}", room);
        
        messagingTemplate.convertAndSend("/topic/time-auction/rooms", room);
    }

    @MessageMapping("/time-auction/join")
    public void joinRoom(@Payload JoinRoomMessage message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("방 참가 요청 받음: {}", message);
        String playerId = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : message.getPlayerId();
        log.info("사용자 ID: {}", playerId);
        
        String roomId = message.getRoomId();
        TimeAuctionRoomDto room = roomService.joinRoom(roomId, playerId);
        log.info("방 참가 완료: {}", room);
        
        messagingTemplate.convertAndSend("/topic/time-auction/rooms", room);
    }

    @MessageMapping("/time-auction/leave")
    public void leaveRoom(@Payload LeaveRoomMessage message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("방 퇴장 요청 받음: {}", message);
        String playerId = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : message.getPlayerId();
        log.info("사용자 ID: {}", playerId);
        
        String roomId = message.getRoomId();
        TimeAuctionRoomDto room = roomService.leaveRoom(roomId, playerId);
        log.info("방 퇴장 완료: {}", room);
        
        messagingTemplate.convertAndSend("/topic/time-auction/rooms", room);
    }
} 