package de.tud.tk3.distsnake.gameLogic;

import java.util.ArrayList;
import java.util.List;
import de.tud.tk3.distsnake.GameStatus.GameState.Builder;

import de.tud.tk3.distsnake.GameStatus.Coordinates;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.GameStatus.GameState.Builder;
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

	private Object syncObject = new Object();
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

	/**
	 * Updates the game state and notifies all observers.
	 * 
	 * TODO Should be called in intervals somewhere.
	 * 
	 * TODO synchronize
	 */
	private void updateGameState() {
		// TODO
		Builder gameBuilder = gameState.toBuilder();

		// Set the new orientation
		Orientation orient = (newOrientation == null) ? gameBuilder.getOrient()
				: newOrientation;
		gameBuilder.setOrient(orient);
		// Add the new head
		Coordinates newHead = null;
		Coordinates oldHead = gameBuilder.getSnake(0);
		switch (orient) {
		case NORTH:
			newHead = oldHead.toBuilder().setY(oldHead.getY() - 1).build();
			break;
		case EAST:
			newHead = oldHead.toBuilder().setX(oldHead.getX() + 1).build();
			break;
		case SOUTH:
			newHead = oldHead.toBuilder().setY(oldHead.getY() + 1).build();
			break;
		case WEST:
			newHead = oldHead.toBuilder().setX(oldHead.getX() - 1).build();
			break;
		}
		gameBuilder.addSnake(0, newHead);
		// Check if the goal was eaten
		if (gameState.getGoal().getX() == newHead.getX()
				&& gameState.getGoal().getY() == newHead.getY()) {
			// TODO
		} else {
			// Remove the tail
			gameBuilder.removeSnake(gameBuilder.getSnakeCount() - 1);
		}

		// Decrease to remaining steps
		gameBuilder.setRemainSteps(gameBuilder.getRemainSteps() - 1);

		// Update all observers
		notifyOnGameUpdate(gameState);

		// Validate state
		if (!isValidGameState(gameState)) {
			// TODO end the game
		}

		// Set new orientation to null
		newOrientation = null;
	}

	/**
	 * Checks a game state for validity (game over or not).
	 * 
	 * @param state
	 *            the state
	 * @return if the state is valid.
	 */
	private boolean isValidGameState(GameState state) {
		Coordinates head = state.getSnake(0);

		// The head should be inside of the game field
		if (head.getX() < 0 || head.getY() < 0
				|| head.getX() >= GameStateHelper.WIDTH
				|| head.getY() >= GameStateHelper.HEIGHT) {
			return false;
		}
		// The position of the head should be nowhere in the remaining snake
		for (int i = 1; i < state.getSnakeCount(); i++) {
			if (head.getX() == state.getSnake(i).getX()
					&& head.getY() == state.getSnake(i).getY()) {
				return false;
			}
		}

		return true;
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

	public void onHello(String playerName) {
		System.out.println(player + ": Hello received\t" + playerName);
		synchronized (syncObject) {
			if (isCurrentPlayer) {
				Builder gameStateBuilder = gameState.toBuilder();
				gameState = gameStateBuilder.addPlayers(playerName).build();
			}
		}
	}
}
