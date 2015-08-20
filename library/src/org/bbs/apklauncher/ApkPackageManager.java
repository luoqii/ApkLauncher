package org.bbs.apklauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipFile;

import org.bbs.apklauncher.api.ExportApi;
import org.bbs.apkparser.ApkManifestParser;
import org.bbs.apkparser.PackageInfoX;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;
import org.bbs.apkparser.PackageInfoX.ApplicationInfoX;
import org.bbs.apkparser.PackageInfoX.IntentFilterX;
import org.bbs.apkparser.PackageInfoX.PermissionTreeX;
import org.bbs.apkparser.PackageInfoX.ServiceInfoX;
import org.bbs.apkparser.PackageInfoX.UsesPermissionX;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class ApkPackageManager extends BasePackageManager {
	private static final String TAG = ApkPackageManager.class.getSimpleName();
	
	private static /*final*/ String PLUGIN_DIR_NAME = "plugin_data";
	private static final String APK_FILE_SUFFIX = ".apk";
    private static final String PREF_EXTRACT_APK = ApkPackageManager.class.getName() + "";
    private static final String PERF_KEY_APK_HAS_SCANNED = "apk_has_scanned";
    
    private static final boolean DEBUG = ApkLauncherConfig.DEBUG && true;
    private static final boolean PROFILE = ApkLauncherConfig.DEBUG && true;

	private static ApkPackageManager sInstance;
	
	public static Map<String, Reference<ClassLoader>> sApk2ClassLoaderMap = new HashMap<String, Reference<ClassLoader>>();
	public static Map<String, WeakReference<Application>> sApk2ApplicationtMap = new HashMap<String, WeakReference<Application>>();
	public static Map<String, WeakReference<ResourcesMerger>> sApk2ResourceMap = new HashMap<String, WeakReference<ResourcesMerger>>();

	private InstallApks mInfos;
	private Application mApplication;
	private static Context sFileContext;

	private SerializableUtil mSerUtil;
	private PackageInfoX mHostPkgInfo;	
	private ClassLoaderFactory mClassLoaderFactory;
	private AtomicBoolean mInited;
	
	public static void setPluginDataDir(String name){
		PLUGIN_DIR_NAME = name;
	}
	
	private ApkPackageManager() {
		mInited = new AtomicBoolean();
	}
		
	/**
	 * @return
	 * 
	 * NOTE:  <p>
	 * you must init this before use by {@link #init(Application, File)}.
	 */
	@ExportApi
	public static ApkPackageManager getInstance() {
		if (null == sInstance) {
			sInstance = new ApkPackageManager();
		}
		
		return sInstance;
	}
	
	public void setClassLoaderFactory(ClassLoaderFactory f) {
		mClassLoaderFactory = f;
	}

	/**
	 * @param context
	 * @param apkDir where apk file located.
	 * 
	 */	
	void init(Application context, String assetsPath, boolean overwrite){
		long time = 0;
		if (PROFILE){
			time = System.currentTimeMillis();
			Log.d(TAG, "start profile[init].");
		}
		
		synchronized (mInited) {
			if (mInited.get()) {
				Log.w(TAG, "has inited, ignore.");
				return;
			}
			mApplication = context;

			sFileContext = new FileContext(context) {
				
				@Override
				public String getTargetPackageName() {
					return "org.bbs.apklauncher.sdk";
				}
			};
			mInfos = new InstallApks();
			mSerUtil = new SerializableUtil(context);

			// XXX replace will failed for first time. ???
			if (!hasUpdatedPlugin()) {
				Version version = Version.getInstance((Application) mApplication.getApplicationContext());
				if (version.appUpdated() || version.firstUsage()) {
					// re-build install apk info.
					scanApkDir(getApkDir(), false);

					//			mSerUtil.put(mInfos);
					
					// for first time or update force copy new/delete old files.
					overwrite |= true;
				} else {
					scanApkDir(getApkDir(), false);
					//			mInfos = mSerUtil.get();
				}
			} else {
				Log.i(TAG, "has update plguin, ignore old plguin.");

				File autoUpdateDir = getAutoUpdatePluginDir();
				scanApkDir(autoUpdateDir, true);
				deleteFileOrDir(autoUpdateDir);
			}
			
			scanAssetDir(assetsPath, overwrite);

			mInited.set(true);
		}
		
		if (PROFILE){
			time = System.currentTimeMillis() - time;
			Log.d(TAG, "end   profile[init]: " + ( time / 1000.) + "s");
		}
	}
	
	public boolean inInited(){
		return mInited.get();
	}
	
	boolean hasUpdatedPlugin(){
		boolean has = false;
		String[] files = getAutoUpdatePluginDir().list();
		has = files != null && files.length > 0;
		
		return has;
	}

	public static ResourcesMerger getTargetResource(String mTargetApkPath,
			Context context) {
		WeakReference<ResourcesMerger> rr = ApkPackageManager.sApk2ResourceMap.get(mTargetApkPath);
		Resources targetRes;
		ResourcesMerger resMerger;
		if (rr != null && rr.get() != null) {
			resMerger = rr.get();
			targetRes = resMerger.mFirst;
		} else {
			targetRes = ApkUtil.loadApkResource(mTargetApkPath, context);
			resMerger = new ResourcesMerger(targetRes, context.getResources());
			ApkPackageManager.sApk2ResourceMap.put(mTargetApkPath, new WeakReference<ResourcesMerger>(resMerger));
		}
		
		return resMerger;
	}
	
	public ClassLoader createClassLoader(Context baseContext, PackageInfoX pInfo){
		return createClassLoader(baseContext, pInfo.applicationInfo.publicSourceDir, 
				pInfo.mLibPath, pInfo.packageName);
	}

	public ClassLoader createClassLoader(Context baseContext, String apkPath, String libPath, String targetPackageName) {
		return createClassLoader(baseContext, apkPath, libPath, targetPackageName, false);
	}

	public ClassLoader createClassLoader(Context baseContext, String apkPath, String libPath, String targetPackageName, boolean force) {
		ClassLoader cl = ApkPackageManager.getClassLoader(targetPackageName);
		if (null == cl || force) {
			if (mClassLoaderFactory != null) {
				cl = mClassLoaderFactory.createClassLoader(this, baseContext, apkPath, libPath, targetPackageName);
			} else {
				String optPath =  getOptDir().getPath();
				cl = new DexClassLoader(apkPath, optPath, libPath, baseContext.getClassLoader());
//				cl = new TargetClassLoader(apkPath, optPath, libPath, baseContext.getClassLoader(), targetPackageName, baseContext);
			}
			ApkPackageManager.putClassLoader(targetPackageName, (cl));
		}
	
		return cl;
	}

	public static ClassLoader getClassLoader(String packageName) {
		Reference<ClassLoader> reference = sApk2ClassLoaderMap.get(packageName);
		if (null != reference) {
			ClassLoader c = reference.get();
			return c;
		}
		return null;
	}
	
	public static void putClassLoader(String packageName, ClassLoader classLoader) {
		sApk2ClassLoaderMap.put(packageName, new SoftReference<ClassLoader>(classLoader));
	}
	
	@ExportApi
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
	
	/**
	 * all plug-related dir SHOULD base on this dir.
	 * @return
	 */
	@ExportApi
	public File getPluginDir() {
		File placeHolder = mApplication.getDir("placeholder", 0);
		File dir = new File(placeHolder.getParent(), PLUGIN_DIR_NAME);
		dir.mkdirs();
		
		assureDir(dir);
		
		return dir;
		
//		return mContext.getDir(PLUGIN_DIR_NAME, 0);
	}
	
	private void assureDir(File dir) {
		if (null == dir || !dir.isDirectory()){
			throw new RuntimeException("can not creaet dir: " + dir);
		}
	}

	@ExportApi
	public File getAutoUpdatePluginDir() {
		File dir = new File(getPluginDir(), "auto_update");
		dir.mkdirs();
		
		return dir;
	}	
	
	@ExportApi
	public File getApkDir() {
		File dir = new File(getPluginDir(), "app");
		dir.mkdirs();
		
		return dir;
	}
	
	@ExportApi
	public File getOptDir() {
		File dir = new File(getPluginDir(), "dalvik-cache");
		dir.mkdirs();
		
		return dir;
	}
	
	public File getAppDataDir(String packageName){
		File dir = new File(getPluginDir() + "/data", packageName);
		dir.mkdirs();
		
		return dir;
	}

	@ExportApi
	private File getDataDir(String name) {
		File dir = new File(new File(getPluginDir(), name), "temp_apk");
		dir.mkdirs();
		
		return dir;
	}
	
	public void scanAssetDir(String assetsPath, boolean overwritee){
        Version version = Version.getInstance((Application) mApplication.getApplicationContext());
        if (version.appUpdated() || version.firstUsage()||overwritee
        		) {
            doScanApk(assetsPath);
        } else {
        	reScanApkIfNecessary(assetsPath);
        }
	}

	private void doScanApk(String assetsPath) {
		File tempApkDir = getDataDir("temp_apk");
		extractApkFromAsset(assetsPath, tempApkDir.getPath());
		scanApkDir(tempApkDir);
		deleteFileOrDir(tempApkDir);
		
		SharedPreferences s = sFileContext.getSharedPreferences(PREF_EXTRACT_APK, 0);
		s.edit().putBoolean(PERF_KEY_APK_HAS_SCANNED, true).commit();		
	}
	
    private void extractApkFromAsset(String srcDir, String destDir) {
    	long time = 0;
    	if (PROFILE) {
    		time = System.currentTimeMillis();
			Log.d(TAG, "start profile[extractApkFromAsset].");
    	}
        AssetManager am = mApplication.getResources().getAssets();
        try {
            String[] files = am.list(srcDir);
            if (null == files || files.length == 0){
        		//==========123456789012345678
            	Log.w(TAG, "empty assets dir:" + srcDir);
            	return;
            }
			for (String fp : files) {
                AndroidUtil.copyStream(am.open(srcDir + "/" + fp), 
                		new FileOutputStream(new File(destDir, fp)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (PROFILE) {
        	time = System.currentTimeMillis() - time;
			Log.d(TAG, "end   profile[extractApkFromAsset]: " + ( time / 1000.) + "s");
        }
    }
	
	private void reScanApkIfNecessary(String assetsPath) {
        SharedPreferences s = sFileContext.getSharedPreferences(PREF_EXTRACT_APK, 0);
        boolean scanned = s.getBoolean(PERF_KEY_APK_HAS_SCANNED, false);
        if (!scanned) {
            doScanApk(assetsPath);
        } else {
        	Log.i(TAG, "assets path has scanned before, ignore. path: " + assetsPath);
        }
	}

	@ExportApi
	public void scanApkDir(File apkDir) {
		scanApkDir(apkDir, true);
	}
	
	private void scanApkDir(File apkDir, boolean overwrite) {
		if (DEBUG){
			//==========123456789012345678
			Log.d(TAG, "parse  dir : " + apkDir);
		}
		if (null == apkDir || !apkDir.exists()) {
			//==========123456789012345678
			Log.w(TAG, "invalid dir: " + apkDir);
			return ;
		}
		String[] files = apkDir.list();
		if (null == files) {
			//==========123456789012345678
			Log.w(TAG, "empyt dir  : " + apkDir);
			return;
		}
		
		for (String f : files) {
			File file = new File(apkDir.getAbsolutePath() + "/" + f);
			parseApkFile(file, overwrite);
		}
		
//		mSerUtil.put(mInfos);
	}

	private void parseApkFile(File file, boolean overwrite) {
		long time = 0;
		if (PROFILE) {
			time = System.currentTimeMillis();
			Log.d(TAG, "start profile[parseApkFile].");
		}
		
		if (DEBUG){
			//==========123456789012345678
			Log.d(TAG, "parse file : " + file + " copyFile: " + overwrite);
		}
		if (file.exists() && file.getAbsolutePath().endsWith(APK_FILE_SUFFIX)){
			PackageInfoX info = ApkManifestParser.parseAPk(mApplication, file.getAbsolutePath());			
			try {
				File dest = file;
				if (overwrite) {
					dest = new File(getApkDir(), info.packageName + APK_FILE_SUFFIX);
					deleteFileOrDir(dest);
					AndroidUtil.copyFile(file, dest);
					info = ApkManifestParser.parseAPk(mApplication, dest.getAbsolutePath());
				}
				//==========123456789012345678
				Log.i(TAG, "apk info   : " + appInfoStr(info));
				compareInfo(getHostPacageInfoX(), info);
				String reqSdkV = "";
				if (info.applicationInfo.metaData != null ) {
					reqSdkV = info.applicationInfo.metaData.getString(ApkLauncher.MANIFEST_META_REQUIRE_MIN_SDK_VERSION);
				}
				if (TextUtils.isEmpty(reqSdkV)){
					Log.w(TAG, "no " + ApkLauncher.MANIFEST_META_REQUIRE_MIN_SDK_VERSION + " specified in Manifest.");
				} else {
					String reqVersion = org.bbs.apklauncher.Version.extractVersion(reqSdkV);
					String sdkVersion = org.bbs.apklauncher.Version.extractVersion(org.bbs.apklauncher.Version.VERSION);
					if (org.bbs.apklauncher.Version.isNewerRaw(reqVersion,sdkVersion)){
						Log.w(TAG, "plug require a higher sdk version. req version: " + reqVersion + " our version: " + sdkVersion);
					}
				}
				
				File destLibDir = new File(getAppDataDir(info.packageName), "/lib");
				
				if (overwrite) {
					//TODO native lib
					deleteFileOrDir(destLibDir);

					String abi = SystemPropertiesProxy.get(mApplication, "ro.product.cpu.abi");
					String abi2 = SystemPropertiesProxy.get(mApplication, "ro.product.cpu.abi2");
					Log.d(TAG, "abi: " + abi + " abi2: " + abi2);
					String[] abis = new String[]{abi, abi2};
//					String[] abis = Build.SUPPORTED_ABIS;
					final int L = abis.length;
					for (int i = L - 1 ; i >= 0; i--){
						AndroidUtil.extractZipEntry(new ZipFile(info.applicationInfo.publicSourceDir), "lib/"+ abis[i], destLibDir);
					}

//					AndroidUtil.extractZipEntry(new ZipFile(info.applicationInfo.publicSourceDir), "lib/armeabi", destLibDir);
//					AndroidUtil.extractZipEntry(new ZipFile(info.applicationInfo.publicSourceDir), "lib/armeabi-v7a", destLibDir);
				}
				
				info.mLibPath = destLibDir.getPath();
				
				// asume there is only one apk.
//				ClassLoader cl = createClassLoader(mContext, 
//						info.applicationInfo.sourceDir, 
//						info.mLibPath, 
//						info.applicationInfo.packageName,
//						true);
				
				mInfos.addOrUpdate(info);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			//==========123456789012345678
			Log.i(TAG, "ignre file : " + file);
		}
		
		if (PROFILE){
			time = System.currentTimeMillis() - time;
			Log.d(TAG, "end   profile[parseApkFile]: " + ( time / 1000.) + "s");
		}
	}
	

	private void compareInfo(PackageInfoX hostPacageInfoX, PackageInfoX info) {
		checkPermission(hostPacageInfoX, info);
	}

	private boolean deleteFileOrDir(File file) {
		boolean isD = file.isDirectory();
		//==========123456789012345678
		Log.d(TAG, "delete file: " + file + (isD ? "[D]" : ""));
		
		boolean ret = deleteFile_intenal(file);
		
		return ret;
	}
	
	private boolean deleteFile_intenal(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				File f = new File(file, children[i]);
				boolean success = deleteFile_intenal(f);
				if (!success) {
					Log.w(TAG, "error in deleting file " + f);
					return false;
				}
			}
		}
		// The directory is now empty so now it can be smoked
		return file.delete();
	}
	
	private void checkPermission(PackageInfoX hostPacageInfoX, PackageInfoX info) {
		PackageInfoX host = getHostPacageInfoX();
		List<UsesPermissionX> hostL = toList(host.mUsedPermissions);
		List<UsesPermissionX> targetL =toList(info.mUsedPermissions);
		List<UsesPermissionX> l = substract(hostL, targetL);
		if (l.size() > 0) {
			//----------1234567890123456789
			Log.w(TAG, "unused permission:");
			for (UsesPermissionX p : l){
				Log.w(TAG, "++" + p.name);
			}
		}
		l = substract(targetL, hostL);
		if (l.size() > 0) {
			//----------1234567890123456789
			Log.w(TAG, "need   permission:");
			for (UsesPermissionX p : l){
				Log.w(TAG, "--" + p.name);
			}
		}
		
		List<PermissionGroupInfo> hostPG = toList(host.mPermissionGroups);
		List<PermissionGroupInfo> targetPG = toList(info.mPermissionGroups);
		List<PermissionGroupInfo> pg = substract(hostPG, targetPG);
		if (pg.size() > 0) {
			//----------12345678901234567890123
			Log.w(TAG, "unused permission group:");
			for (PermissionGroupInfo g : pg){
				Log.w(TAG, "++" + g.name);
			}
		}
		pg = substract(targetPG, hostPG);
		if (pg.size() > 0) {
			//----------12345678901234567890123
			Log.w(TAG, "need   permission group:");
			for (PermissionGroupInfo g : pg){
				Log.w(TAG, "--" + g.name);
			}
		}

		List<PermissionTreeX> hostPT = toList(host.mPermissionTrees);
		List<PermissionTreeX> targetPT = toList(info.mPermissionTrees);
		List<PermissionTreeX> pt = substract(hostPT, targetPT);
		if (pt.size() > 0) {
			//----------12345678901234567890123
			Log.w(TAG, "unused permission true:");
			for (PermissionTreeX g : pt){
				Log.w(TAG, "++" + g.name);
			}			
		}
		pt = substract(targetPT, hostPT);
		if (pt.size() > 0) {
			//----------12345678901234567890123
			Log.w(TAG, "unused permission true:");
			for (PermissionTreeX g : pt){
				Log.w(TAG, "--" + g.name);
			}			
		}
		
	}
	
	private <T> List toList(
			T[] pgs) {
		List<T> list = new ArrayList<>();
		if (pgs != null){
			for (T p : pgs) {
				list.add(p);
			}
		}
		
		return list;
	}

//	List<UsesPermissionX> toList(UsesPermissionX[] ps) {
//		List<UsesPermissionX> list = new ArrayList<>();
//		if (ps != null){
//			for (UsesPermissionX p : ps) {
//				list.add(p);
//			}
//		}
//		return list;
//	}	
	
	<T extends PackageItemInfo>List substract(List<T> left, List<T> right){
		List<T> list = new ArrayList<>();
		for (int i = 0; i < left.size() ; i++) {
			boolean found = false;
			for (int j = 0; j < right.size() ; j++) {
				if (left.get(i).name.equals(right.get(j).name)){
					found = true;
					break;
				}
			}
			
			if (!found) {
				list.add(left.get(i));
			}
		}
			
		return list;
	}
	
	PackageInfoX getHostPacageInfoX(){;
		if (mHostPkgInfo == null ) {
			mHostPkgInfo = ApkManifestParser.parseAPk(mApplication, mApplication.getApplicationInfo().publicSourceDir);
		}
		return mHostPkgInfo;
	}

	@ExportApi
	public ApplicationInfoX getApplicationInfo(String packageName) {
		ApplicationInfoX a = null;
		boolean has = false;
		for (PackageInfoX i : mInfos) {
			if (packageName.equals(i.packageName)) {
				has = true;
				a = (ApplicationInfoX) i.applicationInfo;
				break;
			}
		}
		
		return a;
	}
	
	@ExportApi
	public PackageInfoX getPackageInfo(String packageName){
		PackageInfoX p = null;
		for (PackageInfoX i : mInfos) {
			if (i.packageName.equals(packageName)){
				p = i;
				break;
			}
		}
		
		return p;
	}
	
	@ExportApi
	public boolean hasApplicationInfo(String className) {
		boolean has = false;
		for (PackageInfoX m : mInfos) {
			if (className.equals(m.applicationInfo.packageName)) {
				has = true;
				break;
			}
		}
		
		return has;
	}
	
	@ExportApi
	public List<PackageInfoX> getAllApks(){
		return mInfos;
	}

	@ExportApi
	public ActivityInfoX getActivityInfo(Class clazz) {
		return getActivityInfo(clazz.getName());
	}
	
	@ExportApi
	public ActivityInfoX getActivityInfo(String className) {
		for (PackageInfoX m : mInfos) {
			if (m.activities != null) {
				for (ActivityInfo a : m.activities) {
					ActivityInfoX aX = (ActivityInfoX) a;
					if (className.equals(a.name)) {
						return aX;
					}
				}
			}
		}

		return null;
	}	
	
	public List<ActivityInfoX> getLauncherActivityInfo(){
		List<ActivityInfoX> result = new ArrayList<>();
		for (PackageInfoX m : mInfos) {
			if (m.activities != null) {
				for (ActivityInfo a : m.activities) {
					ActivityInfoX aX = (ActivityInfoX) a;
					IntentFilterX[] filters = aX.mIntentFilters;
					if (filters != null && filters.length > 0){
						for (IntentFilterX f : filters) {
							if (f.hasCategory(Intent.CATEGORY_LAUNCHER)) {
								result.add(aX);
								break;
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	
	public List<ActivityInfoX> getLauncherActivityInfo(String packageName){
		List<ActivityInfoX> result = new ArrayList<>();
		List<ActivityInfoX> launchers = getLauncherActivityInfo();
		for (ActivityInfoX i : launchers){
			if (i.packageName.equals(packageName)){
				result.add(i);
			}
		}
		return result;
	}
	
	@ExportApi
	public ServiceInfoX getServiceInfo(String className) {
		for (PackageInfoX m : mInfos) {
				if (m.services != null &&  m.services.length > 0) {
					final int count = m.services.length;
					for (int i = 0 ; i < count; i++){
						ServiceInfoX a = (ServiceInfoX) m.services[i];
						if (className.equals(a.name)) {
							return a;
						}
				}}
		}
		
		return null;
	}
	
	@ExportApi
	public List<ResolveInfo> queryIntentActivities(Intent intent, int flag) {
		List<ResolveInfo> result = new ArrayList<>();
		for (PackageInfoX p : mInfos){
			queryIntentActivities(p.packageName, intent, flag, result);
		}
		return result;
	}

	@ExportApi
	public List<ResolveInfo> queryIntentActivities(String packageName, Intent intent, int flag) {
		List<ResolveInfo> result = new ArrayList<>();
		queryIntentActivities(packageName, intent, flag, result);
		return result;
	}

	@ExportApi
	private void queryIntentActivities(String packageName, Intent intent, int flag, List<ResolveInfo> result){
		if (TextUtils.isEmpty(packageName)) return;
		
		String action = intent.getAction();
		Set<String> categories = intent.getCategories();
		for (PackageInfoX p : mInfos){
			if (packageName.equals(p.packageName)){
				for (ActivityInfo a : p.activities){
					ActivityInfoX aX = (ActivityInfoX) a;
					if (aX.mIntentFilters == null) {
						continue;
					}
					for( IntentFilter intentFilter: aX.mIntentFilters) {
						if (intentFilter.matchAction(action)
								&& intentFilter.matchCategories(categories) == null) {
							ResolveInfo info = new ResolveInfo();
							info.activityInfo = a;
							result.add(info);
						}
					}
				}
			}
		}
	}

	static String appInfoStr(PackageInfoX info) {
		return info.packageName + "|" + info.versionCode + "|" + info.versionName;
	}
	
	public interface ClassLoaderFactory {
		ClassLoader createClassLoader(ApkPackageManager apkPackageManager, Context baseContext, String apkPath,
				String libPath, String targetPackageName);		
	}

	static class InstallApks extends ArrayList<PackageInfoX> implements Serializable {
		public void addOrUpdate(PackageInfoX info){
			int index = -1;
			final int SIZE = size();
			for (int i = 0 ; i < SIZE ; i++){
				if (get(i).packageName.equals(info.packageName)){
					index = i;
					break;
				}
			}
			if (index >= 0) {
				PackageInfoX old = remove(index);
				//==========123456789012345678
				Log.i(TAG, "app updated:");
				Log.i(TAG, "old app    : "  + appInfoStr(old) );
				Log.i(TAG, "new app    : "  + appInfoStr(info) );
			}
			add(info);
		}
	}
	
	class SerializableUtil {
		public Context mContext;
		private File mFile;
		
		SerializableUtil(Application context){
			mContext = context;
			mFile = new File(getPluginDir(), "plugins.xml");
		}
		
		void put(InstallApks apk) {
	        try {
	            ObjectOutputStream oop = new ObjectOutputStream(new FileOutputStream(mFile));
	            oop.writeObject(apk);
	            oop.flush();
	            oop.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		
		InstallApks get(){
	    	Object o = null;
	    	try {
	    		ObjectInputStream oin = new ObjectInputStream(new FileInputStream(mFile));
	    		o = oin.readObject();
	    		oin.close();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} catch (ClassNotFoundException e) {
	    		e.printStackTrace();
	    	}

	    	return (InstallApks) o;
		}
		
	}
	
	//http://my.oschina.net/chaselinfo/blog/213393?p=1
	public static class SystemPropertiesProxy
	{
	 
	    /**
	     * 根据给定Key获取值.
	     * @return 如果不存在该key则返回空字符串
	     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
	     */
	    public static String get(Context context, String key) throws IllegalArgumentException {
	 
	        String ret= "";
	 
	        try{
	 
	          ClassLoader cl = context.getClassLoader(); 
	          @SuppressWarnings("rawtypes")
	          Class SystemProperties = cl.loadClass("android.os.SystemProperties");
	 
	          //参数类型
	          @SuppressWarnings("rawtypes")
	              Class[] paramTypes= new Class[1];
	          paramTypes[0]= String.class;
	 
	          Method get = SystemProperties.getMethod("get", paramTypes);
	 
	          //参数
	          Object[] params= new Object[1];
	          params[0]= new String(key);
	 
	          ret= (String) get.invoke(SystemProperties, params);
	 
	        }catch( IllegalArgumentException iAE ){
	            throw iAE;
	        }catch( Exception e ){
	            ret= "";
	            //TODO
	        }
	 
	        return ret;
	 
	    }
	}
	
	// copied from https://github.com/luoqii/android_common_lib/blob/master/library/src/org/bbs/android/commonlib/Version.java
	static class Version {
		private static final int INVALID_CODE = -1;
		private /*static*/ /*final*/ String PREF_NAME = Version.class.getSimpleName() + "";
		private static final String KEY_PREVIOUS_V_CODE = "previous_version_code";
		private static final String KEY_PREVIOUS_V_NAME = "previous_version_name";
//		private static final String TAG = Version.class.getSimpleName();
		private static Map<Reference<Application>, Version>  sInstances = new HashMap<Reference<Application>, Version>();
		
		public static Version getInstance(Application appContext){
			Version v = null;
			for (Reference<Application> r : sInstances.keySet()) {
				if (r != null && r.get() == appContext) {
					v = sInstances.get(r);
					if (null != v){
						return v;
					}
				}
			}
			if (null == v){
				v = new Version(appContext);
				sInstances.put(new WeakReference<Application>(appContext), v);
			}
			
			return v;
		}

		private int mCurrentVersionCode;
		private String mCurrentVersionName;
		private int mPreviousVersionCode;
		private String mPreviousVersionName;
		private boolean mInited;
		
		private Version(Application appContext){
			PREF_NAME = appContext.getPackageName() + "." + PREF_NAME;
			init(appContext);
		};
		
		void init(Application appContext){
			if (mInited) {
				Log.w(TAG, "this has inited already, ignore.");
				return;
			}
			try {
				Log.i(TAG, "sdk version: " + org.bbs.apklauncher.Version.VERSION);
				
				PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
				mCurrentVersionCode = pInfo.versionCode;
				mCurrentVersionName = pInfo.versionName;
				
				SharedPreferences p = sFileContext.getSharedPreferences(PREF_NAME, 0);
				mPreviousVersionCode = p.getInt(KEY_PREVIOUS_V_CODE, INVALID_CODE);
				mPreviousVersionName = p.getString(KEY_PREVIOUS_V_NAME, "");

				Log.i(TAG, "mCurrentVersionCode  : " + mCurrentVersionCode);
				Log.i(TAG, "mCurrentVersionName  : " + mCurrentVersionName);
				Log.i(TAG, "mPreviousVersionCode : " + mPreviousVersionCode);
				Log.i(TAG, "mPreviousVersionName : " + mPreviousVersionName);
				
				p.edit()
					.putInt(KEY_PREVIOUS_V_CODE, mCurrentVersionCode)
					.putString(KEY_PREVIOUS_V_NAME, mCurrentVersionName)
					.commit();
			} catch (NameNotFoundException e) {
				throw new RuntimeException("can not get packageinfo. ");
			}
			mInited = true;
		}
			
		public int getVersionCode(){
			return mCurrentVersionCode;
		}
		
		public String getVersionName() {
			return mCurrentVersionName;
		}	
		
		public int getPreviousVersionCode(){
			return mPreviousVersionCode;
		}
		
		public String getPreviousVersionName() {
			return mPreviousVersionName;
		}
		
		public boolean firstUsage(){
			return mPreviousVersionCode == INVALID_CODE;
		}
		
		public boolean appUpdated(){
			return mPreviousVersionCode != mCurrentVersionCode;
		}
	}
	
}
