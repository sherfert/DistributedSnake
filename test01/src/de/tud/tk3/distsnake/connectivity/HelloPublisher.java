package de.tud.tk3.distsnake.connectivity;

import org.umundo.s11n.TypedPublisher;

import de.tud.tk3.distsnake.connectivity.HelloMSG.Hello;
import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gameLogic.HelloObserver;

/**
 * A publisher for hello messages.
 */
public class HelloPublisher extends TypedPublisher implements HelloObserver {

	/**
	 * Creating the publisher and registering with the game.
	 */
	public HelloPublisher() {
		super("hello");
		Game.getInstance().setHelloObserver(this);
	}

	@Override
	public void onGameStart(String username) {
		System.out.println("Entered onGameStart");
		try {
			Hello msg = Hello.newBuilder().setName(username)
					.setMsg("I am here").build();
			this.sendObject(msg);
			System.out.println("Sent the hello message");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * FIXME This method is buggy, when the app is force-closed and restarted.
	 * It will return true, although there are other players already (i.e. there
	 * should be subsribers).
	 * 
	 * We suspect, this is an MDNS issue and recommend not to force-close the
	 * app in order to avoid this issue.
	 */
	@Override
	public boolean isOnlyPlayer() {
		return this.getSubscribers().isEmpty();
	}

	@Override
	public void onGameLeave(String username) {
		System.out.println("Entered onGameLeave");
		try {
			Hello msg = Hello.newBuilder().setName(username).setMsg("farewell")
					.build();
			this.sendObject(msg);
			System.out.println("Sent the farewell message");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
