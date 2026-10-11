package com.pelusas.game.service;

import com.pelusas.game.domain.model.Card;
import com.pelusas.game.domain.model.Deck;
import com.pelusas.game.domain.model.Game;
import com.pelusas.game.domain.rules.TurnRules;
import com.pelusas.game.dto.CreateGameRequest;
import com.pelusas.game.dto.DrawActionResponseDto;
import com.pelusas.game.dto.GameResponseDto;
import com.pelusas.game.entity.ActiveGameEntity;
import com.pelusas.game.exception.GameNotFoundException;
import com.pelusas.game.exception.InvalidGameActionException;
import com.pelusas.game.repository.ActiveGameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias con Mockito - GameService")
class GameServiceTest {

    @Mock
    private GameHistoryService gameHistoryService;

    @Mock
    private ActiveGameRepository activeGameRepository;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(gameHistoryService, activeGameRepository);
    }

    @Test
    @DisplayName("Debe crear una partida y persistir su estado activo")
    void shouldCreateGameAndPersistActiveState() {
        CreateGameRequest request = new CreateGameRequest(List.of("Fabian", "Laura"));

        GameResponseDto response = gameService.createGame(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Fabian", response.getActivePlayer().getName());
        assertEquals(2, response.getPlayers().size());
        assertEquals("IN_PROGRESS", response.getStatus());

        verify(activeGameRepository, atLeastOnce()).save(any(ActiveGameEntity.class));
    }

    @Test
    @DisplayName("Debe permitir crear una partida con mazo controlado (rigged deck)")
    void shouldCreateGameWithRiggedDeck() {
        CreateGameRequest request = new CreateGameRequest(
                List.of("Fabian", "Laura"),
                List.of(7, 3, 5)
        );

        GameResponseDto response = gameService.createGame(request);

        assertNotNull(response);
        assertEquals(3, response.getRemainingDeckCards());

        // Al sacar carta debe salir el 7 (primero en la lista especificada)
        DrawActionResponseDto drawResult = gameService.drawCard(response.getId());
        assertEquals(7, drawResult.getDrawnCard().getValue());
    }

    @Test
    @DisplayName("Debe lanzar InvalidGameActionException si no se proporcionan jugadores")
    void shouldThrowWhenCreatingGameWithoutPlayers() {
        assertThrows(InvalidGameActionException.class, () -> gameService.createGame(null));
        assertThrows(InvalidGameActionException.class, () -> gameService.createGame(new CreateGameRequest(List.of())));
    }

    @Test
    @DisplayName("Debe consultar partida existente y lanzar GameNotFoundException si no existe")
    void shouldGetGameOrThrowNotFound() {
        CreateGameRequest request = new CreateGameRequest(List.of("Fabian", "Laura"));
        GameResponseDto created = gameService.createGame(request);

        GameResponseDto found = gameService.getGame(created.getId());
        assertEquals(created.getId(), found.getId());

        assertThrows(GameNotFoundException.class, () -> gameService.getGame("inexistente-123"));
    }

    @Test
    @DisplayName("Debe ejecutar extracción de carta y actualizar persistencia")
    void shouldDrawCardSuccessfully() {
        CreateGameRequest request = new CreateGameRequest(List.of("Fabian", "Laura"));
        GameResponseDto created = gameService.createGame(request);

        DrawActionResponseDto result = gameService.drawCard(created.getId());

        assertNotNull(result);
        assertNotNull(result.getDrawnCard());
        verify(activeGameRepository, atLeast(2)).save(any(ActiveGameEntity.class));
    }

    @Test
    @DisplayName("Debe guardar historial cuando la partida finaliza al agotar el mazo")
    void shouldSaveHistoryWhenGameFinishes() {
        // Creamos partida con solo 1 carta en el mazo
        Deck singleCardDeck = new Deck(new ArrayList<>(List.of(new Card(8))));
        Game game = new Game("game-last-card", List.of("Fabian", "Laura"), singleCardDeck, new TurnRules());
        gameService.registerGame(game);

        DrawActionResponseDto result = gameService.drawCard("game-last-card");

        assertEquals("FINISHED", result.getGameState().getStatus());
        verify(gameHistoryService, times(1)).saveGameHistory(any(Game.class));
    }

    @Test
    @DisplayName("Debe permitir plantarse (stopTurn) y pasar turno")
    void shouldStopTurnSuccessfully() {
        CreateGameRequest request = new CreateGameRequest(List.of("Fabian", "Laura"));
        GameResponseDto created = gameService.createGame(request);

        // Fabian saca carta
        gameService.drawCard(created.getId());

        // Fabian se planta
        GameResponseDto afterStop = gameService.stopTurn(created.getId());

        assertEquals("Laura", afterStop.getActivePlayer().getName());
        verify(activeGameRepository, atLeast(2)).save(any(ActiveGameEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar InvalidGameActionException si se intenta plantarse sin haber sacado carta")
    void shouldThrowWhenStoppingWithoutDrawing() {
        CreateGameRequest request = new CreateGameRequest(List.of("Fabian", "Laura"));
        GameResponseDto created = gameService.createGame(request);

        assertThrows(InvalidGameActionException.class, () -> gameService.stopTurn(created.getId()));
    }

    @Test
    @DisplayName("Debe permitir guardar cartas (bankCards) y lanzar excepción si es inválido")
    void shouldBankCardsOrThrowInvalidAction() {
        CreateGameRequest request = new CreateGameRequest(List.of("Fabian", "Laura"));
        GameResponseDto created = gameService.createGame(request);

        // Sin cartas acumuladas en START_OF_TURN debe lanzar InvalidGameActionException
        assertThrows(InvalidGameActionException.class, () -> gameService.bankCards(created.getId()));
    }
}
