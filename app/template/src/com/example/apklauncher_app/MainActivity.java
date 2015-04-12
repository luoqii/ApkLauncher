package com.example.apklauncher_app;

import org.bbs.apklauncher.api.Base_Activity;

import android.os.Bundle;

import com.example.apklauncher_replace_with_new_app_name.R;

public class MainActivity extends Base_Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	}
}
