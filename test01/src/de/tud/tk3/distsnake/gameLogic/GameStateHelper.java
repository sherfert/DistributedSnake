package de.tud.tk3.distsnake.gameLogic;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.tud.tk3.distsnake.GameStatus.Coordinates;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.GameStatus.GameState.Orientation;

public class GameStateHelper {

	private static final int DEFAULT_STEPS = 20;
	private static final int X_SNAKE_DEFAULT_VALUE_1 = 10;
	private static final int X_SNAKE_DEFAULT_VALUE_2 = 11;
	private static final int X_SNAKE_DEFAULT_VALUE_3 = 12;
	private static final int Y_SNAKE_DEFAULT_VALUE = 10;
	
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	
	private static Random rand = new Random();

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
		return GameState.newBuilder().addSnake(cord1).addSnake(cord2)
				.addSnake(cord3).setCurrentPlayer(player)
				.setOrient(Orientation.EAST).setRemainSteps(DEFAULT_STEPS)
				.addPlayers(player).setGoal(createRandomGoal()).build();
	}
	
	public static Coordinates createRandomGoal(){
		int x, y;
		do {
			x = rand.nextInt() % WIDTH;
			y = rand.nextInt() % HEIGHT;	
		} while(!checkVaildCoord(x, y));
		return Coordinates.newBuilder().setX(x).setY(y).build();
	}
	
	public static boolean checkVaildCoord(int x, int y){
		//TODO refactor for general snake
		return ((y == Y_SNAKE_DEFAULT_VALUE) && 
				((x == X_SNAKE_DEFAULT_VALUE_1) || 
				(x == X_SNAKE_DEFAULT_VALUE_2) || 
				(x == X_SNAKE_DEFAULT_VALUE_3)));
	}
}
