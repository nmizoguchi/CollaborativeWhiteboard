package edu.mit.collaborativewhiteboard;

import java.io.IOException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import client.ClientApplication;

import com.example.collaborativewhiteboard.R;

public class MainActivity extends Activity {
	
	MainApplication mApplication;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApplication = (MainApplication) getApplication();
		intent = new Intent(this, EditorActivity.class);

		setContentView(R.layout.activity_main);

		/* Initialize Title! */
		// Font path
		String fontPath = "fonts/Roboto-LightItalic.ttf";
		// text view label
		TextView txtGhost = (TextView) findViewById(R.id.title);
		// Loading Font Face
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		// Applying font
		txtGhost.setTypeface(tf);
		
		Button connectButton = (Button) findViewById(R.id.button_connect);
		connectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText usernameText = (EditText) findViewById(R.id.username);
				EditText addressText = (EditText) findViewById(R.id.server_address);
				EditText portText = (EditText) findViewById(R.id.server_port);
				
				String username = usernameText.getText().toString();
				String address = addressText.getText().toString();
				int port = Integer.valueOf(portText.getText().toString());
				
				initializeClient(address,port,username);
			}
		});
		
	}
	
	public void  initializeClient(String ipAddress, int port, String username) {
		
		mApplication.getUser().setName(username);
		
		Thread initializeClient = new Thread(new InitializeClient(ipAddress, port));
		initializeClient.start();
	}

	public class InitializeClient implements Runnable {

		final String ipAddress;
		final int port;
		
		public InitializeClient(String ipAddress, int port) {
			this.ipAddress = ipAddress;
			this.port = port;
		}
		
		@Override
		public void run() {
			try {
				mApplication.setClient(new ClientApplication(ipAddress,
						port));
				startActivity(intent);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
