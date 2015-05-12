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
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.LinearLayout;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		// Create Bitmap and Canvas
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		Bitmap bg = Bitmap.createBitmap(metrics, width, width,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bg);

		// Integrate canvas into layout
		LinearLayout ll = (LinearLayout) findViewById(R.id.canvas);
		ll.setBackground(new BitmapDrawable(getResources(), bg));
		ll.setLayoutParams(new GridLayout.LayoutParams(new LayoutParams(width,
				width)));

		// Paint for the backgroud
		Paint bgPaint = new Paint();
		bgPaint.setColor(Color.parseColor("#DADADA"));
		bgPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, width, width, bgPaint);

		// Mockup data TODO
		Coordinates s0 = Coordinates.newBuilder().setX(3).setY(5).build();
		Coordinates s1 = Coordinates.newBuilder().setX(3).setY(6).build();
		Coordinates s2 = Coordinates.newBuilder().setX(4).setY(6).build();
		Coordinates goalCoordinates = Coordinates.newBuilder().setX(10)
				.setY(12).build();
		GameState gameState = GameState.newBuilder().addSnake(s0).addSnake(s1)
				.addSnake(s2).setCurrentPlayer("Blah").setGoal(goalCoordinates)
				.setOrient(Orientation.NORTH).setRemainSteps(1).build();
		
		// Paint for the backgroud
		Paint snakePaint = new Paint();
		snakePaint.setColor(Color.parseColor("#000000"));
		snakePaint.setStrokeWidth(10);
		snakePaint.setStyle(Paint.Style.FILL);

		canvas.drawRect(0, 0, width, width, bgPaint);
		
		// Draw snake
		canvas.drawCircle(30, 30, 5, snakePaint);
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
