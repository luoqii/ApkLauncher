package org.bbs.apklauncher;

import org.bbs.apklauncher.api.ExportApi;
import org.bbs.apkparser.PackageInfoX;

import android.app.Activity;
import android.os.Bundle;
@ExportApi
public abstract class BaseLauncherActivity extends Activity {
	private static final String TAG = BaseLauncherActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		startLauncherActivity();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		
//		startLauncherActivity();
	}

	protected void startLauncherActivity() {
		PackageInfoX.ActivityInfoX a = ApkPackageManager.getInstance().getActivityInfo(getTargetActivityClassName());
		ApkLauncher.getInstance().startActivity(this, a);
		finish();
	}

	abstract protected String getTargetActivityClassName();
}
