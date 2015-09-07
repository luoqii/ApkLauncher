package com.example.apklauncher_plugin_a;

import org.bbs.apklauncher.api.Base_Activity;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Base_Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		((TextView)findViewById(R.id.text1)).append(getVersion());
		
		com.example.apklauncher.lib.Common.doSth();
	}
	
	private CharSequence getVersion() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			return "\nV" + info.versionName + "|" + info.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void onClick(View view){
		Intent t = new Intent("org.apklauncher.action.PLUBIN_B");
		startActivity(t);
	}
}
