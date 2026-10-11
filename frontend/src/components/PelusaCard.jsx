import React from 'react';

export default function PelusaCard({ value, count = 1, isLarge = false, onClick }) {
  const cardClass = `pelusa-card card-val-${value} ${isLarge ? 'pelusa-card-lg' : ''}`;

  return (
    <div
      className={cardClass}
      onClick={onClick}
      data-testid={`card-${value}`}
      title={`Pelusa de valor ${value}`}
    >
      <span className="pelusa-card-val">{value}</span>
      <span className="pelusa-card-sub">pelusa</span>
      {count > 1 && <span className="group-badge">x{count}</span>}
    </div>
  );
}
