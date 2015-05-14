package de.tud.tk3.distsnake.gameLogic;

public interface HelloObserver {
	
	public void onGameStart(String username);
	public boolean isFirstPlayer();
}
