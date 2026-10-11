import React from 'react';

export default function RulesModal({ isOpen, onClose }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" data-testid="rules-modal">
      <div className="modal-card" style={{ maxWidth: '680px' }}>
        <div className="modal-header">
          <h2 className="modal-title">📖 Reglamento Oficial de Pelusas</h2>
          <button className="btn-close" onClick={onClose} data-testid="btn-close-rules">✕</button>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem', fontSize: '0.9rem', color: '#cbd5e1' }}>
          <div>
            <h3 style={{ color: '#818cf8', fontSize: '1.1rem', marginBottom: '0.4rem' }}>
              🎯 Objetivo del Juego
            </h3>
            <p>
              Acumular la mayor cantidad de puntos en tu montón protegido de pelusas recolectando cartas del montón de polvo y robándoselas a tus rivales.
            </p>
          </div>

          <div style={{ background: 'rgba(255,255,255,0.03)', padding: '1rem', borderRadius: '12px' }}>
            <h3 style={{ color: '#ec4899', fontSize: '1rem', marginBottom: '0.4rem' }}>
              🎴 Composición del Mazo (110 Cartas)
            </h3>
            <ul style={{ paddingLeft: '1.2rem', lineHeight: '1.6' }}>
              <li><strong>Valores 1 al 5:</strong> 13 cartas de cada valor (65 cartas en total).</li>
              <li><strong>Valores 6 al 10:</strong> 9 cartas de cada valor (45 cartas en total).</li>
              <li><strong>Total:</strong> 110 cartas (distribución oficial no uniforme).</li>
            </ul>
          </div>

          <div>
            <h3 style={{ color: '#34d399', fontSize: '1.1rem', marginBottom: '0.4rem' }}>
              🔄 Estructura de un Turno
            </h3>
            <ol style={{ paddingLeft: '1.2rem', display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
              <li>
                <strong>Paso 1 — Guardar pelusas:</strong> Si tienes cartas acumuladas delante de un turno anterior, puedes asegurarlas en tu montón de puntos al comenzar tu turno antes de sacar cartas.
              </li>
              <li>
                <strong>Paso 2 — Buscar pelusas:</strong> Sacas una carta del montón de polvo y la colocas boca arriba delante de ti.
              </li>
              <li>
                <strong>Paso 3 — Robar cartas:</strong> Tomas todas las cartas del mismo valor que estén delante de los demás jugadores.
              </li>
              <li>
                <strong>Decisión:</strong> Eliges si continúas sacando otra carta o te plantas (finalizas voluntariamente tu turno).
              </li>
            </ol>
          </div>

          <div style={{ background: 'rgba(239, 68, 68, 0.12)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '1rem', borderRadius: '12px' }}>
            <h3 style={{ color: '#f87171', fontSize: '1rem', marginBottom: '0.4rem' }}>
              ⚠️ Regla de Pérdida del Turno (Busto)
            </h3>
            <p style={{ lineHeight: '1.6' }}>
              Un jugador <strong>pierde el turno</strong> si saca una carta cuyo valor ya tiene delante de él <strong>Y</strong> tiene <strong>3 o más cartas en total</strong> delante de él.
            </p>
            <ul style={{ paddingLeft: '1.2rem', marginTop: '0.4rem' }}>
              <li>Repetición con menos de 3 cartas (ej. tiene [5], saca 5 → total 2) → <strong>NO pierde</strong>.</li>
              <li>Repetición teniendo 3 cartas o más (ej. tiene [5, 4], saca 5 → total 3) → <strong>¡PIERDE!</strong></li>
              <li>Al perder el turno, <strong>todas</strong> las cartas de delante son descartadas y el turno pasa inmediatamente.</li>
            </ul>
          </div>

          <div>
            <h3 style={{ color: '#f59e0b', fontSize: '1.1rem', marginBottom: '0.4rem' }}>
              🏁 Fin de la Partida y Desempate
            </h3>
            <p>
              La partida finaliza cuando se agota el montón de polvo. Las cartas restantes se guardan automáticamente. Gana quien sume más puntos. En caso de empate, gana quien tenga <strong>más cartas acumuladas</strong>.
            </p>
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1.5rem' }}>
          <button className="btn btn-primary" onClick={onClose}>
            Entendido
          </button>
        </div>
      </div>
    </div>
  );
}
