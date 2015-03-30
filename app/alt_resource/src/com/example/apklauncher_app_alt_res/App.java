package com.example.apklauncher_app_alt_res;

import android.app.Application;
import android.util.Log;

public class App extends Application {
	private static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG, "congfig: " + getResources().getConfiguration());
		
		int dimen = getResources().getDimensionPixelSize(R.dimen.alt_dimen);
		Log.d(TAG, "dimen: " + dimen);
	}
}
