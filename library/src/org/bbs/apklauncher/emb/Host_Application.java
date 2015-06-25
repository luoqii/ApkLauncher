package org.bbs.apklauncher.emb;

import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.ApkUtil;
import org.bbs.apklauncher.PackageManagerProxy;
import org.bbs.apklauncher.ReflectUtil;
import org.bbs.apklauncher.ResourcesMerger;
import org.bbs.apklauncher.TargetContext;
import org.bbs.apklauncher.emb.IntentHelper.PersistentObject;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

public class Host_Application extends 
Application
{
	private static final String TAG = Host_Application.class.getSimpleName();;
	Application mTargetAppliction;
	private PersistentObject mPersistent;
	
	public /*static*/ Application onPrepareApplictionStub(ApplicationInfo appInfo, 
			ClassLoader classLoader, PackageManager pm) {
		String apkPath = appInfo.publicSourceDir;
		Application app = ApkPackageManager.getApplication(appInfo.packageName);
		if (null == app) {
			// init IntentHelper
			IntentHelper.PersistentObject.getsInstance().init(this, classLoader);
			
			String appClassName = appInfo.className;
			if (!TextUtils.isEmpty(appClassName)) {
				try {
	
					TargetContext appBaseContext = new TargetContext(this);
					Resources appRes = ApkPackageManager.makeTargetResource(apkPath, this);
					appRes = new ResourcesMerger(appRes, getResources());
					appBaseContext.resReady(appRes);
					int appTheme = appInfo.theme;
					if (appTheme  > 0) {
					} else {
					}
					appTheme = ReflectUtil.ResourceUtil.selectDefaultTheme(appRes, appTheme, appInfo.targetSdkVersion);
					Log.d(TAG, "resolved application theme: " + appTheme);
					appBaseContext.themeReady(appTheme);
	
					appBaseContext.packageManagerReady(new PackageManagerProxy(pm));
					appBaseContext.packageNameReady(appInfo.packageName);
	
					Class<?> clazz = classLoader.loadClass(appClassName);
	
					app = (Application) clazz.newInstance();
					ApkUtil.dumpClassType((app.getClass()));
					if (!(app instanceof Target_Application)) {
						throw new RuntimeException("youe application must extends " + Target_Application.class.getName());
					}
					((Target_Application)app).attachTargetClassLoader(classLoader);
					appBaseContext.applicationContextReady(app);
	
					attachBundleAplication(app, appBaseContext);
	
					ApkPackageManager.putApplication(appInfo.packageName, app);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in create application: " + appClassName , e);
				}
			} else {
				throw new RuntimeException("invalid appClassName: " + appClassName);
			}
		}
		
		return app;
	}

	public void attachBundleAplication(Application app, Context baseCcontext){
		ReflectUtil.ActivityReflectUtil.attachBaseContext(app, baseCcontext);
		mTargetAppliction = app;
		
		mTargetAppliction.onCreate();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}

	public void onTerminate() {
		if (null != mTargetAppliction) {
			mTargetAppliction.onTerminate();
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		if (null != mTargetAppliction) {
			mTargetAppliction.onConfigurationChanged(newConfig);;
		}
	}

	public void onLowMemory() {
		if (null != mTargetAppliction) {
			mTargetAppliction.onLowMemory();
		}
	}

	public void onTrimMemory(int level) {
		if (null != mTargetAppliction) {
			mTargetAppliction.onTrimMemory(level);
		}
	}
	
}
