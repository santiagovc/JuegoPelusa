import React, { useEffect, useState } from 'react';
import { gameApi } from './api/gameApi';
import TableCenter from './components/TableCenter';
import PlayerMat from './components/PlayerMat';
import NewGameModal from './components/NewGameModal';
import HistoryModal from './components/HistoryModal';
import RulesModal from './components/RulesModal';
import VictoryModal from './components/VictoryModal';

export default function App() {
  const [gameState, setGameState] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  // Modals state
  const [isNewGameOpen, setIsNewGameOpen] = useState(false);
  const [isHistoryOpen, setIsHistoryOpen] = useState(false);
  const [isRulesOpen, setIsRulesOpen] = useState(false);
  const [isVictoryOpen, setIsVictoryOpen] = useState(false);

  // Inicializar partida al cargar
  useEffect(() => {
    startInitialGame();
  }, []);

  const startInitialGame = async () => {
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const initialGame = await gameApi.createGame(['Fabian', 'Laura']);
      setGameState(initialGame);
    } catch (err) {
      setErrorMessage(err.message || 'Error al conectar con el backend');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateGame = async (playerNames, riggedDeckValues) => {
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const newGame = await gameApi.createGame(playerNames, riggedDeckValues);
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
    try {
      const result = await gameApi.drawCard(gameState.id);
      setGameState(result.gameState);

      if (result.gameState.status === 'FINISHED') {
        setIsVictoryOpen(true);
      }
    } catch (err) {
      setErrorMessage(err.message || 'Error al sacar carta');
    } finally {
      setIsLoading(false);
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

  const getBannerType = () => {
    if (!gameState?.lastActionMessage) return 'info';
    const msg = gameState.lastActionMessage.toLowerCase();
    if (msg.includes('pérdida') || msg.includes('busto') || msg.includes('perdió')) return 'bust';
    if (msg.includes('guardó') || msg.includes('victoria') || msg.includes('ganador')) return 'success';
    return 'info';
  };

  return (
    <div className="app-container">
      {/* Header */}
      <header className="app-header">
        <div className="logo-area">
          <div className="logo-icon">🧶</div>
          <div className="logo-text">
            <h1>PELUSAS</h1>
            <span className="logo-badge">Edición Oficial Digital</span>
          </div>
        </div>

        <div className="header-actions">
          <button
            className="btn btn-secondary"
            id="btn-rules"
            data-testid="btn-open-rules"
            onClick={() => setIsRulesOpen(true)}
          >
            📖 Reglas
          </button>
          <button
            className="btn btn-secondary"
            id="btn-history"
            data-testid="btn-open-history"
            onClick={() => setIsHistoryOpen(true)}
          >
            📜 Historial BD
          </button>
          <button
            className="btn btn-primary"
            id="btn-new-game"
            data-testid="btn-open-new-game"
            onClick={() => setIsNewGameOpen(true)}
          >
            + Nueva Partida
          </button>
        </div>
      </header>

      {/* Main content */}
      <main className="main-content">
        {/* Banner de error si ocurre */}
        {errorMessage && (
          <div className="action-banner banner-bust" data-testid="error-banner">
            <div className="banner-content">
              <span className="banner-icon">⚠️</span>
              <span className="banner-text">{errorMessage}</span>
            </div>
          </div>
        )}

        {/* Banner de acción / suceso del turno */}
        {gameState?.lastActionMessage && (
          <div className={`action-banner banner-${getBannerType()}`} data-testid="action-banner">
            <div className="banner-content">
              <span className="banner-icon">
                {getBannerType() === 'bust' ? '💥' : getBannerType() === 'success' ? '✨' : '📢'}
              </span>
              <span className="banner-text" data-testid="banner-message">
                {gameState.lastActionMessage}
              </span>
            </div>
            {gameState.status === 'FINISHED' && (
              <button
                className="btn btn-primary"
                style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem' }}
                onClick={() => setIsVictoryOpen(true)}
              >
                Ver Podio
              </button>
            )}
          </div>
        )}

        {/* Tablero central */}
        <TableCenter
          gameState={gameState}
          onDraw={handleDrawCard}
          onBank={handleBankCards}
          onStop={handleStopTurn}
          isLoading={isLoading}
        />

        {/* Tapetes de todos los jugadores */}
        <div className="players-grid" data-testid="players-grid">
          {gameState?.players?.map((player, index) => (
            <PlayerMat
              key={player.id}
              player={player}
              index={index}
              isActive={gameState.activePlayerIndex === index}
            />
          ))}
        </div>
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
