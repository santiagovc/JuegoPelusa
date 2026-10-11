package com.pelusas.game.domain.model;

import com.pelusas.game.domain.rules.TurnRules;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Partida (Game)")
class GameTest {

    @Test
    @DisplayName("Debe inicializar la partida con estado válido y el primer jugador activo")
    void shouldInitializeGameWithValidState() {
        Game game = new Game(List.of("Alice", "Bob", "Charlie"));

        assertNotNull(game.getId());
        assertEquals(3, game.getPlayers().size());
        assertEquals("Alice", game.getActivePlayer().getName());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertEquals(TurnPhase.START_OF_TURN, game.getTurnPhase());
        assertEquals(110, game.getDeck().remainingCardsCount());
        assertTrue(game.getDiscardPile().isEmpty());
    }

    @Test
    @DisplayName("Debe validar la cantidad de jugadores entre 2 y 6")
    void shouldValidatePlayerCount() {
        assertThrows(IllegalArgumentException.class, () -> new Game(List.of("SinglePlayer")));
        assertThrows(IllegalArgumentException.class, () -> new Game(List.of("P1", "P2", "P3", "P4", "P5", "P6", "P7")));
        assertThrows(IllegalArgumentException.class, () -> new Game((List<String>) null));
    }

    @Test
    @DisplayName("Debe permitir al jugador activo sacar una carta y cambiar a fase CAN_CONTINUE_OR_STOP")
    void shouldDrawCardAndChangePhase() {
        Deck deck = new Deck(new ArrayList<>(List.of(new Card(5), new Card(8))));
        Game game = new Game("g-1", List.of("Alice", "Bob"), deck, new TurnRules());

        Game.DrawExecutionResult result = game.drawCard();

        assertNotNull(result);
        assertEquals(8, result.getDrawnCard().getValue());
        assertFalse(result.isBusted());
        assertEquals(TurnPhase.CAN_CONTINUE_OR_STOP, game.getTurnPhase());
        assertEquals(1, game.getActivePlayer().countCardsInFront());
        assertEquals(1, game.getDeck().remainingCardsCount());
    }

    @Test
    @DisplayName("Debe permitir al jugador plantarse y pasar el turno al siguiente jugador")
    void shouldStopTurnAndPassToNextPlayer() {
        Deck deck = new Deck(new ArrayList<>(List.of(new Card(5), new Card(8))));
        Game game = new Game("g-1", List.of("Alice", "Bob"), deck, new TurnRules());

        game.drawCard(); // Alice saca un 8
        assertEquals("Alice", game.getActivePlayer().getName());

        game.stopTurn(); // Alice se planta

        assertEquals("Bob", game.getActivePlayer().getName());
        assertEquals(TurnPhase.START_OF_TURN, game.getTurnPhase());
        assertEquals(1, game.getPlayers().get(0).countCardsInFront(), "Alice conserva su carta delante en riesgo");
    }

    @Test
    @DisplayName("Debe impedir plantarse sin haber sacado ninguna carta")
    void shouldNotAllowStopBeforeDrawing() {
        Game game = new Game(List.of("Alice", "Bob"));
        assertThrows(IllegalStateException.class, game::stopTurn);
    }

    @Test
    @DisplayName("Debe permitir al jugador guardar sus cartas al inicio de su turno (Paso 1)")
    void shouldBankCardsAtStartOfTurn() {
        // Configuramos a Alice con cartas acumuladas de un turno previo
        Deck deck = new Deck(new ArrayList<>(List.of(new Card(2), new Card(5), new Card(7))));
        Game game = new Game("g-1", List.of("Alice", "Bob"), deck, new TurnRules());

        // Alice saca un 7 y se planta
        game.drawCard();
        game.stopTurn();

        // Ahora es turno de Bob. Bob saca un 5 y se planta
        game.drawCard();
        game.stopTurn();

        // Vuelve el turno a Alice. Alice tiene 1 carta (7) delante y está en START_OF_TURN
        assertEquals("Alice", game.getActivePlayer().getName());
        assertEquals(1, game.getActivePlayer().countCardsInFront());

        List<Card> banked = game.bankActivePlayerCards();

        assertEquals(1, banked.size());
        assertEquals(0, game.getActivePlayer().countCardsInFront(), "Cartas delante quedaron vacías");
        assertEquals(1, game.getActivePlayer().countBankedCards(), "Cartas pasaron al montón de puntos");
        assertEquals(7, game.getActivePlayer().calculateScore());
    }

