package com.example.apklauncher_app_intnet_helper;

import org.bbs.apklauncher.emb.IntentHelper;
import org.bbs.apklauncher.emb.Target_Application;

public class App extends Target_Application {
	private static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		
//		IntentHelper.PersistentObject.getsInstance().init(this, getClassLoader());
	}
}
