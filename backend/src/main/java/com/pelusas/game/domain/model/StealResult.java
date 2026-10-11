package com.pelusas.game.domain.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa el resultado del robo de cartas a un jugador rival.
 */
public class StealResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String victimPlayerId;
    private final String victimPlayerName;
    private final int cardValue;
    private final int count;

    public StealResult(String victimPlayerId, String victimPlayerName, int cardValue, int count) {
        this.victimPlayerId = victimPlayerId;
        this.victimPlayerName = victimPlayerName;
        this.cardValue = cardValue;
        this.count = count;
    }

    public String getVictimPlayerId() {
        return victimPlayerId;
    }

    public String getVictimPlayerName() {
        return victimPlayerName;
    }

    public int getCardValue() {
        return cardValue;
    }

    public int count() {
        return count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StealResult that = (StealResult) o;
        return cardValue == that.cardValue && count == that.count && Objects.equals(victimPlayerId, that.victimPlayerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(victimPlayerId, cardValue, count);
    }

    @Override
    public String toString() {
        return "StealResult{" +
                "victimPlayerName='" + victimPlayerName + '\'' +
                ", cardValue=" + cardValue +
                ", count=" + count +
                '}';
    }
}
