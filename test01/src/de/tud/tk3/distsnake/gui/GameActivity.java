package de.tud.tk3.distsnake.gui;

import de.tud.tk3.distsnake.GameStatus.Coordinates;
import de.tud.tk3.distsnake.GameStatus.GameState;
import de.tud.tk3.distsnake.GameStatus.GameState.Orientation;
import de.tud.tk3.distsnake.R;
import de.tud.tk3.distsnake.R.id;
import de.tud.tk3.distsnake.R.layout;
import de.tud.tk3.distsnake.R.menu;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends Activity {

	/**
	 * Units for the game size. Both width and height.
	 * TODO use constants of GameHelper
	 */
	public static final int GAME_SIZE = 30;

	private Canvas canvas;
	private int windowSize;
	private Paint snakePaint;
	private Paint goalPaint;
	private Paint bgPaint;
	private TextView currentPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		// Create Bitmap and Canvas
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		windowSize = metrics.widthPixels;
		Bitmap bg = Bitmap.createBitmap(metrics, windowSize, windowSize,
				Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bg);

		currentPlayer = (TextView) findViewById(R.id.gameActivity_textView_currentPlayer);

		// Integrate canvas into layout
		LinearLayout ll = (LinearLayout) findViewById(R.id.canvas);
		ll.setBackground(new BitmapDrawable(getResources(), bg));
		ll.setLayoutParams(new GridLayout.LayoutParams(new LayoutParams(
				windowSize, windowSize)));

		// Paint for the backgroud
		bgPaint = new Paint();
		bgPaint.setColor(Color.parseColor("#DADADA"));
		bgPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, windowSize, windowSize, bgPaint);

		// Paint for the backgroud
		snakePaint = new Paint();
		snakePaint.setColor(Color.parseColor("#000000"));
		snakePaint.setStyle(Paint.Style.FILL);

		goalPaint = new Paint();
		goalPaint.setColor(Color.parseColor("#FF0000"));
		goalPaint.setStyle(Paint.Style.FILL);

		// Mockup data TODO
		Coordinates s0 = Coordinates.newBuilder().setX(3).setY(5).build();
		Coordinates s1 = Coordinates.newBuilder().setX(3).setY(6).build();
		Coordinates s2 = Coordinates.newBuilder().setX(4).setY(6).build();
		Coordinates goalCoordinates = Coordinates.newBuilder().setX(10)
				.setY(12).build();
		GameState gameState = GameState.newBuilder().addSnake(s0).addSnake(s1)
				.addSnake(s2).setCurrentPlayer("Blah").addPlayers("Ilmi").addPlayers("Satia")
				.addPlayers("Ment")
				.addPlayers("Omar")
				.addPlayers("Shin")
				.addPlayers("Ahmed Malik Al Madun")
				.addPlayers("Waris Manisatienrattana")
				.setGoal(goalCoordinates).setOrient(Orientation.NORTH)
				.setRemainSteps(1).build();

		updateGameDisplay(gameState);

	}

	private void updateGameDisplay(GameState gameState) {
		// Background
		canvas.drawRect(0, 0, windowSize, windowSize, bgPaint);
		// Snake
		for (Coordinates snakePart : gameState.getSnakeList()) {
			canvas.drawCircle(snakePart.getX() * getUnitSize(windowSize),
					snakePart.getY() * getUnitSize(windowSize),
					getUnitSize(windowSize) / 2, snakePaint);
		}
		// Goal
		canvas.drawCircle(gameState.getGoal().getX() * getUnitSize(windowSize),
				gameState.getGoal().getY() * getUnitSize(windowSize),
				getUnitSize(windowSize) / 2, goalPaint);

		String namesToDisplay = "<h1><b>"
				+ gameState.getCurrentPlayer() + "</b></h1>";
		String playerList= "";
		
		for(String player : gameState.getPlayersList())
			playerList+= player + ", ";
		

		currentPlayer.setText(Html.fromHtml(namesToDisplay + playerList));
	}

	// TODO Add border around actually used part of canvas
	private int getUnitSize(int windowWidth) {
		return (int) (windowWidth / (GAME_SIZE));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
