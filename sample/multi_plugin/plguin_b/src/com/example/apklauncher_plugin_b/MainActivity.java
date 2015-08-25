package com.example.apklauncher_plugin_b;

import org.bbs.apklauncher.api.Base_Activity;

import android.os.Bundle;

import com.example.apklauncher_plguin_b.R;

public class MainActivity extends Base_Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	}
}
