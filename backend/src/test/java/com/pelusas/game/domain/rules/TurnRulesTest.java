package com.pelusas.game.domain.rules;

import com.pelusas.game.domain.model.Card;
import com.pelusas.game.domain.model.Deck;
import com.pelusas.game.domain.model.GameStatus;
import com.pelusas.game.domain.model.Player;
import com.pelusas.game.domain.model.StealResult;
import com.pelusas.game.domain.model.TurnPhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Reglas del Turno (TurnRules)")
class TurnRulesTest {

    private TurnRules turnRules;

    @BeforeEach
    void setUp() {
        turnRules = new TurnRules();
    }

    @Nested
    @DisplayName("Regla de pérdida del turno (Busto)")
    class BustRuleTests {

        @Test
        @DisplayName("1. Repetición con menos de 3 cartas -> NO pierde")
        void shouldNotBustWhenRepeatingWithLessThan3Cards() {
            // Jugador tiene 1 carta delante: [5]. Saca un 5.
            // Total después de sacar = 2 cartas (< 3).
            List<Card> cardsInFrontBefore = List.of(new Card(5));
            Card drawnCard = new Card(5);

            boolean busted = turnRules.evaluateBust(cardsInFrontBefore, drawnCard, 0);

            assertFalse(busted, "Repetición con menos de 3 cartas (total 2) NO debe causar pérdida de turno");
            assertFalse(turnRules.checkBust(true, 2), "checkBust directo con repetición y 2 cartas debe ser false");
        }

        @Test
        @DisplayName("2. Repetición teniendo exactamente 3 cartas -> PIERDE")
        void shouldBustWhenRepeatingWithExactly3Cards() {
            // Jugador tiene 2 cartas delante: [5, 4]. Saca un 5.
            // Total después de sacar = 3 cartas (exactamente 3).
            List<Card> cardsInFrontBefore = List.of(new Card(5), new Card(4));
            Card drawnCard = new Card(5);

            boolean busted = turnRules.evaluateBust(cardsInFrontBefore, drawnCard, 0);

            assertTrue(busted, "Repetición teniendo exactamente 3 cartas DEBE causar pérdida de turno");
            assertTrue(turnRules.checkBust(true, 3), "checkBust directo con repetición y 3 cartas debe ser true");
        }

        @Test
        @DisplayName("3. Repetición teniendo más de 3 cartas -> PIERDE")
        void shouldBustWhenRepeatingWithMoreThan3Cards() {
            // Jugador tiene 3 cartas delante: [5, 4, 8]. Saca un 4.
            // Total después de sacar = 4 cartas (> 3).
            List<Card> cardsInFrontBefore = List.of(new Card(5), new Card(4), new Card(8));
            Card drawnCard = new Card(4);

            boolean busted = turnRules.evaluateBust(cardsInFrontBefore, drawnCard, 0);

            assertTrue(busted, "Repetición teniendo más de 3 cartas (total 4) DEBE causar pérdida de turno");
            assertTrue(turnRules.checkBust(true, 4), "checkBust directo con repetición y 4 cartas debe ser true");
            assertTrue(turnRules.checkBust(true, 5), "checkBust directo con repetición y 5 cartas debe ser true");
        }

        @Test
        @DisplayName("4. Carta que no está repetida -> NO pierde")
        void shouldNotBustWhenCardIsNotRepeated() {
            // Jugador tiene varias cartas delante: [5, 4, 8, 9]. Saca un 2.
            List<Card> cardsInFrontBefore = List.of(new Card(5), new Card(4), new Card(8), new Card(9));
            Card drawnCard = new Card(2);

            boolean busted = turnRules.evaluateBust(cardsInFrontBefore, drawnCard, 0);

            assertFalse(busted, "Una carta que no está repetida NO debe causar pérdida de turno, sin importar el total");
            assertFalse(turnRules.checkBust(false, 3), "checkBust sin repetición con 3 cartas debe ser false");
            assertFalse(turnRules.checkBust(false, 10), "checkBust sin repetición con 10 cartas debe ser false");
        }

        @Test
        @DisplayName("5. Casos límite: jugador sin cartas delante antes de robar")
        void shouldHandleEdgeCaseNoCardsBeforeDraw() {
            List<Card> cardsInFrontBefore = List.of();
            Card drawnCard = new Card(7);

            boolean busted = turnRules.evaluateBust(cardsInFrontBefore, drawnCard, 0);

            assertFalse(busted, "Sacar primera carta nunca debe causar busto");
        }

        @Test
        @DisplayName("6. Caso límite: lista nula de cartas antes de robar")
        void shouldHandleNullListBeforeDraw() {
            Card drawnCard = new Card(7);
            boolean busted = turnRules.evaluateBust(null, drawnCard, 0);
            assertFalse(busted);
        }

