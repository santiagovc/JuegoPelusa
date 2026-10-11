package com.pelusas.game.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Jugador (Player)")
class PlayerTest {

    @Test
    @DisplayName("Debe crear un jugador con estado inicial limpio")
    void shouldCreatePlayerWithInitialState() {
        Player player = new Player("p1", "Fabian");
        assertEquals("p1", player.getId());
        assertEquals("Fabian", player.getName());
        assertEquals(0, player.countCardsInFront());
        assertEquals(0, player.countBankedCards());
        assertEquals(0, player.calculateScore());
        assertTrue(player.getCardsInFront().isEmpty());
        assertTrue(player.getBankedCards().isEmpty());
    }

    @Test
    @DisplayName("Debe validar que el ID y el nombre no sean nulos ni vacíos")
    void shouldValidateIdAndName() {
        assertThrows(IllegalArgumentException.class, () -> new Player(null, "Fabian"));
        assertThrows(IllegalArgumentException.class, () -> new Player("", "Fabian"));
        assertThrows(IllegalArgumentException.class, () -> new Player("   ", "Fabian"));
        assertThrows(IllegalArgumentException.class, () -> new Player("p1", null));
        assertThrows(IllegalArgumentException.class, () -> new Player("p1", ""));
        assertThrows(IllegalArgumentException.class, () -> new Player("p1", "   "));
    }

    @Test
    @DisplayName("Debe agregar cartas delante del jugador")
    void shouldAddCardsInFront() {
        Player player = new Player("p1", "Fabian");
        player.addCardInFront(new Card(5));
        player.addCardsInFront(List.of(new Card(3), new Card(8)));

        assertEquals(3, player.countCardsInFront());
        assertTrue(player.hasCardInFrontWithValue(5));
        assertTrue(player.hasCardInFrontWithValue(3));
        assertTrue(player.hasCardInFrontWithValue(8));
        assertFalse(player.hasCardInFrontWithValue(10));
        assertEquals(1, player.countCardsInFrontWithValue(5));

        assertThrows(IllegalArgumentException.class, () -> player.addCardInFront(null));
        assertThrows(IllegalArgumentException.class, () -> player.addCardsInFront(null));
    }

    @Test
    @DisplayName("Debe remover cartas de un valor específico al ser robadas")
    void shouldRemoveCardsOfSpecificValue() {
        Player player = new Player("p1", "Fabian");
        player.addCardsInFront(List.of(new Card(4), new Card(7), new Card(4), new Card(2)));

        List<Card> removed = player.removeCardsOfValueInFront(4);

        assertEquals(2, removed.size());
        assertEquals(2, player.countCardsInFront());
        assertFalse(player.hasCardInFrontWithValue(4));
        assertTrue(player.hasCardInFrontWithValue(7));
        assertTrue(player.hasCardInFrontWithValue(2));
    }

    @Test
    @DisplayName("Debe limpiar y retornar cartas al sufrir un busto")
    void shouldClearCardsInFrontOnBust() {
        Player player = new Player("p1", "Fabian");
        player.addCardsInFront(List.of(new Card(1), new Card(2), new Card(3)));

        List<Card> discarded = player.clearCardsInFront();

        assertEquals(3, discarded.size());
        assertEquals(0, player.countCardsInFront());
    }

    @Test
    @DisplayName("Debe pasar cartas al montón de puntos y calcular puntuación acumulada")
    void shouldBankCardsAndCalculateScore() {
        Player player = new Player("p1", "Fabian");
        player.addCardsInFront(List.of(new Card(5), new Card(10)));

        List<Card> secured = player.bankCardsInFront();

        assertEquals(2, secured.size());
        assertEquals(0, player.countCardsInFront());
        assertEquals(2, player.countBankedCards());
        assertEquals(15, player.calculateScore());
    }

    @Test
    @DisplayName("Debe implementar equals, hashCode y toString adecuadamente")
    void shouldImplementEqualsHashCodeAndToString() {
        Player p1 = new Player("p1", "Fabian");
        Player p2 = new Player("p1", "Fabian Diferente");
        Player p3 = new Player("p2", "Lucas");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
        assertNotEquals(p1, null);
        assertNotEquals(p1, "Fabian");

        assertTrue(p1.toString().contains("Fabian"));
    }
}
