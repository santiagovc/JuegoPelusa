package com.pelusas.game.dto;

import java.util.List;

public class PlayerResponseDto {
    private String id;
    private String name;
    private List<CardDto> cardsInFront;
    private int cardsInFrontCount;
    private List<CardDto> bankedCards;
    private int bankedCardsCount;
    private int score;
    private boolean isWinner;

    public PlayerResponseDto() {
    }

    public PlayerResponseDto(String id, String name, List<CardDto> cardsInFront, int cardsInFrontCount,
                             List<CardDto> bankedCards, int bankedCardsCount, int score, boolean isWinner) {
        this.id = id;
        this.name = name;
        this.cardsInFront = cardsInFront;
        this.cardsInFrontCount = cardsInFrontCount;
        this.bankedCards = bankedCards;
        this.bankedCardsCount = bankedCardsCount;
        this.score = score;
        this.isWinner = isWinner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CardDto> getCardsInFront() {
        return cardsInFront;
    }

    public void setCardsInFront(List<CardDto> cardsInFront) {
        this.cardsInFront = cardsInFront;
    }

    public int getCardsInFrontCount() {
        return cardsInFrontCount;
    }

    public void setCardsInFrontCount(int cardsInFrontCount) {
        this.cardsInFrontCount = cardsInFrontCount;
    }

    public List<CardDto> getBankedCards() {
        return bankedCards;
    }

    public void setBankedCards(List<CardDto> bankedCards) {
        this.bankedCards = bankedCards;
    }

    public int getBankedCardsCount() {
        return bankedCardsCount;
    }

    public void setBankedCardsCount(int bankedCardsCount) {
        this.bankedCardsCount = bankedCardsCount;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }
}
