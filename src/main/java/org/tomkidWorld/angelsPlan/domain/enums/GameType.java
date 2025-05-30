package org.tomkidWorld.angelsPlan.domain.enums;

public enum GameType {
    TIME_AUCTION("시간 경매"),
    // 추후 다른 게임 타입들이 추가될 예정
    ;

    private final String description;

    GameType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 