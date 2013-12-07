package edu.mit.collaborativewhiteboard.tools;

import android.view.*;

public interface ToolController {
	public void paint(String[] args);
	public boolean onTouchEvent(MotionEvent event);
}
