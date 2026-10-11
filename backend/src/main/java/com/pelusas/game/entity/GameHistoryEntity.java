package com.pelusas.game.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA para persistir el histórico de partidas terminadas.
 */
@Entity
@Table(name = "game_history")
public class GameHistoryEntity {

    @Id
    @Column(name = "game_id", nullable = false, length = 64)
    private String gameId;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    @Column(name = "total_players", nullable = false)
    private int totalPlayers;

    @Column(name = "winner_name", length = 255)
    private String winnerName;

    @Column(name = "result_summary", length = 1000)
    private String resultSummary;

    @OneToMany(mappedBy = "gameHistory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PlayerHistoryEntity> players = new ArrayList<>();

    public GameHistoryEntity() {
    }

    public GameHistoryEntity(String gameId, LocalDateTime playedAt, int totalPlayers, String winnerName, String resultSummary) {
        this.gameId = gameId;
        this.playedAt = playedAt;
        this.totalPlayers = totalPlayers;
        this.winnerName = winnerName;
        this.resultSummary = resultSummary;
    }

    public void addPlayer(PlayerHistoryEntity playerHistory) {
        players.add(playerHistory);
        playerHistory.setGameHistory(this);
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    public List<PlayerHistoryEntity> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerHistoryEntity> players) {
        this.players = players;
    }
}
