package de.tud.tk3.distsnake.gui;

import org.umundo.core.Discovery;
import org.umundo.core.Node;
import org.umundo.s11n.TypedPublisher;
import org.umundo.s11n.TypedSubscriber;

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
 * Getting it to run:
 * 
 * 1. Replace the umundo.jar in the libs folder by the one from the intaller and
 * add it to the classpath. Make sure to take the umundo.jar for Android, as you
 * are not allowed to have JNI code within and the desktop umundo.jar includes
 * all supported JNI libraries.
 * 
 * 2. Replace the JNI library libumundoNativeJava.so (or the debug variant) into
 * libs/armeabi/
 * 
 * 3. Make sure System.loadLibrary() loads the correct variant.
 * 
 * 4. Make sure you have set the correct permissions: <uses-permission
 * android:name="android.permission.INTERNET"/> <uses-permission
 * android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
 * 
 * @author sradomski
 *
 */
public class MainActivity extends Activity {

	// TextView tv;
	Thread testPublishing;
	Discovery disc;
	Node node;
	TypedPublisher fooPub;
	TypedSubscriber fooSub;

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
					// XXX is it too fast register and then sending right away?
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

	// Do nothing when people pressed back key
	@Override
	public void onBackPressed() {
		new Thread() {
			public void von() {
				Connector.getInstance().cleanup();
			}
		}.start();
		super.onBackPressed();
	}
}
