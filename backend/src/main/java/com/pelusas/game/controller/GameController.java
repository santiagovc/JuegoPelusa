package com.pelusas.game.controller;

import com.pelusas.game.dto.CreateGameRequest;
import com.pelusas.game.dto.DrawActionResponseDto;
import com.pelusas.game.dto.GameResponseDto;
import com.pelusas.game.dto.PlayerResponseDto;
import com.pelusas.game.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * 1. Crear una nueva partida.
     * POST /api/games
     */
    @PostMapping
    public ResponseEntity<GameResponseDto> createGame(@Valid @RequestBody CreateGameRequest request) {
        GameResponseDto createdGame = gameService.createGame(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGame);
    }

    /**
     * 2. Consultar el estado de una partida.
     * GET /api/games/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDto> getGame(@PathVariable String id) {
        GameResponseDto game = gameService.getGame(id);
        return ResponseEntity.ok(game);
    }

    /**
     * 3. Consultar jugadores de una partida.
     * GET /api/games/{id}/players
     */
    @GetMapping("/{id}/players")
    public ResponseEntity<List<PlayerResponseDto>> getPlayers(@PathVariable String id) {
        GameResponseDto game = gameService.getGame(id);
        return ResponseEntity.ok(game.getPlayers());
    }

    /**
     * 4. Paso 1: Guardar pelusas en el montón de puntos.
     * POST /api/games/{id}/bank
     */
    @PostMapping("/{id}/bank")
    public ResponseEntity<GameResponseDto> bankCards(@PathVariable String id) {
        GameResponseDto game = gameService.bankCards(id);
        return ResponseEntity.ok(game);
    }

    /**
     * 5. Paso 2 y 3: Sacar una carta y robar a rivales si aplica.
     * POST /api/games/{id}/draw
     */
    @PostMapping("/{id}/draw")
    public ResponseEntity<DrawActionResponseDto> drawCard(@PathVariable String id) {
        DrawActionResponseDto response = gameService.drawCard(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 6. Finalizar voluntariamente el turno (plantarse / stop).
     * POST /api/games/{id}/stop
     */
    @PostMapping("/{id}/stop")
    public ResponseEntity<GameResponseDto> stopTurn(@PathVariable String id) {
        GameResponseDto game = gameService.stopTurn(id);
        return ResponseEntity.ok(game);
    }

    /**
     * Paso 3 Manual: Robar cartas del mismo valor a un rival.
     * POST /api/games/{id}/steal
     */
    @PostMapping("/{id}/steal")
    public ResponseEntity<GameResponseDto> stealCards(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> payload) {
        String victimPlayerId = null;
        Integer cardValue = null;
        if (payload != null) {
            if (payload.get("victimPlayerId") != null) {
                victimPlayerId = payload.get("victimPlayerId").toString();
            }
            if (payload.get("cardValue") != null) {
                cardValue = Integer.valueOf(payload.get("cardValue").toString());
            }
        }
        GameResponseDto game = gameService.executeSteal(id, victimPlayerId, cardValue);
        return ResponseEntity.ok(game);
    }

    /**
     * 7. Consultar el resultado de la partida.
     * GET /api/games/{id}/result
     */
    @GetMapping("/{id}/result")
    public ResponseEntity<Map<String, Object>> getGameResult(@PathVariable String id) {
        GameResponseDto game = gameService.getGame(id);
        Map<String, Object> result = Map.of(
                "gameId", game.getId(),
                "status", game.getStatus(),
                "winnerNames", game.getWinnerNames(),
                "players", game.getPlayers(),
                "summary", game.getLastActionMessage()
        );
        return ResponseEntity.ok(result);
    }
}
