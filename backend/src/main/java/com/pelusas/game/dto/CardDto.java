package com.pelusas.game.dto;

public class CardDto {
    private int value;

    public CardDto() {
    }

    public CardDto(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
