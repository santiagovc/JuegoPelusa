package com.pelusas.game.controller;

import com.pelusas.game.dto.GameHistoryResponseDto;
import com.pelusas.game.service.GameHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/history", "/api/games/history"})
@CrossOrigin(origins = "*")
public class GameHistoryController {

    private final GameHistoryService gameHistoryService;

    public GameHistoryController(GameHistoryService gameHistoryService) {
        this.gameHistoryService = gameHistoryService;
    }

    /**
     * Consultar todo el histórico de partidas terminadas.
     * GET /api/history
     */
    @GetMapping
    public ResponseEntity<List<GameHistoryResponseDto>> getAllHistory() {
        List<GameHistoryResponseDto> history = gameHistoryService.getAllHistory();
        return ResponseEntity.ok(history);
    }

    /**
     * Consultar detalle de una partida terminada del histórico.
     * GET /api/history/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameHistoryResponseDto> getHistoryById(@PathVariable String id) {
        GameHistoryResponseDto history = gameHistoryService.getHistoryById(id);
        return ResponseEntity.ok(history);
    }
}
