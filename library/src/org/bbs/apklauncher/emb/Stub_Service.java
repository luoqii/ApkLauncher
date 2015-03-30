package org.bbs.apklauncher.emb;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.bbs.apklauncher.InstalledAPks;
import org.bbs.apklauncher.ReflectUtil;
import org.bbs.apklauncher.ResourcesMerger;
import org.bbs.apkparser.PackageInfoX.ServiceInfoX;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

public class Stub_Service extends Host_Service {
	/**
	 * type {@link ComponentName}
	 */
	public static final String EXTRA_COMPONENT = Util.ACTIVITY_EXTRA_COMPONENT_CLASS_NAME;
	private ClassLoader sLastClassLoader;
	private ComponentName mComponent;
	private ClassLoader mClassLoader;
	private String mApkPath;
	private String mServiceClassNmae;
	private boolean mCallOnCreate;
	public static Map<String, WeakReference<ResourcesMerger>> sApk2ResourceMap = new HashMap<String, WeakReference<ResourcesMerger>>();
	private ResourcesMerger mResourceMerger;
	private Resources mTargetResource;
	private Application mRealApplication;
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	protected void onPrepareServiceStub(Intent intent) {
		if (null != sLastClassLoader) {
			return ;
		}
		
		// how to get classloader berfore parse intent.
		if (InstalledAPks.sLastClassLoader != null) {
			sLastClassLoader = InstalledAPks.sLastClassLoader;
		} 
		if (sLastClassLoader != null) {
//			mTargetContext.classLoaderReady(sLastClassLoader);
			intent.setExtrasClassLoader(sLastClassLoader);
			intent.getExtras().setClassLoader(sLastClassLoader);
		}
		
		mComponent = intent.getParcelableExtra(EXTRA_COMPONENT);
		mServiceClassNmae = mComponent.getClassName();
		ServiceInfoX s = InstalledAPks.getInstance().getServiceInfo(mServiceClassNmae);
		
		mApkPath = s.applicationInfo.publicSourceDir;
		mClassLoader = InstalledAPks.getClassLoader(mApkPath);
		if (null == mClassLoader) {
			mClassLoader = onCreateClassLoader(mApkPath, s.mPackageInfo.mLibPath);
			InstalledAPks.putClassLoader(mApkPath, (mClassLoader));
		}
		sLastClassLoader = mClassLoader;
		
		try {
			WeakReference<ResourcesMerger> rr = sApk2ResourceMap.get(mApkPath);
			if (rr != null && rr.get() != null) {
				mResourceMerger = rr.get();
				mTargetResource = mResourceMerger.mFirst;
			} else {
				mTargetResource = InstalledAPks.makeTargetResource(mApkPath, this);
				mResourceMerger = new ResourcesMerger(mTargetResource, getResources());
				sApk2ResourceMap.put(mApkPath, new WeakReference<ResourcesMerger>(mResourceMerger));
			}

//			if (mTargetThemeId  > 0) {
//			} else {
//			}
//			mTargetThemeId = ReflectUtil.ResourceUtil.selectDefaultTheme(mResourceMerger, mTargetThemeId, mActInfo.applicationInfo.targetSdkVersion);
//			Log.d(TAG, "resolved activity theme: " + mTargetThemeId);
//			mTargetContext.setTheme(mTargetThemeId);
//			mTargetContext.themeReady(mTargetThemeId);
			
			mTargetContext.resReady(mResourceMerger);
			mTargetContext.packageNameReady(s.applicationInfo.packageName);
			
			mRealApplication = getApplication();
			Application app = ((Host_Application)mRealApplication).onPrepareApplictionStub(s.applicationInfo, mClassLoader,
																							null);
			ReflectUtil.ActivityReflectUtil.setServiceApplication(this, app);
			
			mTargetService = (Target_Service) mClassLoader.loadClass(mServiceClassNmae).newInstance();
			if (!mCallOnCreate) {
//				ReflectUtil.ActivityReflectUtil.setApplication(this, app);
				ReflectUtil.ActivityReflectUtil.attachBaseContext(mTargetService, 
						this
//						mTargetContext
						);
				mTargetService.onCreate();
				mCallOnCreate = true;
			}
		} catch (Exception e) {
			throw new RuntimeException("error in create target service.  class: " + mServiceClassNmae, e);
		}
	}
	
	private ClassLoader onCreateClassLoader(String apkPath, String libPath) {	
		return InstalledAPks.createClassLoader(apkPath, libPath, this);
	}
	
	@Override
	public Object getSystemService(String name) {
		if (Context.NOTIFICATION_SERVICE.equals(name) && null != mRealApplication) {
			return mRealApplication.getSystemService(name);
		}
		return super.getSystemService(name);
	}
	
//	@Override
//	public Theme getTheme() {
//		if (null != mTargetContext) {
//			return mTargetContext.getTheme();
//		} else {
//			return super.getTheme();
//		}
//	}
//
//	// for Window to get target's resource
//	public Resources getResources() {
//		if (null != mTargetContext ) {
//			return mTargetContext.getResources();
//		} else {
//			return super.getResources();
//		}
//	}	
	
}
