package org.bbs.apklauncher;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import org.bbs.apklauncher.emb.Util;
import org.bbs.apkparser.ApkManifestParser;
import org.bbs.apkparser.PackageInfoX;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;
import org.bbs.apkparser.PackageInfoX.ApplicationInfoX;
import org.bbs.apkparser.PackageInfoX.ServiceInfoX;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;

public class InstalledAPks {
	private static InstalledAPks sInstance;
	
	public static Map<String, WeakReference<ClassLoader>> sApk2ClassLoaderMap = new HashMap<String, WeakReference<ClassLoader>>();
	public static Map<String, WeakReference<Application>> sApk2ApplicationtMap = new HashMap<String, WeakReference<Application>>();
	public static Map<String, WeakReference<ResourcesMerger>> sApk2ResourceMap = new HashMap<String, WeakReference<ResourcesMerger>>();

	public static ClassLoader sLastClassLoader;

	private ArrayList<PackageInfoX> mInfos;
	private Application mContext;
	
	private InstalledAPks() {
		
	}

	public static ClassLoader getClassLoader(String packageName) {
		WeakReference<ClassLoader> weakReference = sApk2ClassLoaderMap.get(packageName);
		if (null != weakReference) {
			ClassLoader c = weakReference.get();
			return c;
		}
		return null;
	}
	
	public static void putClassLoader(String packageName, ClassLoader classLoader) {
		sLastClassLoader = classLoader;
		sApk2ClassLoaderMap.put(packageName, new WeakReference<ClassLoader>(classLoader));
	}
	
	public static ClassLoader createClassLoader(String apkPath, String libPath, Context baseContext) {		
			ClassLoader c = null;	

			c = new DexClassLoader(apkPath, baseContext.getDir("apk_code_cache", 0).getPath(), libPath, baseContext.getClassLoader());
			return c;
		}

	public static Application getApplication(String packageName) {
		WeakReference<Application> weakReference = sApk2ApplicationtMap.get(packageName);
		if (null != weakReference) {
			return weakReference.get();
		}
		return null;
	}
	
	public static void putApplication(String packageName, Application app) {
		sApk2ApplicationtMap.put(packageName, new WeakReference<Application>(app));
	}
	
	public void init(Application context, File apkDir){
		mContext = context;
		mInfos = new ArrayList<PackageInfoX>();
		
		scanApkDir(apkDir);
	}
	
