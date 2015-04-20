package org.bbs.apklauncher.emb;

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
import android.inputmethodservice.InputMethodService;
import android.service.dreams.DreamService;
import android.text.TextUtils;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class LoadedApk {
	private static final String TAG = LoadedApk.class.getSimpleName();

	static HashMap<String, String> sActivitySuperClassNameMap = new HashMap<String, String>();
	static HashMap<String, String> sSuperClassNameMap = new HashMap<String, String>();
	
	PackageInfoX mApkInfo;
	private String mDexCacheDir;

	private ClassLoader mClassLoader;

	public LoadedApk(Application appContext, PackageInfoX apkInfo){
		mApkInfo = apkInfo;
		
		mDexCacheDir = appContext.getDir("apk_code_cache", 0).getPath();
	}
	
	public ClassLoader getClassLoader(ClassLoader parentClassLoader, String libPath) {
		if (mClassLoader != null) {
			return mClassLoader;
		}
		
		ClassLoader c = new DexClassLoader(mApkInfo.applicationInfo.publicSourceDir, mDexCacheDir, libPath, parentClassLoader);
		Log.d(TAG, "new classloader for apk: " + c);
		mClassLoader = c;
		return c;
	}
	
	public static String getActivitySuperClassName(ClassLoader classloader, String activityClassName) {
		String cName = null;
		if (sActivitySuperClassNameMap.containsKey(activityClassName)) {
			cName = sActivitySuperClassNameMap.get(activityClassName);
		}
		if (TextUtils.isEmpty(cName)) {
			try {
				Class<?> clazz = classloader.loadClass(activityClassName);
				List<String> superClassNames = new ArrayList<String>();
				dumpClassType(clazz, superClassNames);
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
		
		if (!TextUtils.isEmpty(cName)) {
			sActivitySuperClassNameMap.put(activityClassName, cName);
		} else {
			throw new RuntimeException("no usefull super class for activity: " + activityClassName);
		}
		
		Log.d(TAG, "superClass: " + cName + " for class: " + activityClassName);
		return cName;
	}	
	
	@SuppressLint("NewApi")
	public static String getServiceSuperClassName(ClassLoader classloader, String serviceClassName) {
		String cName = null;
		if (sSuperClassNameMap.containsKey(serviceClassName)) {
			cName = sSuperClassNameMap.get(serviceClassName);
		}
		if (TextUtils.isEmpty(cName)) {
			try {
				Class<?> clazz = classloader.loadClass(serviceClassName);
				List<String> superClassNames = new ArrayList<String>();
				dumpClassType(clazz, superClassNames);
				if (superClassNames.contains(JobService.class.getName())
						|| superClassNames.contains(DreamService.class.getName())
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
			throw new RuntimeException("no usefull super class for service: " + serviceClassName);
		}
		
		Log.d(TAG, "superClass: " + cName + " for class: " + serviceClassName);
		return cName;
	}
	
	private static void notImpl() {
		throw new RuntimeException("not impl yet.");
	}

	private static void dumpClassType(Class clazz, List<String> superClassName) {
		//==========123456789012345678
		Log.d(TAG, "class        : " + clazz + " name: " + clazz.getName());
		while (!clazz.getName().equals(Object.class.getName())) {
			clazz = clazz.getSuperclass();

			//=========123456789012345678
			Log.d(TAG, "super class : " + clazz);
			superClassName.add(clazz.getName());
		}
	}	
}
