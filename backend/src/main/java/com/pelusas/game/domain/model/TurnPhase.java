package com.pelusas.game.domain.model;

/**
 * Fases posibles dentro del turno de un jugador.
 */
public enum TurnPhase {
    START_OF_TURN,           // Inicio del turno: Puede guardar pelusas anteriores o sacar carta
    CAN_CONTINUE_OR_STOP,    // Ya sacó al menos una carta: Puede seguir sacando o plantarse
    TURN_ENDED               // Turno finalizado (por busto voluntario o forzado)
}
