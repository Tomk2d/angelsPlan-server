package org.tomkidWorld.angelsPlan.domain.game.timeAuction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.TimeAuctionMessage;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.TimeAuctionRoomDto;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.entity.TimeAuctionRoom;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.repository.TimeAuctionRoomRepository;
import org.tomkidWorld.angelsPlan.domain.user.entity.User;
import org.tomkidWorld.angelsPlan.domain.user.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeAuctionRoomService {
    private final TimeAuctionRoomRepository roomRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public TimeAuctionRoomDto createRoom(String roomName, Long userId) {
        User user = userService.findById(userId);
        
        TimeAuctionRoom room = new TimeAuctionRoom();
        room.setRoomId(UUID.randomUUID().toString());
        room.setRoomName(roomName);
        room.addPlayer(user);
        
        TimeAuctionRoom savedRoom = roomRepository.save(room);
        
        // 룸 생성 메시지 브로드캐스트
        TimeAuctionMessage message = new TimeAuctionMessage();
        message.setType("ROOM_CREATED");
        message.setRoomId(room.getRoomId());
        messagingTemplate.convertAndSend("/topic/game/rooms", message);
        
        return TimeAuctionRoomDto.from(savedRoom);
    }

    public TimeAuctionRoomDto joinRoom(String roomId, Long userId) {
        TimeAuctionRoom room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        User user = userService.findById(userId);
        
        if (room.isFull()) {
            throw new RuntimeException("Room is full");
        }
        
        if (room.isGameStarted()) {
            throw new RuntimeException("Game already started");
        }
        
        room.addPlayer(user);
        TimeAuctionRoom savedRoom = roomRepository.save(room);
        
        // 플레이어 입장 메시지 브로드캐스트
        TimeAuctionMessage message = new TimeAuctionMessage();
        message.setType("PLAYER_JOINED");
        message.setRoomId(roomId);
        message.setPlayerId(userId.toString());
        messagingTemplate.convertAndSend("/topic/game/" + roomId, message);
        
        return TimeAuctionRoomDto.from(savedRoom);
    }

    public void leaveRoom(String roomId, Long userId) {
        TimeAuctionRoom room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        User user = userService.findById(userId);
        room.removePlayer(user);
        
        // 플레이어 퇴장 메시지 브로드캐스트
        TimeAuctionMessage message = new TimeAuctionMessage();
        message.setType("PLAYER_LEFT");
        message.setRoomId(roomId);
        message.setPlayerId(userId.toString());
        messagingTemplate.convertAndSend("/topic/game/" + roomId, message);
        
        if (room.isEmpty()) {
            roomRepository.delete(room);
            // 룸 삭제 메시지 브로드캐스트
            message.setType("ROOM_DELETED");
            messagingTemplate.convertAndSend("/topic/game/rooms", message);
        } else {
            roomRepository.save(room);
        }
    }

    public List<TimeAuctionRoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(TimeAuctionRoomDto::from)
                .collect(Collectors.toList());
    }

    public TimeAuctionRoomDto getRoom(String roomId) {
        TimeAuctionRoom room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return TimeAuctionRoomDto.from(room);
    }
} 