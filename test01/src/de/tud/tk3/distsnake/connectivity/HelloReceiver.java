package de.tud.tk3.distsnake.connectivity;

import org.umundo.core.Message;
import org.umundo.s11n.ITypedReceiver;

import android.app.Activity;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gui.MainActivity;
import de.tud.tk3.distsnake.connectivity.HelloMSG.Hello;

public class HelloReceiver implements ITypedReceiver {
	
	MainActivity mainActivity;
	Game game;
		
	public HelloReceiver(Game game) {
		this.game = game;
	}

	public void receiveObject(Object object, Message msg) {
		System.out.println("Received a message");
		// we receive message instance as well for its meta-fields
		// final GameState chatMsg = (GameState) object; // check for
													// um.s11n.type if there
													// are different types
		final Hello helloMsg = (Hello) object;
		
		System.out.println(helloMsg.getName());
		//TODO check for uniqueness and make sure the key exists.
//		mainActivity.addPlayer(msg.getMeta("um.proc"), helloMsg.newBuilder().getName());
//		mainActivity.getPlayers().put(msg.getMeta("um.proc"), helloMsg.newBuilder().getName());
		game.onHello(helloMsg.getName());
	}
}
