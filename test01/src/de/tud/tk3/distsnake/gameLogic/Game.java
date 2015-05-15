package de.tud.tk3.distsnake.gameLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.provider.SyncStateContract.Helpers;
import de.tud.tk3.distsnake.GameStatus.GameState.Builder;
import de.tud.tk3.distsnake.GameStatus.Coordinates;
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

	private Object syncObject = new Object();
	private String player;

	/**
	 * This will be the new orientation at game state change or {@code null} if
	 * the orientation did not change.
	 */
	private Orientation newOrientation;
	
	/**
	 * The timer that handles scheduling of above task.
	 */
	private Timer timer;

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

			/**
			 * The task that will update the game state in intervals.
			 */
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					updateGameState();
				}
			};
			timer = new Timer();
			// Start the task repeatedly
			timer.scheduleAtFixedRate(task,
					GameStateHelper.DEFAULT_STEP_TIME_MS,
					GameStateHelper.DEFAULT_STEP_TIME_MS);
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
	 * Called when the game is leaved by pressing the back button.
	 * 
	 * TODO isCurrentPlayer should be set to false in switching logic?
	 */
	public void leaveGame() {
		// TODO If we're the current player...
		// Control should be given to the next player. Therefore,
		// there should be one last game state update with remaining
		// steps 0!
		if(isCurrentPlayer) {
			// The updating task should be cancelled.
			timer.cancel();
			
			this.gameState = gameState.toBuilder().setRemainSteps(0).build();
			notifyOnGameUpdate(gameState);
		}
		
		// We're not the current player anymore
		isCurrentPlayer = false;
		
		// TODO Unsubscribe from game channel
	}

	/**
	 * Updates the game state and notifies all observers.
	 */
	private void updateGameState() {
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
			// Set a new goal
			Coordinates newGoal = GameStateHelper.createRandomGoal(gameBuilder
					.buildPartial());
			gameBuilder.setGoal(newGoal);
		} else {
			// Remove the tail
			gameBuilder.removeSnake(gameBuilder.getSnakeCount() - 1);
		}

		// Decrease to remaining steps
		gameBuilder.setRemainSteps(gameBuilder.getRemainSteps() - 1);

		// Set the new game state
		synchronized (syncObject) {
			this.gameState = gameBuilder.build();
		}

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
			newOrientation = Orientation.WEST;
		}
	}

	/**
	 * Changes the orientation of the game to the right, if possible.
	 */
	public void rightPressed() {
		if (gameState.getOrient() == Orientation.NORTH
				|| gameState.getOrient() == Orientation.SOUTH) {
			newOrientation = Orientation.EAST;
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
			if (isCurrentPlayer()) {
				Builder gameStateBuilder = gameState.toBuilder();
				gameState = gameStateBuilder.addPlayers(playerName).build();
			}
		}
	}

	public void onGameStateReceived(GameState state) {
		synchronized (syncObject) {
			gameState = state;
		}
		/* TODO Validate if the players is in the playing screen in otder to execute the refresh
		 * if not nothing should be done 
		 * */
//		if (isValidGameState(state)) {
//			if (state.getRemainSteps() == 0 && isNextPlayer()) {
//				isCurrentPlayer = true;
//				gameState = gameState.toBuilder()
//						.setRemainSteps(GameStateHelper.DEFAULT_STEPS).build();
//				String oldPlayer = gameState.toBuilder().getPlayers(0);
//				// TODO not finished updating players list and taking turn
//			}
//
//		} else {
//			// TODO end game
//		}
		notifyOnGameUpdate(gameState);

	}

	private boolean isNextPlayer() {
		List<String> playerList = gameState.getPlayersList();
		// TODO Be careful not to check for an inexistent player in an invalid
		// position
		return player.equals(playerList.get(1));
	}

	public boolean isCurrentPlayer() {
		return isCurrentPlayer;
	}

}
