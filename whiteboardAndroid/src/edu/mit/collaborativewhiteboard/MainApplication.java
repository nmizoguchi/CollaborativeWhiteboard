package edu.mit.collaborativewhiteboard;

import shared.models.User;
import android.app.Application;
import android.content.res.Configuration;
import client.ClientApplication;

public class MainApplication extends Application {
	
	private static MainApplication singleton;
	private ClientApplication mClient;
	private final User mUser = new User("Android");


	public MainApplication getInstance() {
		return singleton;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public ClientApplication getClient() {
		return mClient;
	}

	public void setClient(ClientApplication mClient) {
		this.mClient = mClient;
	}

	public User getUser() {
		return mUser;
	}
}
