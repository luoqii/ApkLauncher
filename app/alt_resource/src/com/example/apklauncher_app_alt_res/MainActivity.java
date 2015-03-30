package com.example.apklauncher_app_alt_res;

import org.bbs.apklauncher.emb.auto_gen.Target_Activity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Target_Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		int dimen = getResources().getDimensionPixelSize(R.dimen.alt_dimen);
		Log.d(TAG, "dimen: " + dimen);
	}
}
