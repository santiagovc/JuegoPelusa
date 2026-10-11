package com.pelusas.game.controller;

import com.pelusas.game.dto.ErrorResponseDto;
import com.pelusas.game.exception.GameNotFoundException;
import com.pelusas.game.exception.InvalidGameActionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Debe manejar GameNotFoundException retornando 404")
    void shouldHandleGameNotFound() {
        ResponseEntity<ErrorResponseDto> resp = handler.handleGameNotFound(new GameNotFoundException("Partida no encontrada"));
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("Partida no encontrada", resp.getBody().getMessage());
    }

    @Test
    @DisplayName("Debe manejar InvalidGameActionException retornando 400")
    void shouldHandleInvalidGameAction() {
        ResponseEntity<ErrorResponseDto> resp = handler.handleInvalidGameAction(new InvalidGameActionException("Acción inválida"));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Acción inválida", resp.getBody().getMessage());
    }

    @Test
    @DisplayName("Debe manejar IllegalArgumentException e IllegalStateException retornando 400")
    void shouldHandleIllegalStateAndArgument() {
        ResponseEntity<ErrorResponseDto> resp = handler.handleIllegalState(new IllegalStateException("Estado no válido"));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Estado no válido", resp.getBody().getMessage());
    }

    @Test
    @DisplayName("Debe manejar excepciones generales retornando 500")
    void shouldHandleGeneralException() {
        ResponseEntity<ErrorResponseDto> resp = handler.handleGeneralException(new RuntimeException("Error inesperado"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("Error inesperado", resp.getBody().getMessage());
    }
}
