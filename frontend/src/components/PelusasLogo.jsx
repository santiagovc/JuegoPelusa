import React from 'react';

/**
 * PelusasLogo: Renderiza el logo original de 'PELUSAS' respetando rigurosamente
 * sus colores, proporciones, ilustraciones y composición, sin aplicar filtros,
 * máscaras ni alteraciones que cambien su apariencia.
 */
export default function PelusasLogo({ size = 'normal', className = '' }) {
  return (
    <div className={`pelusas-logo-frame logo-size-${size} ${className}`} title="Pelusas — Juego de Mesa">
      <img
        src="/pelusas-logo.png"
        alt="Pelusas"
        className="pelusas-logo-img"
        draggable="false"
      />
    </div>
  );
}

