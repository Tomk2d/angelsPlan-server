package org.tomkidWorld.angelsPlan.domain.game.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tomkidWorld.angelsPlan.domain.entity.Game;
import org.tomkidWorld.angelsPlan.domain.repository.GameRepository;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/games")
public class GameListController {

    private final GameRepository gameRepository;

    public GameListController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @GetMapping("/list")
    public List<GameInfo> getGameList() {
        return gameRepository.findByIsActiveTrue().stream()
                .map(game -> new GameInfo(
                    game.getId(),
                    game.getName(),
                    game.getDescription(),
                    game.getMinPlayers(),
                    game.getMaxPlayers(),
                    game.getThumbnailUrl()
                ))
                .collect(Collectors.toList());
    }

    public static class GameInfo {
        private final Long id;
        private final String name;
        private final String description;
        private final Integer minPlayers;
        private final Integer maxPlayers;
        private final String thumbnailUrl;

        public GameInfo(Long id, String name, String description, Integer minPlayers, Integer maxPlayers, String thumbnailUrl) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.minPlayers = minPlayers;
            this.maxPlayers = maxPlayers;
            this.thumbnailUrl = thumbnailUrl;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Integer getMinPlayers() {
            return minPlayers;
        }

        public Integer getMaxPlayers() {
            return maxPlayers;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }
    }
} 