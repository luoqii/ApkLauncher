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
import org.bbs.apkparser.PackageInfoX.ServiceInfoX;
import org.bbs.apkparser.PackageInfoX.UsesPermissionX;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
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
    private static final String PREF_EXTRACT_APK = "extract_apk";
    private static final String PERF_KEY_APK_HAS_SCANNED = "apk_has_scanned";
    
    private static final boolean DEBUG = ApkLauncherConfig.DEBUG && true;

	private static ApkPackageManager sInstance;
	
	public static Map<String, Reference<ClassLoader>> sApk2ClassLoaderMap = new HashMap<String, Reference<ClassLoader>>();
	public static Map<String, WeakReference<Application>> sApk2ApplicationtMap = new HashMap<String, WeakReference<Application>>();
	public static Map<String, WeakReference<ResourcesMerger>> sApk2ResourceMap = new HashMap<String, WeakReference<ResourcesMerger>>();

	private InstallApks mInfos;
	private Application mContext;

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
	void init(Application context, String assetsPath, boolean force){
		synchronized (mInited) {
			if (mInited.get()) {
				Log.w(TAG, "has inited, ignore.");
				return;
			}
			mContext = context;
			mInfos = new InstallApks();
			mSerUtil = new SerializableUtil(context);

			// XXX replace will failed for first time. ???
			if (!hasUpdateApp()) {
				Version version = Version.getInstance((Application) mContext.getApplicationContext());
				if (version.appUpdated() || version.firstUsage()) {
					// re-build install apk info.
					scanApkDir(getApkDir(), false);

					//			mSerUtil.put(mInfos);
				} else {
					scanApkDir(getApkDir(), false);
					//			mInfos = mSerUtil.get();
				}
			} else {
				Log.i(TAG, "has update app, ignore old app.");
			}

			File autoUpdateDir = getAutoUpdatePluginDir();
			scanApkDir(autoUpdateDir, true);
			deleteFile(autoUpdateDir);

			scanAssetDir(assetsPath, force);

			mInited.set(true);

		}
	}
	
	public boolean inInited(){
		return mInited.get();
	}
	
	boolean hasUpdateApp(){
		boolean has = false;
		String[] files = getAutoUpdatePluginDir().list();
		has = files != null && files.length > 0;
		
		return has;
	}

	public static ResourcesMerger makeTargetResource(String mTargetApkPath,
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
	
	@ExportApi
	public File getPluginDir() {
		return mContext.getDir(PLUGIN_DIR_NAME, 0);
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
	
	public void scanAssetDir(String assetsPath, boolean force){
        Version version = Version.getInstance((Application) mContext.getApplicationContext());
        if (version.appUpdated() || version.firstUsage()||force
        		) {
            doScanApk(assetsPath);
        } else {
        	reScanApkIfNecessary(assetsPath);
        }
	}

	private void doScanApk(String assetsPath) {
		File tempApkDir = mContext.getDir("temp_apk", 0);
		extractApkFromAsset(assetsPath, tempApkDir.getPath());
		scanApkDir(tempApkDir);
		deleteFile(tempApkDir);
		
		SharedPreferences s = mContext.getSharedPreferences(PREF_EXTRACT_APK, 0);
		s.edit().putBoolean(PERF_KEY_APK_HAS_SCANNED, true).commit();		
	}
	
    private void extractApkFromAsset(String srcDir, String destDir) {
    	long time = System.currentTimeMillis();
        AssetManager am = mContext.getResources().getAssets();
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

		time = System.currentTimeMillis() - time;
		Log.i(TAG, "elapse time[extractApkFromAsset]: " + ((float)time / 1000));
    }
	
	private void reScanApkIfNecessary(String assetsPath) {
        SharedPreferences s = mContext.getSharedPreferences(PREF_EXTRACT_APK, 0);
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
	
	private void scanApkDir(File apkDir, boolean copyFile) {
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
			parseApkFile(file, copyFile);
		}
		
//		mSerUtil.put(mInfos);
	}

	private void parseApkFile(File file, boolean copyFile) {
		long time = System.currentTimeMillis();
		if (DEBUG){
			//==========123456789012345678
			Log.d(TAG, "parse file : " + file + " copyFile: " + copyFile);
		}
		if (file.exists() && file.getAbsolutePath().endsWith(APK_FILE_SUFFIX)){
			PackageInfoX info = ApkManifestParser.parseAPk(mContext, file.getAbsolutePath());			
			try {
				File dest = file;
				if (copyFile) {
					dest = new File(getApkDir(), info.packageName + APK_FILE_SUFFIX);
					deleteFile(dest);
					AndroidUtil.copyFile(file, dest);
					info = ApkManifestParser.parseAPk(mContext, dest.getAbsolutePath());
				}
				//==========123456789012345678
				Log.i(TAG, "apk info   : " + appInfoStr(info));
				compareInfo(getHostPacageInfoX(), info);
				
				File destLibDir = new File(getAppDataDir(info.packageName), "/lib");
				
				if (copyFile) {
					//TODO native lib
					deleteFile(destLibDir);

					String abi = SystemPropertiesProxy.get(mContext, "ro.product.cpu.abi");
					String abi2 = SystemPropertiesProxy.get(mContext, "ro.product.cpu.abi2");
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
				ClassLoader cl = createClassLoader(mContext, 
						info.applicationInfo.sourceDir, 
						info.mLibPath, 
						info.applicationInfo.packageName,
						true);
				
				mInfos.addOrUpdate(info);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			//==========123456789012345678
			Log.i(TAG, "ignre file : " + file);
		}
		
		time = System.currentTimeMillis() - time;
		Log.i(TAG, "elapse time[parseApkFile]: " + ((float)time / 1000));
	}
	

	private void compareInfo(PackageInfoX hostPacageInfoX, PackageInfoX info) {
		checkPermission(hostPacageInfoX, info);
	}

	private boolean deleteFile(File file) {
		//==========123456789012345678
		boolean isD = file.isDirectory();
		Log.d(TAG, "delete file: " + file + (isD ? "[D]" : ""));
		
		boolean ret = deleteFile_intenal(file);
		
		return ret;
	}
	
	private boolean deleteFile_intenal(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteFile_intenal(new File(file, children[i]));
				if (!success) {
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
				Log.w(TAG, "++" + p.mName);
			}
		}
		l = substract(targetL, hostL);
		if (l.size() > 0) {
			//----------1234567890123456789
			Log.w(TAG, "need   permission:");
			for (UsesPermissionX p : l){
				Log.w(TAG, "--" + p.mName);
			}
		}
		
		// TODO permissionGroup ... 
	}
	
	List<UsesPermissionX> toList(UsesPermissionX[] ps) {
		List<UsesPermissionX> list = new ArrayList<>();
		if (ps != null){
			for (UsesPermissionX p : ps) {
				list.add(p);
			}
		}
		return list;
	}
	
	
	List<UsesPermissionX> substract(List<UsesPermissionX> left, List<UsesPermissionX> right){
		List<UsesPermissionX> list = new ArrayList<>();
		for (int i = 0; i < left.size() ; i++) {
			boolean found = false;
			for (int j = 0; j < right.size() ; j++) {
				if (left.get(i).mName.equals(right.get(j).mName)){
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
			mHostPkgInfo = ApkManifestParser.parseAPk(mContext, mContext.getApplicationInfo().publicSourceDir);
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
	
	static class SerializableUtil {
		public Context mContext;
		private File mFile;
		
		SerializableUtil(Application context){
			mContext = context;
			mFile = new File(context.getDir(PLUGIN_DIR_NAME, 0), "plugins.xml");
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
		private /*static*/ /*final*/ String PREF_NAME = Version.class.getSimpleName() + ".pref";
		private static final String KEY_PREVIOUS_V_CODE = "previous_version_code";
		private static final String KEY_PREVIOUS_V_NAME = "previous_version_name";
		private static final String TAG = Version.class.getSimpleName();
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
				PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
				mCurrentVersionCode = pInfo.versionCode;
				mCurrentVersionName = pInfo.versionName;
				
				SharedPreferences p = appContext.getSharedPreferences(PREF_NAME, 0);
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
