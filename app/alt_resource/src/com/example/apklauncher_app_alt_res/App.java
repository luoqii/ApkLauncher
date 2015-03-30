package com.example.apklauncher_app_alt_res;

import android.app.Application;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class App extends Application {
	private static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG, "congfig: " + getResources().getConfiguration());
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		Log.d(TAG, "dm: " + dm);
		
		int dimen = getResources().getDimensionPixelSize(R.dimen.alt_dimen);
		Log.d(TAG, "dimen: " + dimen);
	}
}
