package org.tomkidWorld.angelsPlan.domain.game.timeAuction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.model.TimeAuctionGameRoom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TimeAuctionGameService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ROOM_KEY_PREFIX = "time_auction:room:";
    private static final String ACTIVE_ROOMS_KEY = "time_auction:active_rooms";

    public TimeAuctionGameRoom createRoom(String roomName, String playerId) {
        TimeAuctionGameRoom room = new TimeAuctionGameRoom();
        room.setRoomId(UUID.randomUUID().toString());
        room.setRoomName(roomName);
        room.addPlayer(playerId);
        
        String roomKey = ROOM_KEY_PREFIX + room.getRoomId();
        redisTemplate.opsForValue().set(roomKey, room);
        redisTemplate.opsForSet().add(ACTIVE_ROOMS_KEY, room.getRoomId());
        
        return room;
    }

    public TimeAuctionGameRoom joinRoom(String roomId, String playerId) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        TimeAuctionGameRoom room = (TimeAuctionGameRoom) redisTemplate.opsForValue().get(roomKey);
        
        if (room == null) {
            throw new IllegalArgumentException("존재하지 않는 방입니다.");
        }
        
        if (room.isFull()) {
            throw new IllegalStateException("방이 가득 찼습니다.");
        }
        
        if (room.getPlayerIds().contains(playerId)) {
            throw new IllegalStateException("이미 참가 중인 방입니다.");
        }
        
        room.addPlayer(playerId);
        redisTemplate.opsForValue().set(roomKey, room);
        return room;
    }

    public TimeAuctionGameRoom leaveRoom(String roomId, String playerId) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        TimeAuctionGameRoom room = (TimeAuctionGameRoom) redisTemplate.opsForValue().get(roomKey);
        
        if (room != null) {
            room.removePlayer(playerId);
            if (room.getPlayerIds().isEmpty()) {
                redisTemplate.delete(roomKey);
                redisTemplate.opsForSet().remove(ACTIVE_ROOMS_KEY, roomId);
            } else {
                redisTemplate.opsForValue().set(roomKey, room);
            }
        }
        
        return room;
    }

    public TimeAuctionGameRoom getRoom(String roomId) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        return (TimeAuctionGameRoom) redisTemplate.opsForValue().get(roomKey);
    }

    public TimeAuctionGameRoom startGame(String roomId, String playerId) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        TimeAuctionGameRoom room = (TimeAuctionGameRoom) redisTemplate.opsForValue().get(roomKey);
        
        if (room != null && room.isHost(playerId) && room.canStartGame()) {
            room.startRound();
            redisTemplate.opsForValue().set(roomKey, room);
        }
        
        return room;
    }

    public void placeBet(String roomId, String playerId, Integer betTime) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        TimeAuctionGameRoom room = (TimeAuctionGameRoom) redisTemplate.opsForValue().get(roomKey);
        
        if (room != null) {
            room.placeBet(playerId, betTime);
            redisTemplate.opsForValue().set(roomKey, room);
        }
    }

    public void startRound(String roomId) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        TimeAuctionGameRoom room = (TimeAuctionGameRoom) redisTemplate.opsForValue().get(roomKey);
        
        if (room != null) {
            room.startRound();
            redisTemplate.opsForValue().set(roomKey, room);
        }
    }

    public void endRound(String roomId) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        TimeAuctionGameRoom room = (TimeAuctionGameRoom) redisTemplate.opsForValue().get(roomKey);
        
        if (room != null) {
            room.endRound();
            redisTemplate.opsForValue().set(roomKey, room);
        }
    }

    public void endGame(String roomId) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        TimeAuctionGameRoom room = (TimeAuctionGameRoom) redisTemplate.opsForValue().get(roomKey);
        
        if (room != null) {
            room.endGame();
            // TODO: 게임 결과를 DB에 저장하는 로직 추가
            redisTemplate.delete(roomKey);
            redisTemplate.opsForSet().remove(ACTIVE_ROOMS_KEY, roomId);
        }
    }
} 