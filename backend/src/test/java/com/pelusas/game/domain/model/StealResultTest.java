package com.pelusas.game.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - StealResult")
class StealResultTest {

    @Test
    @DisplayName("Debe instanciar y acceder a las propiedades de StealResult")
    void shouldCreateAndAccessStealResult() {
        StealResult sr = new StealResult("p2", "Mateo", 7, 2);

        assertEquals("p2", sr.getVictimPlayerId());
        assertEquals("Mateo", sr.getVictimPlayerName());
        assertEquals(7, sr.getCardValue());
        assertEquals(2, sr.getCount());
        assertEquals(2, sr.count());
    }

    @Test
    @DisplayName("Debe implementar equals, hashCode y toString correctamente")
    void shouldImplementEqualsHashCodeAndToString() {
        StealResult sr1 = new StealResult("p2", "Mateo", 7, 2);
        StealResult sr2 = new StealResult("p2", "Mateo", 7, 2);
        StealResult sr3 = new StealResult("p3", "Juan", 7, 1);

        assertEquals(sr1, sr2);
        assertEquals(sr1.hashCode(), sr2.hashCode());
        assertNotEquals(sr1, sr3);
        assertNotEquals(sr1, null);
        assertNotEquals(sr1, "String");
        assertEquals(sr1, sr1);

        assertTrue(sr1.toString().contains("Mateo"));
        assertTrue(sr1.toString().contains("7"));
    }
}
