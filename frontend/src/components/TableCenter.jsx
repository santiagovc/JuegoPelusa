import React from 'react';
import { Layers, Hand, Shield, Trash2, Zap } from 'lucide-react';
import PelusaCard from './PelusaCard';

/**
 * TableCenter: Centro de la mesa de juego digital de Pelusas.
 * Estructura editorial en 3 zonas claras:
 *  1. Mazo para robar (con representación física limpia y borde negro uniforme).
 *  2. Carta actual y controles de acción (Robar carta y Plantarse).
 *  3. Zona de descarte de bustos.
 */
export default function TableCenter({
  gameState,
  onDraw,
  onBank,
  onStop,
  onSteal,
  isLoading,
  isDealing = false,
  isBusting = false,
}) {
  if (!gameState) return null;

  const activePlayer = gameState.activePlayer;
  const canDraw = gameState.canDraw && !isLoading;
  const canStop = gameState.canStop && !isLoading;
  const canBank = gameState.canBank && !isLoading;
  const pendingSteals = gameState.pendingSteals || [];

  return (
    <div className="table-center-container">
      <div className="game-table-card">
        {/* 1. Mazo de Robo */}
        <div className="deck-zone">
          <div className="zone-header-label">
            <Layers size={14} className="zone-icon" />
            <span>Mazo de robo</span>
          </div>

          <div className="deck-stack-wrapper">
            <div
              className={`deck-card-button ${canDraw ? 'deck-active' : 'deck-disabled'}`}
              onClick={() => canDraw && onDraw()}
              id="btn-deck-draw"
              data-testid="deck-draw-trigger"
              title={canDraw ? 'Haz clic para robar una carta' : 'Espera tu turno'}
              role="button"
              tabIndex={canDraw ? 0 : -1}
            >
              {/* Capas decorativas del mazo */}
              <div className="deck-underlay underlay-2" />
              <div className="deck-underlay underlay-1" />
              <div className="deck-top-card">
                <div className="deck-inner-art">
                  <div className="deck-brand-title">PELUSAS</div>
                  <div className="deck-brand-sub">Montón de polvo</div>
                  <div className="deck-counter-badge" data-testid="deck-count">
                    {gameState.remainingDeckCards} cartas
                  </div>
                </div>
              </div>
            </div>
          </div>
          <span className="zone-hint-text">Toca el mazo para robar</span>
        </div>

        {/* 2. Área Central: Turno activo, Carta en juego y Acciones */}
        <div className="play-area-zone">
          {/* Indicador de turno actual */}
          <div className="turn-indicator-pill">
            <span className="turn-label">Turno de:</span>
            <strong className="turn-player-name" data-testid="active-player-name">
              {activePlayer?.name}
            </strong>
          </div>

          {/* Escaparate de la carta actual en juego con borde negro */}
          <div className={`active-card-stage ${isDealing ? 'dealing-anim' : ''} ${isBusting ? 'busting-anim' : ''}`}>
            {gameState.lastDrawnCard ? (
              <div className="drawn-card-wrapper">
                <PelusaCard value={gameState.lastDrawnCard.value} isLarge />
              </div>
            ) : (
              <div className="drawn-card-placeholder">
                <div className="placeholder-content">
                  <Layers size={24} className="placeholder-icon" />
                  <span>Mesa lista para robar</span>
                </div>
              </div>
            )}
          </div>

          {/* Alerta de oportunidad de robo manual */}
          {pendingSteals.length > 0 && (
            <div className="manual-steal-opportunity-card">
              <div className="steal-opportunity-info">
                <Zap size={16} className="steal-zap-icon" />
                <span>
                  ¡Puedes robar carta(s) a <strong>{pendingSteals.map((s) => s.victimPlayerName).join(', ')}</strong>!
                </span>
              </div>
              <button
                className="btn btn-steal-action"
                onClick={() => onSteal && onSteal(pendingSteals[0].victimPlayerId, pendingSteals[0].cardValue)}
                data-testid="btn-steal-opportunity"
              >
                <Zap size={14} />
                <span>¡Robar ahora!</span>
              </button>
            </div>
          )}

          {/* Botones de acción principales */}
          <div className="turn-actions-toolbar">
            {canBank && (
              <button
                className="btn btn-bank-action"
                id="btn-bank"
                data-testid="btn-bank"
                onClick={onBank}
                disabled={!canBank}
                title="Asegura tus cartas en riesgo en tu montón de puntos protegido"
              >
                <Shield size={16} />
                <span>Guardar Pelusas</span>
              </button>
            )}

            <button
              className="btn btn-primary btn-draw-action"
              id="btn-draw"
              data-testid="btn-draw"
              onClick={onDraw}
              disabled={!canDraw}
              title={canDraw ? 'Saca una carta del montón de polvo' : 'Espera tu turno'}
            >
              <Layers size={17} />
              <span>Robar Carta</span>
            </button>

            <button
              className="btn btn-secondary btn-stop-action"
              id="btn-stop"
              data-testid="btn-stop"
              onClick={onStop}
              disabled={!canStop}
              title={canStop ? 'Finaliza voluntariamente tu turno sin arriesgarte a un busto' : 'Debes sacar al menos 1 carta antes de plantarte'}
            >
              <Hand size={17} />
              <span>Plantarse</span>
            </button>
          </div>
        </div>

        {/* 3. Zona de Descarte (Bustos) */}
        <div className="discard-zone">
          <div className="zone-header-label">
            <Trash2 size={14} className="zone-icon" />
            <span>Descarte</span>
          </div>

          <div className="discard-card-wrapper">
            <div className={`discard-pile-card ${isBusting ? 'discard-shake' : ''}`} data-testid="discard-zone">
              <div className="discard-icon-wrap">
                <Trash2 size={28} className="discard-trash-icon" />
              </div>
              <div className="discard-title">Bustos</div>
              <div className="discard-count-badge" data-testid="discard-count">
                {gameState.discardPileCount} cartas
              </div>
            </div>
          </div>
          <span className="zone-hint-text">Cartas descartadas</span>
        </div>
      </div>
    </div>
  );
}

