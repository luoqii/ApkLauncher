package org.bbs.apklauncher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bbs.apklauncher.emb.auto_gen.Target_ActionBarActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_Activity;
import org.bbs.apklauncher.emb.auto_gen.Target_ActivityGroup;
import org.bbs.apklauncher.emb.auto_gen.Target_ExpandableListActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_FragmentActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_ListActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_PreferenceActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_Service;
import org.bbs.apklauncher.emb.auto_gen.Target_TabActivity;
import org.bbs.apkparser.PackageInfoX;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.job.JobService;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.service.dreams.DreamService;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import dalvik.system.DexClassLoader;

public class LoadedApk {
	private static final String TAG = LoadedApk.class.getSimpleName();

	static HashMap<String, String> sActivitySuperClassNameMap = new HashMap<String, String>();
	static HashMap<String, String> sActivityClassMap = new HashMap<String, String>();
	static HashMap<String, String> sPreDefinedActivityClassMap = new HashMap<String, String>();
	static HashMap<String, String> sSuperClassNameMap = new HashMap<String, String>();
	
	PackageInfoX mApkInfo;
	private String mDexCacheDir;

	private ClassLoader mClassLoader;

	public LoadedApk(Application appContext, PackageInfoX apkInfo){
		mApkInfo = apkInfo;
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
	
	static String getHostActivityClassName(ClassLoader classloader, String targetActivityClassName){
		String cName = null;
		if (sPreDefinedActivityClassMap.containsKey(targetActivityClassName)){
			cName = sPreDefinedActivityClassMap.get(targetActivityClassName);
		}
		if (TextUtils.isEmpty(cName)) {
			
		}
		
		return cName;
	}
	
	public static String getActivitySuperClassName(ClassLoader classloader, String activityClassName) {
		String cName = null;
		if (sActivitySuperClassNameMap.containsKey(activityClassName)) {
			cName = sActivitySuperClassNameMap.get(activityClassName);
		}
		List<String> superClassNames = new ArrayList<String>();
		if (TextUtils.isEmpty(cName)) {
			try {
				Class<?> clazz = classloader.loadClass(activityClassName);
				superClassNames = getClassType(clazz);
				if (superClassNames.contains(Target_ActionBarActivity.class.getName())) {
					cName = Target_ActionBarActivity.class.getName();
				} else if (superClassNames.contains(Target_FragmentActivity.class.getName())) {
					cName = Target_FragmentActivity.class.getName();
				} else if (superClassNames.contains(Target_ExpandableListActivity.class.getName())) {
					cName = Target_ExpandableListActivity.class.getName();
				} else if (superClassNames.contains(Target_ListActivity.class.getName())) {
					cName = Target_ListActivity.class.getName();
				} else if (superClassNames.contains(Target_PreferenceActivity.class.getName())){
					cName = Target_PreferenceActivity.class.getName();
				} else if (superClassNames.contains(Target_TabActivity.class.getName())){
					cName = Target_TabActivity.class.getName();
				} else if (superClassNames.contains(Target_ActivityGroup.class.getName())){
					cName = Target_ActivityGroup.class.getName();
				} else if (superClassNames.contains(Target_Activity.class.getName())) {
					cName = Target_Activity.class.getName();
				} 
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		dumpClassType(superClassNames);
		if (!TextUtils.isEmpty(cName)) {
			sActivitySuperClassNameMap.put(activityClassName, cName);
		} else {
			throw new RuntimeException("no usefull super class for activity: " + activityClassName);
		}
		//==========123456789012345678
		Log.d(TAG, "superClass  : " + cName + " for " + activityClassName);
		return cName;
	}	
	
	@SuppressLint("NewApi")
	public static String getServiceSuperClassName(ClassLoader classloader, String serviceClassName) {
		String cName = null;
		if (sSuperClassNameMap.containsKey(serviceClassName)) {
			cName = sSuperClassNameMap.get(serviceClassName);
		}
		List<String> superClassNames = new ArrayList<String>();
		if (TextUtils.isEmpty(cName)) {
			try {
				Class<?> clazz = classloader.loadClass(serviceClassName);
				superClassNames = getClassType(clazz);
				if (
//						superClassNames.contains(JobService.class.getName())
						superClassNames.contains("android.app.job.JobService")						
//						|| superClassNames.contains(DreamService.class.getName())				
						|| superClassNames.contains("android.service.dreams.DreamService")
//						|| superClassNames.contains(InputMethodService.class.getName())
						|| superClassNames.contains(InputMethodService.class.getName())
						
						) {
					notImpl();
				}
				if (superClassNames.contains(Target_Service.class.getName())) {
					cName = Target_Service.class.getName();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		if (!TextUtils.isEmpty(cName)) {
			sSuperClassNameMap.put(serviceClassName, cName);
		} else {
			dumpClassType(superClassNames);
			throw new RuntimeException("no usefull super class for service: " + serviceClassName);
		}
		//==========123456789012345678
		Log.d(TAG, "super class : " + cName + " for " + serviceClassName);
		return cName;
	}
	
	private static void notImpl() {
		throw new RuntimeException("not impl yet.");
	}

	public static List<String> getClassType(Class clazz) {
		List<String> superClassName = new ArrayList<String>();
		while (!clazz.getName().equals(Object.class.getName())) {
			clazz = clazz.getSuperclass();
			superClassName.add(clazz.getName());
		}
		
		return superClassName;
	}	
	
	public static void dumpClassType(List<String> superClassName) {
		Log.d(TAG, "class hierarchy: ");
		for (String c : superClassName){
			//==========123456789012345678
			Log.d(TAG, "class : " + c);
		}
	}	
}
