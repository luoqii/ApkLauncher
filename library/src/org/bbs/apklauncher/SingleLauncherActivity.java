package org.bbs.apklauncher;

import org.bbs.apklauncher.api.ExportApi;
import org.bbs.apkparser.PackageInfoX;

import android.app.Activity;
import android.os.Bundle;
@ExportApi
public class SingleLauncherActivity extends BaseLauncherActivity {
	private static final String TAG = SingleLauncherActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startLauncherActivity();
	}

	protected String getTargetActivityClassName(){
		return ApkPackageManager.getInstance().getLauncherActivityInfo().get(0).name;
	}
}
