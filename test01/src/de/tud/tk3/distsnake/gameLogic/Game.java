package de.tud.tk3.distsnake.gameLogic;

import java.util.ArrayList;
import java.util.List;

import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.GameStatus.GameState.Orientation;

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
	private String player;

	/**
	 * This will be the new orientation at game state change or {@code null} if
	 * the orientation did not change.
	 */
	private Orientation newOrientation;

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
		boolean isFirstPlayer = helloObserver.onGameStart(player);
		if (isFirstPlayer) {

		}
		isCurrentPlayer = !helloObserver.onGameStart(player);
		if (isCurrentPlayer) {
			createDefaultGameState();
		} else {
			// TODO Wait until his turn
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

	/**
	 * Updates the game state and notifies all observers. TODO Should be called
	 * in intervals somewhere.
	 */
	private void updateGameState() {
		// TODO
		// this.gameState = state;
		// notifyOnGameUpdate();
	}

	/**
	 * Changes the orientation of the game to the left, if possible.
	 */
	public void leftPressed() {
		if (gameState.getOrient() == Orientation.NORTH
				|| gameState.getOrient() == Orientation.SOUTH) {
			newOrientation = Orientation.EAST;
		}
	}

	/**
	 * Changes the orientation of the game to the right, if possible.
	 */
	public void rightPressed() {
		if (gameState.getOrient() == Orientation.NORTH
				|| gameState.getOrient() == Orientation.SOUTH) {
			newOrientation = Orientation.WEST;
		}
	}

	/**
	 * Changes the orientation of the game to the up, if possible.
	 */
	public void upPressed() {
		if (gameState.getOrient() == Orientation.WEST
				|| gameState.getOrient() == Orientation.EAST) {
			newOrientation = Orientation.NORTH;
		}
	}

	/**
	 * Changes the orientation of the game to the down, if possible.
	 */
	public void downPressed() {
		if (gameState.getOrient() == Orientation.WEST
				|| gameState.getOrient() == Orientation.EAST) {
			newOrientation = Orientation.SOUTH;
		}
	}

	public void onHello(String msgName) {
		// TODO to be implemented
		System.out.println(player + ": Hello received\t" + msgName);
	}

}
