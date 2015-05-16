package de.tud.tk3.distsnake.connectivity;

import org.umundo.s11n.TypedPublisher;

import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gameLogic.GameStateUpdateObserver;

/**
 * A publisher for game states.
 */
public class GameStatePublisher extends TypedPublisher implements
		GameStateUpdateObserver {

	/**
	 * Initializes a publisher.
	 */
	public GameStatePublisher() {
		super("game");
		Game.getInstance().subscribeGameUpdateObserver(this);
	}

	@Override
	public void onGameUpdate(GameState state) {
		// Send the update, if we're the current player
		if (Game.getInstance().isCurrentPlayer()) {
			this.sendObject(state);
		}
	}

	@Override
	public void onGameLost(GameState state) {
		// Send the last state, if we're the current player
		if (Game.getInstance().isCurrentPlayer()) {
			this.sendObject(state);
		}
		// Unsubscribe
		Game.getInstance().unsubscribeGameUpdateObserver(this);
	}

}
