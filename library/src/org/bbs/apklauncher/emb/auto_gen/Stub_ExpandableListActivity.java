package org.bbs.apklauncher.emb.auto_gen;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.bbs.apklauncher.ReflectUtil.ActivityReflectUtil;

import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.PackageManagerProxy;
import org.bbs.apklauncher.emb.Host_Application;
import org.bbs.apklauncher.emb.IntentHelper;
import org.bbs.apklauncher.emb.Util;
import org.bbs.apklauncher.emb.ViewCreater;
import org.bbs.apklauncher.emb.auto_gen.Target_Activity;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;
import org.bbs.apklauncher.InstrumentationWrapper;
import org.bbs.apklauncher.InstrumentationWrapper.CallBack;
import org.bbs.apklauncher.ReflectUtil;
import org.bbs.apklauncher.ResourcesMerger;
import org.bbs.apklauncher.TargetContext;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//do NOT edit this file, auto-generated by host_target.groovy from Target_Activity.java.template
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import dalvik.system.DexClassLoader;

// keep logic outside of this as many as possible.
public class Stub_ExpandableListActivity extends 
StubBase_ExpandableListActivity
implements CallBack {

	/**
	 * type {@link String}
	 */
	public static final String EXTRA_COMPONENT_CLASS_NAME = Util.ACTIVITY_EXTRA_COMPONENT_CLASS_NAME;
	
	private static final String TAG = StubBase_ExpandableListActivity.class.getSimpleName();
	
	private ClassLoader mTargetClassLoader;
	private TargetContext mTargetContext;
	private ActivityInfoX mTargetActivityInfo;
	private ResourcesMerger mResourceMerger;
//do NOT edit this file, auto-generated by host_target.groovy from Target_Activity.java.template
	
	private PackageManager mRealSysPm;
	private Context 	   mRealBaseContext;	
	
	private IntentHelper mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		onPrepareActivityStub();
		
		super.onCreate(savedInstanceState);
		
		updateTitle();
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		mRealBaseContext = newBase;
		mTargetContext = new TargetContext(newBase);
		super.attachBaseContext(mTargetContext);
		
		mRealSysPm = getPackageManager();
	}

	private void onPrepareActivityStub() {
		
		Intent intent = getIntent();
//do NOT edit this file, auto-generated by host_target.groovy from Target_Activity.java.template
		// TODO is there a way to update ClassLoader before parse intent?
		//intent.getExtras().setClassLoader(loader);
		
		// get target activity info
		String targetActivityClassName = intent.getStringExtra(EXTRA_COMPONENT_CLASS_NAME);
		mTargetActivityInfo = ApkPackageManager.getInstance().getActivityInfo(targetActivityClassName);
		String libPath = mTargetActivityInfo.mPackageInfo.mLibPath;
		String targetApplicationClassName = mTargetActivityInfo.applicationInfo.className;
		int targetThemeId = mTargetActivityInfo.theme;
		String apkPath = mTargetActivityInfo.applicationInfo.publicSourceDir;
		if (TextUtils.isEmpty(targetApplicationClassName)){
			targetApplicationClassName = Application.class.getCanonicalName();
			Log.d(TAG, "no packageName, user default.");
		}
		String targetPackageName = mTargetActivityInfo.packageName;

		Log.d(TAG, "host activity              : " + this);
		Log.d(TAG, "targetApplicationClassName : " + targetApplicationClassName);
		Log.d(TAG, "targetPackageName          : " + targetPackageName);
		Log.d(TAG, "targetActivityClassName    : " + targetActivityClassName);
		Log.d(TAG, "targetThemeId              : " + targetThemeId);
		Log.d(TAG, "targetApkPath              : " + apkPath);
		Log.d(TAG, "targetLibPath              : " + libPath);
		
		// create target classloader if necessary.
		mTargetClassLoader = ApkPackageManager.makeClassLoader(mRealBaseContext, apkPath, libPath);
		mTargetContext.classLoaderReady(mTargetClassLoader);
//do NOT edit this file, auto-generated by host_target.groovy from Target_Activity.java.template

		// do application init. must before activity init.
		Application app = ((Host_Application)getApplication()).onPrepareApplictionStub(mTargetActivityInfo.applicationInfo, 
																						mTargetClassLoader, mRealSysPm);
		
		// do activity init
		InstrumentationWrapper.injectInstrumentation(this, this);
		try {
			mResourceMerger = ApkPackageManager.makeTargetResource(apkPath, mRealBaseContext);

			targetThemeId = ReflectUtil.ResourceUtil.selectDefaultTheme(mResourceMerger, targetThemeId, 
																			mTargetActivityInfo.applicationInfo.targetSdkVersion);

			Log.d(TAG, "resolved activity theme: " + targetThemeId);
			mTargetContext.setTheme(targetThemeId);
			mTargetContext.themeReady(targetThemeId);
			mTargetContext.resReady(mResourceMerger);
			mTargetContext.applicationContextReady(ApkPackageManager.getApplication(targetPackageName));
			
			ReflectUtil.ActivityReflectUtil.setActivityApplication(this, app);
			Class clazz = mTargetClassLoader.loadClass(targetActivityClassName);
			mTargetActivity = (Target_ExpandableListActivity) clazz.newInstance();
			mTargetActivity.setHostActivity(this);
			ReflectUtil.ActivityReflectUtil.attachBaseContext(mTargetActivity, this);
		} catch (Exception e) {
			throw new RuntimeException("error in create activity: " + targetActivityClassName , e);
		}		
//do NOT edit this file, auto-generated by host_target.groovy from Target_Activity.java.template
	}
	
	@Override
	public Intent getIntent() {
		if (null == mIntent) {
			mIntent = new IntentHelper(super.getIntent());
		}
		return mIntent;
		
//		return super.getIntent();
	}

	private void updateTitle() {
		CharSequence title = "";
		if (mTargetActivityInfo.labelRes  > 0) {
			title = mResourceMerger.getString(mTargetActivityInfo.labelRes);
		}
		if (TextUtils.isEmpty(title)) {
			title = mTargetActivityInfo.nonLocalizedLabel;
		}
		if (!TextUtils.isEmpty(title)) {
			setTitle(title);
		}
	}

	// XXX are we need this really???
	// Activity extends ContextThemeWrapper which had these 2 methods,
//do NOT edit this file, auto-generated by host_target.groovy from Target_Activity.java.template
	// so we override those with ours.
	@Override
	public Theme getTheme() {
		return mTargetContext.getTheme();
	}
	@Override
	public Resources getResources() {
		return mTargetContext.getResources();
	}

	@Override
	public void processIntent(android.content.Intent intent) {
		 Util.onProcessStartActivityIntent(intent, mTargetClassLoader, mRealBaseContext);
	}
	
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		View view = super.onCreateView(name, context, attrs);
		return view != null ? view 
							: ViewCreater.onCreateView(name, context, attrs, mTargetClassLoader, mTargetActivity);
	}
}
