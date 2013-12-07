package edu.mit.collaborativewhiteboard;

import java.util.ArrayList;
import java.util.List;

import Protocol.CWPMessage;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import client.gui.canvas.Canvas.MODE;
import edu.mit.collaborativewhiteboard.tools.EraseController;
import edu.mit.collaborativewhiteboard.tools.FreehandController;
import edu.mit.collaborativewhiteboard.tools.LineController;
import edu.mit.collaborativewhiteboard.tools.RectangleController;
import edu.mit.collaborativewhiteboard.tools.ToolController;

public class CanvasView extends View {

	private EditorActivity mContext;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private final Rect mRect = new Rect();
	private Paint mPaint;
	private int mCurWidth;
	
	private ToolController activeController;
    private List<ToolController> mTools;
    private MODE editorMode;
    
	public CanvasView(Context c) {
		super(c);
		mContext = (EditorActivity) c;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 0, 0, 0);
		
        this.mTools = new ArrayList<ToolController>();
        initializeTools();

        // Set mode to FREEHAND by default
        this.setMode(MODE.FREEHAND);
	}
	
    private void initializeTools() {
        mTools.add(MODE.FREEHAND.ordinal(), new FreehandController(mContext,this));
        mTools.add(MODE.ERASE.ordinal(), new EraseController(mContext,this));
        mTools.add(MODE.LINE.ordinal(), new LineController(mContext,this));
        mTools.add(MODE.RECTANGLE.ordinal(), new RectangleController(mContext,this));
    }

    public void execute(CWPMessage message) {
    	
        String[] tokens = message.getArguments();
        String cmd = message.getAction();

        MODE action = MODE.FREEHAND;

        if (cmd.equals("freehand"))
            action = MODE.FREEHAND;

        else if (cmd.equals("erase"))
            action = MODE.ERASE;

        else if (cmd.equals("drawline"))
            action = MODE.LINE;
        
        else if (cmd.equals("drawrect"))
            action = MODE.RECTANGLE;
        
        mTools.get(action.ordinal()).paint(tokens);
    }
    
    public void setMode(MODE m) {

        editorMode = m;
        activeController = mTools.get(m.ordinal());
    }

	public void fillWithWhite() {
		if (mCanvas != null) {
			Paint white = new Paint();
			white.setARGB(0xff, 0xff, 0xff, 0xff);
			mCanvas.drawPaint(white);
			invalidate();
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

	    int desiredWidth = 800;
	    int desiredHeight = 600;

	    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	    int width;
	    int height;

	    //Measure Width
	    if (widthMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        width = widthSize;
	    } else if (widthMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        width = Math.min(desiredWidth, widthSize);
	    } else {
	        //Be whatever you want
	        width = desiredWidth;
	    }

	    //Measure Height
	    if (heightMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        height = heightSize;
	    } else if (heightMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        height = Math.min(desiredHeight, heightSize);
	    } else {
	        //Be whatever you want
	        height = desiredHeight;
	    }

	    //MUST CALL THIS
	    setMeasuredDimension(width, height);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		int curW = mBitmap != null ? mBitmap.getWidth() : 0;
		int curH = mBitmap != null ? mBitmap.getHeight() : 0;
		if (curW >= w && curH >= h) {
			return;
		}

		if (curW < w)
			curW = w;
		if (curH < h)
			curH = h;

		Bitmap newBitmap = Bitmap.createBitmap(curW, curH,
				Bitmap.Config.RGB_565);
		Canvas newCanvas = new Canvas();
		newCanvas.setBitmap(newBitmap);
		if (mBitmap != null) {
			newCanvas.drawBitmap(mBitmap, 0, 0, null);
		}
		mBitmap = newBitmap;
		mCanvas = newCanvas;

		// Changeboard to itself, and reload it
		fillWithWhite();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		activeController.onTouchEvent(event);
		Log.d("Canvas",activeController.toString());
		return true;
	}

	private void drawLine(int x1, int y1, int x2, int y2, Paint paint) {
		mCanvas.drawLine(x1, y1, x2, y2, paint);
		postInvalidate();
	}

	public void updateModelView(String message) {
		CWPMessage msg = new CWPMessage(message);

		int x1 = Integer.valueOf(msg.getArgument(0));
		int y1 = Integer.valueOf(msg.getArgument(1));
		int x2 = Integer.valueOf(msg.getArgument(2));
		int y2 = Integer.valueOf(msg.getArgument(3));

		int color = Integer.valueOf(msg.getArgument(4));
		int brushSize = Integer.valueOf(msg.getArgument(5));
		Paint p = new Paint();
		p.setColor(color);
		p.setStrokeWidth(brushSize);

		drawLine(x1, y1, x2, y2, p);
	}
	
	public Paint getPaint() {
		return mPaint;
	}
	
	public Canvas getCanvas() {
		return mCanvas;
	}
}