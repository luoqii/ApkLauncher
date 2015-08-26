package com.example.apklauncher_plugin_b;

import org.bbs.apklauncher.api.Base_Activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.example.apklauncher_plguin_b.R;

public class MainActivity extends Base_Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		((TextView)findViewById(R.id.text1)).append(getVersion());
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
}
