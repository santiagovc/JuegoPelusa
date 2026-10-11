package com.pelusas.game.controller;

import com.pelusas.game.entity.GameHistoryEntity;
import com.pelusas.game.entity.PlayerHistoryEntity;
import com.pelusas.game.repository.GameHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Integración REST (MockMvc) - GameHistoryController")
class GameHistoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameHistoryRepository gameHistoryRepository;

    @Test
    @DisplayName("Debe consultar lista de históricos de partidas mediante GET /api/history")
    void shouldGetAllGameHistoryViaHttp() throws Exception {
        GameHistoryEntity entity = new GameHistoryEntity(
                "hist-http-1",
                LocalDateTime.now(),
                2,
                "Pedro",
                "Pedro ganó con 32 puntos"
        );
        entity.addPlayer(new PlayerHistoryEntity("Pedro", 8, 32, true));
        entity.addPlayer(new PlayerHistoryEntity("Santiago", 5, 21, false));
        gameHistoryRepository.save(entity);

        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[?(@.gameId == 'hist-http-1')].winnerName").value("Pedro"));
    }

    @Test
    @DisplayName("Debe consultar detalle de histórico por ID mediante GET /api/history/{id}")
    void shouldGetGameHistoryByIdViaHttp() throws Exception {
        GameHistoryEntity entity = new GameHistoryEntity(
                "hist-http-2",
                LocalDateTime.now(),
                2,
                "Valeria",
                "Valeria ganó"
        );
        entity.addPlayer(new PlayerHistoryEntity("Valeria", 7, 29, true));
        entity.addPlayer(new PlayerHistoryEntity("Camilo", 4, 15, false));
        gameHistoryRepository.save(entity);

        mockMvc.perform(get("/api/history/hist-http-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("hist-http-2"))
                .andExpect(jsonPath("$.winnerName").value("Valeria"))
                .andExpect(jsonPath("$.players", hasSize(2)));
    }

    @Test
    @DisplayName("Debe retornar 404 NOT FOUND para ID de histórico inexistente")
    void shouldReturn404ForNonExistentHistoryId() throws Exception {
        mockMvc.perform(get("/api/history/hist-no-existe-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
