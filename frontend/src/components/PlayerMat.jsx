import React from 'react';
import PelusaCard from './PelusaCard';

const AVATAR_COLORS = [
  'linear-gradient(135deg, #6366f1, #4f46e5)',
  'linear-gradient(135deg, #ec4899, #db2777)',
  'linear-gradient(135deg, #10b981, #059669)',
  'linear-gradient(135deg, #f59e0b, #d97706)',
  'linear-gradient(135deg, #8b5cf6, #7c3aed)',
  'linear-gradient(135deg, #06b6d4, #0891b2)',
];

export default function PlayerMat({ player, isActive, index }) {
  // Agrupar cartas delante por valor
  const groupedInFront = (player.cardsInFront || []).reduce((acc, card) => {
    acc[card.value] = (acc[card.value] || 0) + 1;
    return acc;
  }, {});

  const avatarColor = AVATAR_COLORS[index % AVATAR_COLORS.length];
  const initial = player.name ? player.name.charAt(0).toUpperCase() : '?';

  return (
    <div
      className={`player-mat ${isActive ? 'active' : ''}`}
      data-testid={`player-mat-${player.id}`}
      id={`player-mat-${player.id}`}
    >
      <div className="player-mat-header">
        <div className="player-identity">
          <div className="player-avatar" style={{ background: avatarColor }}>
            {initial}
          </div>
          <div>
            <div className="player-name">{player.name}</div>
          </div>
        </div>
        {isActive && (
          <span className="active-pill" data-testid="active-turn-pill">
            Turno Actual
          </span>
        )}
      </div>

      {/* Cartas delante (En riesgo) */}
      <div>
        <div className="mat-section-title">
          <span>En Riesgo (Boca Arriba)</span>
          <span>{player.cardsInFrontCount || 0} cartas</span>
        </div>
        <div className={`cards-in-front-zone ${player.cardsInFrontCount === 0 ? 'empty' : ''}`}>
          {player.cardsInFrontCount === 0 ? (
            <span>Sin cartas delante</span>
          ) : (
            Object.entries(groupedInFront).map(([value, count]) => (
              <PelusaCard
                key={value}
                value={Number(value)}
                count={count}
              />
            ))
          )}
        </div>
      </div>

      {/* Montón de puntos (Seguras) */}
      <div className="banked-points-summary">
        <div>
          <div className="score-label">Montón de Puntos</div>
          <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>
            {player.bankedCardsCount || 0} cartas guardadas
          </div>
        </div>
        <div className="score-display">
          <span className="score-number" data-testid={`score-${player.id}`}>
            {player.score || 0}
          </span>
          <span className="score-label">pts</span>
        </div>
      </div>
    </div>
  );
}
