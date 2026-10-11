package com.pelusas.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidad JPA para persistir el estado de partidas activas en la base de datos.
 */
@Entity
@Table(name = "active_games")
public class ActiveGameEntity {

    @Id
    @Column(name = "game_id", nullable = false, length = 64)
    private String gameId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "active_player_index", nullable = false)
    private int activePlayerIndex;

    @Column(name = "turn_phase", nullable = false, length = 32)
    private String turnPhase;

    @Column(name = "last_action_message", length = 1000)
    private String lastActionMessage;

    @Lob
    @Column(name = "state_payload", length = 65536)
    private String statePayload;

    public ActiveGameEntity() {
    }

    public ActiveGameEntity(String gameId, LocalDateTime createdAt, LocalDateTime updatedAt, String status,
                            int activePlayerIndex, String turnPhase, String lastActionMessage, String statePayload) {
        this.gameId = gameId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.activePlayerIndex = activePlayerIndex;
        this.turnPhase = turnPhase;
        this.lastActionMessage = lastActionMessage;
        this.statePayload = statePayload;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getActivePlayerIndex() {
        return activePlayerIndex;
    }

    public void setActivePlayerIndex(int activePlayerIndex) {
        this.activePlayerIndex = activePlayerIndex;
    }

    public String getTurnPhase() {
        return turnPhase;
    }

    public void setTurnPhase(String turnPhase) {
        this.turnPhase = turnPhase;
    }

    public String getLastActionMessage() {
        return lastActionMessage;
    }

    public void setLastActionMessage(String lastActionMessage) {
        this.lastActionMessage = lastActionMessage;
    }

    public String getStatePayload() {
        return statePayload;
    }

    public void setStatePayload(String statePayload) {
        this.statePayload = statePayload;
    }
}