	public void scanApkDir(File apkDir) {
		if (null == apkDir || !apkDir.exists()) {
			return ;
		}
		String[] files = apkDir.list();
		if (null == files) {
			return;
		}
		
		for (String f : files) {
			File file = new File(apkDir.getAbsolutePath() + "/" + f);
			if (file.exists() && file.getAbsolutePath().endsWith("apk")){
				PackageInfoX info = ApkManifestParser.parseAPk(mContext, file.getAbsolutePath());
				mInfos.add(info);
				
				try {
					File dataDir = mContext.getDir("plugin", 0);
					File destDir = new File(dataDir, info.packageName + "/lib");
					
					//TODO native lib
					AndroidUtil.extractZipEntry(new ZipFile(info.applicationInfo.publicSourceDir), "lib/armeabi", destDir);
					AndroidUtil.extractZipEntry(new ZipFile(info.applicationInfo.publicSourceDir), "lib/armeabi-v7a", destDir);
					
					info.mLibPath = destDir.getPath();
					
					// asume there is only one apk.
					ClassLoader cl = createClassLoader(info.applicationInfo.sourceDir, info.mLibPath, mContext);
					putClassLoader(info.applicationInfo.sourceDir, cl);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public ApplicationInfoX getApplicationInfo(String applicationName) {
		ApplicationInfoX a = null;
		boolean has = false;
		for (PackageInfoX m : mInfos) {
			if (applicationName.equals(m.applicationInfo.name)) {
				has = true;
				a = (ApplicationInfoX) m.applicationInfo;
				break;
			}
		}
		
		return a;
	}
	
	public PackageInfoX getPackageInfo(ComponentName comName){
		PackageInfoX p = null;
		for (PackageInfoX a : mInfos) {
			if (a.packageName.equals(comName.getPackageName())){
				p = a;
				break;
			}
		}
		
		return p;
	}
	
	public boolean hasApplicationInfo(String applicationName) {
		boolean has = false;
		for (PackageInfoX m : mInfos) {
			if (applicationName.equals(m.applicationInfo.packageName)) {
				has = true;
				break;
			}
		}
		
		return has;
	}
	
	public ActivityInfoX getActivity(String activityName) {
		ActivityInfoX aInfo = null;
		boolean has = false;
		for (PackageInfoX m : mInfos) {
			if (m.activities != null) {
				for (ActivityInfo a : m.activities) {
					ActivityInfoX aX = (ActivityInfoX) a;
					if (activityName.equals(a.name)) {
						has = true;
						aInfo = aX;
						break;
					}
				}
			}
		}
		
		return aInfo;
	}
	
	public boolean hasActivity(String activityName) {
		boolean has = false;
		for (PackageInfoX m : mInfos) {
			if (m.activities != null) {
				for (ActivityInfo a : m.activities) {
					if (activityName.equals(a.name)) {
						has = true;
						break;
					}
				}
			}
		}
		
		return has;
	}
	
	public List<PackageInfoX> getAllApks(){
		return mInfos;
	}
	
	public ActivityInfoX getActivityInfo(String className) {
		ActivityInfoX info = null;
		for (PackageInfoX m : mInfos) {
				if (m.activities != null &&  m.activities.length > 0) {
					final int count = m.activities.length;
					for (int i = 0 ; i < count; i++){
						ActivityInfoX a = (ActivityInfoX) m.activities[i];
						if (className.equals(a.name)) {
							info  = a;
							break;
						}
				}}
		}
		
		return info;
	}	
	
	public ServiceInfoX getServiceInfo(String className) {
		ServiceInfoX info = null;
		for (PackageInfoX m : mInfos) {
				if (m.services != null &&  m.services.length > 0) {
					final int count = m.services.length;
					for (int i = 0 ; i < count; i++){
						ServiceInfoX a = (ServiceInfoX) m.services[i];
						if (className.equals(a.name)) {
							info = a;
							break;
						}
				}}
		}
		
		return info;
	}
	
	// after call this, must init it.
	public static InstalledAPks getInstance() {
		if (null == sInstance) {
			sInstance = new InstalledAPks();
		}
		
		return sInstance;
	}

	public static ResourcesMerger makeTargetResource(String mTargetApkPath,
			Context context) {
		WeakReference<ResourcesMerger> rr = InstalledAPks.sApk2ResourceMap.get(mTargetApkPath);
		Resources targetRes;
		ResourcesMerger resMerger;
		if (rr != null && rr.get() != null) {
			resMerger = rr.get();
			targetRes = resMerger.mFirst;
		} else {
			targetRes = Util.loadApkResource(mTargetApkPath, context);
			resMerger = new ResourcesMerger(targetRes, context.getResources());
			InstalledAPks.sApk2ResourceMap.put(mTargetApkPath, new WeakReference<ResourcesMerger>(resMerger));
		}
		
		return resMerger;
	}

	public static ClassLoader makeClassLoader(Context context, String apkPath, String libPath) {
		ClassLoader cl = InstalledAPks.getClassLoader(apkPath);
		if (null == cl) {
			cl = createClassLoader(apkPath, libPath, context);
			InstalledAPks.putClassLoader(apkPath, (cl));
		}
	
		return cl;
	}
	
	class TargetClassLoader extends DexClassLoader {

		public TargetClassLoader(String dexPath, String optimizedDirectory,
				String libraryPath, ClassLoader parent) {
			super(dexPath, optimizedDirectory, libraryPath, parent);
		}
		
		@Override
		protected Class<?> loadClass(String className, boolean resolve)
				throws ClassNotFoundException {
			return super.loadClass(className, resolve);
		}
		
	}
}
