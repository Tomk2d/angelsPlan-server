package org.tomkidWorld.angelsPlan.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import java.time.LocalDateTime;

@Entity
@Table(name = "Games")
@Getter
@Setter
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("게임 이름")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Comment("게임 설명")
    private String description;

    @Column(nullable = false)
    @Comment("최소 플레이어 수")
    private Integer minPlayers = 2;

    @Column(nullable = false)
    @Comment("최대 플레이어 수")
    private Integer maxPlayers = 4;

    @Column(length = 255)
    @Comment("게임 썸네일 이미지 URL")
    private String thumbnailUrl;

    @Column(nullable = false)
    @Comment("게임 활성화 여부")
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    @Comment("생성일")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("수정일")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 