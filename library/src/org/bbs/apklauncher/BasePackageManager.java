package org.bbs.apklauncher;

import java.util.List;

import android.content.ComponentName;
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

public abstract class BasePackageManager extends PackageManager {

	public BasePackageManager() {
		super();
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
	public int[] getPackageGids(String packageName) throws NameNotFoundException {
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
	public List<PackageInfo> getPackagesHoldingPermissions(String[] permissions, int flags) {
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
	public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics,
			Intent intent, int flags) {
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
	public List<ResolveInfo> queryIntentContentProviders(Intent intent, int flags) {
		notSupported();
		return null;
	}

	@Override
	public ProviderInfo resolveContentProvider(String name, int flags) {
		notSupported();
		return null;
	}

	@Override
	public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) {
		notSupported();
		return null;
	}

	@Override
	public InstrumentationInfo getInstrumentationInfo(ComponentName className, int flags)
			throws NameNotFoundException {
				notSupported();
				return null;
			}

	@Override
	public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) {
		notSupported();
		return null;
	}

	@Override
	public Drawable getDrawable(String packageName, int resid, ApplicationInfo appInfo) {
		notSupported();
		return null;
	}

	@Override
	public Drawable getActivityIcon(ComponentName activityName) throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getActivityIcon(Intent intent) throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getActivityBanner(ComponentName activityName) throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getActivityBanner(Intent intent) throws NameNotFoundException {
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
	public Drawable getApplicationIcon(String packageName) throws NameNotFoundException {
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
	public Drawable getActivityLogo(ComponentName activityName) throws NameNotFoundException {
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
	public Drawable getApplicationLogo(String packageName) throws NameNotFoundException {
		notSupported();
		return null;
	}

	@Override
	public Drawable getUserBadgedIcon(Drawable icon, UserHandle user) {
		notSupported();
		return null;
	}

	@Override
	public Drawable getUserBadgedDrawableForDensity(Drawable drawable, UserHandle user,
			Rect badgeLocation, int badgeDensity) {
				notSupported();
				return null;
			}

	@Override
	public CharSequence getUserBadgedLabel(CharSequence label, UserHandle user) {
		notSupported();
		return null;
	}

	@Override
	public CharSequence getText(String packageName, int resid, ApplicationInfo appInfo) {
		notSupported();
		return null;
	}

	@Override
	public XmlResourceParser getXml(String packageName, int resid, ApplicationInfo appInfo) {
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
	public void extendVerificationTimeout(int id, int verificationCodeAtTimeout,
			long millisecondsToDelay) {
				notSupported();
				
			}

	@Override
	public void setInstallerPackageName(String targetPackage, String installerPackageName) {
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
	public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set,
			ComponentName activity) {
				notSupported();
				
			}

	@Override
	public void clearPackagePreferredActivities(String packageName) {
		notSupported();
		
	}

	@Override
	public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) {
		notSupported();
		return 0;
	}

	@Override
	public void setComponentEnabledSetting(ComponentName componentName, int newState,
			int flags) {
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

	void notSupported() {
		throw new RuntimeException("not supported. you can impl it instead.");
	}

}