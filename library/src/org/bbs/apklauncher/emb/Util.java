package org.bbs.apklauncher.emb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;
import org.bbs.apkparser.PackageInfoX.ServiceInfoX;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class Util {
	private static final String TAG = Util.class.getSimpleName();;

    public static final String ACTIVITY_EXTRA_COMPONENT_CLASS_NAME = "EXTRA_COMPONENT_CLASS_NAME";
    public static final String SERVICE_EXTRA_COMPONENT_CLASS_NAME = "EXTRA_COMPONENT_CLASS_NAME";
	
	// TODO
	public static final String toMemoryLevel(int level) {
		String levelStr = "";
		levelStr = level + "";
		
		return levelStr;
	}

	public static void onProcessStartActivityIntent(Intent intent, ClassLoader classLoader, Context realContext) {
		Log.d(TAG, "processIntent. intent: " + intent);
		ComponentName com = intent.getComponent();
		if (null != com) {
			String c = com.getClassName();
			if (!TextUtils.isEmpty(c)) {
				String superClassName = LoadedApk.getActivitySuperClassName(classLoader, c);
				com = new ComponentName(realContext.getPackageName(), superClassName.replace("Target", "Stub"));
                // inject and replace with our component.
				intent.setComponent(com);
				ActivityInfoX a = ApkPackageManager.getInstance().getActivityInfo(c);
				if (a != null) {
//					ApkLuncherActivity.putExtra(a, intent);
					intent.putExtra(Util.ACTIVITY_EXTRA_COMPONENT_CLASS_NAME, a.name);
				}
			} 
		} else {
			Log.w(TAG, "can not handle intent:  "  + intent);
		}
	}

	public static void onProcessStartServiceIntent(Intent intent, ClassLoader classLoader, Context realContext) {
		Log.d(TAG, "processIntent. intent: " + intent);
		ComponentName com = intent.getComponent();
		if (null != com) {
			String c = com.getClassName();
			if (!TextUtils.isEmpty(c)) {
				String superClassName = LoadedApk.getServiceSuperClassName(classLoader, c);
				com = new ComponentName(realContext.getPackageName(), superClassName.replace("Target", "Stub"));
                // inject and replace with our component.
				intent.setComponent(com);
				ServiceInfoX a = ApkPackageManager.getInstance().getServiceInfo(c);
				if (a != null) {
//					intent.putExtra(Stub_Service.EXTRA_COMPONENT, new ComponentName(a.packageName, a.name));
					intent.putExtra(Util.SERVICE_EXTRA_COMPONENT_CLASS_NAME, a.name);
				}
			} 
		} else {
			Log.w(TAG, "can not handle intent:  "  + intent);
		}
	}
	
	public static  Resources loadApkResource(String apkFilePath, Context context) {
		AssetManager assets = null;
		try {
			assets = AssetManager.class.getConstructor(null).newInstance(null);
			Method method = assets.getClass().getMethod("addAssetPath", new Class[]{String.class});
			Object r = method.invoke(assets, apkFilePath);
			DisplayMetrics metrics = new DisplayMetrics();
			((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
			// TODO add config & metrics
			Configuration config = context.getResources().getConfiguration();
//			metrics = context.getResources().getDisplayMetrics();
			Resources res = new Resources(assets, metrics, config);
			res.updateConfiguration(config, metrics);
			return res;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
    public static Context getContextImpl(Context context) {
        Context nextContext;
        while ((context instanceof ContextWrapper) &&
                (nextContext=((ContextWrapper)context).getBaseContext()) != null) {
            context = nextContext;
        }
        return (Context)context;
    }
	
}
