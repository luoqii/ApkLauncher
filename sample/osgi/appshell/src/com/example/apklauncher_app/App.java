package com.example.apklauncher_app;

import java.io.File;
import java.util.List;

import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkLauncher.OnProcessIntent;
import org.bbs.apklauncher.ApkLauncher.TKey;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.TargetClassLoaderCreator;
import org.bbs.apklauncher.TargetClassLoaderCreator.Factory;
import org.bbs.apklauncher.emb.Host_Application;
import org.bbs.apklauncher.osgi.bundlemanager.FrameworkHelper;
import org.bbs.apklauncher.osgi.bundlemanager.OsgiUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.service.startlevel.StartLevel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class App extends 
//Application
Host_Application  {
	private static final String TAG = App.class.getSimpleName();
	protected static final String EXTRA_BUNDLE_ID = TAG + ".EXTRA_BUNDLE_ID";
	private Framework mFrameWork;

	@Override
	public void onCreate() {
		super.onCreate();
		
//		ApkLauncher.getInstance().setT2HMap(TKey.ACTIVITY, MyStub.class.getName());;
		ApkPackageManager.getInstance().init(this, "non-exist", false);
		mFrameWork = FrameworkHelper.getInstance(getApplicationContext()).getFramework();

		AndroidUtil.extractAssetFile(getAssets(), "auto_extracted_2_sdcard", new File(Environment.getExternalStorageDirectory(), "bundle"));
		File autoInstallDir = getDir("bundle", MODE_PRIVATE);
//		autoInstallDir = Environment.getExternalStorageDirectory();
		AndroidUtil.extractAssetFile(getAssets(), "auto_install_bundle", autoInstallDir);
		for (String f : autoInstallDir.list()){
			try {
				mFrameWork.getBundleContext().installBundle("file://" + autoInstallDir.getPath() + "/" + f).start();
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
				List<ResolveInfo> acts = ApkPackageManager.getInstance().queryIntentActivities(intent, 0);
				if (acts.size() > 0) {
					Log.i(TAG, "intent matchs a installed plugin.");
					ActivityInfo aInfo = acts.get(0).activityInfo;
					// may be we need a new classloader.
					//						targetClassLoader = ApkPackageManager.getInstance().createClassLoader(hostContext, ((ActivityInfoX)aInfo).mPackageInfo);
					String location = "file://" + aInfo.applicationInfo.publicSourceDir;
					Bundle b = FrameworkHelper.getInstance(null).getFramework().getBundleContext().getBundle(location);
					targetClassLoader = OsgiUtil.getBundleClassLoader(b);
					intent.putExtra(EXTRA_BUNDLE_ID, b.getBundleId());
					ApkLauncher.getInstance().prepareIntent(intent, targetClassLoader, hostContext, aInfo.name);
				} else {
					Log.w(TAG, "can not handle intent:  "  + intent);
				}		
				return true;
			}
		});
		
		TargetClassLoaderCreator.setFactory(new Factory() {
			
			@Override
			public ClassLoader onCreateTargetClassLoader(Context hostBaseContext,
					Intent intent) {
				long bundleId = intent.getLongExtra(EXTRA_BUNDLE_ID, -1);
				Bundle targetBundle = FrameworkHelper.getInstance(null).getFramework().getBundleContext().getBundle(bundleId);
				ClassLoader classloader = OsgiUtil.getBundleClassLoader(targetBundle);
				Log.d(TAG, "targetClassloader: " + classloader);				
				return classloader;
			}

			@Override
			public Resources onCreateTargetResources(Context hostBaseContext,
					Intent intent) {
				long bundleId = intent.getLongExtra(EXTRA_BUNDLE_ID, -1);
				Bundle targetBundle = FrameworkHelper.getInstance(null).getFramework().getBundleContext().getBundle(bundleId);
				String name = targetBundle.getHeaders().get(FrameworkHelper.HEADER_REQUIRED_RESOURCE_BUNDLE);
				if (!TextUtils.isEmpty(name)){
					Bundle b = OsgiUtil.getBundleBySymblicName(targetBundle.getBundleContext(), name);
				}
				return null;
			}
		});
		
		setStartLevel();
	}
	
	void setStartLevel (){
		StartLevel sl = (StartLevel) mFrameWork.getBundleContext().getService(mFrameWork.getBundleContext().getServiceReference(StartLevel.class));
		sl.setBundleStartLevel(mFrameWork.getBundleContext().getBundle("file:///data/data/com.example.apklauncher_osgi/app_bundle/Youku_TV.apk"), 10);
		sl.setBundleStartLevel(mFrameWork.getBundleContext().getBundle("file:///data/data/com.example.apklauncher_osgi/app_bundle/Youku_TV_serach_plguin.apk"), 10 + 1);
		sl.setStartLevel(100);
	}
}
