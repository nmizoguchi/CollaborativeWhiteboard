package edu.mit.collaborativewhiteboard;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Paint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import client.gui.canvas.Canvas.MODE;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;

import edu.mit.collaborativewhiteboard.ui.ToolAdapter;

public class ToolsMenu implements OnItemClickListener, OnItemLongClickListener {

	private ListView mDrawerList;
	private final EditorActivity mContext;

	public ToolsMenu(EditorActivity activity, int id) {

		mContext = activity;

		mDrawerList = (ListView) mContext.findViewById(id);

		// Set itself as a listener for all the actions in the menu
		mDrawerList.setOnItemClickListener(this);
		mDrawerList.setOnItemLongClickListener(this);

		String[] web = { "Erase", "Freehand", "Draw Line", "Draw Rect", "Brush Color", "Brush Size" };
		Integer[] imageId = { R.drawable.eraser, R.drawable.brush,
				R.drawable.line, R.drawable.rectangle, R.drawable.color, R.drawable.brushsize };

		ToolAdapter adapter = new
                ToolAdapter(mContext, web, imageId);
		
		// Set the adapter for the list view
		mDrawerList.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int id, long arg3) {
		switch (id) {
		case 0:
			mContext.getCanvasView().setMode(MODE.ERASE);
			break;
		case 1:
			mContext.getCanvasView().setMode(MODE.FREEHAND);
			break;
		case 2:
			mContext.getCanvasView().setMode(MODE.LINE);
			break;
		case 3:
			mContext.getCanvasView().setMode(MODE.RECTANGLE);
			break;
		case 4:
			Dialog dialog = new Dialog(mContext);
			
			View main = ((Activity)mContext).getLayoutInflater().inflate(R.layout.color_picker,null);
			dialog.setContentView(main);
			dialog.setTitle("Choose a Color");
			dialog.show();
			
			ColorPicker picker = (ColorPicker) main.findViewById(R.id.picker);
			picker.setOldCenterColor(mContext.getCanvasView().getPaint().getColor());
			picker.setOnColorChangedListener(new OnColorChangedListener() {
				
				@Override
				public void onColorChanged(int color) {
					Paint p = mContext.getCanvasView().getPaint();
					p.setColor(color);
				}
			});
			break;
		case 5:
			Dialog dialog2 = new Dialog(mContext);
			View brushsize = ((Activity)mContext).getLayoutInflater().inflate(R.layout.brushsize_picker,null);
			
			dialog2.setContentView(brushsize);
			dialog2.setTitle("Choose a brush size");
			dialog2.show();
			
			final TextView text = (TextView) brushsize.findViewById(R.id.brushsize_text);
			
			text.setText("Brush size: "+mContext.getCanvasView().getPaint().getStrokeWidth());
			SeekBar sb = (SeekBar) brushsize.findViewById(R.id.brushsize_seekbar);
			sb.setProgress(((int)mContext.getCanvasView().getPaint().getStrokeWidth()));
			sb.setMax(200);
			sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					mContext.getCanvasView().getPaint().setStrokeWidth(progress);
					text.setText("Brush size: "+progress);
				}
			});
		}
		mContext.mDrawerLayout.closeDrawers();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int blockPosition, long arg3) {

		return true;
	}
}
