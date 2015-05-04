package org.bbs.apklauncher;

import org.bbs.apkparser.PackageInfoX;

import android.app.Activity;
import android.os.Bundle;

public class LauncherActivity extends Activity {
	private static final String TAG = LauncherActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		
		PackageInfoX.ActivityInfoX a = ApkPackageManager.getInstance().getActivityInfo(getTargetActivityClassName());
		ApkUtil.startActivity(this, a);
		finish();
	}

	protected String getTargetActivityClassName() {
		return "com.example.apklauncher_app.MainActivity";
	}
}
