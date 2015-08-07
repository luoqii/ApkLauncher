package org.bbs.apklauncher.demo;

import java.io.File;
import java.lang.reflect.Field;

import org.bbs.android.commonlib.ExceptionCatcher;
import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkLauncherConfig;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.ApkUtil;
import org.bbs.apklauncher.InstrumentationWrapper;
import org.bbs.apklauncher.LogClassLoader;
import org.bbs.apklauncher.ReflectUtil;
import org.bbs.apklauncher.ResourcesMerger;
import org.bbs.apklauncher.TargetContext;
import org.bbs.apklauncher.TargetInstrumentation;
import org.bbs.apklauncher.TargetInstrumentation.CallBack;
import org.bbs.apklauncher.emb.Host_Application;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;

public class App extends 
//Application
Host_Application 
{

//	private static final String TARGET_PKG_NAME = "com.youku.tv";
//	public static final String TARGET_LAUNCHER_NAME = "com.youku.tv.WelcomeActivity";
	private static final String TARGET_PKG_NAME = "com.cibn.tv.debug";
	public static final String TARGET_LAUNCHER_NAME = "com.cibn.tv.WelcomeActivity";
//	private static final String TARGET_PKG_NAME = "com.example.apklauncher_app_intent_helper";
//	public static final String TARGET_LAUNCHER_NAME = "com.example.apklauncher_app_intnet_helper.MainActivity";
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
		
		File apkDir = null;
//		apkDir = getDir(APK_LAUNCHER_DIR, 0);
		apkDir = new File(Environment.getExternalStorageDirectory(), "apk");

		ApkLauncherConfig.setDebug(true);
		ApkLauncher apk = ApkLauncher.getInstance();
		apk.init(this, "plugin", true);
//		ApkPackageManager.getInstance().scanApkDir(apkDir);
		TargetContext.ENBABLE_FILE = false;
		ApkLauncherConfig.ENALBE_SERVICE = false;
		
		injectInstrumentation(this);
	}
	
	public void injectInstrumentation(final Application app){
		try {
			// inject ClassLoader
			Class contextImplClass = Class.forName("android.app.ContextImpl");
			Object contextImpl = AndroidUtil.getContextImpl(app);
			Field packageInfoF = contextImplClass.getDeclaredField("mPackageInfo");
			packageInfoF.setAccessible(true);
			Object packageInfo = packageInfoF.get(contextImpl);
			Field classloaderF = packageInfo.getClass().getDeclaredField("mClassLoader");
			classloaderF.setAccessible(true);
			ClassLoader cl = new LogClassLoader(app.getClassLoader());
			ApkPackageManager apk = ApkPackageManager.getInstance();
			cl = apk.createClassLoader(app, apk.getPackageInfo(TARGET_PKG_NAME));
//			c = new TargetFirstClassLoader(dexPath, optimizedDirectory, libraryPath, parent, targetPackageName, hostContext)
//			cl = new LogClassLoader(cl);
			classloaderF.set(packageInfo, cl);
			
			// inject Intrumentation
			Field activityThreadF = contextImplClass.getDeclaredField("mMainThread");
			activityThreadF.setAccessible(true);
			Object activityThreadObject = activityThreadF.get(contextImpl);
			Field instruF = activityThreadObject.getClass().getDeclaredField("mInstrumentation");
			instruF.setAccessible(true);
			Object instru = instruF.get(activityThreadObject);
			Instrumentation ins = new ApkInstrumentation(app, (Instrumentation) instru, cl);
			ins = new TargetInstrumentation(ins, new Handler());
			((TargetInstrumentation)ins).setCallBack(new CallBack() {
				
				@Override
				public void onProcessIntent(Intent intent) {
					ComponentName com = intent.getComponent();
					if (null != com) {
						String pkgName = app.getPackageName();
						intent.setComponent(new ComponentName(pkgName, com.getClassName()));
					}
				}
			});
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
	
	static class ApkInstrumentation extends InstrumentationWrapper {

		private static final String CLASS_NAME_CONTEXT_THEME_WRAPPER = "android.view.ContextThemeWrapper";
		private ClassLoader mLoader;
		private Application mApp;
		private Application mTargetApp;

		public ApkInstrumentation(Application app, Instrumentation base, ClassLoader apkClassLoader) {
			super(base);
			mApp = app;
			mLoader = apkClassLoader;
		}
		
		@Override
		public Activity newActivity(ClassLoader cl, String className,
				Intent intent) throws InstantiationException,
				IllegalAccessException, ClassNotFoundException {
			if (shouldIgnore(className)) {
				return super.newActivity(cl, className, intent);
			}
			
			ClassLoader targetClassLoader = mLoader;
			
			// try init app first.
			ApkPackageManager apk = ApkPackageManager.getInstance();
			ActivityInfoX info = apk.getActivityInfo(className);
			PackageManager pm = mApp.getPackageManager();
			mTargetApp = ((Host_Application)mApp).onPrepareApplictionStub(info.applicationInfo, targetClassLoader, pm, false);
//			injectBaseContext(mTargetApp);
			
			Activity a =  super.newActivity(targetClassLoader, className, intent);
			
			ResourcesMerger r = ApkPackageManager.getTargetResource(info.applicationInfo.publicSourceDir, mApp);
			
			try {
				// inject resource
				Field resF = Class.forName(CLASS_NAME_CONTEXT_THEME_WRAPPER).getDeclaredField("mResources");
				resF.setAccessible(true);
				resF.set(a, r);
				
				// inject theme
				int targetThemeId = ReflectUtil.ResourceUtil
						.selectDefaultTheme(r, 
								info.theme, 
								info.applicationInfo.targetSdkVersion);
				Log.d(TAG, "resolved activity theme: " + targetThemeId);
				Theme t = r.getFirst().newTheme();
				t.applyStyle(targetThemeId, true);
				Field themeF = Class.forName(CLASS_NAME_CONTEXT_THEME_WRAPPER).getDeclaredField("mTheme");
				themeF.setAccessible(true);
				themeF.set(a, t);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return a;
		}

		private boolean shouldIgnore(String className) {
			return ApkInitActivity.class.getName().equals(className)
					||BackUpActivity.class.getName().equals(className)
					;
		}
		
		@Override
		public void callActivityOnCreate(Activity activity, Bundle icicle) {
			injectBaseContext(activity);
			super.callActivityOnCreate(activity, icicle);
			updateTitle(activity);
		}
		
		@Override
		public void callActivityOnCreate(Activity activity, Bundle icicle,
				PersistableBundle persistentState) {
			injectBaseContext(activity);
			super.callActivityOnCreate(activity, icicle, persistentState);
			updateTitle(activity);
		}
		
		private void injectBaseContext(Object object) {
			if (shouldIgnore(object.getClass().getName())) {
				return;
			}
			
			try {
				Field baseF = Class.forName("android.content.ContextWrapper").getDeclaredField("mBase");
				baseF.setAccessible(true);
				TargetContext injectContext = new TargetContext((Context) baseF.get(object));
				injectContext.packageNameReady(TARGET_PKG_NAME);
				injectContext.applicationContextReady(mTargetApp);
				baseF.set(object, injectContext);

				// inject application
				ReflectUtil.ActivityReflectUtil.setActivityApplication(object, mTargetApp);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		void updateTitle(Activity activity){
			if (shouldIgnore(activity.getClass().getName())) {
				return;
			}
			ApkUtil.updateTitle(activity);
		}
		
	}
	
}
