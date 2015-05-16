package de.tud.tk3.distsnake.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import de.tud.tk3.distsnake.R;
import de.tud.tk3.distsnake.connectivity.Connector;
import de.tud.tk3.distsnake.gameLogic.Game;

/**
 * The Main Activity with a field to enter the username and the start game
 * button. *
 */
public class MainActivity extends Activity {

	/**
	 * Starts the game
	 * 
	 * @param view
	 *            the button clicked
	 */
	public void startGame(View view) {
		System.out.println("Pressed startGameButton");
		Intent intent = new Intent(this, GameActivity.class);
		EditText usernameEditText = (EditText) findViewById(R.id.mainActivity_editText_username);
		final String username = usernameEditText.getText().toString();
		if (username.trim().isEmpty()) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.mainActivity_invalidUsername)
					.setMessage(R.string.mainActivity_invalidUsernameMessage)
					.setPositiveButton(android.R.string.yes, null)
					.setIcon(android.R.drawable.ic_dialog_alert).show();
		} else {
			new Thread() {
				public void run() {
					Connector.getInstance().registerGameChannel();
					Game.getInstance().setPlayer(username);
					Game.getInstance().startGame();
				}
			}.start();

			intent.putExtra("username", username);
			startActivity(intent);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final WifiManager wifi2 = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		new Thread() {
			public void run() {
				Connector.getInstance().initialize(wifi2);
			}
		}.start();
	}

	/**
	 * cleanup umundo when exiting app.
	 */
	@Override
	public void onBackPressed() {
		new Thread() {
			public void run() {
				Connector.getInstance().cleanup();
			}
		}.start();
		super.onBackPressed();
	}
}
