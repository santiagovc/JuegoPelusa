package com.pelusas.game.dto;

import java.time.LocalDateTime;
import java.util.List;

public class GameResponseDto {
    private String id;
    private LocalDateTime createdAt;
    private String status;
    private String turnPhase;
    private int activePlayerIndex;
    private PlayerResponseDto activePlayer;
    private List<PlayerResponseDto> players;
    private int remainingDeckCards;
    private int discardPileCount;
    private CardDto lastDrawnCard;
    private List<StealResponseDto> lastSteals;
    private String lastActionMessage;
    private List<String> winnerNames;

    // Indicadores para el frontend
    private boolean canBank;
    private boolean canDraw;
    private boolean canStop;

    public GameResponseDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTurnPhase() {
        return turnPhase;
    }

    public void setTurnPhase(String turnPhase) {
        this.turnPhase = turnPhase;
    }

    public int getActivePlayerIndex() {
        return activePlayerIndex;
    }

    public void setActivePlayerIndex(int activePlayerIndex) {
        this.activePlayerIndex = activePlayerIndex;
    }

    public PlayerResponseDto getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(PlayerResponseDto activePlayer) {
        this.activePlayer = activePlayer;
    }

    public List<PlayerResponseDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerResponseDto> players) {
        this.players = players;
    }

    public int getRemainingDeckCards() {
        return remainingDeckCards;
    }

    public void setRemainingDeckCards(int remainingDeckCards) {
        this.remainingDeckCards = remainingDeckCards;
    }

    public int getDiscardPileCount() {
        return discardPileCount;
    }

    public void setDiscardPileCount(int discardPileCount) {
        this.discardPileCount = discardPileCount;
    }

    public CardDto getLastDrawnCard() {
        return lastDrawnCard;
    }

    public void setLastDrawnCard(CardDto lastDrawnCard) {
        this.lastDrawnCard = lastDrawnCard;
    }

    public List<StealResponseDto> getLastSteals() {
        return lastSteals;
    }

    public void setLastSteals(List<StealResponseDto> lastSteals) {
        this.lastSteals = lastSteals;
    }

    public String getLastActionMessage() {
        return lastActionMessage;
    }

    public void setLastActionMessage(String lastActionMessage) {
        this.lastActionMessage = lastActionMessage;
    }

    public List<String> getWinnerNames() {
        return winnerNames;
    }

    public void setWinnerNames(List<String> winnerNames) {
        this.winnerNames = winnerNames;
    }

    public boolean isCanBank() {
        return canBank;
    }

    public void setCanBank(boolean canBank) {
        this.canBank = canBank;
    }

    public boolean isCanDraw() {
        return canDraw;
    }

    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }

    public boolean isCanStop() {
        return canStop;
    }

    public void setCanStop(boolean canStop) {
        this.canStop = canStop;
    }
}
