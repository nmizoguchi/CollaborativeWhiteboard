package edu.mit.collaborativewhiteboard;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import Protocol.CWPMessage;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import client.ClientListener;

import com.example.collaborativewhiteboard.R;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.

/**
 * Demonstrates the handling of touch screen and trackball events to implement a
 * simple painting app.
 */
public class EditorActivity extends GraphicsActivity implements ClientListener {

	/** Menu ID for the command to clear the window. */
	private static final int CLEAR_ID = Menu.FIRST;

	public MainApplication mApplication;
	DrawerLayout mDrawerLayout;
	ToolsMenu mMenu;
	private CanvasView mCanvasView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_editor);

		// Create and attach the view that is responsible for painting.
		mCanvasView = new CanvasView(this);
		mCanvasView.requestFocus();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.editor_drawer_layout);

		RelativeLayout mMainLayout = (RelativeLayout) findViewById(R.id.editor_canvas);

		mMainLayout.addView(mCanvasView);

		mMenu = new ToolsMenu(this, R.id.editor_tools);

		mApplication = (MainApplication) getApplication();
		mApplication.getClient().initialize(this,
				mApplication.getUser().getName());
	}

	public CanvasView getCanvasView() {
		return mCanvasView;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, CLEAR_ID, 0, "Clear");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CLEAR_ID:
			mCanvasView.fillWithWhite();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onNewuserMessageReceived(CWPMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnecteduserMessageReceived(CWPMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWhiteboardsMessageReceived(CWPMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChangeboardMessageReceived(CWPMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChatMessageReceived(CWPMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPaintMessageReceived(CWPMessage message) {
		mCanvasView.execute(message);
	}

	@Override
	public void onInvalidMessageReceived(CWPMessage message) {
		// TODO Auto-generated method stub

	}
}