    @Test
    @DisplayName("Debe ejecutar el robo de cartas a los rivales al sacar una carta coincidente")
    void shouldStealCardsFromRivalsOnDraw() {
        Deck deck = new Deck(new ArrayList<>(List.of(new Card(4), new Card(4))));
        Game game = new Game("g-1", List.of("Alice", "Bob"), deck, new TurnRules());

        // Simulamos que Bob ya tenía una carta 4 delante
        game.getPlayers().get(1).addCardInFront(new Card(4));

        // Alice saca un 4 del mazo
        Game.DrawExecutionResult result = game.drawCard();

        assertFalse(result.isBusted());
        assertEquals(1, result.getSteals().size());
        assertEquals("Bob", result.getSteals().get(0).getVictimPlayerName());
        assertEquals(4, result.getSteals().get(0).getCardValue());

        // Alice debe tener 2 cartas de valor 4 (la sacada + la robada)
        assertEquals(2, game.getActivePlayer().countCardsInFront());
        // Bob debe quedar con 0 cartas delante
        assertEquals(0, game.getPlayers().get(1).countCardsInFront());
    }

    @Test
    @DisplayName("Debe aplicar regla de pérdida de turno (busto) cuando repite carta con 3 o más cartas delante")
    void shouldTriggerBustWhenRepeatingWith3OrMoreCards() {
        // El mazo tiene una carta 5 en el tope y otra carta para no agotar el mazo
        Deck deck = new Deck(new ArrayList<>(List.of(new Card(9), new Card(5))));
        Game game = new Game("g-1", List.of("Alice", "Bob"), deck, new TurnRules());

        // Alice ya tiene 2 cartas delante: [5, 2]
        game.getActivePlayer().addCardInFront(new Card(5));
        game.getActivePlayer().addCardInFront(new Card(2));

        // Al sacar el 5, repite el 5 y llega a 3 cartas -> ¡BUSTO!
        Game.DrawExecutionResult result = game.drawCard();

        assertTrue(result.isBusted());
        assertEquals(3, result.getDiscardedCards().size());
        assertEquals(3, game.getDiscardPile().size());
        // El turno debió pasar automáticamente a Bob
        assertEquals("Bob", game.getActivePlayer().getName());
        assertEquals(TurnPhase.START_OF_TURN, game.getTurnPhase());
    }

    @Test
    @DisplayName("Debe finalizar la partida cuando se agota el mazo y guardar automáticamente cartas restantes")
    void shouldFinalizeGameWhenDeckBecomesEmpty() {
        // Mazo con solo 1 carta
        Deck deck = new Deck(new ArrayList<>(List.of(new Card(9))));
        Game game = new Game("g-1", List.of("Alice", "Bob"), deck, new TurnRules());

        // Bob tiene una carta 4 delante
        game.getPlayers().get(1).addCardInFront(new Card(4));

        // Alice saca la última carta del mazo (un 9)
        game.drawCard();

        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertEquals(0, game.getDeck().remainingCardsCount());

        // Alice guardó automáticamente su 9 (9 pts)
        assertEquals(9, game.getPlayers().get(0).calculateScore());
        // Bob guardó automáticamente su 4 (4 pts)
        assertEquals(4, game.getPlayers().get(1).calculateScore());

        // Ganador: Alice
        assertEquals(1, game.getWinners().size());
        assertEquals("Alice", game.getWinners().get(0).getName());
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta actuar en partida finalizada")
    void shouldThrowExceptionWhenActingOnFinishedGame() {
        Deck deck = new Deck(new ArrayList<>(List.of(new Card(5))));
        Game game = new Game("g-1", List.of("Alice", "Bob"), deck, new TurnRules());
        game.drawCard(); // Agota el mazo y finaliza la partida

        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertThrows(IllegalStateException.class, game::drawCard);
        assertThrows(IllegalStateException.class, game::bankActivePlayerCards);
        assertThrows(IllegalStateException.class, game::stopTurn);
    }
}
