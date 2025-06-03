package org.tomkidWorld.angelsPlan.domain.game.timeAuction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.dto.TimeAuctionRoomDto;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.service.TimeAuctionRoomService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rest/time-auction")
@RequiredArgsConstructor
public class TimeAuctionRestController {

    private final TimeAuctionRoomService roomService;

    /**
     * 헬스 체크용 테스트 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("=== Health check 요청 받음 ===");
        return ResponseEntity.ok("Time Auction REST API is working!");
    }

    /**
     * DB를 사용하지 않는 간단한 테스트 엔드포인트
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("=== Test 요청 받음 ===");
        return ResponseEntity.ok("Test API working without DB access!");
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<TimeAuctionRoomDto>> getAllRooms() {
        log.info("=== getAllRooms 요청 받음 ===");
        try {
            log.info("roomService 호출 시작");
            List<TimeAuctionRoomDto> rooms = roomService.getAllRooms();
            log.info("roomService 호출 완료, 방 개수: {}", rooms.size());
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            log.error("getAllRooms 에러 발생: ", e);
            throw e;
        }
    }

    @GetMapping("/rooms/active")
    public ResponseEntity<List<TimeAuctionRoomDto>> getActiveRooms() {
        log.info("=== getActiveRooms 요청 받음 ===");
        try {
            log.info("roomService.getActiveRooms() 호출 시작");
            List<TimeAuctionRoomDto> rooms = roomService.getActiveRooms();
            log.info("roomService.getActiveRooms() 호출 완료, 활성 방 개수: {}", rooms.size());
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            log.error("getActiveRooms 에러 발생: ", e);
            throw e;
        }
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<TimeAuctionRoomDto> getRoomById(@PathVariable String roomId) {
        log.info("=== getRoomById 요청 받음, roomId: {} ===", roomId);
        try {
            log.info("roomService.getRoomByRoomId() 호출 시작");
            TimeAuctionRoomDto room = roomService.getRoomByRoomId(roomId);
            log.info("roomService.getRoomByRoomId() 호출 완료, room: {}", room != null ? "존재" : "null");
            return room != null ? ResponseEntity.ok(room) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("getRoomById 에러 발생: ", e);
            throw e;
        }
    }

    @GetMapping("/rooms/summary")
    public ResponseEntity<RoomSummary> getRoomsSummary() {
        log.info("=== getRoomsSummary 요청 받음 ===");
        try {
            log.info("getAllRooms 호출 시작");
            List<TimeAuctionRoomDto> allRooms = roomService.getAllRooms();
            log.info("getAllRooms 완료, 전체 방 개수: {}", allRooms.size());
            
            log.info("getActiveRooms 호출 시작");
            List<TimeAuctionRoomDto> activeRooms = roomService.getActiveRooms();
            log.info("getActiveRooms 완료, 활성 방 개수: {}", activeRooms.size());
            
            int totalPlayers = activeRooms.stream()
                    .mapToInt(room -> room.getPlayerIds().size())
                    .sum();
            log.info("총 플레이어 수 계산 완료: {}", totalPlayers);
            
            RoomSummary summary = new RoomSummary(allRooms.size(), activeRooms.size(), totalPlayers);
            log.info("요약 정보 생성 완료: {}", summary);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("getRoomsSummary 에러 발생: ", e);
            throw e;
        }
    }

    public record RoomSummary(int totalRooms, int activeRooms, int totalPlayers) {}
} 