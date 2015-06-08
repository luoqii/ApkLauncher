package org.bbs.apklauncher;

import org.bbs.apklauncher.api.ExportApi;
import org.bbs.apkparser.PackageInfoX;

import android.app.Activity;
import android.os.Bundle;
@ExportApi
public class LauncherActivity extends Activity {
	private static final String TAG = LauncherActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startLauncherActivity();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		
//		startLauncherActivity();
	}

	private void startLauncherActivity() {
		PackageInfoX.ActivityInfoX a = ApkPackageManager.getInstance().getActivityInfo(getTargetActivityClassName());
		ApkUtil.startActivity(this, a);
		finish();
	}

	protected String getTargetActivityClassName() {
		return "com.example.apklauncher_app.MainActivity";
	}
}
