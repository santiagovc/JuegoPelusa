import { test, expect } from '@playwright/test';

test.describe('Pruebas Funcionales Automatizadas - Juego de Mesa Pelusas', () => {

  test('Caso Funcional Obligatorio 1 — Turno Exitoso', async ({ page }) => {
    // 1. Abrir la aplicación
    await page.goto('/');
    await expect(page).toHaveTitle(/Pelusas/);

    // 2. Crear / configurar una partida
    await page.click('[data-testid="btn-open-new-game"]');
    await expect(page.locator('[data-testid="new-game-modal"]')).toBeVisible();

    await page.fill('[data-testid="player-input-0"]', 'Ana');
    await page.fill('[data-testid="player-input-1"]', 'Beto');

    // 3. Iniciar el juego
    await page.click('[data-testid="btn-start-game"]');
    await expect(page.locator('[data-testid="new-game-modal"]')).not.toBeVisible();

    // Verificar que Ana es la jugadora activa inicial
    await expect(page.locator('[data-testid="active-player-name"]')).toHaveText('Ana');

    // 4. Realizar un turno: Sacar una carta
    const initialDeckCountText = await page.locator('[data-testid="deck-count"]').innerText();
    const initialCount = parseInt(initialDeckCountText);

    await page.click('[data-testid="btn-draw"]');

    // Verificar que la carta fue sacada y el mazo disminuyó en 1
    await expect(page.locator('[data-testid="deck-count"]')).toHaveText(`${initialCount - 1} cartas`);
    await page.waitForTimeout(400);
    await page.screenshot({ path: 'pelusas_drawn_card_screenshot.png', fullPage: true });

    // Verificar que el botón de plantarse está habilitado tras sacar carta
    const btnStop = page.locator('[data-testid="btn-stop"]');
    await expect(btnStop).toBeEnabled();

    // 5. Finalizar voluntariamente el turno
    await btnStop.click();

    // 6. Verificar que el turno termina correctamente y pasa al siguiente jugador (Beto)
    await expect(page.locator('[data-testid="active-player-name"]')).toHaveText('Beto');

    // 7. Verificar que las cartas de Ana permanecen delante de ella en riesgo
    const anaMat = page.locator('#player-mat-player-1');
    await expect(anaMat).toBeVisible();
    await expect(anaMat).toContainText('1 cartas');

    // Capturar screenshot visual del tablero
    await page.screenshot({ path: 'pelusas_board_screenshot.png', fullPage: true });
  });

  test('Caso Funcional Obligatorio 2 — Turno Perdido (Busto por repetición con >= 3 cartas)', async ({ page }) => {
    // 1. Abrir la aplicación
    await page.goto('/');

    // 2. Crear partida con modo demostración de busto (secuencia controlada [5, 4, 5])
    await page.click('[data-testid="btn-open-new-game"]');
    await page.fill('[data-testid="player-input-0"]', 'Carlos');
    await page.fill('[data-testid="player-input-1"]', 'Diana');

    // Activar modo sustentación con preset 'bust-demo'
    await page.check('[data-testid="toggle-simulation"]');
    await page.selectOption('[data-testid="select-preset"]', 'bust-demo');

    // Iniciar juego
    await page.click('[data-testid="btn-start-game"]');
    await expect(page.locator('[data-testid="active-player-name"]')).toHaveText('Carlos');

    // 3. Llevar al jugador a tener al menos 3 cartas y sacar repetida
    // Carta 1: Saca 5
    await page.click('[data-testid="btn-draw"]');
    await expect(page.locator('#player-mat-player-1')).toContainText('1 cartas');

    // Carta 2: Saca 4 (Total 2 cartas: 5 y 4)
    await page.click('[data-testid="btn-draw"]');
    await expect(page.locator('#player-mat-player-1')).toContainText('2 cartas');

    // Carta 3: Saca 5 (Repite el 5 teniendo ya 5 y 4 -> Llega a 3 cartas con repetición -> ¡PIERDE EL TURNO!)
    await page.click('[data-testid="btn-draw"]');

    // 4. Verificar que el sistema detecta la pérdida
    const actionBanner = page.locator('[data-testid="action-banner"]');
    await expect(actionBanner).toBeVisible();
    await expect(actionBanner).toContainText('Pérdida de turno');
    await expect(actionBanner).toContainText('Carlos');

    // 5. Verificar que las cartas son descartadas (Carlos queda con 0 cartas delante)
    const carlosMat = page.locator('#player-mat-player-1');
    await expect(carlosMat).toContainText('0 cartas');
    await expect(carlosMat).toContainText('Sin cartas delante');

    // Verificar montón de descarte tiene al menos 3 cartas
    await expect(page.locator('[data-testid="discard-count"]')).toHaveText('3 cartas');

    // 6. Verificar que el turno pasa automáticamente al siguiente jugador (Diana)
    await expect(page.locator('[data-testid="active-player-name"]')).toHaveText('Diana');

    // 7. Verificar visualmente el nuevo estado del juego
    const dianaMat = page.locator('#player-mat-player-2');
    await expect(dianaMat.locator('[data-testid="active-turn-pill"]')).toBeVisible();
  });

  test('Caso Funcional 3 — Robo de Cartas Entre Rivales', async ({ page }) => {
    await page.goto('/');

    // Crear partida con preset 'steal-demo' (secuencia [7, 7])
    await page.click('[data-testid="btn-open-new-game"]');
    await page.fill('[data-testid="player-input-0"]', 'Mateo');
    await page.fill('[data-testid="player-input-1"]', 'Sofia');

    await page.check('[data-testid="toggle-simulation"]');
    await page.selectOption('[data-testid="select-preset"]', 'steal-demo');
    await page.click('[data-testid="btn-start-game"]');

    // Turno de Mateo: Saca un 7 y se planta
    await page.click('[data-testid="btn-draw"]');
    await expect(page.locator('#player-mat-player-1')).toContainText('1 cartas');
    await page.click('[data-testid="btn-stop"]');

    // Turno de Sofia: Saca un 7 -> Roba el 7 que Mateo tenía delante
    await page.click('[data-testid="btn-draw"]');

    // Ejecutar el robo manual (el jugador activo decide robar)
    await page.click('[data-testid="btn-steal-opportunity"]');

    // Verificar que Sofia ahora tiene 2 cartas (la sacada + la robada) y Mateo quedó con 0
    await expect(page.locator('#player-mat-player-2')).toContainText('2 cartas');
    await expect(page.locator('#player-mat-player-1')).toContainText('0 cartas');
    await expect(page.locator('[data-testid="banner-message"]')).toContainText('Robó 1 carta(s) a Mateo');
  });

  test('Caso Funcional 4 — Consulta del Histórico de Partidas en Base de Datos', async ({ page }) => {
    await page.goto('/');

    // Abrir modal de historial
    await page.click('[data-testid="btn-open-history"]');
    await expect(page.locator('[data-testid="history-modal"]')).toBeVisible();

    // Cerrar modal
    await page.click('[data-testid="btn-close-history"]');
    await expect(page.locator('[data-testid="history-modal"]')).not.toBeVisible();
  });
});
