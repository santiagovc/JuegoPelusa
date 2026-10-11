package com.pelusas.game.domain.rules;

import com.pelusas.game.domain.model.Card;
import com.pelusas.game.domain.model.Deck;
import com.pelusas.game.domain.model.GameStatus;
import com.pelusas.game.domain.model.Player;
import com.pelusas.game.domain.model.StealResult;
import com.pelusas.game.domain.model.TurnPhase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Contiene y centraliza las reglas de negocio oficiales del juego Pelusas.
 */
public class TurnRules {

    public static final int BUST_CARD_THRESHOLD = 3;

    /**
     * Evalúa si un jugador pierde el turno al sacar una carta.
     * Regla oficial:
     * Un jugador pierde el turno cuando:
     * 1. Saca una carta cuyo valor ya tiene delante de él.
     * 2. Y tiene 3 o más cartas en total delante de él (contando la recién sacada).
     *
     * @param hadRepeatedValueBeforeDraw true si el jugador ya tenía al menos una carta con este valor delante
     * @param totalCardsInFrontAfterDraw número total de cartas delante del jugador después de robar
     * @return true si pierde el turno (busto), false en caso contrario
     */
    public boolean checkBust(boolean hadRepeatedValueBeforeDraw, int totalCardsInFrontAfterDraw) {
        return hadRepeatedValueBeforeDraw && (totalCardsInFrontAfterDraw >= BUST_CARD_THRESHOLD);
    }

    /**
     * Sobrecarga de conveniencia que evalúa las cartas actuales y la carta recién sacada.
     */
    public boolean evaluateBust(List<Card> cardsInFrontBeforeDraw, Card drawnCard, int extraCardsStolen) {
        if (drawnCard == null) {
            throw new IllegalArgumentException("La carta sacada no puede ser nula");
        }
        boolean hadRepeated = cardsInFrontBeforeDraw != null && cardsInFrontBeforeDraw.stream()
                .anyMatch(c -> c.getValue() == drawnCard.getValue());
        
        int totalAfterDraw = (cardsInFrontBeforeDraw != null ? cardsInFrontBeforeDraw.size() : 0) + 1 + extraCardsStolen;
        return checkBust(hadRepeated, totalAfterDraw);
    }

    /**
     * Valida si el jugador puede guardar sus cartas en el montón de puntos.
     * Solo es válido en el Paso 1 (inicio de turno), antes de haber sacado cartas en el turno actual,
     * y si tiene cartas delante provenientes de un turno anterior.
     */
    public boolean canBank(TurnPhase phase, Player player) {
        if (player == null || phase == null) {
            return false;
        }
        return phase == TurnPhase.START_OF_TURN && player.countCardsInFront() > 0;
    }

    /**
     * Valida si se puede extraer una carta del mazo.
     */
    public boolean canDraw(GameStatus status, Deck deck) {
        if (status == null || deck == null) {
            return false;
        }
        return status == GameStatus.IN_PROGRESS && !deck.isEmpty();
    }

    /**
     * Valida si el jugador puede finalizar voluntariamente su turno (plantarse).
     * Solo puede hacerlo si ya sacó al menos una carta en el turno actual y no ha busteado.
     */
    public boolean canStop(TurnPhase phase) {
        return phase == TurnPhase.CAN_CONTINUE_OR_STOP;
    }

    /**
     * Ejecuta el robo de cartas a los demás jugadores.
     * Cuando un jugador saca una carta, toma todas las cartas del mismo valor que estén boca arriba delante de los rivales.
     *
     * @param activePlayer jugador en turno
     * @param otherPlayers lista de jugadores rivales
     * @param drawnCard carta extraída
     * @return lista de resultados de robo realizados
     */
    public List<StealResult> executeSteals(Player activePlayer, List<Player> otherPlayers, Card drawnCard) {
        List<StealResult> steals = new ArrayList<>();
        if (activePlayer == null || otherPlayers == null || drawnCard == null) {
            return steals;
        }

        int targetValue = drawnCard.getValue();
        for (Player rival : otherPlayers) {
            if (rival.equals(activePlayer)) {
                continue;
            }
            List<Card> stolenCards = rival.removeCardsOfValueInFront(targetValue);
            if (!stolenCards.isEmpty()) {
                activePlayer.addCardsInFront(stolenCards);
                steals.add(new StealResult(rival.getId(), rival.getName(), targetValue, stolenCards.size()));
            }
        }
        return steals;
    }

    /**
     * Calcula la puntuación de un jugador sumando las cartas guardadas en su montón de puntos.
     */
    public int calculateScore(Player player) {
        if (player == null) {
            return 0;
        }
        return player.calculateScore();
    }

    /**
     * Determina el o los ganadores de la partida según las reglas de Pelusas:
     * 1. Mayor puntuación acumulada.
     * 2. Criterio de desempate: mayor cantidad total de cartas acumuladas en el montón de puntos.
     *
     * @param players lista de jugadores de la partida
     * @return lista de ganadores (en caso de empate exacto en puntos y cartas)
     */
    public List<Player> determineWinners(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return List.of();
        }

        // Ordenar primero por puntuación descendente, luego por cantidad de cartas guardadas descendente
        Comparator<Player> comparator = Comparator
                .comparingInt(Player::calculateScore)
                .thenComparingInt(Player::countBankedCards)
                .reversed();

        List<Player> sorted = new ArrayList<>(players);
        sorted.sort(comparator);

        Player best = sorted.get(0);
        int bestScore = best.calculateScore();
        int bestCardCount = best.countBankedCards();

        List<Player> winners = new ArrayList<>();
        for (Player p : sorted) {
            if (p.calculateScore() == bestScore && p.countBankedCards() == bestCardCount) {
                winners.add(p);
            }
        }
        return winners;
    }
}
