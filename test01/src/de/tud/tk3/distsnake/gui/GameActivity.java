package de.tud.tk3.distsnake.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
	private Paint borderPaint;
	private TextView currentPlayer;
	private String username;

	private final static int CANV_DRAW_OFFSET = 2;
	private final static int DOT_DRAW_OFFSET = 1;
	// private static final MAX_WIDTH = 300
	
	private AlertDialog.Builder aDialog;

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
		Bitmap bg = Bitmap.createBitmap(metrics, maxWindowSize
				+ CANV_DRAW_OFFSET, maxWindowSize + CANV_DRAW_OFFSET,
				Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bg);

		// Integrate canvas into layout
		LinearLayout ll = (LinearLayout) findViewById(R.id.gameActivity_linearLayout_canvas);
		ll.setBackground(new BitmapDrawable(getResources(), bg));
		GridLayout.LayoutParams params = new GridLayout.LayoutParams(
				new LayoutParams(maxWindowSize, maxWindowSize));
		params.setGravity(Gravity.CENTER);
		ll.setLayoutParams(params);

		// Paint for the backgroud
		snakePaint = new Paint();
		snakePaint.setColor(Color.parseColor("#000000"));
		snakePaint.setStyle(Paint.Style.FILL);
		
		borderPaint = new Paint();
		borderPaint.setColor(Color.parseColor("#000000"));
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(getUnitSize(maxWindowSize));

		goalPaint = new Paint();
		goalPaint.setColor(Color.parseColor("#FF0000"));
		goalPaint.setStyle(Paint.Style.FILL);

		// Register GUI as gamestate observer
		Game.getInstance().subscribeGameUpdateObserver(this);
		
		aDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.gameActivity_gamelost_title)
		.setPositiveButton(android.R.string.yes, 
			new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					finish();
				}
			})
		.setIcon(android.R.drawable.ic_dialog_alert);
	}

	/**
	 * Called when the game is over.
	 */
	@Override
	public void onGameLost() {
		Game.getInstance().unsubscribeGameUpdateObserver(this);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				aDialog.show(); 
				
			}
		});
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
				canvas.drawColor(Color.parseColor("#DADADA"));
				canvas.drawRect(0,0, maxWindowSize, maxWindowSize, borderPaint);
				//canvas.drawRect(0, 0, maxWindowSize, maxWindowSize, bgPaint);
				// Snake
				for (Coordinates snakePart : gameState.getSnakeList()) {
					canvas.drawCircle((snakePart.getX() + DOT_DRAW_OFFSET)
							* getUnitSize(maxWindowSize),
							(snakePart.getY() + DOT_DRAW_OFFSET)
									* getUnitSize(maxWindowSize),
							getUnitSize(maxWindowSize) / 2, snakePaint);
				}
				// Goal
				canvas.drawCircle(
						(gameState.getGoal().getX() + DOT_DRAW_OFFSET)
								* getUnitSize(maxWindowSize), (gameState
								.getGoal().getY() + DOT_DRAW_OFFSET)
								* getUnitSize(maxWindowSize),
						getUnitSize(maxWindowSize) / 2, goalPaint);

				// Check if we're the current player
				if (Game.getInstance().isCurrentPlayer()) {
					// if (username.equals(gameState.getCurrentPlayer())) {
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

				// Redraw
				LinearLayout ll = (LinearLayout) findViewById(R.id.gameActivity_linearLayout_canvas);
				ll.invalidate();}
		});
	}

	// TODO implement backButton functionality
	// There, cancel the task

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

	// Do nothing when people pressed back key
	@Override
	public void onBackPressed() {
		Game.getInstance().leaveGame();
		finish();
	}
}
