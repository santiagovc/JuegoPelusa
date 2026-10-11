package com.pelusas.game.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Mazo (Deck)")
class DeckTest {

    @Test
    @DisplayName("Debe crear un mazo con exactamente 110 cartas según la regla oficial")
    void shouldCreateDeckWithExactly110Cards() {
        Deck deck = new Deck();
        assertEquals(110, deck.remainingCardsCount());
        assertEquals(Deck.TOTAL_CARDS, deck.remainingCardsCount());
        assertFalse(deck.isEmpty());
    }

    @Test
    @DisplayName("Debe contener exactamente 13 cartas de cada valor del 1 al 5 (65 cartas)")
    void shouldContain13CardsOfValues1To5() {
        Deck deck = new Deck();
        int totalLowValues = 0;

        for (int value = 1; value <= 5; value++) {
            int count = deck.getCardCountByValue(value);
            assertEquals(13, count, "El valor " + value + " debe tener exactamente 13 cartas");
            totalLowValues += count;
        }

        assertEquals(65, totalLowValues, "Los valores del 1 al 5 deben sumar exactamente 65 cartas");
    }

    @Test
    @DisplayName("Debe contener exactamente 9 cartas de cada valor del 6 al 10 (45 cartas)")
    void shouldContain9CardsOfValues6To10() {
        Deck deck = new Deck();
        int totalHighValues = 0;

        for (int value = 6; value <= 10; value++) {
            int count = deck.getCardCountByValue(value);
            assertEquals(9, count, "El valor " + value + " debe tener exactamente 9 cartas");
            totalHighValues += count;
        }

        assertEquals(45, totalHighValues, "Los valores del 6 al 10 deben sumar exactamente 45 cartas");
    }

    @Test
    @DisplayName("No debe contener cartas fuera del rango permitido de 1 a 10")
    void shouldNotContainCardsOutsideAllowedRange() {
        Deck deck = new Deck();
        for (Card card : deck.getCards()) {
            assertTrue(card.getValue() >= Card.MIN_VALUE && card.getValue() <= Card.MAX_VALUE,
                    "La carta debe tener un valor entre 1 y 10");
        }
        assertEquals(0, deck.getCardCountByValue(0));
        assertEquals(0, deck.getCardCountByValue(11));
    }

    @Test
    @DisplayName("Debe permitir extraer cartas una a una reduciendo la cantidad restante")
    void shouldDrawCardsCorrectly() {
        Deck deck = new Deck();
        int initialCount = deck.remainingCardsCount();

        Card card = deck.draw();
        assertNotNull(card);
        assertEquals(initialCount - 1, deck.remainingCardsCount());
    }

    @Test
    @DisplayName("Debe permitir consultar la carta superior con peek sin removerla")
    void shouldPeekTopCardWithoutRemovingIt() {
        Deck deck = new Deck();
        int initialCount = deck.remainingCardsCount();

        Card peeked = deck.peek();
        assertNotNull(peeked);
        assertEquals(initialCount, deck.remainingCardsCount());

        Card drawn = deck.draw();
        assertEquals(peeked, drawn);
        assertEquals(initialCount - 1, deck.remainingCardsCount());
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException al intentar extraer de un mazo vacío")
    void shouldThrowExceptionWhenDrawingFromEmptyDeck() {
        Deck emptyDeck = new Deck(new ArrayList<>());
        assertTrue(emptyDeck.isEmpty());
        assertEquals(0, emptyDeck.remainingCardsCount());

        assertThrows(NoSuchElementException.class, emptyDeck::draw);
        assertThrows(NoSuchElementException.class, emptyDeck::peek);
    }

    @Test
    @DisplayName("Debe permitir mezclar las cartas")
    void shouldShuffleDeck() {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        deck2.shuffle();

        // Al mezclar, la cantidad de cartas y distribución se mantiene igual
        assertEquals(110, deck2.remainingCardsCount());
        assertEquals(13, deck2.getCardCountByValue(1));
        assertEquals(9, deck2.getCardCountByValue(10));
    }

    @Test
    @DisplayName("Debe soportar la inicialización con una lista personalizada de cartas")
    void shouldSupportCustomCardsInitialization() {
        List<Card> customCards = List.of(new Card(3), new Card(7), new Card(5));
        Deck customDeck = new Deck(customCards);

        assertEquals(3, customDeck.remainingCardsCount());
        assertEquals(new Card(5), customDeck.draw());
        assertEquals(new Card(7), customDeck.draw());
        assertEquals(new Card(3), customDeck.draw());
        assertTrue(customDeck.isEmpty());
    }

    @Test
    @DisplayName("Debe validar que la lista de cartas no sea nula al crear mazo personalizado")
    void shouldThrowWhenCustomCardsListIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Deck(null));
    }

    @Test
    @DisplayName("Debe permitir agregar cartas en la parte superior del mazo")
    void shouldPushCardOnTop() {
        Deck deck = new Deck(new ArrayList<>());
        deck.pushCardOnTop(new Card(4));
        assertEquals(1, deck.remainingCardsCount());
        assertEquals(new Card(4), deck.draw());

        assertThrows(IllegalArgumentException.class, () -> deck.pushCardOnTop(null));
    }
}
