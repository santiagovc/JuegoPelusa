import React from 'react';
import PelusaCard from './PelusaCard';

export default function TableCenter({
  gameState,
  onDraw,
  onBank,
  onStop,
  isLoading,
}) {
  if (!gameState) return null;

  const activePlayer = gameState.activePlayer;

  return (
    <div className="game-table-center glass-panel">
      {/* Mazo (Montón de polvo) */}
      <div className="deck-zone">
        <div
          className={`deck-card ${gameState.canDraw ? 'can-draw' : ''}`}
          onClick={() => gameState.canDraw && !isLoading && onDraw()}
          id="btn-deck-draw"
          data-testid="deck-draw-trigger"
          title={gameState.canDraw ? 'Haz clic para sacar una carta' : 'No puedes sacar cartas ahora'}
        >
          <div className="deck-card-pattern">
            <span style={{ fontSize: '2rem' }}>🧶</span>
            <span className="deck-badge" data-testid="deck-count">
              {gameState.remainingDeckCards} cartas
            </span>
          </div>
        </div>
        <span style={{ fontSize: '0.8rem', color: '#94a3b8', fontWeight: 600 }}>
          Montón de Polvo
        </span>
      </div>

      {/* Controles de turno central */}
      <div className="turn-controls-box">
        <div className="turn-player-headline">
          Turno de <span data-testid="active-player-name">{activePlayer?.name}</span>
        </div>

        {/* Última carta extraída */}
        {gameState.lastDrawnCard ? (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '0.4rem' }}>
            <span style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>
              Última carta sacada
            </span>
            <PelusaCard value={gameState.lastDrawnCard.value} isLarge />
          </div>
        ) : (
          <div style={{ height: '90px', display: 'flex', alignItems: 'center', color: '#64748b', fontSize: '0.85rem' }}>
            <span>Esperando acción del turno...</span>
          </div>
        )}

        {/* Botones de acción del turno */}
        <div className="turn-buttons-group">
          {/* Paso 1: Guardar */}
          <button
            className="btn btn-bank"
            id="btn-bank"
            data-testid="btn-bank"
            onClick={onBank}
            disabled={!gameState.canBank || isLoading}
            title="Asegura tus cartas en riesgo del turno anterior en tu montón de puntos"
          >
            🛡️ Guardar Pelusas
          </button>

          {/* Paso 2: Sacar carta */}
          <button
            className="btn btn-primary"
            id="btn-draw"
            data-testid="btn-draw"
            onClick={onDraw}
            disabled={!gameState.canDraw || isLoading}
          >
            🎴 Sacar Carta
          </button>

          {/* Finalizar turno voluntariamente */}
          <button
            className="btn btn-stop"
            id="btn-stop"
            data-testid="btn-stop"
            onClick={onStop}
            disabled={!gameState.canStop || isLoading}
            title="Finaliza voluntariamente tu turno sin arriesgarte a un busto"
          >
            ✋ Plantarse (Finalizar Turno)
          </button>
        </div>
      </div>

      {/* Montón de Descarte */}
      <div className="deck-zone">
        <div className="discard-card" data-testid="discard-zone">
          <span style={{ fontSize: '1.8rem' }}>🗑️</span>
          <span style={{ fontWeight: 700, color: '#f87171' }} data-testid="discard-count">
            {gameState.discardPileCount} cartas
          </span>
        </div>
        <span style={{ fontSize: '0.8rem', color: '#94a3b8', fontWeight: 600 }}>
          Descarte (Bustos)
        </span>
      </div>
    </div>
  );
}
