import React, { useEffect } from 'react';
import confetti from 'canvas-confetti';

export default function VictoryModal({ isOpen, gameState, onClose, onNewGame }) {
  useEffect(() => {
    if (isOpen) {
      // Lanzar confeti de celebración
      try {
        confetti({
          particleCount: 120,
          spread: 70,
          origin: { y: 0.6 },
        });
      } catch (e) {
        // Ignorar si canvas no está disponible en entorno headless
      }
    }
  }, [isOpen]);

  if (!isOpen || !gameState) return null;

  const winnerNames = gameState.winnerNames || [];
  const players = gameState.players || [];

  // Ordenar jugadores por puntuación descendente
  const sortedPlayers = [...players].sort((a, b) => b.score - a.score || b.bankedCardsCount - a.bankedCardsCount);

  return (
    <div className="modal-overlay" data-testid="victory-modal">
      <div className="modal-card" style={{ textAlign: 'center', maxWidth: '520px' }}>
        <div style={{ fontSize: '3.5rem', marginBottom: '0.5rem' }}>🏆</div>
        <h2 className="modal-title" style={{ fontSize: '1.8rem', color: '#f8fafc', marginBottom: '0.25rem' }}>
          ¡Partida Finalizada!
        </h2>
        <p style={{ color: '#34d399', fontSize: '1.2rem', fontWeight: 700, marginBottom: '1.5rem' }}>
          Ganador(es): {winnerNames.join(', ')}
        </p>

        {/* Podio / Tabla de puntuaciones */}
        <div style={{ background: 'rgba(255,255,255,0.03)', borderRadius: '14px', padding: '1rem', marginBottom: '1.5rem' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            {sortedPlayers.map((player, index) => (
              <div
                key={player.id}
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  padding: '0.5rem 0.75rem',
                  borderRadius: '8px',
                  background: index === 0 ? 'rgba(52, 211, 153, 0.15)' : 'rgba(255,255,255,0.02)',
                  border: index === 0 ? '1px solid rgba(52, 211, 153, 0.3)' : '1px solid transparent',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <span style={{ fontWeight: 800, color: index === 0 ? '#34d399' : '#94a3b8' }}>
                    #{index + 1}
                  </span>
                  <span style={{ fontWeight: 600, color: index === 0 ? 'white' : '#cbd5e1' }}>
                    {player.name}
                  </span>
                </div>
                <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                  <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                    {player.bankedCardsCount} cartas
                  </span>
                  <span style={{ fontWeight: 800, fontSize: '1.1rem', color: index === 0 ? '#34d399' : 'white' }}>
                    {player.score} pts
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', gap: '0.75rem' }}>
          <button className="btn btn-secondary" onClick={onClose}>
            Ver Tablero
          </button>
          <button
            className="btn btn-primary"
            id="btn-play-again"
            data-testid="btn-play-again"
            onClick={onNewGame}
          >
            Nueva Partida
          </button>
        </div>
      </div>
    </div>
  );
}
