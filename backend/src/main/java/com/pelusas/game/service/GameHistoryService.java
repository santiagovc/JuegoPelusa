package com.pelusas.game.service;

import com.pelusas.game.domain.model.Game;
import com.pelusas.game.domain.model.Player;
import com.pelusas.game.dto.GameHistoryResponseDto;
import com.pelusas.game.dto.PlayerHistoryResponseDto;
import com.pelusas.game.entity.GameHistoryEntity;
import com.pelusas.game.entity.PlayerHistoryEntity;
import com.pelusas.game.exception.GameNotFoundException;
import com.pelusas.game.repository.GameHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameHistoryService {

    private final GameHistoryRepository gameHistoryRepository;

    public GameHistoryService(GameHistoryRepository gameHistoryRepository) {
        this.gameHistoryRepository = gameHistoryRepository;
    }

    @Transactional
    public GameHistoryEntity saveGameHistory(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula");
        }

        List<Player> winners = game.getWinners();
        String winnerNames = winners.stream().map(Player::getName).collect(Collectors.joining(", "));

        GameHistoryEntity historyEntity = new GameHistoryEntity(
                game.getId(),
                game.getCreatedAt(),
                game.getPlayers().size(),
                winnerNames,
                game.getLastActionMessage()
        );

        for (Player player : game.getPlayers()) {
            boolean isWinner = winners.contains(player);
            PlayerHistoryEntity playerHistory = new PlayerHistoryEntity(
                    player.getName(),
                    player.countBankedCards(),
                    player.calculateScore(),
                    isWinner
            );
            historyEntity.addPlayer(playerHistory);
        }

        return gameHistoryRepository.save(historyEntity);
    }

    @Transactional(readOnly = true)
    public List<GameHistoryResponseDto> getAllHistory() {
        return gameHistoryRepository.findAllByOrderByPlayedAtDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GameHistoryResponseDto getHistoryById(String gameId) {
        GameHistoryEntity entity = gameHistoryRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("No se encontró el histórico de la partida con ID: " + gameId));
        return mapToDto(entity);
    }

    private GameHistoryResponseDto mapToDto(GameHistoryEntity entity) {
        List<PlayerHistoryResponseDto> playerDtos = new ArrayList<>();
        if (entity.getPlayers() != null) {
            for (PlayerHistoryEntity p : entity.getPlayers()) {
                playerDtos.add(new PlayerHistoryResponseDto(
                        p.getPlayerName(),
                        p.getAccumulatedCards(),
                        p.getFinalScore(),
                        p.isWinner()
                ));
            }
        }

        return new GameHistoryResponseDto(
                entity.getGameId(),
                entity.getPlayedAt(),
                entity.getTotalPlayers(),
                entity.getWinnerName(),
                entity.getResultSummary(),
                playerDtos
        );
    }
}
