import React, { useEffect, useState } from 'react';
import { gameApi } from '../api/gameApi';

export default function HistoryModal({ isOpen, onClose }) {
  const [history, setHistory] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen) {
      loadHistory();
    }
  }, [isOpen]);

  const loadHistory = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await gameApi.getHistory();
      setHistory(data);
    } catch (err) {
      setError(err.message || 'Error al cargar el histórico');
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" data-testid="history-modal">
      <div className="modal-card" style={{ maxWidth: '820px' }}>
        <div className="modal-header">
          <h2 className="modal-title">📜 Histórico de Partidas Terminadas</h2>
          <button className="btn-close" onClick={onClose} data-testid="btn-close-history">✕</button>
        </div>

        {isLoading ? (
          <div style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>
            Cargando historial desde la base de datos...
          </div>
        ) : error ? (
          <div style={{ color: '#ef4444', padding: '1rem', background: 'rgba(239,68,68,0.1)', borderRadius: '8px' }}>
            {error}
          </div>
        ) : history.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem 1rem', color: '#64748b' }}>
            <span style={{ fontSize: '3rem', display: 'block', marginBottom: '0.5rem' }}>📭</span>
            Aún no hay partidas terminadas registradas en la base de datos.
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table className="history-table" data-testid="history-table">
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Ganador(es)</th>
                  <th>Jugadores</th>
                  <th>Puntuaciones</th>
                  <th>Resumen</th>
                </tr>
              </thead>
              <tbody>
                {history.map((game) => (
                  <tr key={game.gameId} data-testid={`history-row-${game.gameId}`}>
                    <td style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                      {new Date(game.playedAt).toLocaleDateString()} {new Date(game.playedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </td>
                    <td>
                      <span style={{ color: '#34d399', fontWeight: 700 }}>
                        🏆 {game.winnerName || 'N/A'}
                      </span>
                    </td>
                    <td>{game.totalPlayers}</td>
                    <td>
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '2px', fontSize: '0.8rem' }}>
                        {(game.players || []).map((p, i) => (
                          <span key={i} style={{ color: p.isWinner ? '#34d399' : '#cbd5e1' }}>
                            {p.playerName}: {p.finalScore} pts ({p.accumulatedCards} cartas)
                          </span>
                        ))}
                      </div>
                    </td>
                    <td style={{ fontSize: '0.8rem', color: '#94a3b8', maxWidth: '200px' }}>
                      {game.resultSummary}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1.5rem' }}>
          <button className="btn btn-secondary" onClick={onClose}>
            Cerrar
          </button>
        </div>
      </div>
    </div>
  );
}
