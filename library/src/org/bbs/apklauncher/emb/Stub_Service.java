package org.bbs.apklauncher.emb;


import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.ReflectUtil;
import org.bbs.apklauncher.ResourcesMerger;
import org.bbs.apklauncher.TargetContext;
import org.bbs.apkparser.PackageInfoX.ServiceInfoX;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.util.Log;

public class Stub_Service extends Host_Service {
	private static final String TAG = Stub_Service.class.getSimpleName();
	/**
	 * type {@link String}
	 */
	public static final String EXTRA_COMPONENT_CLASS_NAME = Util.SERVICE_EXTRA_COMPONENT_CLASS_NAME;

	private ResourcesMerger mResourceMerger;
	private Application mRealApplication;
	private String mTargetServiceClassName;
	private PackageManager mRealSysPm;
	private ClassLoader mTargetClassLoader;
	private TargetContext mTargetContext;
	private Context mRealBaseContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		mRealBaseContext = newBase;
		mTargetContext = new TargetContext(newBase);
		super.attachBaseContext(mTargetContext);
		
		mRealSysPm = getPackageManager();
	}
	
	protected void onPrepareServiceStub(Intent intent) {
		mTargetServiceClassName = intent.getStringExtra(EXTRA_COMPONENT_CLASS_NAME);
		ServiceInfoX serviceInfo = ApkPackageManager.getInstance().getServiceInfo(mTargetServiceClassName);
		
		String apkPath = serviceInfo.applicationInfo.publicSourceDir;
		String libPath = serviceInfo.mPackageInfo.mLibPath;
		
		Log.d(TAG, "host service               : " + this);
		Log.d(TAG, "targetApplicationClassName : " + serviceInfo.applicationInfo.className);
		Log.d(TAG, "targetPackageName          : " + serviceInfo.packageName);
		Log.d(TAG, "targetServiceClassName     : " + mTargetServiceClassName);
		Log.d(TAG, "targetApkPath              : " + apkPath);
		Log.d(TAG, "targetLibPath              : " + libPath);
		
		mTargetClassLoader = ApkPackageManager.makeClassLoader(mRealBaseContext, apkPath, libPath);
		mTargetContext.classLoaderReady(mTargetClassLoader);
		
		// do application init. must before service init.
		Application app = ((Host_Application)getApplication()).onPrepareApplictionStub(serviceInfo.applicationInfo, 
																						mTargetClassLoader, mRealSysPm);
		
		try {
			mResourceMerger = ApkPackageManager.makeTargetResource(apkPath, mRealBaseContext);
			
			mTargetContext.resReady(mResourceMerger);
//			mTargetContext.packageNameReady(serviceInfo.applicationInfo.packageName);
			
			mTargetService = (Target_Service) mTargetClassLoader.loadClass(mTargetServiceClassName).newInstance();
				ReflectUtil.ActivityReflectUtil.attachBaseContext(mTargetService, 
						this
						);		
				if (DEBUG_LIEFT_CYCLE) {
					Log.d(TAG, "call target service onCreate().");
				}
				mTargetService.onCreate();
		} catch (Exception e) {
			throw new RuntimeException("error in create target service.  class: " + mTargetServiceClassName, e);
		}
	}
	
	private ClassLoader onCreateClassLoader(String apkPath, String libPath) {	
		return ApkPackageManager.createClassLoader(apkPath, libPath, this);
	}
	
	@Override
	public Object getSystemService(String name) {
		if (Context.NOTIFICATION_SERVICE.equals(name) && null != mRealApplication) {
			return mRealApplication.getSystemService(name);
		}
		return super.getSystemService(name);
	}
	
	@Override
	public Theme getTheme() {
		return mTargetContext.getTheme();
	}
	@Override
	public Resources getResources() {
		return mTargetContext.getResources();
	}
	
}
