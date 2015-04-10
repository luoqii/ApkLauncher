package com.example.apklauncher_app_intnet_helper;

import org.bbs.apklauncher.emb.IntentHelper;

import android.app.Application;
import android.util.Log;

public class App extends Application {
	private static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		
		IntentHelper.PersistentObject.getsInstance().init(this, getClassLoader());
	}
}
