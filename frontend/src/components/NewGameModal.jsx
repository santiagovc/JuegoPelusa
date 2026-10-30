import React, { useState } from 'react';
import { Gamepad2, X, UserPlus, Trash2, GraduationCap } from 'lucide-react';

export default function NewGameModal({ isOpen, onClose, onCreateGame }) {
  const [playerNames, setPlayerNames] = useState(['Fabian', 'Laura']);
  const [isSimulationMode, setIsSimulationMode] = useState(false);
  const [simulationDeckPreset, setSimulationDeckPreset] = useState('none');

  if (!isOpen) return null;

  const handleNameChange = (index, value) => {
    const updated = [...playerNames];
    updated[index] = value;
    setPlayerNames(updated);
  };

  const addPlayer = () => {
    if (playerNames.length < 6) {
      setPlayerNames([...playerNames, `Jugador ${playerNames.length + 1}`]);
    }
  };

  const removePlayer = (index) => {
    if (playerNames.length > 2) {
      setPlayerNames(playerNames.filter((_, i) => i !== index));
    }
  };

  const applyPreset = (count) => {
    const defaultNames = ['Fabian', 'Laura', 'Carlos', 'Daniela', 'Mateo', 'Sofia'];
    setPlayerNames(defaultNames.slice(0, count));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const cleanNames = playerNames.map((n) => n.trim()).filter((n) => n.length > 0);
    if (cleanNames.length < 2) {
      alert('Debes ingresar al menos 2 jugadores');
      return;
    }

    let riggedValues = null;
    if (isSimulationMode) {
      if (simulationDeckPreset === 'bust-demo') {
        // [5, 4, 5]: Fabian saca 5 (total 1, no bustea), saca 4 (total 2, no bustea), saca 5 (repite con 3 cartas -> ¡BUSTO!)
        riggedValues = [5, 4, 5, 8, 9, 10];
      } else if (simulationDeckPreset === 'steal-demo') {
        // [7, 7]: Fabian saca 7, se planta. Laura saca 7 y le roba el 7 a Fabian.
        riggedValues = [7, 7, 3, 4, 5];
      }
    }

    onCreateGame(cleanNames, riggedValues);
  };

  return (
    <div className="modal-overlay" data-testid="new-game-modal">
      <div className="modal-card">
        <div className="modal-header">
          <div className="modal-title-wrap">
            <Gamepad2 size={20} className="modal-title-icon" />
            <h2 className="modal-title">Nueva Partida de Pelusas</h2>
          </div>
          <button className="btn-modal-close" onClick={onClose} data-testid="btn-close-modal" aria-label="Cerrar modal">
            <X size={18} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          {/* Presets rápidos */}
          <div className="form-group">
            <label className="form-label">Plantillas rápidas:</label>
            <div className="presets-pill-group">
              <button
                type="button"
                className="btn btn-preset-pill"
                onClick={() => applyPreset(2)}
              >
                2 Jugadores
              </button>
              <button
                type="button"
                className="btn btn-preset-pill"
                onClick={() => applyPreset(3)}
              >
                3 Jugadores
              </button>
              <button
                type="button"
                className="btn btn-preset-pill"
                onClick={() => applyPreset(4)}
              >
                4 Jugadores
              </button>
            </div>
          </div>

          {/* Lista de nombres */}
          <div className="form-group">
            <div className="form-header-row">
              <label className="form-label">
                Nombres de Jugadores ({playerNames.length} / 6):
              </label>
              {playerNames.length < 6 && (
                <button
                  type="button"
                  className="btn btn-secondary btn-add-player"
                  onClick={addPlayer}
                >
                  <UserPlus size={14} />
                  <span>Agregar Jugador</span>
                </button>
              )}
            </div>

            <div className="player-inputs-list">
              {playerNames.map((name, index) => (
                <div key={index} className="player-input-row">
                  <input
                    type="text"
                    className="form-input"
                    id={`player-input-${index}`}
                    data-testid={`player-input-${index}`}
                    value={name}
                    onChange={(e) => handleNameChange(index, e.target.value)}
                    placeholder={`Jugador ${index + 1}`}
                    required
                  />
                  {playerNames.length > 2 && (
                    <button
                      type="button"
                      className="btn-remove-player"
                      onClick={() => removePlayer(index)}
                      title="Quitar jugador"
                      aria-label="Quitar jugador"
                    >
                      <Trash2 size={16} />
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Modo Demostración para sustentación */}
          <div className="form-group simulation-group-box">
            <div className="simulation-checkbox-row">
              <input
                type="checkbox"
                id="toggle-simulation"
                data-testid="toggle-simulation"
                checked={isSimulationMode}
                onChange={(e) => setIsSimulationMode(e.target.checked)}
                className="form-checkbox"
              />
              <label htmlFor="toggle-simulation" className="simulation-checkbox-label">
                <GraduationCap size={16} className="cap-icon" />
                <span>Modo Sustentación (Controlar Mazo)</span>
              </label>
            </div>
            {isSimulationMode && (
              <div className="simulation-select-wrap">
                <span className="simulation-hint-text">
                  Selecciona una secuencia predefinida para demostrar una regla:
                </span>
                <select
                  className="form-input form-select"
                  data-testid="select-preset"
                  value={simulationDeckPreset}
                  onChange={(e) => setSimulationDeckPreset(e.target.value)}
                >
                  <option value="none">Mazo estándar (110 cartas barajadas)</option>
                  <option value="bust-demo">Demostración de Busto (5, 4, 5 repetido con 3 cartas)</option>
                  <option value="steal-demo">Demostración de Robo (7 sacado roba 7 al rival)</option>
                </select>
              </div>
            )}
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancelar
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              id="btn-start-game"
              data-testid="btn-start-game"
            >
              Comenzar Partida
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

