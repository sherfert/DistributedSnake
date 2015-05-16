package de.tud.tk3.distsnake.gameLogic;

public interface HelloObserver {

	/**
	 * Called when the game is started
	 * 
	 * @param username
	 *            the username
	 */
	public void onGameStart(String username);

	/**
	 * Called when the game is left.
	 * 
	 * @param username
	 *            the username
	 */
	public void onGameLeave(String username);

	/**
	 * This method should return, if the player is the only player.
	 * 
	 * @return {@code true} if there is no other player.
	 */
	public boolean isOnlyPlayer();
}
