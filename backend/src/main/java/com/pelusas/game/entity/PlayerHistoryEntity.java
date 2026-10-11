package com.pelusas.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA para persistir los datos de cada jugador en una partida terminada.
 */
@Entity
@Table(name = "player_history")
public class PlayerHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameHistoryEntity gameHistory;

    @Column(name = "player_name", nullable = false, length = 100)
    private String playerName;

    @Column(name = "accumulated_cards", nullable = false)
    private int accumulatedCards;

    @Column(name = "final_score", nullable = false)
    private int finalScore;

    @Column(name = "is_winner", nullable = false)
    private boolean isWinner;

    public PlayerHistoryEntity() {
    }

    public PlayerHistoryEntity(String playerName, int accumulatedCards, int finalScore, boolean isWinner) {
        this.playerName = playerName;
        this.accumulatedCards = accumulatedCards;
        this.finalScore = finalScore;
        this.isWinner = isWinner;
    }

    public Long getId() {
        return id;
    }

    public GameHistoryEntity getGameHistory() {
        return gameHistory;
    }

    public void setGameHistory(GameHistoryEntity gameHistory) {
        this.gameHistory = gameHistory;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getAccumulatedCards() {
        return accumulatedCards;
    }

    public void setAccumulatedCards(int accumulatedCards) {
        this.accumulatedCards = accumulatedCards;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }
}
