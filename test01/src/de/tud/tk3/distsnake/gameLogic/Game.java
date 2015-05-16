package de.tud.tk3.distsnake.gameLogic;

import java.util.ArrayList;
import java.util.List;

import de.tud.tk3.distsnake.GameStatus.GameState.Builder;
import de.tud.tk3.distsnake.GameStatus.Coordinates;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.GameStatus.GameState.Orientation;
import de.tud.tk3.distsnake.connectivity.Connector;

/**
 * Central game logic.
 */
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

	/**
	 * All observers for game updates.
	 */
	private List<GameStateUpdateObserver> gameUpdateObservers = new ArrayList<GameStateUpdateObserver>();
	/**
	 * The observer for hello updates.
	 */
	private HelloObserver helloObserver;
	/**
	 * The current game state.
	 */
	private GameState gameState;
	private Object syncObject = new Object();
	/**
	 * The name of the player.
	 */
	private String player;

	/**
	 * This will be the new orientation at game state change or {@code null} if
	 * the orientation did not change.
	 */
	private Orientation newOrientation;

	/**
	 * The timer that handles scheduling of above task.
	 */
	private Thread task;

	/**
	 * @param player
	 *            the current (initial) player
	 */
	public void setPlayer(String player) {
		this.player = player;
	}

	/**
	 * Starts the game. This must make sure, that nothing remains from an old
	 * game, i.e. clean up old state. If this is the first player to play, it
	 * will give him control over the game and otherwise not.
	 */
	public void startGame() {
		System.out.println("Entered Game.startGame");
		boolean isFirstPlayer = helloObserver.isOnlyPlayer();
		if (isFirstPlayer) {
			System.out.println("I'm the only player.");
			createDefaultGameState();
			startGameLoop();
		} else {
			helloObserver.onGameStart(player);
		}
		Connector.getInstance().subscribeHello();
	}

	/**
	 * This starts the game loop that will continue running, as long is this
	 * player is the current player. When another player gets control, the game
	 * is lost, or he presses the back button, the loop is stopped.
	 */
	private void startGameLoop() {
		/**
		 * The task that will update the game state in intervals.
		 */
		System.out.println("starting the game loop");
		task = new Thread() {
			@Override
			public void run() {
				// while > 0, so that once it is 0, no execution no more!
				while (gameState.getRemainSteps() > 0 && !Thread.interrupted()) {
					try {
						Thread.sleep(GameStateHelper.DEFAULT_STEP_TIME_MS);
					} catch (InterruptedException e) {
						interrupt();
					}
					updateGameState();
				}

			}
		};
		task.start();
	}

	/**
	 * This creates the default game state as current game state with a random
	 * goal.
	 */
	public void createDefaultGameState() {
		synchronized (syncObject) {
			this.gameState = GameStateHelper.constructDefaultGameState(player);
		}
	}

	/**
	 * Sets the hello observer, which is the HelloPublisher
	 */
	public void setHelloObserver(HelloObserver helloObserver) {
		this.helloObserver = helloObserver;
	}

	/**
	 * Notifies all observers if the game state changed.
	 * 
	 * @param state
	 *            the new state.
	 */
	public void notifyOnGameUpdate(GameState state) {
		for (GameStateUpdateObserver observer : new ArrayList<GameStateUpdateObserver>(
				gameUpdateObservers)) {
			observer.onGameUpdate(state);
		}
	}

	/**
	 * Notifies all observers if the game was lost.
	 * 
	 * @param state
	 *            the lost game state.
	 */
	public void notifyOnGameLost(GameState state) {
		for (GameStateUpdateObserver observer : new ArrayList<GameStateUpdateObserver>(
				gameUpdateObservers)) {
			observer.onGameLost(state);
		}
	}

	/**
	 * Adds an observer for game updates
	 * 
	 * @param observer
	 *            the observer
	 */
	public void subscribeGameUpdateObserver(GameStateUpdateObserver observer) {
		gameUpdateObservers.add(observer);
	}

	/**
	 * Removes an observer for game updates
	 * 
	 * @param observer
	 *            the observer
	 */
	public void unsubscribeGameUpdateObserver(GameStateUpdateObserver observer) {
		gameUpdateObservers.remove(observer);
	}

	/**
	 * Called when the game is leaved by pressing the back button, or when the
	 * game was lost.
	 */
	public void leaveGame() {
		// If we're the current player...
		// Control should be given to the next player. Therefore,
		// there should be one last game state update with remaining
		// steps 0!
		if (isCurrentPlayer()) {
			// The updating task should be cancelled.
			task.interrupt();

			synchronized (syncObject) {
				this.gameState = gameState.toBuilder().setRemainSteps(0)
						.build();
			}
			notifyOnGameUpdate(gameState);
		}

		try {
			Thread.sleep(500);
			helloObserver.onGameLeave(player);
			// Unsubscribe and unpublish from game channel
			Connector.getInstance().unregisterGameChannel();
			// Unsubscribe from hello channel
			Connector.getInstance().unSubscribeHello();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is called when the game is lost.
	 * 
	 * XXX if lost game state is received, the GUI is not updated
	 */
	private void gameLost(GameState state) {
		// leave the game
		leaveGame();

		// Notify the observers
		notifyOnGameLost(state);
	}

	/**
	 * Updates the game state and notifies all observers.
	 */
	private void updateGameState() {
		synchronized (syncObject) {
			Builder gameBuilder = gameState.toBuilder();

			// Set the new orientation
			Orientation orient = (newOrientation == null) ? gameBuilder
					.getOrient() : newOrientation;
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
				Coordinates newGoal = GameStateHelper
						.createRandomGoal(gameBuilder.buildPartial());
				gameBuilder.setGoal(newGoal);
			} else {
				// Remove the tail
				gameBuilder.removeSnake(gameBuilder.getSnakeCount() - 1);
			}

			// Decrease to remaining steps
			gameBuilder.setRemainSteps(gameBuilder.getRemainSteps() - 1);

			if (gameBuilder.getRemainSteps() == 0) {
				if (helloObserver.isOnlyPlayer()) {
					gameBuilder.setRemainSteps(GameStateHelper.DEFAULT_STEPS);
				}
			}

			// Set the new game state
			this.gameState = gameBuilder.build();
		}

		// Validate state
		if (!isValidGameState(gameState)) {
			// End the game
			gameLost(gameState);
		} else {
			// Update all observers
			notifyOnGameUpdate(gameState);
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

	/**
	 * Called when a new player joins the game.
	 * 
	 * @param playerName
	 *            his/her name
	 */
	public void onHello(String playerName) {
		System.out.println(player + ": Hello received\t" + playerName);

		if (isCurrentPlayer()) {
			synchronized (syncObject) {
				gameState = gameState.toBuilder().addPlayers(playerName)
						.build();
			}
		}
	}

	/**
	 * Called when a remote gamestate is received
	 * 
	 * @param state
	 *            the state
	 */
	public void onGameStateReceived(GameState state) {
		// Checking if the game was lost
		if (isValidGameState(state)) {
			// If we're the next player, and the quantum is over
			// We have to take over control
			if (state.getRemainSteps() == 0 && isNextPlayer()) {
				synchronized (syncObject) {
					Builder gameStateBuilder = state.toBuilder();
					gameStateBuilder
							.setRemainSteps(GameStateHelper.DEFAULT_STEPS);
					String oldPlayer = gameStateBuilder.getPlayers(0);

					List<String> players = new ArrayList<String>(
							state.getPlayersList());
					players.remove(0);
					players.add(oldPlayer);
					gameStateBuilder.clearPlayers().addAllPlayers(players);

					gameState = gameStateBuilder.build();
				}
				notifyOnGameUpdate(gameState);
				startGameLoop();
			} else {
				// Otherwise we just display the state
				synchronized (syncObject) {
					gameState = state;
				}
				notifyOnGameUpdate(gameState);
			}

		} else {
			// End the game if it was lost.
			gameLost(gameState);
		}

	}

	/**
	 * @return if we're the next player in the list
	 */
	private boolean isNextPlayer() {
		List<String> playerList = gameState.getPlayersList();
		// XXX Be careful not to check for an inexistent player in an invalid
		// position
		return player.equals(playerList.get(1));
	}

	/**
	 * @return if the player is the current player.
	 */
	public boolean isCurrentPlayer() {
		return gameState.getPlayers(0).equals(player);
	}

	/**
	 * Removes a player from the game state if the player leaves.
	 */
	public void onFarewell(String playerName) {
		System.out.println(player + ": Farewell received\t" + playerName);
		synchronized (syncObject) {
			if (isCurrentPlayer()) {
				Builder gameStateBuilder = gameState.toBuilder();

				List<String> players = new ArrayList<String>(
						gameState.getPlayersList());
				players.remove(playerName);
				gameStateBuilder.clearPlayers().addAllPlayers(players);

				gameState = gameStateBuilder.build();
			}
		}
	}

}
