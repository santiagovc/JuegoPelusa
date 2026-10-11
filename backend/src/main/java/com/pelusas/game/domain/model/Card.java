package com.pelusas.game.domain.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa una carta del juego Pelusas con un valor entre 1 y 10.
 */
public final class Card implements Serializable, Comparable<Card> {
    private static final long serialVersionUID = 1L;
    
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 10;

    private final int value;

    public Card(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("El valor de la carta debe estar entre " + MIN_VALUE + " y " + MAX_VALUE);
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(Card other) {
        return Integer.compare(this.value, other.value);
    }

    @Override
    public String toString() {
        return "Card{" + value + '}';
    }
}
