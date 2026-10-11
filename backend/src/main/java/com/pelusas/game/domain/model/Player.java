package com.pelusas.game.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Representa a un jugador de la partida de Pelusas.
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final List<Card> cardsInFront; // Cartas boca arriba delante del jugador (en riesgo)
    private final List<Card> bankedCards;  // Montón de puntos (cartas seguras)

    public Player(String id, String name) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del jugador no puede ser nulo o vacío");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede ser nulo o vacío");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.cardsInFront = new ArrayList<>();
        this.bankedCards = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Card> getCardsInFront() {
        return Collections.unmodifiableList(cardsInFront);
    }

    public List<Card> getBankedCards() {
        return Collections.unmodifiableList(bankedCards);
    }

    public void addCardInFront(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("La carta a agregar no puede ser nula");
        }
        cardsInFront.add(card);
    }

    public void addCardsInFront(List<Card> cards) {
        if (cards == null) {
            throw new IllegalArgumentException("La lista de cartas no puede ser nula");
        }
        for (Card card : cards) {
            addCardInFront(card);
        }
    }

    /**
     * Remueve y devuelve todas las cartas boca arriba delante del jugador que tengan el valor especificado.
     * Utilizado cuando otro jugador roba estas cartas.
     */
    public List<Card> removeCardsOfValueInFront(int value) {
        List<Card> removed = new ArrayList<>();
        Iterator<Card> iterator = cardsInFront.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            if (card.getValue() == value) {
                removed.add(card);
                iterator.remove();
            }
        }
        return removed;
    }

    /**
     * Limpia y devuelve todas las cartas que el jugador tenía delante.
     * Utilizado cuando el jugador pierde el turno (busto) y las cartas son descartadas.
     */
    public List<Card> clearCardsInFront() {
        List<Card> discarded = new ArrayList<>(cardsInFront);
        cardsInFront.clear();
        return discarded;
    }

    /**
     * Pasa todas las cartas boca arriba de delante del jugador a su montón de puntos protegido.
     * Utilizado en el Paso 1 (Guardar pelusas) o al final de la partida.
     */
    public List<Card> bankCardsInFront() {
        List<Card> secured = new ArrayList<>(cardsInFront);
        bankedCards.addAll(secured);
        cardsInFront.clear();
        return secured;
    }

    /**
     * Calcula la puntuación actual del jugador sumando el valor de sus cartas en el montón de puntos.
     */
    public int calculateScore() {
        int total = 0;
        for (Card card : bankedCards) {
            total += card.getValue();
        }
        return total;
    }

    /**
     * Verifica si el jugador ya tiene al menos una carta con el valor especificado delante de él.
     */
    public boolean hasCardInFrontWithValue(int value) {
        for (Card card : cardsInFront) {
            if (card.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    public int countCardsInFront() {
        return cardsInFront.size();
    }

    public int countBankedCards() {
        return bankedCards.size();
    }

    public int countCardsInFrontWithValue(int value) {
        int count = 0;
        for (Card card : cardsInFront) {
            if (card.getValue() == value) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cardsInFront=" + cardsInFront.size() +
                ", bankedCards=" + bankedCards.size() +
                ", score=" + calculateScore() +
                '}';
    }
}
