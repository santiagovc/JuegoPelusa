package com.pelusas.game.domain.model;

import com.pelusas.game.domain.rules.TurnRules;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Agregado principal que modela la partida completa de Pelusas y orquesta
 * el flujo del juego, el mazo, los jugadores y las reglas de negocio.
 */
public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 6;

    private final String id;
    private final List<Player> players;
    private final Deck deck;
    private final List<Card> discardPile;
    private final TurnRules rules;
    private final LocalDateTime createdAt;

    private int activePlayerIndex;
    private GameStatus status;
    private TurnPhase turnPhase;
    private Card lastDrawnCard;
    private List<StealResult> lastSteals;
    private String lastActionMessage;
    private List<Player> winners;
    private boolean autoSteal = true;
    private List<StealResult> pendingSteals = new ArrayList<>();

    /**
     * Constructor para iniciar una partida nueva con nombres de jugadores.
     */
    public Game(List<String> playerNames) {
        this(UUID.randomUUID().toString(), playerNames, new Deck(), new TurnRules());
    }

    public Game(String id, List<String> playerNames, Deck deck, TurnRules rules, boolean autoSteal) {
        this(id, playerNames, deck, rules);
        this.autoSteal = autoSteal;
    }

    /**
     * Constructor con mazo inyectable (ideal para pruebas y escenarios controlados).
     */
    public Game(String id, List<String> playerNames, Deck deck, TurnRules rules) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador de la partida no puede ser nulo o vacío");
        }
        if (playerNames == null || playerNames.size() < MIN_PLAYERS || playerNames.size() > MAX_PLAYERS) {
            throw new IllegalArgumentException("La cantidad de jugadores debe estar entre " + MIN_PLAYERS + " y " + MAX_PLAYERS);
        }
        if (deck == null) {
            throw new IllegalArgumentException("El mazo no puede ser nulo");
        }
        if (rules == null) {
            throw new IllegalArgumentException("Las reglas del juego no pueden ser nulas");
        }

        this.id = id;
        this.deck = deck;
        this.rules = rules;
        this.createdAt = LocalDateTime.now();
        this.players = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.lastSteals = new ArrayList<>();
        this.winners = new ArrayList<>();

        for (int i = 0; i < playerNames.size(); i++) {
            String name = playerNames.get(i);
            this.players.add(new Player("player-" + (i + 1), name));
        }

        this.activePlayerIndex = 0;
        this.status = GameStatus.IN_PROGRESS;
        this.turnPhase = TurnPhase.START_OF_TURN;
        this.lastActionMessage = "Partida iniciada. Turno inicial de " + getActivePlayer().getName();
    }

    public Player getActivePlayer() {
        return players.get(activePlayerIndex);
    }

    /**
     * Paso 1 — Guardar pelusas.
     * Si el jugador posee cartas boca arriba provenientes de un turno anterior,
     * las guarda en su montón de puntos protegido.
     */
    public List<Card> bankActivePlayerCards() {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("La partida no está en curso");
        }
        Player activePlayer = getActivePlayer();
        if (!rules.canBank(turnPhase, activePlayer)) {
            throw new IllegalStateException("Solo se pueden guardar cartas al inicio del turno y si se tienen cartas acumuladas");
        }

        List<Card> banked = activePlayer.bankCardsInFront();
        this.lastActionMessage = activePlayer.getName() + " guardó " + banked.size() + " carta(s) en su montón de puntos";
        return banked;
    }

    /**
     * Paso 2 y 3 — Buscar pelusas y robar cartas.
     * Saca una carta del montón de polvo, ejecuta los robos a rivales y verifica si el jugador pierde el turno.
     */
    public DrawExecutionResult drawCard() {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("La partida no está en curso");
        }
        if (!rules.canDraw(status, deck)) {
            throw new IllegalStateException("No hay cartas disponibles en el mazo");
        }

        Player activePlayer = getActivePlayer();
        Card drawnCard = deck.draw();
        this.lastDrawnCard = drawnCard;

        // Verificar si ya tenía este valor delante antes del robo
        boolean hadRepeatedValue = activePlayer.hasCardInFrontWithValue(drawnCard.getValue());

        List<StealResult> steals = new ArrayList<>();
        this.pendingSteals = new ArrayList<>();

        if (autoSteal) {
            // Paso 3: Robar cartas del mismo valor a los rivales automáticamente
            steals = rules.executeSteals(activePlayer, players, drawnCard);
            this.lastSteals = steals;
        } else {
            // Paso 3 manual: verificar qué rivales tienen cartas de este valor
            for (Player rival : players) {
                if (!rival.getId().equals(activePlayer.getId()) && rival.hasCardInFrontWithValue(drawnCard.getValue())) {
                    long count = rival.getCardsInFront().stream().filter(c -> c.getValue() == drawnCard.getValue()).count();
                    this.pendingSteals.add(new StealResult(rival.getId(), rival.getName(), drawnCard.getValue(), (int) count));
                }
            }
            this.lastSteals = Collections.emptyList();
        }

        // Colocar la carta sacada delante del jugador
        activePlayer.addCardInFront(drawnCard);

        // Regla 8: Evaluar si el jugador pierde el turno
        boolean busted = rules.checkBust(hadRepeatedValue, activePlayer.countCardsInFront());

        if (busted) {
            if (this.pendingSteals != null) {
                this.pendingSteals.clear();
            }
            List<Card> lostCards = activePlayer.clearCardsInFront();
            discardPile.addAll(lostCards);
            this.lastActionMessage = "¡Pérdida de turno! " + activePlayer.getName() +
                    " sacó una carta " + drawnCard.getValue() + " repetida con 3 o más cartas delante. Perdió " +
                    lostCards.size() + " cartas.";

            // Si el mazo se agotó al sacar esta carta, finaliza la partida
            if (deck.isEmpty()) {
                finalizeGame();
            } else {
                nextTurn();
            }
            return new DrawExecutionResult(drawnCard, steals, true, lostCards);
        } else {
            // Continúa en el turno
            this.turnPhase = TurnPhase.CAN_CONTINUE_OR_STOP;
            StringBuilder sb = new StringBuilder();
            sb.append(activePlayer.getName()).append(" sacó un ").append(drawnCard.getValue()).append(".");
            if (!steals.isEmpty()) {
                for (StealResult steal : steals) {
                    sb.append(" Robó ").append(steal.getCount()).append(" carta(s) a ").append(steal.getVictimPlayerName()).append(".");
                }
            } else if (!pendingSteals.isEmpty()) {
                sb.append(" ¡Hay cartas del mismo valor en rivales disponibles para robar!");
            }
            this.lastActionMessage = sb.toString();

            // Si el mazo se agotó con esta extracción, finaliza la partida
            if (deck.isEmpty()) {
                finalizeGame();
            }

            return new DrawExecutionResult(drawnCard, steals, false, List.of());
        }
    }

    /**
     * Paso 3 manual — El jugador ejecuta expresamente el robo de cartas coincidentes a un rival.
     */
    public StealResult executeManualSteal(String victimPlayerId, int cardValue) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("La partida no está en curso");
        }
        Player activePlayer = getActivePlayer();
        Player victim = players.stream()
                .filter(p -> p.getId().equals(victimPlayerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Jugador rival no encontrado con id: " + victimPlayerId));

        if (victim.getId().equals(activePlayer.getId())) {
            throw new IllegalArgumentException("No puedes robarte a ti mismo");
        }

        List<Card> stolen = victim.removeCardsOfValueInFront(cardValue);
        if (stolen.isEmpty()) {
            throw new IllegalStateException(victim.getName() + " no tiene cartas de valor " + cardValue + " en riesgo");
        }

        activePlayer.addCardsInFront(stolen);

        StealResult stealResult = new StealResult(victim.getId(), victim.getName(), cardValue, stolen.size());
        if (this.lastSteals == null || this.lastSteals.isEmpty()) {
            this.lastSteals = new ArrayList<>();
        } else {
            this.lastSteals = new ArrayList<>(this.lastSteals);
        }
        this.lastSteals.add(stealResult);

        if (this.pendingSteals != null) {
            this.pendingSteals.removeIf(s -> s.getVictimPlayerId().equals(victimPlayerId) && s.getCardValue() == cardValue);
        }

        this.lastActionMessage = activePlayer.getName() + " Robó " + stolen.size() +
                " carta(s) a " + victim.getName() + ".";
        return stealResult;
    }

    /**
     * Finaliza voluntariamente el turno (plantarse).
     */
    public void stopTurn() {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("La partida no está en curso");
        }
        if (!rules.canStop(turnPhase)) {
            throw new IllegalStateException("No se puede finalizar el turno sin haber sacado al menos una carta");
        }

        Player activePlayer = getActivePlayer();
        this.lastActionMessage = activePlayer.getName() + " decidió finalizar su turno voluntariamente (" +
                activePlayer.countCardsInFront() + " cartas acumuladas en riesgo).";

        nextTurn();
    }

    /**
     * Avanza el turno al siguiente jugador.
     */
    private void nextTurn() {
        this.activePlayerIndex = (this.activePlayerIndex + 1) % players.size();
        this.turnPhase = TurnPhase.START_OF_TURN;
        this.lastDrawnCard = null;
        this.lastSteals = Collections.emptyList();
        if (this.pendingSteals != null) {
            this.pendingSteals.clear();
        }
    }

    /**
     * Finaliza la partida cuando se agota el mazo:
     * - Guarda automáticamente las cartas que los jugadores aún tengan delante en sus montones de puntos.
     * - Calcula las puntuaciones.
     * - Determina el ganador (y desempate).
     */
    public void finalizeGame() {
        this.status = GameStatus.FINISHED;
        this.turnPhase = TurnPhase.TURN_ENDED;

        // Todos los jugadores guardan automáticamente sus cartas restantes
        for (Player player : players) {
            player.bankCardsInFront();
        }

        this.winners = rules.determineWinners(players);
        StringBuilder sb = new StringBuilder("Partida finalizada. Ganador(es): ");
        for (int i = 0; i < winners.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(winners.get(i).getName()).append(" (").append(winners.get(i).calculateScore()).append(" pts)");
        }
        this.lastActionMessage = sb.toString();
    }

    // Getters
    public String getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Card> getDiscardPile() {
        return Collections.unmodifiableList(discardPile);
    }

    public TurnRules getRules() {
        return rules;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getActivePlayerIndex() {
        return activePlayerIndex;
    }

    public GameStatus getStatus() {
        return status;
    }

    public TurnPhase getTurnPhase() {
        return turnPhase;
    }

    public Card getLastDrawnCard() {
        return lastDrawnCard;
    }

    public List<StealResult> getLastSteals() {
        return Collections.unmodifiableList(lastSteals);
    }

    public String getLastActionMessage() {
        return lastActionMessage;
    }

    public List<Player> getWinners() {
        return Collections.unmodifiableList(winners);
    }

    public boolean isAutoSteal() {
        return autoSteal;
    }

    public void setAutoSteal(boolean autoSteal) {
        this.autoSteal = autoSteal;
    }

    public List<StealResult> getPendingSteals() {
        return pendingSteals != null ? Collections.unmodifiableList(pendingSteals) : Collections.emptyList();
    }

    /**
     * Estructura interna para el retorno de la acción de sacar carta.
     */
    public static class DrawExecutionResult {
        private final Card drawnCard;
        private final List<StealResult> steals;
        private final boolean busted;
        private final List<Card> discardedCards;

        public DrawExecutionResult(Card drawnCard, List<StealResult> steals, boolean busted, List<Card> discardedCards) {
            this.drawnCard = drawnCard;
            this.steals = steals;
            this.busted = busted;
            this.discardedCards = discardedCards;
        }

        public Card getDrawnCard() {
            return drawnCard;
        }

        public List<StealResult> getSteals() {
            return steals;
        }

        public boolean isBusted() {
            return busted;
        }

        public List<Card> getDiscardedCards() {
            return discardedCards;
        }
    }
}
