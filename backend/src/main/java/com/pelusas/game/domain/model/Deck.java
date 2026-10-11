package com.pelusas.game.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Representa el mazo (montón de polvo) del juego Pelusas.
 * Contiene exactamente 110 cartas:
 * - Valores 1 al 5: 13 cartas de cada valor (65 cartas)
 * - Valores 6 al 10: 9 cartas de cada valor (45 cartas)
 */
public class Deck implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TOTAL_CARDS = 110;
    public static final int LOW_VALUES_COUNT = 13; // Para valores 1 al 5
    public static final int HIGH_VALUES_COUNT = 9;  // Para valores 6 al 10

    private final List<Card> cards;

    /**
     * Crea un mazo estándar con las 110 cartas según la distribución oficial.
     */
    public Deck() {
        this.cards = new ArrayList<>(TOTAL_CARDS);
        initializeStandardDeck();
    }

    /**
     * Permite crear un mazo con una lista predefinida de cartas (útil para pruebas y escenarios controlados).
     */
    public Deck(List<Card> initialCards) {
        if (initialCards == null) {
            throw new IllegalArgumentException("La lista inicial de cartas no puede ser nula");
        }
        this.cards = new ArrayList<>(initialCards);
    }

    private void initializeStandardDeck() {
        // Valores 1 al 5: 13 cartas de cada valor
        for (int value = 1; value <= 5; value++) {
            for (int i = 0; i < LOW_VALUES_COUNT; i++) {
                cards.add(new Card(value));
            }
        }
        // Valores 6 al 10: 9 cartas de cada valor
        for (int value = 6; value <= 10; value++) {
            for (int i = 0; i < HIGH_VALUES_COUNT; i++) {
                cards.add(new Card(value));
            }
        }
    }

    /**
     * Mezcla las cartas del mazo.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Extrae la carta superior del mazo.
     * @return la carta extraída
     * @throws NoSuchElementException si el mazo está vacío
     */
    public Card draw() {
        if (cards.isEmpty()) {
            throw new NoSuchElementException("El montón de polvo está vacío, no se pueden extraer más cartas");
        }
        return cards.remove(cards.size() - 1);
    }

    /**
     * Permite consultar la carta superior sin extraerla.
     */
    public Card peek() {
        if (cards.isEmpty()) {
            throw new NoSuchElementException("El montón de polvo está vacío");
        }
        return cards.get(cards.size() - 1);
    }

    /**
     * Agrega una carta en la parte superior del mazo (útil para pruebas).
     */
    public void pushCardOnTop(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("La carta no puede ser nula");
        }
        cards.add(card);
    }

    public int remainingCardsCount() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int getCardCountByValue(int value) {
        int count = 0;
        for (Card card : cards) {
            if (card.getValue() == value) {
                count++;
            }
        }
        return count;
    }
}
