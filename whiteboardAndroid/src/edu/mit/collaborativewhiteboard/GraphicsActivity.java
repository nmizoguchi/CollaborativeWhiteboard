package edu.mit.collaborativewhiteboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import edu.mit.collaborativewhiteboard.ui.PictureLayout;

class GraphicsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(View view) {
		if (false) { // set to true to test Picture
			ViewGroup vg = new PictureLayout(this);
			vg.addView(view);
			view = vg;
		}

		super.setContentView(view);
	}
}