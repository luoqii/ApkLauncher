package com.example.apklauncher_app_intnet_helper;

import org.bbs.apklauncher.api.Base_Application;
import org.bbs.apklauncher.emb.IntentHelper;


public class App extends Base_Application {
	private static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		
		IntentHelper.PersistentObject.getsInstance().init(this, getClassLoader());
	}
}
