package org.bbs.apklauncher;

import org.bbs.apkparser.PackageInfoX.ActivityInfoX;

import android.content.Context;
import android.content.Intent;

public class TargetClassLoaderCreator {
	private static Callback sCallback;

	public static ClassLoader createTargetClassLoader(Context hostBaseContext, Intent intent) {
		if (null != sCallback){
			return sCallback.onCreateTargetClassLoader(hostBaseContext, intent);
		}

		String targetActivityClassName = intent.getStringExtra(ApkLauncher.EXTRA_TARGET_COMPONENT_CLASS_NAME);
		ActivityInfoX actInfo = ApkPackageManager.getInstance()
												.getActivityInfo(targetActivityClassName);
		return ApkPackageManager.getInstance()
				.createClassLoader(hostBaseContext, 
						actInfo.applicationInfo.publicSourceDir, 
						actInfo.mPackageInfo.applicationInfo.nativeLibraryDir, 
						actInfo.packageName);
	}
	
	public static void setCallBack(Callback callback) {
		sCallback = callback;
	}
	
	public static interface Callback {
		public ClassLoader onCreateTargetClassLoader(Context hostBaseContext, Intent intent);
	}
}
