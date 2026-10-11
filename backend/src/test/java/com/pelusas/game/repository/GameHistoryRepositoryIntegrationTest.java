package com.pelusas.game.repository;

import com.pelusas.game.entity.GameHistoryEntity;
import com.pelusas.game.entity.PlayerHistoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Pruebas de Integración con Base de Datos Real - GameHistoryRepository")
class GameHistoryRepositoryIntegrationTest {

    @Autowired
    private GameHistoryRepository gameHistoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe persistir y recuperar una partida finalizada y sus jugadores relacionados (sin mocks)")
    void shouldPersistAndRetrieveGameHistoryWithPlayers() {
        // 1. Preparar entidades con relaciones
        GameHistoryEntity gameHistory = new GameHistoryEntity(
                "game-db-101",
                LocalDateTime.now(),
                2,
                "Lucas",
                "Lucas ganó con 28 puntos"
        );

        PlayerHistoryEntity p1 = new PlayerHistoryEntity("Lucas", 6, 28, true);
        PlayerHistoryEntity p2 = new PlayerHistoryEntity("Mateo", 4, 18, false);

        gameHistory.addPlayer(p1);
        gameHistory.addPlayer(p2);

        // 2. Persistir en la base de datos real (H2)
        gameHistoryRepository.save(gameHistory);
        entityManager.flush();
        entityManager.clear();

        // 3. Recuperar información
        Optional<GameHistoryEntity> foundOptional = gameHistoryRepository.findById("game-db-101");
        assertTrue(foundOptional.isPresent(), "La partida debe estar presente en la base de datos");

        GameHistoryEntity retrieved = foundOptional.get();
        assertEquals("game-db-101", retrieved.getGameId());
        assertEquals("Lucas", retrieved.getWinnerName());
        assertEquals(2, retrieved.getTotalPlayers());

        // 4. Verificar relación y jugadores
        List<PlayerHistoryEntity> players = retrieved.getPlayers();
        assertEquals(2, players.size(), "Deben haberse persistido los 2 jugadores relacionados");

        PlayerHistoryEntity retrievedLucas = players.stream()
                .filter(p -> p.getPlayerName().equals("Lucas"))
                .findFirst().orElseThrow();
        assertEquals(28, retrievedLucas.getFinalScore());
        assertEquals(6, retrievedLucas.getAccumulatedCards());
        assertTrue(retrievedLucas.isWinner());

        PlayerHistoryEntity retrievedMateo = players.stream()
                .filter(p -> p.getPlayerName().equals("Mateo"))
                .findFirst().orElseThrow();
        assertEquals(18, retrievedMateo.getFinalScore());
        assertEquals(4, retrievedMateo.getAccumulatedCards());
        assertFalse(retrievedMateo.isWinner());
    }

    @Test
    @DisplayName("Debe listar las partidas ordenadas por fecha descendente")
    void shouldFindAllOrderedByPlayedAtDesc() {
        LocalDateTime now = LocalDateTime.now();
        GameHistoryEntity g1 = new GameHistoryEntity("g-old", now.minusHours(2), 2, "A", "Gana A");
        GameHistoryEntity g2 = new GameHistoryEntity("g-new", now.minusHours(1), 2, "B", "Gana B");

        gameHistoryRepository.save(g1);
        gameHistoryRepository.save(g2);
        entityManager.flush();
        entityManager.clear();

        List<GameHistoryEntity> list = gameHistoryRepository.findAllByOrderByPlayedAtDesc();
        assertFalse(list.isEmpty());
        assertEquals("g-new", list.get(0).getGameId(), "La más reciente debe aparecer primero");
    }
}
