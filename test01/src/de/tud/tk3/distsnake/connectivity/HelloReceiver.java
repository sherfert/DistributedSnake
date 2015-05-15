package de.tud.tk3.distsnake.connectivity;

import org.umundo.core.Message;
import org.umundo.s11n.ITypedReceiver;

import android.app.Activity;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gui.MainActivity;
import de.tud.tk3.distsnake.connectivity.HelloMSG.Hello;

public class HelloReceiver implements ITypedReceiver {
			
	public HelloReceiver() {
	}

	public void receiveObject(Object object, Message msg) {
		final Hello helloMsg = (Hello) object;
		Game.getInstance().onHello(helloMsg.getName());
	}
}
