package com.pelusas.game.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateGameRequest {

    @NotEmpty(message = "Debe proporcionar la lista de nombres de jugadores")
    @Size(min = 2, max = 6, message = "La cantidad de jugadores debe estar entre 2 y 6")
    private List<String> playerNames;

    /**
     * Opcional: lista de valores de cartas para fijar el mazo en pruebas automatizadas.
     */
    private List<Integer> riggedDeckValues;

    /**
     * Opcional: si es false, los robos a rivales deben ser ejecutados manualmente por el jugador.
     * Por defecto es true para compatibilidad hacia atrás con tests automáticos existentes.
     */
    private Boolean autoSteal;

    public CreateGameRequest() {
    }

    public CreateGameRequest(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public CreateGameRequest(List<String> playerNames, List<Integer> riggedDeckValues) {
        this.playerNames = playerNames;
        this.riggedDeckValues = riggedDeckValues;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public List<Integer> getRiggedDeckValues() {
        return riggedDeckValues;
    }

    public void setRiggedDeckValues(List<Integer> riggedDeckValues) {
        this.riggedDeckValues = riggedDeckValues;
    }

    public Boolean getAutoSteal() {
        return autoSteal;
    }

    public void setAutoSteal(Boolean autoSteal) {
        this.autoSteal = autoSteal;
    }
}
