package org.tomkidWorld.angelsPlan.domain.game.timeAuction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tomkidWorld.angelsPlan.domain.game.timeAuction.entity.TimeAuctionRoom;
import java.util.List;

@Repository
public interface TimeAuctionRoomRepository extends JpaRepository<TimeAuctionRoom, Long> {
    TimeAuctionRoom findByRoomId(String roomId);
    List<TimeAuctionRoom> findByIsActiveTrue();
    List<TimeAuctionRoom> findByIsActiveTrueOrderByCreatedAtDesc();
} 