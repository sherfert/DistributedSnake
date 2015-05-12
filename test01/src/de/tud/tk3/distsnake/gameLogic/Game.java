package de.tud.tk3.distsnake.gameLogic;

import java.util.ArrayList;
import java.util.List;

import de.tud.tk3.distsnake.GameStatus.GameState;

public class Game {

	private List<GameStateUpdateObserver> gameUpdateObservers = new ArrayList<GameStateUpdateObserver>();
	private HelloObserver helloObserver;
	private GameState gameState;
	private boolean isCurrentPlayer;
	
	private String player;

	public Game() {
	}
	
	public Game(String player){
		this.player = player;
	}
	
	public void startGame(){
		isCurrentPlayer = !helloObserver.onGameStart(player);
		if(isCurrentPlayer){
			createDefaultGameState();
		} else {
			//TODO Wait until his turn
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
		this.gameState = state;
		// notifyOnGameUpdate();
	}
	
	public void onHello(String msgName) {
		// TODO to be implemented
		System.out.println(player + ": Hello received\t" + msgName);
	}

}
