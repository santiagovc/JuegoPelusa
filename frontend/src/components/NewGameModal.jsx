import React, { useState } from 'react';

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
          <h2 className="modal-title">🎮 Nueva Partida de Pelusas</h2>
          <button className="btn-close" onClick={onClose} data-testid="btn-close-modal">✕</button>
        </div>

        <form onSubmit={handleSubmit}>
          {/* Presets rápidos */}
          <div className="form-group">
            <label className="form-label">Plantillas rápidas:</label>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
              <button
                type="button"
                className="btn btn-secondary"
                style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem' }}
                onClick={() => applyPreset(2)}
              >
                2 Jugadores
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem' }}
                onClick={() => applyPreset(3)}
              >
                3 Jugadores
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem' }}
                onClick={() => applyPreset(4)}
              >
                4 Jugadores
              </button>
            </div>
          </div>

          {/* Lista de nombres */}
          <div className="form-group">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
              <label className="form-label" style={{ margin: 0 }}>
                Nombres de Jugadores ({playerNames.length} / 6):
              </label>
              {playerNames.length < 6 && (
                <button
                  type="button"
                  className="btn btn-secondary"
                  style={{ padding: '0.25rem 0.6rem', fontSize: '0.75rem' }}
                  onClick={addPlayer}
                >
                  + Agregar Jugador
                </button>
              )}
            </div>

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
                    className="btn btn-secondary"
                    style={{ padding: '0.5rem 0.8rem', color: '#ef4444' }}
                    onClick={() => removePlayer(index)}
                  >
                    ✕
                  </button>
                )}
              </div>
            ))}
          </div>

          {/* Modo Demostración para sustentación */}
          <div className="form-group" style={{ background: 'rgba(255,255,255,0.03)', padding: '1rem', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.08)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '0.5rem' }}>
              <input
                type="checkbox"
                id="toggle-simulation"
                data-testid="toggle-simulation"
                checked={isSimulationMode}
                onChange={(e) => setIsSimulationMode(e.target.checked)}
                style={{ cursor: 'pointer', width: '16px', height: '16px' }}
              />
              <label htmlFor="toggle-simulation" style={{ fontWeight: 600, fontSize: '0.85rem', cursor: 'pointer' }}>
                🎓 Modo Sustentación (Controlar Mazo)
              </label>
            </div>
            {isSimulationMode && (
              <div style={{ marginTop: '0.75rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                  Selecciona una secuencia predefinida para demostrar una regla al profesor:
                </span>
                <select
                  className="form-input"
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

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
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
