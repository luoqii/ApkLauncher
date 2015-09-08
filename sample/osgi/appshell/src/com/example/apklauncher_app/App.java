package com.example.apklauncher_app;

import java.io.File;
import java.util.List;

import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkLauncher.OnProcessIntent;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.ApkLauncher.TKey;
import org.bbs.apklauncher.emb.Host_Application;
import org.bbs.apklauncher.osgi.bundlemanager.FrameworkHelper;
import org.bbs.apklauncher.osgi.bundlemanager.OsgiUtil;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class App extends 
//Application
Host_Application  {
	private static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		
		ApkLauncher.getInstance().setT2HMap(TKey.ACTIVITY, MyStub.class.getName());;
		ApkPackageManager.getInstance().init(this, "non-exist", false);
		Framework fm = FrameworkHelper.getInstance(getApplicationContext()).getFramework();

		AndroidUtil.extractAssetFile(getAssets(), "auto_extracted_2_sdcard", new File(Environment.getExternalStorageDirectory(), "bundle"));
		File autoInstallDir = getDir("bundle", MODE_PRIVATE);
//		autoInstallDir = Environment.getExternalStorageDirectory();
		AndroidUtil.extractAssetFile(getAssets(), "auto_install_bundle", autoInstallDir);
		for (String f : autoInstallDir.list()){
			try {
				fm.getBundleContext().installBundle("file://" + autoInstallDir.getPath() + "/" + f).start();
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ApkLauncher.getInstance().setOnProcessIntentCallback(new OnProcessIntent() {
			
			@Override
			public boolean onProcessStartActivityIntent(Intent intent,
					ClassLoader targetClassLoader, Context hostContext) {
				Log.i(TAG, "processIntent. intent: " + intent);
				ComponentName com = intent.getComponent();
				if (null != com) {
					String className = com.getClassName();
					if (!TextUtils.isEmpty(className)) {
						ApkLauncher.getInstance().prepareIntent(intent, targetClassLoader, hostContext, className);
					}
				} else {
					// try within installed plugins.
					List<ResolveInfo> acts = ApkPackageManager.getInstance().queryIntentActivities(intent, 0);
					if (acts.size() > 0) {
						Log.i(TAG, "intent matchs a installed plugin.");
						ActivityInfo aInfo = acts.get(0).activityInfo;
						// may be we need a new classloader.
//						targetClassLoader = ApkPackageManager.getInstance().createClassLoader(hostContext, ((ActivityInfoX)aInfo).mPackageInfo);
						String location = "file://" + aInfo.applicationInfo.publicSourceDir;
						Bundle b = FrameworkHelper.getInstance(null).getFramework().getBundleContext().getBundle(location);
						MyStub.mBundleId = b.getBundleId();
						targetClassLoader = OsgiUtil.getBundleClassLoader(b);
						ApkLauncher.getInstance().prepareIntent(intent, targetClassLoader, hostContext, aInfo.name);
					} else {
						Log.w(TAG, "can not handle intent:  "  + intent);
					}
				}
				
				return true;
			}
		});
	}
}
