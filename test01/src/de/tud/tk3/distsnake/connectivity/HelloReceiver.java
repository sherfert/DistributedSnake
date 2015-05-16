package de.tud.tk3.distsnake.connectivity;

import org.umundo.core.Message;
import org.umundo.s11n.ITypedReceiver;

import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.connectivity.HelloMSG.Hello;

/**
 * A receiver for hello messages.
 */
public class HelloReceiver implements ITypedReceiver {

	@Override
	public void receiveObject(Object object, Message msg) {
		final Hello helloMsg = (Hello) object;
		// Forward to the game depending if it was hello or farewell
		if (helloMsg.getMsg().equals("farewell")) {
			Game.getInstance().onFarewell(helloMsg.getName());
		} else {
			Game.getInstance().onHello(helloMsg.getName());
		}
	}
}
