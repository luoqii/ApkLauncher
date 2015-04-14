package org.bbs.apklauncher;

import java.io.File;
import java.io.IOException;
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
import dalvik.system.DexClassLoader;

public class ApkPackageManager extends PackageManager {
	private static ApkPackageManager sInstance;
	
	public static Map<String, WeakReference<ClassLoader>> sApk2ClassLoaderMap = new HashMap<String, WeakReference<ClassLoader>>();
	public static Map<String, WeakReference<Application>> sApk2ApplicationtMap = new HashMap<String, WeakReference<Application>>();
	public static Map<String, WeakReference<ResourcesMerger>> sApk2ResourceMap = new HashMap<String, WeakReference<ResourcesMerger>>();

	public static ClassLoader sLastClassLoader;

	private ArrayList<PackageInfoX> mInfos;
	private Application mContext;
	
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
}