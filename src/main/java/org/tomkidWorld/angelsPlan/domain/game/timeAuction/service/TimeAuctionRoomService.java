package org.tomkidWorld.angelsPlan.domain.game.timeAuction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.TimeAuctionRoomDto;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.entity.TimeAuctionRoom;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.repository.TimeAuctionRoomRepository;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.model.TimeAuctionGameRoom;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeAuctionRoomService {

    private final TimeAuctionRoomRepository roomRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ROOM_KEY_PREFIX = "time_auction:room:";
    private static final String ACTIVE_ROOMS_KEY = "time_auction:active_rooms";

    @Transactional
    public TimeAuctionRoomDto createRoom(TimeAuctionRoomDto roomDto, String playerId) {
        log.info("=== createRoom 시작, playerId: {}, roomName: {} ===", playerId, roomDto.getRoomName());
        try {
            TimeAuctionRoom room = new TimeAuctionRoom();
            room.setRoomId(UUID.randomUUID().toString());
            room.setRoomName(roomDto.getRoomName());
            room.addPlayer(playerId);
        
            log.info("데이터베이스 저장 시작");
            room = roomRepository.save(room);
            log.info("데이터베이스 저장 완료, roomId: {}", room.getRoomId());
            
            return convertToDto(room);
        } catch (Exception e) {
            log.error("createRoom 에러 발생: ", e);
            throw e;
        }
    }

    @Transactional
    public TimeAuctionRoomDto joinRoom(String roomId, String playerId) {
        log.info("=== joinRoom 시작, roomId: {}, playerId: {} ===", roomId, playerId);
        try {
            TimeAuctionRoomDto room = getRoomByRoomId(roomId);
            if (room == null) {
                throw new RuntimeException("방을 찾을 수 없습니다: " + roomId);
            }
            
            if (room.isFull()) {
                throw new RuntimeException("방이 가득 찼습니다.");
            }
            
            if (room.getStatus().equals("PLAYING")) {
                throw new RuntimeException("이미 게임이 시작된 방입니다.");
            }
            
            // Set을 List로 변환
            List<String> playerIdsList = new ArrayList<>(room.getPlayerIds());
            if (!playerIdsList.contains(playerId)) {
                playerIdsList.add(playerId);
            }
            
            TimeAuctionRoomDto updatedRoom = TimeAuctionRoomDto.builder()
                    .roomId(room.getRoomId())
                    .roomName(room.getRoomName())
                    .hostId(room.getHostId())
                    .currentRound(room.getCurrentRound())
                    .totalRounds(room.getTotalRounds())
                    .maxPlayers(room.getMaxPlayers())
                    .minPlayers(room.getMinPlayers())
                    .playerIds(playerIdsList)
                    .currentBets(room.getCurrentBets())
                    .remainingTime(room.getRemainingTime())
                    .status(room.getStatus())
                    .createdAt(room.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .full(playerIdsList.size() >= room.getMaxPlayers())
                    .build();
            
            return updateRoom(updatedRoom);
        } catch (Exception e) {
            log.error("joinRoom 에러 발생: ", e);
            throw e;
        }
    }

    @Transactional
    public TimeAuctionRoomDto leaveRoom(String roomId, String playerId) {
        log.info("=== leaveRoom 시작, roomId: {}, playerId: {} ===", roomId, playerId);
        try {
            log.info("데이터베이스에서 방 조회 시작");
            TimeAuctionRoom room = roomRepository.findByRoomId(roomId);
            log.info("방 조회 완료, room: {}", room != null ? "존재" : "null");
            
            if (room != null) {
                room.removePlayer(playerId);
                if (room.getPlayerIds().isEmpty()) {
                    room.setIsActive(false);
                }
                log.info("플레이어 제거 후 저장 시작");
                room = roomRepository.save(room);
                log.info("저장 완료");
            }
            return convertToDto(room);
        } catch (Exception e) {
            log.error("leaveRoom 에러 발생: ", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<TimeAuctionRoomDto> getAllRooms() {
        Collection<Object> roomIds = redisTemplate.opsForSet().members(ACTIVE_ROOMS_KEY);
        if (roomIds == null) {
            return new ArrayList<>();
        }
        return roomIds.stream()
                .map(roomId -> (String) roomId)
                .map(this::getRoomByRoomId)
                .filter(room -> room != null)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimeAuctionRoomDto> getActiveRooms() {
        List<TimeAuctionRoomDto> allRooms = getAllRooms();
        if (allRooms == null) {
            return new ArrayList<>();
        }
        return allRooms.stream()
                .filter(room -> room.getStatus().equals("WAITING"))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TimeAuctionRoomDto getRoomByRoomId(String roomId) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        TimeAuctionGameRoom room = (TimeAuctionGameRoom) redisTemplate.opsForValue().get(roomKey);
        
        if (room == null) {
            return null;
        }

        return TimeAuctionRoomDto.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .hostId(room.getHostId())
                .currentRound(room.getCurrentRound())
                .totalRounds(room.getTotalRounds())
                .maxPlayers(room.getMaxPlayers())
                .minPlayers(room.getMinPlayers())
                .playerIds(new ArrayList<>(room.getPlayerIds()))
                .currentBets(room.getCurrentBets())
                .remainingTime(room.getRemainingTime())
                .status(room.getStatus().toString())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }

    @Transactional
    public TimeAuctionRoomDto updateRoom(TimeAuctionRoomDto room) {
        log.info("=== updateRoom 시작, roomId: {} ===", room.getRoomId());
        try {
            // Set을 List로 변환
            List<String> playerIdsList = new ArrayList<>(room.getPlayerIds());
            
            TimeAuctionRoomDto updatedRoom = TimeAuctionRoomDto.builder()
                    .roomId(room.getRoomId())
                    .roomName(room.getRoomName())
                    .hostId(room.getHostId())
                    .currentRound(room.getCurrentRound())
                    .totalRounds(room.getTotalRounds())
                    .maxPlayers(room.getMaxPlayers())
                    .minPlayers(room.getMinPlayers())
                    .playerIds(playerIdsList)
                    .currentBets(room.getCurrentBets())
                    .remainingTime(room.getRemainingTime())
                    .status(room.getStatus())
                    .createdAt(room.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .full(room.isFull())
                    .build();
            
            redisTemplate.opsForValue().set(updatedRoom.getRoomId(), updatedRoom);
            return updatedRoom;
        } catch (Exception e) {
            log.error("updateRoom 에러 발생: ", e);
            throw e;
        }
    }

    @Transactional
    public TimeAuctionRoomDto startGame(String roomId, String playerId) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        TimeAuctionRoomDto room = getRoomByRoomId(roomId);
        
        if (room == null || !room.getHostId().equals(playerId)) {
            throw new RuntimeException("방을 찾을 수 없거나 방장이 아닙니다.");
        }
        
        room.setStatus("PLAYING");
        room.setCurrentRound(1);
        room.setRemainingTime(60); // 60초로 시작
        
        redisTemplate.opsForValue().set(roomKey, room);
        return room;
    }

    @Transactional
    public TimeAuctionRoomDto placeBet(String roomId, String playerId, Integer amount) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        TimeAuctionRoomDto room = getRoomByRoomId(roomId);
        
        if (room == null) {
            throw new RuntimeException("방을 찾을 수 없습니다.");
        }
        
        if (!room.getStatus().equals("PLAYING")) {
            throw new RuntimeException("게임이 진행 중이 아닙니다.");
        }
        
        if (!room.getPlayerIds().contains(playerId)) {
            throw new RuntimeException("참가자가 아닙니다.");
        }
        
        // 현재 베팅 정보 업데이트
        room.getCurrentBets().put(playerId, amount);
        
        // Redis에 저장
        redisTemplate.opsForValue().set(roomKey, room);
        return room;
    }

    @Transactional
    public TimeAuctionRoomDto quickJoin(String playerId) {
        log.info("=== quickJoin 시작, playerId: {} ===", playerId);
        
        // 1. 참가 가능한 방 찾기 (인원이 많고 오래된 순)
        List<TimeAuctionRoomDto> availableRooms = getAllRooms().stream()
                .filter(room -> room.getStatus().equals("WAITING"))
                .filter(room -> room.getPlayerIds() != null && room.getPlayerIds().size() < room.getMaxPlayers())
                .sorted((a, b) -> {
                    // 1. 인원 수로 정렬 (내림차순)
                    int playerCountCompare = (b.getPlayerIds() != null ? b.getPlayerIds().size() : 0) - (a.getPlayerIds() != null ? a.getPlayerIds().size() : 0);
                    if (playerCountCompare != 0) {
                        return playerCountCompare;
                    }
                    // 2. 생성 시간으로 정렬 (오름차순)
                    return a.getCreatedAt().compareTo(b.getCreatedAt());
                })
                .collect(Collectors.toList());
        
        // 2. 참가 가능한 방이 있으면 참가
        if (!availableRooms.isEmpty()) {
            TimeAuctionRoomDto room = availableRooms.get(0);
            log.info("참가 가능한 방 찾음: {}", room.getRoomId());
            return joinRoom(room.getRoomId(), playerId);
        }
        
        // 3. 없으면 새 방 생성
        log.info("참가 가능한 방이 없어 새 방 생성");
        TimeAuctionRoomDto newRoom = TimeAuctionRoomDto.builder()
                .roomName("빠른 참가 방")
                .hostId(playerId)
                .currentRound(0)
                .totalRounds(10)
                .maxPlayers(4)
                .minPlayers(2)
                .playerIds(new ArrayList<>())
                .currentBets(new java.util.HashMap<>())
                .status("WAITING")
                .build();
        
        return updateRoom(newRoom);
    }

    private TimeAuctionRoomDto convertToDto(TimeAuctionRoom room) {
        if (room == null) return null;
        
        try {
            return TimeAuctionRoomDto.builder()
                    .roomId(room.getRoomId())
                    .roomName(room.getRoomName())
                    .currentRound(room.getCurrentRound())
                    .totalRounds(room.getTotalRounds())
                    .maxPlayers(room.getMaxPlayers())
                    .minPlayers(room.getMinPlayers())
                    .playerIds(new ArrayList<>(room.getPlayerIds()))
                    .status(room.getIsActive() ? "WAITING" : "CLOSED")
                    .build();
        } catch (Exception e) {
            log.error("convertToDto 에러 발생: ", e);
            throw e;
        }
    }
} 