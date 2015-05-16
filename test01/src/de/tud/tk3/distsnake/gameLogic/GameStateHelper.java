package de.tud.tk3.distsnake.gameLogic;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.tud.tk3.distsnake.GameStatus.Coordinates;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.GameStatus.GameState.Orientation;

/**
 * Some static helper fields and methods for the game state.
 */
public class GameStateHelper {

	// The default values for initial snake
	private static final int X_SNAKE_DEFAULT_VALUE_1 = 12;
	private static final int X_SNAKE_DEFAULT_VALUE_2 = 11;
	private static final int X_SNAKE_DEFAULT_VALUE_3 = 10;
	private static final int Y_SNAKE_DEFAULT_VALUE = 10;

	// The grid dimension
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	// The steps of one quantum
	public static final int DEFAULT_STEPS = 20;
	// The time of one step
	public static final int DEFAULT_STEP_TIME_MS = 300;

	private static Random rand = new Random();

	/**
	 * @param player
	 *            the name of the player
	 * @return a default game state
	 */
	public static GameState constructDefaultGameState(String player) {

		Coordinates cord1 = Coordinates.newBuilder()
				.setX(X_SNAKE_DEFAULT_VALUE_1).setY(Y_SNAKE_DEFAULT_VALUE)
				.build();
		Coordinates cord2 = Coordinates.newBuilder()
				.setX(X_SNAKE_DEFAULT_VALUE_2).setY(Y_SNAKE_DEFAULT_VALUE)
				.build();
		Coordinates cord3 = Coordinates.newBuilder()
				.setX(X_SNAKE_DEFAULT_VALUE_3).setY(Y_SNAKE_DEFAULT_VALUE)
				.build();
		GameState gameState = GameState.newBuilder().addSnake(cord1)
				.addSnake(cord2).addSnake(cord3).setCurrentPlayer(player)
				.setOrient(Orientation.EAST).setRemainSteps(DEFAULT_STEPS)
				.addPlayers(player).buildPartial();
		return gameState.toBuilder().setGoal(createRandomGoal(gameState))
				.build();
	}

	/**
	 * Creates a random goal not colliding with the snake.
	 * 
	 * @param state
	 *            the state
	 * @return the goal.
	 */
	public static Coordinates createRandomGoal(GameState state) {
		int x, y;
		do {
			x = rand.nextInt() % WIDTH;
			y = rand.nextInt() % HEIGHT;
		} while (!checkValidCoord(x, y, state));
		return Coordinates.newBuilder().setX(x).setY(y).build();
	}

	/**
	 * Checks if the coordinates hit the snake.
	 * 
	 * @param x
	 *            the x value
	 * @param y
	 *            the y value
	 * @param state
	 *            the state with the snake
	 * @return {@code true} if they don't hit the snake.
	 */
	private static boolean checkValidCoord(int x, int y, GameState state) {
		if ((x < 0) || (y < 0))
			return false;
		List<Coordinates> l = state.getSnakeList();
		Iterator<Coordinates> i = l.iterator();
		while (i.hasNext()) {
			Coordinates c = i.next();
			if ((c.getX() == x) && (c.getY() == y))
				return false;
		}
		return true;
	}
}
