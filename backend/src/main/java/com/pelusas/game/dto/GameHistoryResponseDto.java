package com.pelusas.game.dto;

import java.time.LocalDateTime;
import java.util.List;

public class GameHistoryResponseDto {
    private String gameId;
    private LocalDateTime playedAt;
    private int totalPlayers;
    private String winnerName;
    private String resultSummary;
    private List<PlayerHistoryResponseDto> players;

    public GameHistoryResponseDto() {
    }

    public GameHistoryResponseDto(String gameId, LocalDateTime playedAt, int totalPlayers,
                                  String winnerName, String resultSummary, List<PlayerHistoryResponseDto> players) {
        this.gameId = gameId;
        this.playedAt = playedAt;
        this.totalPlayers = totalPlayers;
        this.winnerName = winnerName;
        this.resultSummary = resultSummary;
        this.players = players;
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

    public List<PlayerHistoryResponseDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerHistoryResponseDto> players) {
        this.players = players;
    }
}
