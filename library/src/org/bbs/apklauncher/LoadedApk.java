package org.bbs.apklauncher;

import org.bbs.apkparser.PackageInfoX;

import android.app.Application;

public class LoadedApk {
	private static final String TAG = LoadedApk.class.getSimpleName();
	
	PackageInfoX mApkInfo;
	private String mDexCacheDir;

	private ClassLoader mClassLoader;

	public LoadedApk(Application appContext, PackageInfoX apkInfo){
		mApkInfo = apkInfo;
	}
	

}