        @Test
        @DisplayName("8. Busto considerando cartas robadas adicionales en el total")
        void shouldBustWhenStolenCardsContributeToThreshold() {
            // Jugador tiene [5] (1 carta). Saca un 5 (ya repetido). Roba 1 carta adicional a un rival.
            // Total = 1 (inicial) + 1 (sacada) + 1 (robada) = 3 cartas (llega al umbral de 3)
            List<Card> cardsInFront = List.of(new Card(5));
            Card drawnCard = new Card(5);

            boolean busted = turnRules.evaluateBust(cardsInFront, drawnCard, 1);
            assertTrue(busted, "Con 1 carta robada extra el total llega a 3 y debe bustear");

            boolean notBustedWithoutStolen = turnRules.evaluateBust(cardsInFront, drawnCard, 0);
            assertFalse(notBustedWithoutStolen, "Sin carta robada queda en 2 y no debe bustear");
        }
    }

    @Nested
    @DisplayName("Paso 1 — Guardar pelusas (canBank)")
    class BankRulesTests {

        @Test
        @DisplayName("Debe permitir guardar si está en START_OF_TURN y tiene cartas acumuladas")
        void shouldAllowBankingAtStartOfTurnWithCards() {
            Player player = new Player("p1", "Fabian");
            player.addCardInFront(new Card(4));

            assertTrue(turnRules.canBank(TurnPhase.START_OF_TURN, player));
        }

        @Test
        @DisplayName("NO debe permitir guardar si no tiene cartas delante")
        void shouldNotAllowBankingWhenNoCardsInFront() {
            Player player = new Player("p1", "Fabian");
            assertFalse(turnRules.canBank(TurnPhase.START_OF_TURN, player));
        }

        @Test
        @DisplayName("NO debe permitir guardar una vez empezado el turno de robo (CAN_CONTINUE_OR_STOP)")
        void shouldNotAllowBankingDuringDrawPhase() {
            Player player = new Player("p1", "Fabian");
            player.addCardInFront(new Card(4));

            assertFalse(turnRules.canBank(TurnPhase.CAN_CONTINUE_OR_STOP, player),
                    "Las cartas sacadas en el turno actual están en riesgo y no se pueden guardar hasta el próximo turno");
        }

        @Test
        @DisplayName("Debe retornar false si los parámetros son nulos")
        void shouldReturnFalseForNullParams() {
            assertFalse(turnRules.canBank(null, new Player("p1", "Fabian")));
            assertFalse(turnRules.canBank(TurnPhase.START_OF_TURN, null));
        }
    }

    @Nested
    @DisplayName("Paso 2 — Sacar cartas y plantarse (canDraw y canStop)")
    class DrawAndStopRulesTests {

        @Test
        @DisplayName("canDraw debe ser true solo si la partida está IN_PROGRESS y el mazo no está vacío")
        void shouldAllowDrawOnlyWhenGameInProgressAndDeckNotEmpty() {
            Deck deck = new Deck();
            assertTrue(turnRules.canDraw(GameStatus.IN_PROGRESS, deck));

            assertFalse(turnRules.canDraw(GameStatus.NOT_STARTED, deck));
            assertFalse(turnRules.canDraw(GameStatus.FINISHED, deck));

            Deck emptyDeck = new Deck(new ArrayList<>());
            assertFalse(turnRules.canDraw(GameStatus.IN_PROGRESS, emptyDeck));

            assertFalse(turnRules.canDraw(null, deck));
            assertFalse(turnRules.canDraw(GameStatus.IN_PROGRESS, null));
        }

        @Test
        @DisplayName("canStop debe ser true solo si está en la fase CAN_CONTINUE_OR_STOP")
        void shouldAllowStopOnlyAfterDrawing() {
            assertTrue(turnRules.canStop(TurnPhase.CAN_CONTINUE_OR_STOP));
            assertFalse(turnRules.canStop(TurnPhase.START_OF_TURN), "No puede plantarse sin sacar al menos una carta");
            assertFalse(turnRules.canStop(TurnPhase.TURN_ENDED));
            assertFalse(turnRules.canStop(null));
        }
    }

    @Nested
    @DisplayName("Paso 3 — Robar cartas a rivales (executeSteals)")
    class StealRulesTests {

        @Test
        @DisplayName("Debe robar todas las cartas del mismo valor que los rivales tengan delante")
        void shouldStealAllMatchingCardsFromRivals() {
            Player p1 = new Player("p1", "Lucas");
            Player p2 = new Player("p2", "Mateo");
            Player p3 = new Player("p3", "Juan");

            // p2 tiene dos 7s delante, p3 tiene un 7 y un 3 delante
            p2.addCardInFront(new Card(7));
            p2.addCardInFront(new Card(7));
            p3.addCardInFront(new Card(7));
            p3.addCardInFront(new Card(3));

            Card drawnCard = new Card(7);
            List<Player> allPlayers = List.of(p1, p2, p3);

            List<StealResult> steals = turnRules.executeSteals(p1, allPlayers, drawnCard);

            assertEquals(2, steals.size());
            assertEquals(3, p1.countCardsInFront(), "p1 debe recibir las 3 cartas de valor 7 robadas");
            assertEquals(0, p2.countCardsInFront(), "p2 debe quedar sin cartas");
            assertEquals(1, p3.countCardsInFront(), "p3 debe conservar solo la carta 3");
            assertEquals(3, p3.getCardsInFront().get(0).getValue());
        }

