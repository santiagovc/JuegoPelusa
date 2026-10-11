package com.pelusas.game.dto;

import java.util.List;

public class DrawActionResponseDto {
    private CardDto drawnCard;
    private boolean busted;
    private List<CardDto> lostCards;
    private List<StealResponseDto> steals;
    private GameResponseDto gameState;

    public DrawActionResponseDto() {
    }

    public DrawActionResponseDto(CardDto drawnCard, boolean busted, List<CardDto> lostCards,
                                 List<StealResponseDto> steals, GameResponseDto gameState) {
        this.drawnCard = drawnCard;
        this.busted = busted;
        this.lostCards = lostCards;
        this.steals = steals;
        this.gameState = gameState;
    }

    public CardDto getDrawnCard() {
        return drawnCard;
    }

    public void setDrawnCard(CardDto drawnCard) {
        this.drawnCard = drawnCard;
    }

    public boolean isBusted() {
        return busted;
    }

    public void setBusted(boolean busted) {
        this.busted = busted;
    }

    public List<CardDto> getLostCards() {
        return lostCards;
    }

    public void setLostCards(List<CardDto> lostCards) {
        this.lostCards = lostCards;
    }

    public List<StealResponseDto> getSteals() {
        return steals;
    }

    public void setSteals(List<StealResponseDto> steals) {
        this.steals = steals;
    }

    public GameResponseDto getGameState() {
        return gameState;
    }

    public void setGameState(GameResponseDto gameState) {
        this.gameState = gameState;
    }
}
