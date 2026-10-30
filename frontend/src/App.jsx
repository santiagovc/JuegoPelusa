import React, { useEffect, useState } from 'react';
import { BookOpen, History, Plus, AlertTriangle, CheckCircle2, Info, Trophy, Users } from 'lucide-react';
import { gameApi } from './api/gameApi';
import TableCenter from './components/TableCenter';
import PlayerMat from './components/PlayerMat';
import NewGameModal from './components/NewGameModal';
import HistoryModal from './components/HistoryModal';
import RulesModal from './components/RulesModal';
import VictoryModal from './components/VictoryModal';
import PelusasLogo from './components/PelusasLogo';

export default function App() {
  const [gameState, setGameState] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  // Estados de animación
  const [isDealing, setIsDealing] = useState(false);
  const [isBusting, setIsBusting] = useState(false);
  const [stealingPlayerId, setStealingPlayerId] = useState(null);

  // Modals state
  const [isNewGameOpen, setIsNewGameOpen] = useState(false);
  const [isHistoryOpen, setIsHistoryOpen] = useState(false);
  const [isRulesOpen, setIsRulesOpen] = useState(false);
  const [isVictoryOpen, setIsVictoryOpen] = useState(false);

  // Inicializar partida al cargar (con autoSteal: false para robo manual)
  useEffect(() => {
    let active = true;
    gameApi.createGame(['Ana', 'Beto'], null, false)
      .then((initialGame) => {
        if (active) {
          setGameState(initialGame);
          setIsLoading(false);
        }
      })
      .catch((err) => {
        if (active) {
          setErrorMessage(err.message || 'Error al conectar con el backend');
          setIsLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, []);

  const handleCreateGame = async (playerNames, riggedDeckValues) => {
    setIsLoading(true);
    setErrorMessage(null);
    try {
      // Por defecto creamos partidas con autoSteal: false para que cada jugador robe manualmente
      const newGame = await gameApi.createGame(playerNames, riggedDeckValues, false);
      setGameState(newGame);
      setIsNewGameOpen(false);
      setIsVictoryOpen(false);
    } catch (err) {
      setErrorMessage(err.message || 'Error al crear la partida');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDrawCard = async () => {
    if (!gameState || isLoading) return;
    setIsLoading(true);
    setErrorMessage(null);
    setIsDealing(true);

    try {
      const result = await gameApi.drawCard(gameState.id);

      // Si bustea, disparar animación de descarte
      if (result.busted) {
        setIsBusting(true);
        setTimeout(() => setIsBusting(false), 900);
      }

      setGameState(result.gameState);

      if (result.gameState.status === 'FINISHED') {
        setIsVictoryOpen(true);
      }
    } catch (err) {
      setErrorMessage(err.message || 'Error al sacar carta');
    } finally {
      setIsLoading(false);
      setTimeout(() => setIsDealing(false), 500);
    }
  };

  const handleBankCards = async () => {
    if (!gameState || isLoading) return;
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const updated = await gameApi.bankCards(gameState.id);
      setGameState(updated);
    } catch (err) {
      setErrorMessage(err.message || 'Error al guardar cartas');
    } finally {
      setIsLoading(false);
    }
  };

  const handleStopTurn = async () => {
    if (!gameState || isLoading) return;
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const updated = await gameApi.stopTurn(gameState.id);
      setGameState(updated);
    } catch (err) {
      setErrorMessage(err.message || 'Error al finalizar turno');
    } finally {
      setIsLoading(false);
    }
  };

  // Acción de Robo Manual: Cada jugador decide expresamente si roba o se le olvida
  const handleManualSteal = async (victimPlayerId, cardValue) => {
    if (!gameState || isLoading) return;
    setIsLoading(true);
    setErrorMessage(null);
    setStealingPlayerId(victimPlayerId);

    try {
      const updated = await gameApi.stealCards(gameState.id, victimPlayerId, cardValue);
      setGameState(updated);
    } catch (err) {
      setErrorMessage(err.message || 'Error al ejecutar el robo');
    } finally {
      setIsLoading(false);
      setTimeout(() => setStealingPlayerId(null), 600);
    }
  };

  const getBannerType = () => {
    if (!gameState?.lastActionMessage) return 'info';
    const msg = gameState.lastActionMessage.toLowerCase();
    if (msg.includes('pérdida') || msg.includes('busto') || msg.includes('perdió')) return 'bust';
    if (msg.includes('guardó') || msg.includes('victoria') || msg.includes('ganador') || msg.includes('robó')) return 'success';
    return 'info';
  };

  const renderBannerIcon = () => {
    const type = getBannerType();
    if (type === 'bust') return <AlertTriangle size={18} className="banner-icon-bust" />;
    if (type === 'success') return <CheckCircle2 size={18} className="banner-icon-success" />;
    return <Info size={18} className="banner-icon-info" />;
  };

  return (
    <div className="app-container">
      {/* Barra superior con logo original al centro y controles de acción */}
      <header className="app-header">
        <div className="header-brand-wrap">
          <span className="brand-subtitle-badge">Juego de Mesa Digital</span>
        </div>

        {/* LOGO ORIGINAL DE PELUSAS (INTACTO Y PROPORCIONADO) */}
        <div className="header-logo-center">
          <PelusasLogo />
        </div>

        <div className="header-actions">
          <button
            className="btn btn-secondary btn-header-action"
            id="btn-rules"
            data-testid="btn-open-rules"
            onClick={() => setIsRulesOpen(true)}
            title="Consultar reglamento"
          >
            <BookOpen size={16} />
            <span>Reglas</span>
          </button>
          <button
            className="btn btn-secondary btn-header-action"
            id="btn-history"
            data-testid="btn-open-history"
            onClick={() => setIsHistoryOpen(true)}
            title="Historial de partidas en base de datos"
          >
            <History size={16} />
            <span>Historial BD</span>
          </button>
          <button
            className="btn btn-primary btn-header-action"
            id="btn-new-game"
            data-testid="btn-open-new-game"
            onClick={() => setIsNewGameOpen(true)}
            title="Configurar una nueva partida"
          >
            <Plus size={16} />
            <span>Nueva Partida</span>
          </button>
        </div>
      </header>

      {/* Superficie Central de la Mesa */}
      <main className="main-content">
        {/* Banner de error */}
        {errorMessage && (
          <div className="action-banner banner-bust" data-testid="error-banner">
            <div className="banner-content">
              <AlertTriangle size={18} className="banner-icon-bust" />
              <span className="banner-text">{errorMessage}</span>
            </div>
          </div>
        )}

        {/* Banner de acción / suceso del turno */}
        {gameState?.lastActionMessage && (
          <div
            className={`action-banner banner-${getBannerType()} ${getBannerType() === 'bust' ? 'shake-bust' : ''}`}
            data-testid="action-banner"
          >
            <div className="banner-content">
              {renderBannerIcon()}
              <span className="banner-text" data-testid="banner-message">
                {gameState.lastActionMessage}
              </span>
            </div>
            {gameState.status === 'FINISHED' && (
              <button
                className="btn btn-primary btn-podium-trigger"
                onClick={() => setIsVictoryOpen(true)}
              >
                <Trophy size={15} />
                <span>Ver Podio</span>
              </button>
            )}
          </div>
        )}

        {/* Centro de la mesa: Mazo de robo, Área de cartas en juego y Botones Robar/Plantarse */}
        <TableCenter
          gameState={gameState}
          onDraw={handleDrawCard}
          onBank={handleBankCards}
          onStop={handleStopTurn}
          onSteal={handleManualSteal}
          isLoading={isLoading}
          isDealing={isDealing}
          isBusting={isBusting}
        />

        {/* Tableros de los jugadores */}
        <section className="players-grid-section">
          <div className="section-header-row">
            <Users size={18} className="section-header-icon" />
            <h2 className="section-title">Tableros de Jugadores</h2>
          </div>

          <div className="players-grid" data-testid="players-grid">
            {gameState?.players?.map((player, index) => (
              <PlayerMat
                key={player.id}
                player={player}
                index={index}
                isActive={gameState.activePlayerIndex === index}
                pendingSteals={gameState.pendingSteals || []}
                onSteal={handleManualSteal}
                isBeingStealed={stealingPlayerId === player.id}
              />
            ))}
          </div>
        </section>
      </main>

      {/* Modales */}
      <NewGameModal
        isOpen={isNewGameOpen}
        onClose={() => setIsNewGameOpen(false)}
        onCreateGame={handleCreateGame}
      />

      <HistoryModal
        isOpen={isHistoryOpen}
        onClose={() => setIsHistoryOpen(false)}
      />

      <RulesModal
        isOpen={isRulesOpen}
        onClose={() => setIsRulesOpen(false)}
      />

      <VictoryModal
        isOpen={isVictoryOpen}
        gameState={gameState}
        onClose={() => setIsVictoryOpen(false)}
        onNewGame={() => {
          setIsVictoryOpen(false);
          setIsNewGameOpen(true);
        }}
      />
    </div>
  );
}

