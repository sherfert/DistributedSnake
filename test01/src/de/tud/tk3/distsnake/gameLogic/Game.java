package de.tud.tk3.distsnake.gameLogic;

import java.util.ArrayList;
import java.util.List;
import de.tud.tk3.distsnake.GameStatus.GameState.Builder;

import de.tud.tk3.distsnake.GameStatus.GameState;

public class Game {

	// Singleton stuff
	private static Game instance;

	public static synchronized Game getInstance() {
		if (instance == null) {
			instance = new Game();
		}
		return instance;
	}

	/**
	 * Private Constructor for the Singleton pattern.
	 */
	private Game() {
	}

	private List<GameStateUpdateObserver> gameUpdateObservers = new ArrayList<GameStateUpdateObserver>();
	private HelloObserver helloObserver;
	private GameState gameState;
	private boolean isCurrentPlayer;
	private Object syncObject = new Object();
	private String player;

	/**
	 * @param player
	 *            the current (initial) player
	 */
	public void setPlayer(String player) {
		this.player = player;
	}

	// TODO this must make sure, that nothing remains from an old game,
	// i.e. clean up old state
	public void startGame() {
		boolean isFirstPlayer = helloObserver.isFirstPlayer();
		if (isFirstPlayer) {
			createDefaultGameState();
			isCurrentPlayer = true;
		} else {
			helloObserver.onGameStart(player);
		}
	}

	public void createDefaultGameState() {
		this.gameState = GameStateHelper.constructDefaultGameState(player);
	}

	public List<GameStateUpdateObserver> getGameUpdateObservers() {
		return gameUpdateObservers;
	}

	public void setGameUpdateObservers(
			List<GameStateUpdateObserver> gameUpdateObservers) {
		this.gameUpdateObservers = gameUpdateObservers;
	}

	public void setHelloObserver(HelloObserver helloObserver) {
		this.helloObserver = helloObserver;
	}

	public void notifyOnGameUpdate(GameState state) {
		for (GameStateUpdateObserver observer : gameUpdateObservers) {
			observer.onGameUpdate(state);
		}
	}

	public void subscribeGameUpdateObserver(GameStateUpdateObserver observer) {
		gameUpdateObservers.add(observer);
	}

	public void unsubscribeGameUpdateObserver(GameStateUpdateObserver observer) {
		gameUpdateObservers.remove(observer);
	}

	public void updateGameState(GameState state) {
		// TODO
		// this.gameState = state;
		// notifyOnGameUpdate();
	}

	public void leftPressed() {

	}

	public void rightPressed() {

	}

	public void upPressed() {

	}

	public void downPressed() {

	}

	public void onHello(String playerName) {
		System.out.println(player + ": Hello received\t" + playerName);
		synchronized (syncObject) {
			if (isCurrentPlayer) {
				Builder gameStateBuilder = gameState.toBuilder();
				gameState = gameStateBuilder.addPlayers(playerName).build();
			}
		}
	}
	
	public void onGameStateReceived(GameState state){
		synchronized (syncObject) {
			gameState = state;			
		}
	}
}
