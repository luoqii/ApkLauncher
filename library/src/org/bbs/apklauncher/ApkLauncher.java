package org.bbs.apklauncher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bbs.apklauncher.api.ExportApi;
import org.bbs.apklauncher.emb.IntentHelper;
import org.bbs.apkparser.PackageInfoX;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author bysong
 *
 */
@ExportApi
public class ApkLauncher {
	private static final String TAG = ApkLauncher.class.getSimpleName();
	public static final String ASSET_PLUGIN_DIR = "plugin";
    public static final String EXTRA_TARGET_COMPONENT_CLASS_NAME = "EXTRA_TARGET_COMPONENT_CLASS_NAME";
    public static final String EXTRA_HOST_COMPONENT_CLASS_NAME = "EXTRA_HOST_COMPONENT_CLASS_NAME";
    public static final String MANIFEST_META_REQUIRE_MIN_SDK_VERSION = "apklauncher.min.sdk.version";
    
    private Map<String, String> mT2HMap;
    
	private static ApkLauncher sInstance;
	
	public static ApkLauncher getInstance(){
		if (sInstance == null) {
			sInstance = new ApkLauncher();
		}
		
		return sInstance;
	}

	private OnProcessIntent mListener;
	
	private ApkLauncher() {
		mT2HMap = new HashMap<String, String>();
	}	
		
	public void init(Application context, String assetsPath, boolean overwrite){
		ApkPackageManager.getInstance().init(context, assetsPath, overwrite);
	}
	
	public void initAsync(final Application context, final String assetsPath, final boolean overwrite, final InitCallBack callback){
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				init(context, assetsPath, overwrite);
				return null;
			}
			
			protected void onPostExecute(Void result) {
				if (callback != null){
					callback.onInited();
				}
			};
			
		}.execute();
	}
	
	public void setT2HMap(TKey key, String hostStubClassName){
		mT2HMap.put(key.key(), hostStubClassName);
	}
	
	public void setOnProcessIntentCallback(OnProcessIntent listener){
		mListener = listener;
	}
	
	public boolean onProcessIntent(Intent intent, ClassLoader targetClassLoader, Context hostContext) {
		if (mListener != null && mListener.onProcessStartActivityIntent(intent, targetClassLoader, hostContext)) {
			return true;
		}
		
		Log.i(TAG, "processIntent. intent: " + intent);
		List<ResolveInfo> acts = ApkPackageManager.getInstance().queryIntentActivities(intent, 0);
		if (acts.size() > 0) {
			Log.i(TAG, "intent matchs a installed plugin.");
			ActivityInfo aInfo = acts.get(0).activityInfo;
			// may be we need a new classloader.
			targetClassLoader = ApkPackageManager.getInstance().createClassLoader(hostContext, ((ActivityInfoX)aInfo).mPackageInfo);
			prepareIntent(intent, targetClassLoader, hostContext, aInfo.name);

			return true;
		} else {
			Log.w(TAG, "can not handle intent:  "  + intent);

			return false;
		}
	}

	// inject new stub class name
	public void prepareIntent(Intent intent, ClassLoader targetClassLoader,
			Context hostContext, String targetClassName) {
		ComponentName com;
		String superClassName = ApkUtil.getSuperClassName(targetClassLoader, targetClassName);
		String hostClassName = targetStubtoHostStubClassName(superClassName);
		com = new ComponentName(hostContext.getPackageName(), hostClassName);
		// inject and replace with our component.
		intent.setComponent(com);
		ActivityInfoX a = ApkPackageManager.getInstance().getActivityInfo(targetClassName);
		if (a != null) {
			intent.putExtra(EXTRA_TARGET_COMPONENT_CLASS_NAME, a.name);
			intent.putExtra(EXTRA_HOST_COMPONENT_CLASS_NAME, hostClassName);
		}
	}	

	public void startActivity(Context context, PackageInfoX.ActivityInfoX a) {		
		ClassLoader cl = ApkPackageManager.getInstance().createClassLoader(context, a.mPackageInfo);
		startActivity(context, cl, a);
	}
	
	public void startActivity(Context context, ClassLoader classloader, PackageInfoX.ActivityInfoX a) {
		if (null == a) {
			throw new RuntimeException("activity info in null");
		}
		Intent launcher = new Intent();
		launcher.setComponent(new ComponentName(a.packageName, a.name));

		onProcessIntent(launcher, classloader, context);
		
		launcher.putExtra(IntentHelper.EXTRA_INJECT, false);
		context.startActivity(launcher);
	}

	private String targetStubtoHostStubClassName(String targetStubClass) {
		if (mT2HMap.containsKey(targetStubClass)){
			return mT2HMap.get(targetStubClass);
		}
		
		return targetStubClass.replace("Target", "Stub");
	}
	
	public static interface OnProcessIntent {
		public boolean onProcessStartActivityIntent(Intent intent, ClassLoader targetClassLoader, Context hostContext);
	}
	
	public static enum TKey {
		ACTIVITY           ("org.bbs.apklauncher.emb.auto_gen.Target_Activity"),
		ACTIVITY_GROUP     ("org.bbs.apklauncher.emb.auto_gen.Target_ActivityGroup"),
		EXPENDABLE_ACTIVITY("org.bbs.apklauncher.emb.auto_gen.Target_ActivityGroup"),
		LIST_ACTIVITY      ("org.bbs.apklauncher.emb.auto_gen.Target_ListActivity"),
		FRAGMENT_ACTIVITY  ("org.bbs.apklauncher.emb.auto_gen.Target_FragmentActivity"),
		ACTIONBAR_ACTIVITY ("org.bbs.apklauncher.emb.auto_gen.Target_ActionBarActivity"),
		PREFERENCE_ACTIVITY("org.bbs.apklauncher.emb.auto_gen.Target_PreferenceActivity"),
		TAB_ACTIVITY       ("org.bbs.apklauncher.emb.auto_gen.Target_TabActivity");
		
		private String mKey;

		private TKey(String key){
			mKey = key;
		}
		
		public String key(){
			return mKey;
		}
	}
	
	public static interface InitCallBack{
		public void onInited();
	}
}
