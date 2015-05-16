package de.tud.tk3.distsnake.connectivity;

import org.umundo.core.Message;
import org.umundo.s11n.ITypedReceiver;

import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.GameStatus.GameState;

/**
 * A receiver for game states.
 */
public class GameStateReceiver implements ITypedReceiver {

	@Override
	public void receiveObject(Object object, Message msg) {
		// we receive message instance as well for its meta-fields
		final GameState gameStateMsg = (GameState) object;
		// Forward received state to the game
		Game.getInstance().onGameStateReceived(gameStateMsg);
	}
}
