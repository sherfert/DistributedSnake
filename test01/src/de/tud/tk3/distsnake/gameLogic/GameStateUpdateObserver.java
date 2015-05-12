package de.tud.tk3.distsnake.gameLogic;

import de.tud.tk3.distsnake.GameStatus.GameState;

public interface GameStateUpdateObserver {
	
	public void onGameUpdate(GameState state);

}
