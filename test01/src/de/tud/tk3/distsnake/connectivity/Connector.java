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


public class Connector {
	
	private Discovery disc;
	private Node node;
	
	
	public Connector(WifiManager wifi) {
//		WifiManager wifi = (WifiManager) mainActivity.getSystemService(Context.WIFI_SERVICE);
		if (wifi != null) {
			MulticastLock mcLock = wifi.createMulticastLock("mylock");
			mcLock.acquire();
			// mcLock.release();
		} else {
			Log.v("android-umundo", "Cannot get WifiManager");
		}

		System.loadLibrary("umundoNativeJava_d");

		disc = new Discovery(DiscoveryType.MDNS);

		node = new Node();
		disc.add(node);

		HelloPublisher helloPublisher = new HelloPublisher();
//		GameStatePublisher gameStatePublisher = new GameStatePublisher();
		
		node.addPublisher(helloPublisher);
//		node.addPublisher(gameStatePublisher);
		
//		GameStateReceiver gameStateReceiver = new GameStateReceiver();
//		TypedSubscriber gameSub = new TypedSubscriber("game");
//		gameSub.setReceiver(gameStateReceiver);
//		gameSub.registerType(GameState.class);
		
		HelloReceiver helloReceiver = new HelloReceiver();
		TypedSubscriber helloSub = new TypedSubscriber("hello");
		helloSub.setReceiver(helloReceiver);
		helloSub.registerType(Hello.class);
		node.addSubscriber(helloSub);
	}
	
}
