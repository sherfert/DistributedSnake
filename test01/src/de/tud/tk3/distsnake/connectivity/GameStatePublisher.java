package de.tud.tk3.distsnake.connectivity;

import org.umundo.s11n.TypedPublisher;

import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gameLogic.GameStateUpdateObserver;

public class GameStatePublisher extends TypedPublisher implements
		GameStateUpdateObserver {

	public GameStatePublisher(Game game) {
		super("game");
		game.subscribeGameUpdateObserver(this);
	}

	@Override
	public void onGameUpdate(GameState state) {
		this.sendObject(state);
	}

}