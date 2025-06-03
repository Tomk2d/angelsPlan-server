package org.tomkidWorld.angelsPlan.domain.game.timeAuction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.TimeAuctionRoomDto;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.entity.TimeAuctionRoom;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.repository.TimeAuctionRoomRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeAuctionRoomService {

    private final TimeAuctionRoomRepository roomRepository;

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
            log.info("데이터베이스에서 방 조회 시작");
            TimeAuctionRoom room = roomRepository.findByRoomId(roomId);
            log.info("방 조회 완료, room: {}", room != null ? "존재" : "null");
            
            if (room != null && !room.isFull()) {
                room.addPlayer(playerId);
                log.info("플레이어 추가 후 저장 시작");
                room = roomRepository.save(room);
                log.info("저장 완료");
            }
            return convertToDto(room);
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
        log.info("=== getAllRooms 시작 ===");
        try {
            log.info("데이터베이스에서 전체 방 조회 시작");
            List<TimeAuctionRoom> rooms = roomRepository.findAll();
            log.info("전체 방 조회 완료, 개수: {}", rooms.size());
            
            log.info("DTO 변환 시작");
            List<TimeAuctionRoomDto> result = rooms.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            log.info("DTO 변환 완료, 결과 개수: {}", result.size());
            
            return result;
        } catch (Exception e) {
            log.error("getAllRooms 에러 발생: ", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<TimeAuctionRoomDto> getActiveRooms() {
        log.info("=== getActiveRooms 시작 ===");
        try {
            log.info("데이터베이스에서 활성 방 조회 시작");
            List<TimeAuctionRoom> rooms = roomRepository.findByIsActiveTrue();
            log.info("활성 방 조회 완료, 개수: {}", rooms.size());
            
            log.info("DTO 변환 시작");
            List<TimeAuctionRoomDto> result = rooms.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            log.info("DTO 변환 완료, 결과 개수: {}", result.size());
            
            return result;
        } catch (Exception e) {
            log.error("getActiveRooms 에러 발생: ", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public TimeAuctionRoomDto getRoomByRoomId(String roomId) {
        log.info("=== getRoomByRoomId 시작, roomId: {} ===", roomId);
        try {
            log.info("데이터베이스에서 특정 방 조회 시작");
            TimeAuctionRoom room = roomRepository.findByRoomId(roomId);
            log.info("방 조회 완료, room: {}", room != null ? "존재" : "null");
            
            TimeAuctionRoomDto result = convertToDto(room);
            log.info("DTO 변환 완료");
            
            return result;
        } catch (Exception e) {
            log.error("getRoomByRoomId 에러 발생: ", e);
            throw e;
        }
    }

    private TimeAuctionRoomDto convertToDto(TimeAuctionRoom room) {
        if (room == null) return null;
        
        try {
            TimeAuctionRoomDto dto = new TimeAuctionRoomDto();
            dto.setRoomId(room.getRoomId());
            dto.setRoomName(room.getRoomName());
            dto.setCurrentRound(room.getCurrentRound());
            dto.setTotalRounds(room.getTotalRounds());
            dto.setMaxPlayers(room.getMaxPlayers());
            dto.setPlayerIds(room.getPlayerIds());
            dto.setIsActive(room.getIsActive());
            return dto;
        } catch (Exception e) {
            log.error("convertToDto 에러 발생: ", e);
            throw e;
        }
    }
} 