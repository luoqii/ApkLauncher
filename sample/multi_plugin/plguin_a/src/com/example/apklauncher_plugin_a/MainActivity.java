package com.example.apklauncher_plugin_a;

import org.bbs.apklauncher.api.Base_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.apklauncher_plugin_a.R;

public class MainActivity extends Base_Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	}
	
	public void onClick(View view){
		Intent t = new Intent("org.apklauncher.action.PLUBIN_B");
		startActivity(t);
	}
}
