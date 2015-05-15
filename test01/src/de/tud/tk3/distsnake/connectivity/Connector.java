package de.tud.tk3.distsnake.connectivity;

import org.umundo.core.Discovery;
import org.umundo.core.Node;
import org.umundo.core.Discovery.DiscoveryType;
import org.umundo.s11n.TypedPublisher;
import org.umundo.s11n.TypedSubscriber;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.connectivity.HelloMSG.Hello;
import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gui.MainActivity.TestTypedReceiver;

/**
 * Central connectivity module.
 */
public class Connector {

	// Singleton stuff
	private static Connector instance;

	public static synchronized Connector getInstance() {
		if (instance == null) {
			instance = new Connector();
		}
		return instance;
	}

	/**
	 * Private Constructor for the Singleton pattern.
	 */
	private Connector() {
	}

	private Discovery disc;
	private Node node;
	
	private GameStateReceiver gameStateReceiver;
	private TypedSubscriber gameSub;
	private GameStatePublisher gameStatePublisher;

	/**
	 * Initializes the connector. Registers a publisher and a subscriber in
	 * "hello" channel.
	 * 
	 * @param wifi
	 *            the wifimanager
	 */
	public void initialize(WifiManager wifi) {
		if (wifi != null) {
			MulticastLock mcLock = wifi.createMulticastLock("mylock");
			mcLock.acquire();
		} else {
			Log.v("android-umundo", "Cannot get WifiManager");
		}

		System.loadLibrary("umundoNativeJava_d");

		disc = new Discovery(DiscoveryType.MDNS);

		node = new Node();
		disc.add(node);
		
		gameStateReceiver = new GameStateReceiver();
		gameSub = new TypedSubscriber("game");
		gameSub.setReceiver(gameStateReceiver);
		gameSub.registerType(GameState.class);

		/* Adding the hello publisher */
		HelloPublisher helloPublisher = new HelloPublisher();
		node.addPublisher(helloPublisher);

		/* Adding the hello subscriber */
		HelloReceiver helloReceiver = new HelloReceiver();
		TypedSubscriber helloSub = new TypedSubscriber("hello");
		helloSub.setReceiver(helloReceiver);
		helloSub.registerType(Hello.class);
		node.addSubscriber(helloSub);
	}

	/**
	 * Registers subscriber and publisher from the game channel. TODO call
	 */
	public void registerGameChannel() {
		// Adding game publisher (needs to be recreated every time!)
		gameStatePublisher = new GameStatePublisher(
				Game.getInstance());
		node.addPublisher(gameStatePublisher);
		
		// Adding game subscriber
		node.addSubscriber(gameSub);
	}

	/**
	 * Unregisters subscriber and publisher from the game channel. TODO call
	 */
	public void unregisterGameChannel() {
		node.removePublisher(gameStatePublisher);
		node.removeSubscriber(gameSub);
	}

}
