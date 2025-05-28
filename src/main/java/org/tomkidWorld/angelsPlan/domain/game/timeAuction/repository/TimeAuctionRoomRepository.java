package org.tomkidWorld.angelsPlan.domain.game.timeAuction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.entity.TimeAuctionRoom;

import java.util.Optional;

public interface TimeAuctionRoomRepository extends JpaRepository<TimeAuctionRoom, Long> {
    Optional<TimeAuctionRoom> findByRoomId(String roomId);
} 