package com.pelusas.game.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Integración REST (MockMvc) - GameController")
class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe crear una partida mediante POST /api/games con código 201 y respuesta válida")
    void shouldCreateGameViaHttp() throws Exception {
        String payload = """
                {
                    "playerNames": ["Carlos", "Daniela", "Esteban"]
                }
                """;

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.turnPhase").value("START_OF_TURN"))
                .andExpect(jsonPath("$.players", hasSize(3)))
                .andExpect(jsonPath("$.activePlayer.name").value("Carlos"))
                .andExpect(jsonPath("$.remainingDeckCards").value(110));
    }

    @Test
    @DisplayName("Debe retornar 400 BAD REQUEST al intentar crear una partida con datos inválidos")
    void shouldReturnBadRequestWhenCreatingInvalidGame() throws Exception {
        String invalidPayload = """
                {
                    "playerNames": ["SoloUno"]
                }
                """;

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @DisplayName("Debe retornar 404 NOT FOUND al consultar una partida inexistente")
    void shouldReturn404WhenGameNotFound() throws Exception {
        mockMvc.perform(get("/api/games/inexistente-uuid"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("Flujo de integración completo: Crear -> Consultar -> Sacar Carta -> Plantarse -> Verificar actualización real de estado")
    void shouldExecuteFullGameTurnFlowViaRest() throws Exception {
        // 1. Crear partida vía HTTP
        String createPayload = """
                {
                    "playerNames": ["Ana", "Bernardo"]
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String gameId = createJson.get("id").asText();
        assertNotNull(gameId);

        // 2. Consultar la partida mediante GET
        mockMvc.perform(get("/api/games/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId))
                .andExpect(jsonPath("$.activePlayer.name").value("Ana"))
                .andExpect(jsonPath("$.turnPhase").value("START_OF_TURN"));

        // 3. Consultar jugadores mediante GET /api/games/{id}/players
        mockMvc.perform(get("/api/games/" + gameId + "/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Ana"))
                .andExpect(jsonPath("$[1].name").value("Bernardo"));

        // 4. Ana ejecuta acción: Sacar carta (POST /api/games/{id}/draw)
        MvcResult drawResult = mockMvc.perform(post("/api/games/" + gameId + "/draw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.drawnCard.value").isNumber())
                .andExpect(jsonPath("$.gameState.turnPhase").value("CAN_CONTINUE_OR_STOP"))
                .andReturn();

        JsonNode drawJson = objectMapper.readTree(drawResult.getResponse().getContentAsString());
        int drawnValue = drawJson.get("drawnCard").get("value").asInt();
        assertTrue(drawnValue >= 1 && drawnValue <= 10);

        // 5. Ana decide plantarse (POST /api/games/{id}/stop)
        mockMvc.perform(post("/api/games/" + gameId + "/stop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activePlayer.name").value("Bernardo"))
                .andExpect(jsonPath("$.turnPhase").value("START_OF_TURN"));

        // 6. Consultar nuevamente el estado para asegurar persistencia e integración
        mockMvc.perform(get("/api/games/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activePlayer.name").value("Bernardo"))
                .andExpect(jsonPath("$.players[0].cardsInFrontCount").value(1));

        // 7. Consultar resultado de la partida
        mockMvc.perform(get("/api/games/" + gameId + "/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(gameId))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Debe rechazar reglas inválidas vía HTTP con 400 Bad Request (ej: plantarse sin sacar cartas)")
    void shouldRejectInvalidRuleActionViaHttp() throws Exception {
        // Crear partida nueva
        String createPayload = """
                {
                    "playerNames": ["Jugador1", "Jugador2"]
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andReturn();

        String gameId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Intentar plantarse inmediatamente sin haber sacado carta debe retornar 400
        mockMvc.perform(post("/api/games/" + gameId + "/stop"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("No se puede finalizar el turno sin haber sacado al menos una carta")));
    }
}
