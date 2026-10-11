package com.pelusas.game.dto;

public class StealResponseDto {
    private String victimPlayerId;
    private String victimPlayerName;
    private int cardValue;
    private int count;

    public StealResponseDto() {
    }

    public StealResponseDto(String victimPlayerId, String victimPlayerName, int cardValue, int count) {
        this.victimPlayerId = victimPlayerId;
        this.victimPlayerName = victimPlayerName;
        this.cardValue = cardValue;
        this.count = count;
    }

    public String getVictimPlayerId() {
        return victimPlayerId;
    }

    public void setVictimPlayerId(String victimPlayerId) {
        this.victimPlayerId = victimPlayerId;
    }

    public String getVictimPlayerName() {
        return victimPlayerName;
    }

    public void setVictimPlayerName(String victimPlayerName) {
        this.victimPlayerName = victimPlayerName;
    }

    public int getCardValue() {
        return cardValue;
    }

    public void setCardValue(int cardValue) {
        this.cardValue = cardValue;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
