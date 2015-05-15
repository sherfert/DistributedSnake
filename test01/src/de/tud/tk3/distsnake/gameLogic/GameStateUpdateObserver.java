package de.tud.tk3.distsnake.gameLogic;

import de.tud.tk3.distsnake.GameStatus.GameState;

/**
 * Implemented by parties interested in game state updates.
 */
public interface GameStateUpdateObserver {

	/**
	 * Invoked whenever the game state changes.
	 * 
	 * @param state
	 *            the new game state.
	 */
	public void onGameUpdate(GameState state);

	/**
	 * Invoked when the game was lost.
	 */
	public void onGameLost();

}
