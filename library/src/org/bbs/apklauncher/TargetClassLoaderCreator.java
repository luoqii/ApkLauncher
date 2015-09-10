package org.bbs.apklauncher;

import org.bbs.apkparser.PackageInfoX.ActivityInfoX;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

public class TargetClassLoaderCreator {
	private static Factory sFactory;

	public static ClassLoader createTargetClassLoader(Context hostBaseContext, Intent intent) {
		if (null != sFactory){
			ClassLoader c = sFactory.onCreateTargetClassLoader(hostBaseContext, intent);
			if (null != c){
				return c;
			}
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
	
	public static Resources createTargetResources(Context hostBaseContext, Intent intent) {
		if (null != sFactory){
			Resources res = sFactory.onCreateTargetResources(hostBaseContext, intent);
			if (null != res){
				return res;
			}
		}

		String targetActivityClassName = intent.getStringExtra(ApkLauncher.EXTRA_TARGET_COMPONENT_CLASS_NAME);
		ActivityInfoX actInfo = ApkPackageManager.getInstance()
												.getActivityInfo(targetActivityClassName);
		String apkPath = actInfo.applicationInfo.publicSourceDir;
		return ApkPackageManager.getTargetResource(apkPath, hostBaseContext);
	}
	
	public static void setFactory(Factory callback) {
		sFactory = callback;
	}
	
	public static interface Factory {
		public ClassLoader onCreateTargetClassLoader(Context hostBaseContext, Intent intent);
		public Resources onCreateTargetResources(Context hostBaseContext, Intent intent);
	}
}
