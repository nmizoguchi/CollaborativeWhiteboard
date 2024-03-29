package edu.mit.collaborativewhiteboard.tools;

import Protocol.CWPMessage;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.view.MotionEvent;
import edu.mit.collaborativewhiteboard.EditorActivity;
import edu.mit.collaborativewhiteboard.MainApplication;
import edu.mit.collaborativewhiteboard.ui.CanvasView;

public class EraseController implements ToolController {

	boolean mCurMove;
	int mLastX;
	int mLastY;
	CanvasView mView;
	EditorActivity mContext;
	MainApplication mApp;

	public EraseController(Context context, CanvasView view) {
		mView = view;
		mContext = (EditorActivity) context;
		mApp = (MainApplication) mContext.getApplication();
	}

	@Override
	public void paint(String[] args) {
		// Get points
		int lastX = Integer.valueOf(args[0]);
		int lastY = Integer.valueOf(args[1]);
		int x = Integer.valueOf(args[2]);
		int y = Integer.valueOf(args[3]);

        // Define Color
        Paint paint = new Paint();
        
        paint.setColor(Integer.valueOf(args[4]));
        paint.setStrokeWidth(Integer.valueOf(args[5]));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Cap.SQUARE);

		while (mView.getCanvas() == null) {

		}
		mView.getCanvas().drawLine(lastX, lastY, x, y, paint);
		mView.postInvalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		mCurMove = action == MotionEvent.ACTION_MOVE;

		if (action == MotionEvent.ACTION_DOWN) {
			mLastX = (int) event.getX();
			mLastY = (int) event.getY();
		} else if (mCurMove) {

			int x = (int) event.getX();
			int y = (int) event.getY();
			int color = Color.WHITE;
			int stroke = (int) mView.getPaint().getStrokeWidth();
			
			// Sends info to the server
			String[] arguments = new String[] { String.valueOf(mLastX),
					String.valueOf(mLastY), String.valueOf(x),
					String.valueOf(y), String.valueOf(color), String.valueOf(stroke) };
			
			mApp.getClient().scheduleMessage(
					CWPMessage.Encode(mApp.getUser(), "erase", arguments));
			mLastX = x;
			mLastY = y;
		}
		return true;
	}
}
