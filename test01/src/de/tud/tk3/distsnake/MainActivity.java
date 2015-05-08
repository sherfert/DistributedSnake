package de.tud.tk3.distsnake;

import java.util.List;

import org.umundo.core.Discovery;
import org.umundo.core.Discovery.DiscoveryType;
import org.umundo.core.Message;
import org.umundo.core.Node;
import org.umundo.core.Publisher;
import org.umundo.core.Receiver;
import org.umundo.core.Subscriber;
import org.umundo.s11n.ITypedReceiver;
import org.umundo.s11n.TypedPublisher;
import org.umundo.s11n.TypedSubscriber;

import de.tud.tk3.distsnake.GameStatus;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.GameStatus.GameState.Builder;
import de.tud.tk3.distsnake.GameStatus.Coordinates;
import de.tud.tk3.distsnake.GameStatus.GameState.Orientation;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

	TextView tv;
	Thread testPublishing;
	Discovery disc;
	Node node;
	TypedPublisher fooPub;
	TypedSubscriber fooSub;

	public class TestPublishing implements Runnable {

		@Override
		public void run() {
			String message = "This is foo from android";

			while (testPublishing != null) {
//				Message msg = new Message();
//				msg.putMeta("position", "34");
//				msg.setData(message.getBytes());
//				fooPub.send(msg);
				Coordinates gCord = Coordinates.newBuilder().setX(1).setY(2).build();
				Coordinates sCord1 = Coordinates.newBuilder().setX(10).setY(15).build();
				Coordinates sCord2 = Coordinates.newBuilder().setX(11).setY(15).build();
                GameState chatMsg;
				try {
				chatMsg= GameState.newBuilder().setGoal(gCord).addSnake(sCord1).addSnake(sCord2).setOrient(Orientation.EAST).setRemainSteps(10).build();
				fooPub.sendObject(chatMsg);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv.setText(tv.getText() + "o");
					}
				});
			}
		}
	}

	public class TestReceiver extends Receiver {
		public void receive(final Message msg) {

			for (String key : msg.getMeta().keySet()) {
				Log.i("umundo", key + ": " + msg.getMeta(key));
			}
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tv.setText(tv.getText() + "Message: "
							+ new String(msg.getData())
							+ msg.getMeta("position"));
				}
			});
		}
	}

	public class TestTypedReceiver implements ITypedReceiver {
		public void receiveObject(Object object, Message msg) {
			// we receive message instance as well for its meta-fields
			final GameState chatMsg = (GameState) object; // check for
														// um.s11n.type if there
														// are different types
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tv.setText(tv.getText() + "MessageObject: "
							+ new String(chatMsg.getGoal().toString())
							+ new String(chatMsg.getOrient().toString()));
				}
			});
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tv = new TextView(this);
		tv.setText("");
		setContentView(tv);

		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi != null) {
			MulticastLock mcLock = wifi.createMulticastLock("mylock");
			mcLock.acquire();
			// mcLock.release();
		} else {
			Log.v("android-umundo", "Cannot get WifiManager");
		}

		// System.loadLibrary("umundoNativeJava");
		System.loadLibrary("umundoNativeJava_d");

		disc = new Discovery(DiscoveryType.MDNS);

		node = new Node();
		disc.add(node);

		fooPub = new TypedPublisher("pingpong");
		node.addPublisher(fooPub);

		fooSub = new TypedSubscriber("pingpong");
		fooSub.setReceiver(new TestTypedReceiver());
		node.addSubscriber(fooSub);
		fooSub.registerType(GameState.class);
		
		/*
		Coordinates s0 = Coordinates.newBuilder().setX(3).setY(5).build();
		Coordinates s1 = Coordinates.newBuilder().setX(4).setY(6).build();
		GameState msg = GameState.newBuilder().addSnake(s0).addSnake(s1)
        		.setUsername("Blah").setPosition("ahh").build();
		try {
			Builder mBuilder = msg.toBuilder();
			mBuilder.removeSnake(0);//   getSnakeList().remove(0);
            ChatMessage msg2 = mBuilder.build();
            List<SnakePart> list2 = msg2.getSnakeList();
            System.out.println(list2);
		} catch(Exception e) {
			e.printStackTrace();
		}
		*/
		
		testPublishing = new Thread(new TestPublishing());
		testPublishing.start();
	}
}
