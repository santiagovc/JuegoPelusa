package com.pelusas.game.dto;

public class PlayerHistoryResponseDto {
    private String playerName;
    private int accumulatedCards;
    private int finalScore;
    private boolean isWinner;

    public PlayerHistoryResponseDto() {
    }

    public PlayerHistoryResponseDto(String playerName, int accumulatedCards, int finalScore, boolean isWinner) {
        this.playerName = playerName;
        this.accumulatedCards = accumulatedCards;
        this.finalScore = finalScore;
        this.isWinner = isWinner;
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
