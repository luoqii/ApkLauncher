package com.example.apklauncher_app;

import org.bbs.apklauncher.api.Base_Activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.example.apklauncher_zero_install.R;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateConfig;

public class MainActivity extends Base_Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		String versionInfo = "";
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionInfo = "code: " + pInfo.versionCode + "\nname: " + pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		((TextView)findViewById(R.id.text1)).setText(versionInfo);
		
		UmengUpdateAgent.setDeltaUpdate(false);
		UpdateConfig.setDebug(true);
		UmengUpdateAgent.update(this);
	}
}
