package com.pelusas.game.service;

import com.pelusas.game.domain.model.Card;
import com.pelusas.game.domain.model.Deck;
import com.pelusas.game.domain.model.Game;
import com.pelusas.game.domain.model.GameStatus;
import com.pelusas.game.domain.model.Player;
import com.pelusas.game.domain.model.StealResult;
import com.pelusas.game.domain.rules.TurnRules;
import com.pelusas.game.dto.CardDto;
import com.pelusas.game.dto.CreateGameRequest;
import com.pelusas.game.dto.DrawActionResponseDto;
import com.pelusas.game.dto.GameResponseDto;
import com.pelusas.game.dto.PlayerResponseDto;
import com.pelusas.game.dto.StealResponseDto;
import com.pelusas.game.entity.ActiveGameEntity;
import com.pelusas.game.exception.GameNotFoundException;
import com.pelusas.game.exception.InvalidGameActionException;
import com.pelusas.game.repository.ActiveGameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();
    private final TurnRules turnRules = new TurnRules();
    private final GameHistoryService gameHistoryService;
    private final ActiveGameRepository activeGameRepository;

    public GameService(GameHistoryService gameHistoryService, ActiveGameRepository activeGameRepository) {
        this.gameHistoryService = gameHistoryService;
        this.activeGameRepository = activeGameRepository;
    }

    @Transactional
    public GameResponseDto createGame(CreateGameRequest request) {
        if (request == null || request.getPlayerNames() == null || request.getPlayerNames().isEmpty()) {
            throw new InvalidGameActionException("Debe proporcionar la lista de jugadores");
        }

        String gameId = UUID.randomUUID().toString();
        Deck deck;

        if (request.getRiggedDeckValues() != null && !request.getRiggedDeckValues().isEmpty()) {
            // Deck controlado para pruebas/demostraciones
            List<Card> customCards = new ArrayList<>();
            for (Integer val : request.getRiggedDeckValues()) {
                customCards.add(new Card(val));
            }
            // En Deck.draw() se extrae desde el final (cards.remove(cards.size() - 1))
            // Si el usuario pasa [A, B, C], queremos que saque A primero, luego B, luego C:
            Collections.reverse(customCards);
            deck = new Deck(customCards);
        } else {
            // Deck oficial de 110 cartas mezclado
            deck = new Deck();
            deck.shuffle();
        }

        boolean autoSteal = request.getAutoSteal() != null ? request.getAutoSteal() : true;
        Game game = new Game(gameId, request.getPlayerNames(), deck, turnRules, autoSteal);
        activeGames.put(gameId, game);

        persistActiveGame(game);
        return mapToDto(game);
    }

    @Transactional(readOnly = true)
    public GameResponseDto getGame(String gameId) {
        Game game = findGameOrThrow(gameId);
        return mapToDto(game);
    }

    @Transactional
    public GameResponseDto bankCards(String gameId) {
        Game game = findGameOrThrow(gameId);
        try {
            game.bankActivePlayerCards();
        } catch (IllegalStateException e) {
            throw new InvalidGameActionException(e.getMessage());
        }

        persistActiveGame(game);
        return mapToDto(game);
    }

    @Transactional
    public DrawActionResponseDto drawCard(String gameId) {
        Game game = findGameOrThrow(gameId);
        Game.DrawExecutionResult result;
        try {
            result = game.drawCard();
        } catch (IllegalStateException e) {
            throw new InvalidGameActionException(e.getMessage());
        }

        if (game.getStatus() == GameStatus.FINISHED) {
            gameHistoryService.saveGameHistory(game);
        }

        persistActiveGame(game);

        CardDto drawnCardDto = new CardDto(result.getDrawnCard().getValue());
        List<CardDto> lostCardsDto = result.getDiscardedCards().stream()
                .map(c -> new CardDto(c.getValue()))
                .collect(Collectors.toList());
        List<StealResponseDto> stealDtos = result.getSteals().stream()
                .map(s -> new StealResponseDto(s.getVictimPlayerId(), s.getVictimPlayerName(), s.getCardValue(), s.getCount()))
                .collect(Collectors.toList());

        return new DrawActionResponseDto(
                drawnCardDto,
                result.isBusted(),
                lostCardsDto,
                stealDtos,
                mapToDto(game)
        );
    }

    @Transactional
    public GameResponseDto stopTurn(String gameId) {
        Game game = findGameOrThrow(gameId);
        try {
            game.stopTurn();
        } catch (IllegalStateException e) {
            throw new InvalidGameActionException(e.getMessage());
        }

        persistActiveGame(game);
        return mapToDto(game);
    }

    @Transactional
    public GameResponseDto executeSteal(String gameId, String victimPlayerId, Integer cardValue) {
        Game game = findGameOrThrow(gameId);
        if (victimPlayerId == null || cardValue == null) {
            // Si no se especifica, robar el primer robo pendiente disponible
            if (game.getPendingSteals() != null && !game.getPendingSteals().isEmpty()) {
                StealResult first = game.getPendingSteals().get(0);
                victimPlayerId = first.getVictimPlayerId();
                cardValue = first.getCardValue();
            } else {
                throw new InvalidGameActionException("No hay cartas pendientes para robar a los rivales");
            }
        }
        try {
            game.executeManualSteal(victimPlayerId, cardValue);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidGameActionException(e.getMessage());
        }

        persistActiveGame(game);
        return mapToDto(game);
    }

    /**
     * Permite reiniciar o configurar una partida con estado específico (útil para pruebas).
     */
    public void registerGame(Game game) {
        activeGames.put(game.getId(), game);
        persistActiveGame(game);
    }

    private Game findGameOrThrow(String gameId) {
        Game game = activeGames.get(gameId);
        if (game == null) {
            throw new GameNotFoundException("No se encontró la partida con identificador: " + gameId);
        }
        return game;
    }

    private void persistActiveGame(Game game) {
        ActiveGameEntity entity = new ActiveGameEntity(
                game.getId(),
                game.getCreatedAt(),
                LocalDateTime.now(),
                game.getStatus().name(),
                game.getActivePlayerIndex(),
                game.getTurnPhase().name(),
                game.getLastActionMessage(),
                "Cards in deck: " + game.getDeck().remainingCardsCount()
        );
        activeGameRepository.save(entity);
    }

    public GameResponseDto mapToDto(Game game) {
        GameResponseDto dto = new GameResponseDto();
        dto.setId(game.getId());
        dto.setCreatedAt(game.getCreatedAt());
        dto.setStatus(game.getStatus().name());
        dto.setTurnPhase(game.getTurnPhase().name());
        dto.setActivePlayerIndex(game.getActivePlayerIndex());
        dto.setRemainingDeckCards(game.getDeck().remainingCardsCount());
        dto.setDiscardPileCount(game.getDiscardPile().size());
        dto.setLastActionMessage(game.getLastActionMessage());
        dto.setAutoSteal(game.isAutoSteal());

        if (game.getPendingSteals() != null) {
            List<StealResponseDto> pendingDtos = game.getPendingSteals().stream()
                    .map(s -> new StealResponseDto(s.getVictimPlayerId(), s.getVictimPlayerName(), s.getCardValue(), s.getCount()))
                    .collect(Collectors.toList());
            dto.setPendingSteals(pendingDtos);
        }

        if (game.getLastDrawnCard() != null) {
            dto.setLastDrawnCard(new CardDto(game.getLastDrawnCard().getValue()));
        }

        List<StealResponseDto> stealDtos = game.getLastSteals().stream()
                .map(s -> new StealResponseDto(s.getVictimPlayerId(), s.getVictimPlayerName(), s.getCardValue(), s.getCount()))
                .collect(Collectors.toList());
        dto.setLastSteals(stealDtos);

        List<String> winnerNames = game.getWinners().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        dto.setWinnerNames(winnerNames);

        List<PlayerResponseDto> playerDtos = game.getPlayers().stream()
                .map(p -> mapPlayerToDto(p, game.getWinners().contains(p)))
                .collect(Collectors.toList());
        dto.setPlayers(playerDtos);

        Player activePlayer = game.getActivePlayer();
        dto.setActivePlayer(mapPlayerToDto(activePlayer, game.getWinners().contains(activePlayer)));

        // Calcular capacidades de acción según reglas
        dto.setCanBank(turnRules.canBank(game.getTurnPhase(), activePlayer));
        dto.setCanDraw(turnRules.canDraw(game.getStatus(), game.getDeck()));
        dto.setCanStop(turnRules.canStop(game.getTurnPhase()));

        return dto;
    }

    private PlayerResponseDto mapPlayerToDto(Player player, boolean isWinner) {
        List<CardDto> inFront = player.getCardsInFront().stream()
                .map(c -> new CardDto(c.getValue()))
                .collect(Collectors.toList());

        List<CardDto> banked = player.getBankedCards().stream()
                .map(c -> new CardDto(c.getValue()))
                .collect(Collectors.toList());

        return new PlayerResponseDto(
                player.getId(),
                player.getName(),
                inFront,
                player.countCardsInFront(),
                banked,
                player.countBankedCards(),
                player.calculateScore(),
                isWinner
        );
    }
}
