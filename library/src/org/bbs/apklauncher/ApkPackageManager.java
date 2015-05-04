package org.bbs.apklauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class ApkPackageManager extends PackageManager {
	private static final String PLUGIN_DIR_NAME = "plugin";

	private static final String APK_FILE_SUFFIX = ".apk";

	private static final String TAG = ApkPackageManager.class.getSimpleName();

	private static ApkPackageManager sInstance;
	
	public static Map<String, WeakReference<ClassLoader>> sApk2ClassLoaderMap = new HashMap<String, WeakReference<ClassLoader>>();
	public static Map<String, WeakReference<Application>> sApk2ApplicationtMap = new HashMap<String, WeakReference<Application>>();
	public static Map<String, WeakReference<ResourcesMerger>> sApk2ResourceMap = new HashMap<String, WeakReference<ResourcesMerger>>();

	public static ClassLoader sLastClassLoader;

	private InstallApks mInfos;
	private Application mContext;

	private SerializableUtil mSerUtil;

	private UpdateUtil mUpdateU;
	
	private ApkPackageManager() {
		
	}
	
	public List<ResolveInfo> queryIntentActivities(Intent intent, int flag) {
		List<ResolveInfo> result = new ArrayList<>();
		for (PackageInfoX p : mInfos){
			queryIntentActivities(p.packageName, intent, flag, result);
		}
		return result;
	}
	
	public List<ResolveInfo> queryIntentActivities(String packageName, Intent intent, int flag) {
		List<ResolveInfo> result = new ArrayList<>();
		queryIntentActivities(packageName, intent, flag, result);
		return result;
	}
	
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
	
	/**
	 * @param context
	 * @param apkDir where apk file located.
	 * 
	 */
	public void init(Application context){
		mContext = context;
		mInfos = new InstallApks();
		mSerUtil = new SerializableUtil(context);
		
		mUpdateU = new UpdateUtil(UpdateUtil.PREF_KEY_VERSION_ID);
		if (mUpdateU.isAppUpdate(context) || mUpdateU.isFirstUsage(context)) {
			// re-build install apk info.
			scanApkDir(getAppDir(), false);
			
//			mSerUtil.put(mInfos);
		} else {
			scanApkDir(getAppDir(), false);
//			mInfos = mSerUtil.get();
		}
		
		if (mUpdateU.isAppUpdate(context)){
			mUpdateU.updateVersion(context);
		}
		
		File autoUpdateDir = getAutoUpdatePluginDir();
		scanApkDir(autoUpdateDir, true);
		String[] files = autoUpdateDir.list();
		if (files != null){
			for (String fname: files){
				new File(autoUpdateDir, fname).delete();
			}
		}
	}

	public File getPluginDir() {
		return mContext.getDir(PLUGIN_DIR_NAME, 0);
	}
	
	public File getAutoUpdatePluginDir() {
		File dir = new File(getPluginDir(), "auto_update");
		dir.mkdirs();
		
		return dir;
	}	
	
	public File getAppDir() {
		File dir = new File(getPluginDir(), "app");
		dir.mkdirs();
		
		return dir;
	}

	public void scanApkDir(File apkDir) {
		scanApkDir(apkDir, true);
	}
	
	private void scanApkDir(File apkDir, boolean copyFile) {
		//==========123456789012345678
		Log.d(TAG, "parse  dir: " + apkDir);
		if (null == apkDir || !apkDir.exists()) {
			return ;
		}
		String[] files = apkDir.list();
		if (null == files) {
			return;
		}
		
		for (String f : files) {
			File file = new File(apkDir.getAbsolutePath() + "/" + f);
			parseApkFile(file, copyFile);
		}
		
//		mSerUtil.put(mInfos);
	}

	private void parseApkFile(File file, boolean copyFile) {
		//==========123456789012345678
		Log.d(TAG, "parse file: " + file);
		if (file.exists() && file.getAbsolutePath().endsWith(APK_FILE_SUFFIX)){
			PackageInfoX info = ApkManifestParser.parseAPk(mContext, file.getAbsolutePath());			
			try {
				File dest = file;
				if (copyFile) {
					dest = new File(getAppDir(), info.packageName + APK_FILE_SUFFIX);
					AndroidUtil.copyFile(file, dest);
					info = ApkManifestParser.parseAPk(mContext, dest.getAbsolutePath());
				}
				//==========123456789012345678
				Log.d(TAG, "apk info  : " + info.packageName + "|" + info.versionCode + "|" +  info.versionName);
				
				File destLibDir = new File(getPluginDir(), info.packageName + "/lib");
				
				//TODO native lib
				AndroidUtil.extractZipEntry(new ZipFile(info.applicationInfo.publicSourceDir), "lib/armeabi", destLibDir);
				AndroidUtil.extractZipEntry(new ZipFile(info.applicationInfo.publicSourceDir), "lib/armeabi-v7a", destLibDir);
				
				info.mLibPath = destLibDir.getPath();
				
				// asume there is only one apk.
				ClassLoader cl = createClassLoader(info.applicationInfo.sourceDir, info.mLibPath, mContext);
				putClassLoader(info.applicationInfo.sourceDir, cl);				
				
				mInfos.addOrUpdate(info);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ApplicationInfoX getApplicationInfo(String className) {
		ApplicationInfoX a = null;
		boolean has = false;
		for (PackageInfoX m : mInfos) {
			if (className.equals(m.applicationInfo.name)) {
				has = true;
				a = (ApplicationInfoX) m.applicationInfo;
				break;
			}
		}
		
		return a;
	}
	
	public PackageInfoX getPackageInfo(String packageName){
		PackageInfoX p = null;
		for (PackageInfoX a : mInfos) {
			if (a.packageName.equals(packageName)){
				p = a;
				break;
			}
		}
		
		return p;
	}
	
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
	
	public List<PackageInfoX> getAllApks(){
		return mInfos;
	}
	
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
	
	/**
	 * @return
	 * 
	 * NOTE:  <p>
	 * you must init this before use by {@link #init(Application, File)}.
	 */
	public static ApkPackageManager getInstance() {
		if (null == sInstance) {
			sInstance = new ApkPackageManager();
		}
		
		return sInstance;
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
			targetRes = Util.loadApkResource(mTargetApkPath, context);
			resMerger = new ResourcesMerger(targetRes, context.getResources());
			ApkPackageManager.sApk2ResourceMap.put(mTargetApkPath, new WeakReference<ResourcesMerger>(resMerger));
		}
		
		return resMerger;
	}

	public static ClassLoader makeClassLoader(Context context, String apkPath, String libPath) {
		ClassLoader cl = ApkPackageManager.getClassLoader(apkPath);
		if (null == cl) {
			cl = createClassLoader(apkPath, libPath, context);
			ApkPackageManager.putClassLoader(apkPath, (cl));
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
	
	void notSupported() {
		throw new RuntimeException("not supported. you can impl it instead.");
	}

	@Override
	public PackageInfo getPackageInfo(String packageName, int flags)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public String[] currentToCanonicalPackageNames(String[] names) {
		notSupported();
		return null;
	}

	@Override
	public String[] canonicalToCurrentPackageNames(String[] names) {
		notSupported();
		return null;
	}

	@Override
	public Intent getLaunchIntentForPackage(String packageName) {
		notSupported();
		return null;
	}

	@Override
	public Intent getLeanbackLaunchIntentForPackage(String packageName) {
		notSupported();
		return null;
	}

	@Override
	public int[] getPackageGids(String packageName)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public PermissionInfo getPermissionInfo(String name, int flags)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public List<PermissionInfo> queryPermissionsByGroup(String group, int flags)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public PermissionGroupInfo getPermissionGroupInfo(String name, int flags)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public List<PermissionGroupInfo> getAllPermissionGroups(int flags) {
		notSupported();
		return null;
	}

	@Override
	public ApplicationInfo getApplicationInfo(String packageName, int flags)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public ActivityInfo getActivityInfo(ComponentName component, int flags)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public ActivityInfo getReceiverInfo(ComponentName component, int flags)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public ServiceInfo getServiceInfo(ComponentName component, int flags)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public ProviderInfo getProviderInfo(ComponentName component, int flags)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public List<PackageInfo> getInstalledPackages(int flags) {
		notSupported();
		return null;
	}

	@Override
	public List<PackageInfo> getPackagesHoldingPermissions(
			String[] permissions, int flags) {
		notSupported();
		return null;
	}

	@Override
	public int checkPermission(String permName, String pkgName) {
		notSupported();
		return 0;
	}

	@Override
	public boolean addPermission(PermissionInfo info) {
		notSupported();
		return false;
	}

	@Override
	public boolean addPermissionAsync(PermissionInfo info) {
		notSupported();
		return false;
	}

	@Override
	public void removePermission(String name) {
		notSupported();
		
	}

	@Override
	public int checkSignatures(String pkg1, String pkg2) {
		notSupported();
		return 0;
	}

	@Override
	public int checkSignatures(int uid1, int uid2) {
		notSupported();
		return 0;
	}

	@Override
	public String[] getPackagesForUid(int uid) {
		notSupported();
		return null;
	}

	@Override
	public String getNameForUid(int uid) {
		notSupported();
		return null;
	}

	@Override
	public List<ApplicationInfo> getInstalledApplications(int flags) {
		notSupported();
		return null;
	}

	@Override
	public String[] getSystemSharedLibraryNames() {
		notSupported();
		return null;
	}

	@Override
	public FeatureInfo[] getSystemAvailableFeatures() {
		notSupported();
		return null;
	}

	@Override
	public boolean hasSystemFeature(String name) {
		notSupported();
		return false;
	}

	@Override
	public ResolveInfo resolveActivity(Intent intent, int flags) {
		notSupported();
		return null;
	}

	@Override
	public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller,
			Intent[] specifics, Intent intent, int flags) {
		notSupported();
		return null;
	}

	@Override
	public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags) {
		notSupported();
		return null;
	}

	@Override
	public ResolveInfo resolveService(Intent intent, int flags) {
		notSupported();
		return null;
	}

	@Override
	public List<ResolveInfo> queryIntentServices(Intent intent, int flags) {
		notSupported();
		return null;
	}

	@Override
	public List<ResolveInfo> queryIntentContentProviders(Intent intent,
			int flags) {
		notSupported();
		return null;
	}

	@Override
	public ProviderInfo resolveContentProvider(String name, int flags) {
		notSupported();
		return null;
	}

	@Override
	public List<ProviderInfo> queryContentProviders(String processName,
			int uid, int flags) {
		notSupported();
		return null;
	}

	@Override
	public InstrumentationInfo getInstrumentationInfo(ComponentName className,
			int flags) throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public List<InstrumentationInfo> queryInstrumentation(String targetPackage,
			int flags) {
		notSupported();
		return null;
	}

	@Override
	public Drawable getDrawable(String packageName, int resid,
			ApplicationInfo appInfo) {
		notSupported();
		return null;
	}

	@Override
	public Drawable getActivityIcon(ComponentName activityName)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getActivityIcon(Intent intent) throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getActivityBanner(ComponentName activityName)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getActivityBanner(Intent intent)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getDefaultActivityIcon() {
		notSupported();
		return null;
	}

	@Override
	public Drawable getApplicationIcon(ApplicationInfo info) {
		notSupported();
		return null;
	}

	@Override
	public Drawable getApplicationIcon(String packageName)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getApplicationBanner(ApplicationInfo info) {
		notSupported();
		return null;
	}

	@Override
	public Drawable getApplicationBanner(String packageName)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getActivityLogo(ComponentName activityName)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getActivityLogo(Intent intent) throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getApplicationLogo(ApplicationInfo info) {
		notSupported();
		return null;
	}

	@Override
	public Drawable getApplicationLogo(String packageName)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getUserBadgedIcon(Drawable icon, UserHandle user) {
		notSupported();
		return null;
	}

	@Override
	public Drawable getUserBadgedDrawableForDensity(Drawable drawable,
			UserHandle user, Rect badgeLocation, int badgeDensity) {
		notSupported();
		return null;
	}

	@Override
	public CharSequence getUserBadgedLabel(CharSequence label, UserHandle user) {
		notSupported();
		return null;
	}

	@Override
	public CharSequence getText(String packageName, int resid,
			ApplicationInfo appInfo) {
		notSupported();
		return null;
	}

	@Override
	public XmlResourceParser getXml(String packageName, int resid,
			ApplicationInfo appInfo) {
		notSupported();
		return null;
	}

	@Override
	public CharSequence getApplicationLabel(ApplicationInfo info) {
		notSupported();
		return null;
	}

	@Override
	public Resources getResourcesForActivity(ComponentName activityName)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Resources getResourcesForApplication(ApplicationInfo app)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Resources getResourcesForApplication(String appPackageName)
			throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public void verifyPendingInstall(int id, int verificationCode) {
		notSupported();
		
	}

	@Override
	public void extendVerificationTimeout(int id,
			int verificationCodeAtTimeout, long millisecondsToDelay) {
		notSupported();
		
	}

	@Override
	public void setInstallerPackageName(String targetPackage,
			String installerPackageName) {
		notSupported();
		
	}

	@Override
	public String getInstallerPackageName(String packageName) {
		notSupported();
		return null;
	}

	@Override
	@Deprecated
	public void addPackageToPreferred(String packageName) {
		notSupported();
		
	}

	@Override
	@Deprecated
	public void removePackageFromPreferred(String packageName) {
		notSupported();
		
	}

	@Override
	public List<PackageInfo> getPreferredPackages(int flags) {
		notSupported();
		return null;
	}

	@Override
	@Deprecated
	public void addPreferredActivity(IntentFilter filter, int match,
			ComponentName[] set, ComponentName activity) {
		notSupported();
		
	}

	@Override
	public void clearPackagePreferredActivities(String packageName) {
		notSupported();
		
	}

	@Override
	public int getPreferredActivities(List<IntentFilter> outFilters,
			List<ComponentName> outActivities, String packageName) {
		notSupported();
		return 0;
	}

	@Override
	public void setComponentEnabledSetting(ComponentName componentName,
			int newState, int flags) {
		notSupported();
		
	}

	@Override
	public int getComponentEnabledSetting(ComponentName componentName) {
		notSupported();
		return 0;
	}

	@Override
	public void setApplicationEnabledSetting(String packageName, int newState,
			int flags) {
		notSupported();
		
	}

	@Override
	public int getApplicationEnabledSetting(String packageName) {
		notSupported();
		return 0;
	}

	@Override
	public boolean isSafeMode() {
		notSupported();
		return false;
	}

	@Override
	public PackageInstaller getPackageInstaller() {
		notSupported();
		return null;
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
				remove(index);
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
	
	public static class UpdateUtil {
		private static final String PREF_LATEST_VERSION_ID = "latest_version_id";
		static final String PREF_KEY_VERSION_ID = "apk_internal_version_id";
		
		private String mKey;
		
		public UpdateUtil(String perfVersionIdKey){
			mKey = perfVersionIdKey;
		}
		
		public boolean isAppUpdate(Context context) {
			boolean update = false;
			
			try {
				String version = "" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;

				SharedPreferences sp = context.getSharedPreferences(PREF_LATEST_VERSION_ID, 0);
				String lastestVersionId = sp.getString(PREF_KEY_VERSION_ID, "");
				if (!TextUtils.isEmpty(lastestVersionId) && !lastestVersionId.equals(version)) {
					update = true;
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return update;
		}		
		
		public boolean updateVersion(Context context) {
			boolean update = false;
			
			try {
				String version = "" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;

				SharedPreferences sp = context.getSharedPreferences(PREF_LATEST_VERSION_ID, 0);
				sp.edit().putString(PREF_KEY_VERSION_ID, version).commit();
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return update;
		}	
		
		public boolean isFirstUsage(Context context) {
			boolean update = false;
			
			SharedPreferences sp = context.getSharedPreferences(PREF_LATEST_VERSION_ID, 0);
			String lastestVersionId = sp.getString(PREF_KEY_VERSION_ID, "");
			if (TextUtils.isEmpty(lastestVersionId)) {
				update = true;
			}
			
			return update;
		}
	}
}
