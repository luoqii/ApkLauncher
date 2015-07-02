package org.bbs.apklauncher.demo;

import java.io.File;
import java.lang.reflect.Field;

import org.bbs.android.commonlib.ExceptionCatcher;
import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkLauncherConfig;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.InstrumentationWrapper;
import org.bbs.apklauncher.LogClassLoader;
import org.bbs.apklauncher.ResourcesMerger;
import org.bbs.apklauncher.emb.Host_Application;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;

public class App extends 
//Application
Host_Application 
{
	
	private static final String TARGET_PKG_NAME = "com.example.apklauncher_app_intent_helper";
	public static final String TARGET_LAUNCHER_NAME = "com.example.apklauncher_app_intnet_helper.MainActivity";
//	private static final String TARGET_PKG_NAME = "com.example.android.apis";
//	public static final String TARGET_LAUNCHER_NAME = "com.example.android.apis.ApiDemos";
//	private static final String TARGET_PKG_NAME = "com.example.apklauncher_zero_install";
//	public static final String TARGET_LAUNCHER_NAME = "com.example.apklauncher_app.MainActivity";
	private static final String TAG = App.class.getSimpleName();
	public static final String APK_LAUNCHER_DIR = "apklauncher";
	
	public App(){
		super();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ExceptionCatcher.attachExceptionHandler(this);
		
		File apkDir = getDir(APK_LAUNCHER_DIR, 0);
		apkDir = new File(Environment.getExternalStorageDirectory(), "apk");

		ApkLauncherConfig.setDebug(true);
		ApkLauncher apk = ApkLauncher.getInstance();
		apk.init(this, "plugin", true);
//		ApkPackageManager.getInstance().scanApkDir(apkDir);
		
		injectInstrumentation(this);
	}
	
	public void injectInstrumentation(Application app){
		try {
			Class contextImplClass = Class.forName("android.app.ContextImpl");
			Object contextImpl = AndroidUtil.getContextImpl(app);
			Field packageInfoF = contextImplClass.getDeclaredField("mPackageInfo");
			packageInfoF.setAccessible(true);
			Object packageInfo = packageInfoF.get(contextImpl);
			Field classloaderF = packageInfo.getClass().getDeclaredField("mClassLoader");
			classloaderF.setAccessible(true);
			Object classLoader = classloaderF.get(packageInfo);
			ClassLoader c = new LogClassLoader(app.getClassLoader());
			ApkPackageManager apk = ApkPackageManager.getInstance();
			c = apk.createClassLoader(app, apk.getPackageInfo(TARGET_PKG_NAME));
//			c = new TargetFirstClassLoader(dexPath, optimizedDirectory, libraryPath, parent, targetPackageName, hostContext)
			c = new LogClassLoader(c);
			classloaderF.set(packageInfo, c);
			
			Field activityThreadF = contextImplClass.getDeclaredField("mMainThread");
			activityThreadF.setAccessible(true);
			Object activityThreadObject = activityThreadF.get(contextImpl);
			Field instruF = activityThreadObject.getClass().getDeclaredField("mInstrumentation");
			instruF.setAccessible(true);
			Object instru = instruF.get(activityThreadObject);
			Instrumentation ins = new ApkInstrumentation(app, (Instrumentation) instru, c);
			instruF.set(activityThreadObject, ins);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class ApkInstrumentation extends InstrumentationWrapper {

		private ClassLoader mLoader;
		private Application mApp;

		public ApkInstrumentation(Application app, Instrumentation base, ClassLoader apkClassLoader) {
			super(base);
			mApp = app;
			mLoader = apkClassLoader;
		}
		
		@Override
		public Activity newActivity(ClassLoader cl, String className,
				Intent intent) throws InstantiationException,
				IllegalAccessException, ClassNotFoundException {
			if (ApkInitActivity.class.getName().equals(className)) {
				return super.newActivity(cl, className, intent);
			}
			
			cl = mLoader;
			ApkPackageManager apk = ApkPackageManager.getInstance();
			ActivityInfoX info = apk.getLauncherActivityInfo(TARGET_PKG_NAME).get(0);
			PackageManager pm = mApp.getPackageManager();
			onPrepareApplictionStub(info.applicationInfo, cl, pm, false);
			
			Activity a =  super.newActivity(cl, className, intent);

			ResourcesMerger r = ApkPackageManager.getTargetResource(info.applicationInfo.publicSourceDir, mApp);
			
			try {
				Field resF = Class.forName("android.view.ContextThemeWrapper").getDeclaredField("mResources");
				resF.setAccessible(true);
				resF.set(a, r);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return a;
		}
		
		
		
	}
	
}
