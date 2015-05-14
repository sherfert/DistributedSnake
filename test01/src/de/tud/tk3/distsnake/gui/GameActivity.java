package de.tud.tk3.distsnake.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.tud.tk3.distsnake.GameStatus.Coordinates;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.GameStatus.GameState.Orientation;
import de.tud.tk3.distsnake.R;
import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gameLogic.GameStateHelper;
import de.tud.tk3.distsnake.gameLogic.GameStateUpdateObserver;

public class GameActivity extends Activity implements GameStateUpdateObserver {

	private Canvas canvas;
	private int maxWindowSize;
	private Paint snakePaint;
	private Paint goalPaint;
	private Paint bgPaint;
	private TextView currentPlayer;
	private String username;
	
	//private static final MAX_WIDTH = 300

	private Button upButton, downButton, rightButton, leftButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		// Initialize fields
		username = getIntent().getExtras().getString("username");
		currentPlayer = (TextView) findViewById(R.id.gameActivity_textView_currentPlayer);
		upButton = (Button) findViewById(R.id.gameActivity_button_up);
		downButton = (Button) findViewById(R.id.gameActivity_button_down);
		rightButton = (Button) findViewById(R.id.gameActivity_button_right);
		leftButton = (Button) findViewById(R.id.gameActivity_button_left);

		// Create Bitmap and Canvas
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		maxWindowSize = metrics.widthPixels;
		double maxHeight = metrics.heightPixels * 0.50;
		if (maxWindowSize > maxHeight)
			maxWindowSize = (int) maxHeight;
		Bitmap bg = Bitmap.createBitmap(metrics, maxWindowSize, maxWindowSize,
				Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bg);

		// Integrate canvas into layout
		LinearLayout ll = (LinearLayout) findViewById(R.id.gameActivity_linearLayout_canvas);
		ll.setBackground(new BitmapDrawable(getResources(), bg));
		GridLayout.LayoutParams params = new GridLayout.LayoutParams(new LayoutParams(
				maxWindowSize, maxWindowSize));
		params.setGravity(Gravity.CENTER);
		ll.setLayoutParams(params);

		// Paint for the backgroud
		bgPaint = new Paint();
		bgPaint.setColor(Color.parseColor("#DADADA"));
		bgPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, maxWindowSize, maxWindowSize, bgPaint);

		// Paint for the backgroud
		snakePaint = new Paint();
		snakePaint.setColor(Color.parseColor("#000000"));
		snakePaint.setStyle(Paint.Style.FILL);

		goalPaint = new Paint();
		goalPaint.setColor(Color.parseColor("#FF0000"));
		goalPaint.setStyle(Paint.Style.FILL);

		// TODO Remove Mockup data
		Coordinates s0 = Coordinates.newBuilder().setX(3).setY(5).build();
		Coordinates s1 = Coordinates.newBuilder().setX(3).setY(6).build();
		Coordinates s2 = Coordinates.newBuilder().setX(4).setY(6).build();
		Coordinates goalCoordinates = Coordinates.newBuilder().setX(10)
				.setY(12).build();
		final GameState gameStateMockUp = GameState.newBuilder().addSnake(s0)
				.addSnake(s1).addSnake(s2).setCurrentPlayer("Blah")
				.addPlayers("Ilmi").addPlayers("Satia").addPlayers("Ment")
				.addPlayers("Omar").addPlayers("Shin")
				.addPlayers("Ahmed Malik Al Madun")
				.addPlayers("Waris Manisatienrattana").setGoal(goalCoordinates)
				.setOrient(Orientation.NORTH).setRemainSteps(1).build();

		GameState gameState = GameStateHelper
				.constructDefaultGameState(username);

		onGameUpdate(gameState);

		// TODO remove this
		Thread t = new Thread() {
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				onGameUpdate(gameStateMockUp);
			}
		};
		t.start();
	}

	/**
	 * Called when the game is over. TODO call this somewhere.
	 */
	public void onGameLost() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.gameActivity_gamelost_title)
				.setPositiveButton(android.R.string.yes, null)
				.setIcon(android.R.drawable.ic_dialog_alert).show();

		finish();
	}

	@Override
	public void onGameUpdate(final GameState gameState) {
		final String namesToDisplay = "<h1><b>" + gameState.getPlayers(0)
				+ "</b></h1>";
		final StringBuilder playerList = new StringBuilder();

		for (int i = 1; i < gameState.getPlayersCount(); i++)
			playerList.append(gameState.getPlayers(i)).append(", ");

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				currentPlayer.setText(Html.fromHtml(namesToDisplay
						+ playerList.toString()));

				// Background
				canvas.drawRect(0, 0, maxWindowSize, maxWindowSize, bgPaint);
				// Snake
				for (Coordinates snakePart : gameState.getSnakeList()) {
					canvas.drawCircle(snakePart.getX()
							* getUnitSize(maxWindowSize), snakePart.getY()
							* getUnitSize(maxWindowSize),
							getUnitSize(maxWindowSize) / 2, snakePaint);
				}
				// Goal
				canvas.drawCircle(gameState.getGoal().getX()
						* getUnitSize(maxWindowSize), gameState.getGoal().getY()
						* getUnitSize(maxWindowSize), getUnitSize(maxWindowSize) / 2,
						goalPaint);

				// Check if we're the current player
				if (username.equals(gameState.getCurrentPlayer())) {
					// Enable buttons
					upButton.setEnabled(true);
					downButton.setEnabled(true);
					rightButton.setEnabled(true);
					leftButton.setEnabled(true);
				} else {
					// Disable buttons
					upButton.setEnabled(false);
					downButton.setEnabled(false);
					rightButton.setEnabled(false);
					leftButton.setEnabled(false);
				}
			}
		});
	}

	// TODO implement backButton functionality

	// XXX Add border around actually used part of canvas
	/**
	 * Return the size of one snake unit in dp.
	 */
	private int getUnitSize(int windowWidth) {
		return (int) (windowWidth / (GameStateHelper.WIDTH));
	}

	/**
	 * Called when a button was pressed. Delegates the control to the Game.
	 * 
	 * @param view
	 *            the pressed button.
	 */
	public void buttonPressed(View view) {
		if (view == upButton) {
			Game.getInstance().upPressed();
		} else if (view == downButton) {
			Game.getInstance().downPressed();
		} else if (view == rightButton) {
			Game.getInstance().rightPressed();
		} else if (view == leftButton) {
			Game.getInstance().leftPressed();
		} else {
			throw new RuntimeException("fuck all this!");
		}
	}
}
