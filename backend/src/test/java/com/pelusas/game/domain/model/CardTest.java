package com.pelusas.game.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Carta (Card)")
class CardTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("Debe permitir crear cartas con valores válidos del 1 al 10")
    void shouldCreateCardWithValidValues(int value) {
        Card card = new Card(value);
        assertEquals(value, card.getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {-5, 0, 11, 20})
    @DisplayName("Debe lanzar IllegalArgumentException con valores fuera del rango 1-10")
    void shouldThrowExceptionForInvalidValues(int invalidValue) {
        assertThrows(IllegalArgumentException.class, () -> new Card(invalidValue));
    }

    @Test
    @DisplayName("Debe implementar equals y hashCode correctamente")
    void shouldImplementEqualsAndHashCode() {
        Card cardA1 = new Card(5);
        Card cardA2 = new Card(5);
        Card cardB = new Card(7);

        assertEquals(cardA1, cardA2);
        assertEquals(cardA1.hashCode(), cardA2.hashCode());
        assertNotEquals(cardA1, cardB);
        assertNotEquals(cardA1, null);
        assertNotEquals(cardA1, "String");
        assertEquals(cardA1, cardA1);
    }

    @Test
    @DisplayName("Debe implementar compareTo ordenando por valor")
    void shouldCompareCardsByValue() {
        Card card3 = new Card(3);
        Card card8 = new Card(8);
        Card card3b = new Card(3);

        assertTrue(card3.compareTo(card8) < 0);
        assertTrue(card8.compareTo(card3) > 0);
        assertEquals(0, card3.compareTo(card3b));
    }

    @Test
    @DisplayName("Debe retornar representación textual legible en toString")
    void shouldReturnReadableStringRepresentation() {
        Card card = new Card(4);
        assertTrue(card.toString().contains("4"));
    }
}
