import React from 'react';
import { UserRound, AlertTriangle, Trophy, Zap, Sparkles } from 'lucide-react';
import PelusaCard from './PelusaCard';

export default function PlayerMat({
  player,
  isActive,
  pendingSteals = [],
  onSteal,
  isBeingStealed = false,
}) {
  // Agrupar cartas delante por valor
  const groupedInFront = (player.cardsInFront || []).reduce((acc, card) => {
    acc[card.value] = (acc[card.value] || 0) + 1;
    return acc;
  }, {});

  const initial = player.name ? player.name.charAt(0).toUpperCase() : '?';

  // Verificar si alguna carta de este jugador puede ser robada por el jugador activo
  const stealableValues = (pendingSteals || [])
    .filter((s) => s.victimPlayerId === player.id)
    .map((s) => s.cardValue);

  return (
    <div
      className={`player-mat-card ${isActive ? 'active-mat' : ''} ${isBeingStealed ? 'shake-steal' : ''}`}
      data-testid={`player-mat-${player.id}`}
      id={`player-mat-${player.id}`}
    >
      {/* Cabecera del tapete del jugador */}
      <div className="player-mat-header">
        <div className="player-identity">
          <div className="player-avatar-badge">
            <span className="avatar-letter">{initial}</span>
          </div>
          <div className="player-name-wrap">
            <span className="player-mat-name">{player.name}</span>
          </div>
        </div>

        {isActive ? (
          <span className="active-turn-tag" data-testid="active-turn-pill">
            <Sparkles size={14} className="tag-icon" />
            <span>Turno actual</span>
          </span>
        ) : (
          <span className="inactive-player-tag">
            <UserRound size={13} />
            <span>En espera</span>
          </span>
        )}
      </div>

      {/* Cuerpo principal del tapete */}
      <div className="mat-body-content">
        {/* Sección de cartas en riesgo delante */}
        <div className="mat-cards-area">
          <div className="mat-risk-label">
            <AlertTriangle size={14} className="risk-icon" />
            <span>En riesgo ({player.cardsInFrontCount || 0} cartas)</span>
          </div>

          <div className={`cards-in-front-zone ${player.cardsInFrontCount === 0 ? 'empty' : ''}`}>
            {player.cardsInFrontCount === 0 ? (
              <span className="empty-zone-text">Sin cartas delante</span>
            ) : (
              <div className="cards-fan-layout">
                {Object.entries(groupedInFront).map(([value, count]) => {
                  const numVal = Number(value);
                  const isStealable = stealableValues.includes(numVal);

                  return (
                    <div key={value} className="card-steal-wrapper">
                      <PelusaCard
                        value={numVal}
                        count={count}
                        onClick={isStealable ? () => onSteal && onSteal(player.id, numVal) : undefined}
                      />
                      {/* Botón interactivo para robar si coincide con la carta sacada */}
                      {isStealable && (
                        <button
                          className="btn-manual-steal"
                          onClick={() => onSteal && onSteal(player.id, numVal)}
                          title={`¡Robar ${numVal} a ${player.name}!`}
                          data-testid={`btn-steal-${player.id}-${numVal}`}
                        >
                          <Zap size={13} />
                          <span>¡Robar!</span>
                        </button>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>

        {/* Sección de puntos asegurados */}
        <div className="mat-points-area" data-testid={`score-${player.id}`}>
          <div className="points-display-card">
            <div className="points-icon-wrap">
              <Trophy size={20} className="trophy-accent" />
            </div>
            <div className="points-score-value">
              {player.score || 0}
            </div>
            <div className="points-score-label">puntos</div>
            <div className="banked-cards-tag">
              {player.bankedCardsCount || 0} cartas guardadas
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

