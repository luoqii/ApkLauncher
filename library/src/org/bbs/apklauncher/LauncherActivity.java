package org.bbs.apklauncher;

import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.ApkUtil;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;

import android.app.Activity;
import android.os.Bundle;

public abstract class LauncherActivity extends Activity {
	private static final String TAG = LauncherActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ApkPackageManager am = ApkPackageManager.getInstance();
		ActivityInfoX aInfo = am.getActivityInfo(getActivityClassName());

		ApkUtil.startActvity(LauncherActivity.this, aInfo);
	}
	
	protected abstract String getActivityClassName() ;
}
