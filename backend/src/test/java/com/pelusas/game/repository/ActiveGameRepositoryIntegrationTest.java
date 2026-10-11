package com.pelusas.game.repository;

import com.pelusas.game.entity.ActiveGameEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Pruebas de Integración con Base de Datos Real - ActiveGameRepository")
class ActiveGameRepositoryIntegrationTest {

    @Autowired
    private ActiveGameRepository activeGameRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe persistir, actualizar el estado del juego y recuperar la información en BD real")
    void shouldPersistAndUpdateActiveGameState() {
        LocalDateTime now = LocalDateTime.now();
        ActiveGameEntity game = new ActiveGameEntity(
                "active-game-001",
                now,
                now,
                "IN_PROGRESS",
                0,
                "START_OF_TURN",
                "Partida iniciada",
                "Cards remaining: 110"
        );

        // 1. Persistencia
        activeGameRepository.save(game);
        entityManager.flush();
        entityManager.clear();

        // 2. Recuperación
        Optional<ActiveGameEntity> retrieved = activeGameRepository.findById("active-game-001");
        assertTrue(retrieved.isPresent());
        assertEquals("IN_PROGRESS", retrieved.get().getStatus());
        assertEquals(0, retrieved.get().getActivePlayerIndex());
        assertEquals("START_OF_TURN", retrieved.get().getTurnPhase());

        // 3. Actualización de estado del juego
        ActiveGameEntity toUpdate = retrieved.get();
        toUpdate.setActivePlayerIndex(1);
        toUpdate.setTurnPhase("CAN_CONTINUE_OR_STOP");
        toUpdate.setLastActionMessage("Jugador sacó carta 8");
        toUpdate.setUpdatedAt(LocalDateTime.now());

        activeGameRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        // 4. Verificar cambio de estado en la base de datos
        ActiveGameEntity updated = activeGameRepository.findById("active-game-001").orElseThrow();
        assertEquals(1, updated.getActivePlayerIndex());
        assertEquals("CAN_CONTINUE_OR_STOP", updated.getTurnPhase());
        assertEquals("Jugador sacó carta 8", updated.getLastActionMessage());
    }
}
