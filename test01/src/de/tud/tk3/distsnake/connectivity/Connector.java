package de.tud.tk3.distsnake.connectivity;

import org.umundo.core.Discovery;
import org.umundo.core.Node;
import org.umundo.core.Discovery.DiscoveryType;
import org.umundo.s11n.TypedPublisher;
import org.umundo.s11n.TypedSubscriber;

import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.connectivity.HelloMSG.Hello;
import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gui.MainActivity.TestTypedReceiver;

public class Connector {
	
	private Game game;
	private Discovery disc;
	private Node node;
	
	
	public Connector(Game game) {
		this.game = game;
		System.loadLibrary("umundoNativeJava_d");

		disc = new Discovery(DiscoveryType.MDNS);

		node = new Node();
		disc.add(node);

		HelloPublisher helloPublisher = new HelloPublisher(game);
		GameStatePublisher gameStatePublisher = new GameStatePublisher(game);
		
		node.addPublisher(helloPublisher);
		node.addPublisher(gameStatePublisher);
		
		HelloReceiver helloReceiver = new HelloReceiver(game);
		GameStateReceiver gameStateReceiver = new GameStateReceiver(game);
		TypedSubscriber helloSub = new TypedSubscriber("hello");
		TypedSubscriber gameSub = new TypedSubscriber("game");
		helloSub.setReceiver(helloReceiver);
		gameSub.setReceiver(gameStateReceiver);
		gameSub.registerType(GameState.class);
		helloSub.registerType(Hello.class);
	}
	
}