        @Test
        @DisplayName("No debe robar nada si ningún rival tiene cartas con ese valor")
        void shouldNotStealWhenNoRivalHasMatchingCards() {
            Player p1 = new Player("p1", "Lucas");
            Player p2 = new Player("p2", "Mateo");
            p2.addCardInFront(new Card(4));

            Card drawnCard = new Card(8);
            List<StealResult> steals = turnRules.executeSteals(p1, List.of(p1, p2), drawnCard);

            assertTrue(steals.isEmpty());
            assertEquals(0, p1.countCardsInFront());
            assertEquals(1, p2.countCardsInFront());
        }

        @Test
        @DisplayName("Debe manejar argumentos nulos de forma segura")
        void shouldHandleNullArgumentsSafely() {
            Player p1 = new Player("p1", "Lucas");
            assertTrue(turnRules.executeSteals(null, List.of(p1), new Card(5)).isEmpty());
            assertTrue(turnRules.executeSteals(p1, null, new Card(5)).isEmpty());
            assertTrue(turnRules.executeSteals(p1, List.of(p1), null).isEmpty());
        }
    }

    @Nested
    @DisplayName("Puntuación y Desempates (calculateScore y determineWinners)")
    class ScoreAndWinnerTests {

        @Test
        @DisplayName("Debe calcular la puntuación correctamente sumando las cartas guardadas")
        void shouldCalculateScoreCorrectly() {
            Player p = new Player("p1", "Lucas");
            p.addCardInFront(new Card(5));
            p.addCardInFront(new Card(10));
            p.bankCardsInFront();

            assertEquals(15, turnRules.calculateScore(p));
            assertEquals(0, turnRules.calculateScore(null));
        }

        @Test
        @DisplayName("Debe declarar ganador al jugador con mayor puntuación")
        void shouldDetermineWinnerWithHighestScore() {
            Player p1 = new Player("p1", "Lucas");
            Player p2 = new Player("p2", "Mateo");

            p1.addCardInFront(new Card(10));
            p1.addCardInFront(new Card(8));
            p1.bankCardsInFront(); // 18 pts

            p2.addCardInFront(new Card(10));
            p2.addCardInFront(new Card(5));
            p2.bankCardsInFront(); // 15 pts

            List<Player> winners = turnRules.determineWinners(List.of(p1, p2));

            assertEquals(1, winners.size());
            assertEquals("Lucas", winners.get(0).getName());
        }

        @Test
        @DisplayName("En caso de empate en puntuación, desempata quien tenga más cartas acumuladas")
        void shouldBreakTieWithHighestCardCount() {
            Player p1 = new Player("p1", "Lucas");
            Player p2 = new Player("p2", "Mateo");

            // p1 tiene 15 puntos con 2 cartas (10 + 5)
            p1.addCardInFront(new Card(10));
            p1.addCardInFront(new Card(5));
            p1.bankCardsInFront();

            // p2 tiene 15 puntos con 3 cartas (5 + 5 + 5)
            p2.addCardInFront(new Card(5));
            p2.addCardInFront(new Card(5));
            p2.addCardInFront(new Card(5));
            p2.bankCardsInFront();

            List<Player> winners = turnRules.determineWinners(List.of(p1, p2));

            assertEquals(1, winners.size());
            assertEquals("Mateo", winners.get(0).getName(), "Mateo gana el desempate por tener 3 cartas frente a 2");
        }

        @Test
        @DisplayName("Si el empate persiste en puntos y en cantidad de cartas, comparten victoria")
        void shouldShareVictoryOnExactTie() {
            Player p1 = new Player("p1", "Lucas");
            Player p2 = new Player("p2", "Mateo");

            p1.addCardInFront(new Card(10));
            p1.bankCardsInFront();

            p2.addCardInFront(new Card(10));
            p2.bankCardsInFront();

            List<Player> winners = turnRules.determineWinners(List.of(p1, p2));

            assertEquals(2, winners.size());
            assertTrue(winners.contains(p1));
            assertTrue(winners.contains(p2));
        }

        @Test
        @DisplayName("Debe manejar lista vacía o nula de jugadores")
        void shouldHandleEmptyOrNullPlayersList() {
            assertTrue(turnRules.determineWinners(null).isEmpty());
            assertTrue(turnRules.determineWinners(List.of()).isEmpty());
        }
    }
}
