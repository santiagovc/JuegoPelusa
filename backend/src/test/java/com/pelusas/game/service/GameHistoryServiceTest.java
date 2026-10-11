package com.pelusas.game.service;

import com.pelusas.game.domain.model.Card;
import com.pelusas.game.domain.model.Deck;
import com.pelusas.game.domain.model.Game;
import com.pelusas.game.domain.rules.TurnRules;
import com.pelusas.game.dto.GameHistoryResponseDto;
import com.pelusas.game.entity.GameHistoryEntity;
import com.pelusas.game.entity.PlayerHistoryEntity;
import com.pelusas.game.exception.GameNotFoundException;
import com.pelusas.game.repository.GameHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias con Mockito - GameHistoryService")
class GameHistoryServiceTest {

    @Mock
    private GameHistoryRepository gameHistoryRepository;

    private GameHistoryService gameHistoryService;

    @BeforeEach
    void setUp() {
        gameHistoryService = new GameHistoryService(gameHistoryRepository);
    }

    @Test
    @DisplayName("Debe guardar la entidad histórica de una partida finalizada")
    void shouldSaveGameHistoryEntity() {
        Deck deck = new Deck(new ArrayList<>(List.of(new Card(7))));
        Game game = new Game("g-hist-1", List.of("Fabian", "Laura"), deck, new TurnRules());
        game.drawCard(); // Finaliza partida

        when(gameHistoryRepository.save(any(GameHistoryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GameHistoryEntity saved = gameHistoryService.saveGameHistory(game);

        assertNotNull(saved);
        assertEquals("g-hist-1", saved.getGameId());
        assertEquals(2, saved.getTotalPlayers());
        assertEquals(2, saved.getPlayers().size());
        verify(gameHistoryRepository, times(1)).save(any(GameHistoryEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta guardar una partida nula")
    void shouldThrowWhenSavingNullGame() {
        assertThrows(IllegalArgumentException.class, () -> gameHistoryService.saveGameHistory(null));
    }

    @Test
    @DisplayName("Debe listar todas las partidas del historial")
    void shouldListAllHistory() {
        GameHistoryEntity entity = new GameHistoryEntity("g-1", LocalDateTime.now(), 2, "Fabian", "Victoria");
        entity.addPlayer(new PlayerHistoryEntity("Fabian", 5, 25, true));
        entity.addPlayer(new PlayerHistoryEntity("Laura", 3, 14, false));

        when(gameHistoryRepository.findAllByOrderByPlayedAtDesc()).thenReturn(List.of(entity));

        List<GameHistoryResponseDto> list = gameHistoryService.getAllHistory();

        assertEquals(1, list.size());
        assertEquals("g-1", list.get(0).getGameId());
        assertEquals(2, list.get(0).getPlayers().size());
        assertTrue(list.get(0).getPlayers().get(0).isWinner());
    }

    @Test
    @DisplayName("Debe consultar partida histórica por ID y lanzar GameNotFoundException si no existe")
    void shouldGetHistoryByIdOrThrow() {
        GameHistoryEntity entity = new GameHistoryEntity("g-1", LocalDateTime.now(), 2, "Fabian", "Victoria");
        when(gameHistoryRepository.findById("g-1")).thenReturn(Optional.of(entity));
        when(gameHistoryRepository.findById("g-inexistente")).thenReturn(Optional.empty());

        GameHistoryResponseDto result = gameHistoryService.getHistoryById("g-1");
        assertNotNull(result);
        assertEquals("g-1", result.getGameId());

        assertThrows(GameNotFoundException.class, () -> gameHistoryService.getHistoryById("g-inexistente"));
    }
}
