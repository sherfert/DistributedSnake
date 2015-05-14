package de.tud.tk3.distsnake.connectivity;

import org.umundo.core.Message;
import org.umundo.s11n.ITypedReceiver;

import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gui.MainActivity;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.GameStatus.GameState.Builder;

public class GameStateReceiver implements ITypedReceiver {

	public GameStateReceiver() {
		
	}

	public void receiveObject(Object object, Message msg) {
		// we receive message instance as well for its meta-fields
		final GameState gameStateMsg = (GameState) object;
		// check for
		// um.s11n.type if there
		// are different types
		
		Game.getInstance().onGameStateReceived(gameStateMsg);

	}
}
