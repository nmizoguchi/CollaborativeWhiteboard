package edu.mit.collaborativewhiteboard.tools;

import Protocol.CWPMessage;
import android.content.Context;
import android.graphics.Paint;
import android.view.MotionEvent;
import edu.mit.collaborativewhiteboard.CanvasView;
import edu.mit.collaborativewhiteboard.EditorActivity;
import edu.mit.collaborativewhiteboard.MainApplication;

public class RectangleController implements ToolController {

	boolean mCurUp;
	int mLastX;
	int mLastY;
	CanvasView mView;
	EditorActivity mContext;
	MainApplication mApp;

	public RectangleController(Context context, CanvasView view) {
		mView = view;
		mContext = (EditorActivity) context;
		mApp = (MainApplication) mContext.getApplication();
	}

	@Override
	public void paint(String[] args) {

		// Get points
		int x1 = Integer.valueOf(args[0]); // lastX
		int y1 = Integer.valueOf(args[1]); // lastY
		int x2 = Integer.valueOf(args[2]); // currentX
		int y2 = Integer.valueOf(args[3]); // currentY
		int width = Math.abs(x2 - x1);
		int height = Math.abs(y2 - y1);

		// Define brushColor
		int colorInt = Integer.valueOf(args[4]);

		// Define Brush Size
		int brush = Integer.valueOf(args[5]);

		// Define hasFill
		int hasFillInt = Integer.valueOf(args[6]);

		// Define Color
		Paint paint = new Paint();
		paint.setColor(colorInt);
		paint.setStrokeWidth(brush);
		
		if(hasFillInt == 0)
			paint.setStyle(Paint.Style.STROKE);
		else
			paint.setStyle(Paint.Style.FILL);

		while (mView.getCanvas() == null) {

		}
		mView.getCanvas().drawRect(x1, y1, x2, y2, paint);
		mView.postInvalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		mCurUp = action == MotionEvent.ACTION_UP;

		if (action == MotionEvent.ACTION_DOWN) {
			mLastX = (int) event.getX();
			mLastY = (int) event.getY();
		} else if (mCurUp) {

			int x = (int) event.getX();
			int y = (int) event.getY();
			int brushColor = mView.getPaint().getColor();
			int brushStroke = (int) mView.getPaint().getStrokeWidth();
			int fillColor, hasFill;
			
			switch(mView.getPaint().getStyle()) {
			case FILL:
				hasFill = 1;
				fillColor = brushColor;
				break;
			case FILL_AND_STROKE:
				hasFill = 1;
				fillColor = brushColor;
				break;
			case STROKE:
				hasFill = 0;
				fillColor = 0;
				break;
			default:
				hasFill = 0;
				fillColor = 0;
				break;
			}

//			drawrect x1 y1 x2 y2 brushColor brushSize fillColor hasFill
			String[] arguments = new String[] { String.valueOf(mLastX),
					String.valueOf(mLastY), String.valueOf(x),
					String.valueOf(y), String.valueOf(brushColor),
					String.valueOf(brushStroke),
					String.valueOf(fillColor),
					String.valueOf(hasFill) };

			mApp.getClient().scheduleMessage(
					CWPMessage.Encode(mApp.getUser(), "drawrect", arguments));
		}
		
		return true;
	}
}
