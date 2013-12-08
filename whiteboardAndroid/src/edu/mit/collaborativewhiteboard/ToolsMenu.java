package edu.mit.collaborativewhiteboard;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import client.gui.canvas.Canvas.MODE;

public class ToolsMenu implements OnItemClickListener, OnItemLongClickListener {

	private ListView mDrawerList;
	private EditorActivity mContext;

	public ToolsMenu(EditorActivity activity, int id) {

		mContext = activity;

		mDrawerList = (ListView) mContext.findViewById(id);

		// Set itself as a listener for all the actions in the menu
		mDrawerList.setOnItemClickListener(this);
		mDrawerList.setOnItemLongClickListener(this);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(mContext,
				android.R.layout.simple_list_item_1, new String[] { "Erase",
						"Freehand", "Draw Line", "Draw Rect" }));
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
		}
		Log.d("DRAWER", "Changed to mode "+id);
		mContext.mDrawerLayout.closeDrawers();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int blockPosition, long arg3) {

		return true;
	}
}
