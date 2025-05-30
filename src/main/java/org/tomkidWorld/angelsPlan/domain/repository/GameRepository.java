package org.tomkidWorld.angelsPlan.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tomkidWorld.angelsPlan.domain.entity.Game;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByIsActiveTrue();
} 