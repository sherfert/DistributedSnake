package de.tud.tk3.distsnake.gui;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#CD5C5C"));
		paint.setStrokeWidth(10);
		paint.setStyle(Paint.Style.STROKE);

		float[] intervals = new float[] { 30.0f, 10.0f };
		float phase = 0;

		DashPathEffect dashPathEffect = new DashPathEffect(intervals, phase);

		paint.setPathEffect(dashPathEffect);

		Path path = new Path();

		path.moveTo(50, 50);
		path.lineTo(50, 500);
		path.lineTo(200, 500);
		path.lineTo(200, 300);
		path.lineTo(350, 300);
		LinearLayout ll = (LinearLayout) findViewById(R.id.canvas);
		Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bg);
		canvas.drawPath(path, paint);

		ll.setBackground(new BitmapDrawable(bg));
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
