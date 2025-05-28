package org.tomkidWorld.angelsPlan.domain.game.timeAuction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.tomkidWorld.angelsPlan.domain.user.entity.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "time_auction_rooms")
public class TimeAuctionRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String roomId;

    private String roomName;
    private boolean isGameStarted;
    private int currentRound;
    private int maxPlayers = 4;
    private int currentPlayers = 0;

    @ManyToMany
    @JoinTable(
        name = "time_auction_room_players",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> players = new HashSet<>();

    public void addPlayer(User player) {
        if (players.size() < maxPlayers && !isGameStarted) {
            players.add(player);
            currentPlayers++;
        }
    }

    public void removePlayer(User player) {
        if (players.remove(player)) {
            currentPlayers--;
        }
    }

    public boolean isFull() {
        return currentPlayers >= maxPlayers;
    }

    public boolean isEmpty() {
        return currentPlayers == 0;
    }
} 