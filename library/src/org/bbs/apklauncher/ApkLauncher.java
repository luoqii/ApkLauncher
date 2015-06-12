package org.bbs.apklauncher;

import java.util.HashMap;
import java.util.Map;

import org.bbs.apklauncher.api.ExportApi;
import org.bbs.apklauncher.emb.IntentHelper;
import org.bbs.apklauncher.emb.auto_gen.Stub_Activity;
import org.bbs.apkparser.PackageInfoX;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

    public static final String EXTRA_TARGET_COMPONENT_CLASS_NAME = "EXTRA_TARGET_COMPONENT_CLASS_NAME";
    public static final String EXTRA_HOST_COMPONENT_CLASS_NAME = "EXTRA_HOST_COMPONENT_CLASS_NAME";
    
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
	
	public void init(Application context, String assetsPath, boolean force){
		ApkPackageManager.getInstance().init(context, assetsPath, force);
	}
	
	public void initAsync(final Application context, final String assetsPath, final boolean force, final InitCallBack callback){
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				init(context, assetsPath, force);
				return null;
			}
			
			protected void onPostExecute(Void result) {
				if (callback != null){
					callback.onInited();
				}
			};
			
		}.execute();
	}
	
	public void setT2HMap(TKey key, String hostComClassName){
		mT2HMap.put(key.key(), hostComClassName);
	}
	
	public void setOnProcessIntentCallback(OnProcessIntent listener){
		mListener = listener;
	}
	
	public void onProcessIntent(Intent intent, ClassLoader targetClassLoader, Context hostContext) {
		if (mListener != null && mListener.onProcessStartActivityIntent(intent, targetClassLoader, hostContext)) {
			return;
		}
		
		Log.d(TAG, "processIntent. intent: " + intent);
		ComponentName com = intent.getComponent();
		if (null != com) {
			String className = com.getClassName();
			if (!TextUtils.isEmpty(className)) {
				prepareIntent(intent, targetClassLoader, hostContext, className);
			}
		} else {
			Log.w(TAG, "can not handle intent:  "  + intent);
		}
	}

	private void prepareIntent(Intent intent, ClassLoader targetClassLoader,
			Context hostContext, String className) {
		ComponentName com;
		String superClassName = ApkUtil.getSuperClassName(targetClassLoader, className);
		String hostClassName = toHostStubClassName(superClassName);
		com = new ComponentName(hostContext.getPackageName(), hostClassName);
		// inject and replace with our component.
		intent.setComponent(com);
		ActivityInfoX a = ApkPackageManager.getInstance().getActivityInfo(className);
		if (a != null) {
			intent.putExtra(EXTRA_TARGET_COMPONENT_CLASS_NAME, a.name);
			intent.putExtra(EXTRA_HOST_COMPONENT_CLASS_NAME, hostClassName);
		}
	}	

	public void startActivity(Context context, PackageInfoX.ActivityInfoX a) {
		if (null == a) {
			throw new RuntimeException("activity info in null");
		}
		
		ClassLoader cl = ApkPackageManager.getClassLoader(a.applicationInfo.packageName);
		Intent launcher = new Intent();

        // inject and replace with our component.
//		String superClassName = ApkUtil.getSuperClassName(cl, a.name);
//		String comClassName = superClassName.replace("Target", "Stub");
//		ComponentName com= new ComponentName(context.getPackageName(), comClassName);
//		launcher.setComponent(com);
//		launcher.putExtra(Stub_Activity.EXTRA_TARGET_COMPONENT_CLASS_NAME, a.name);
		prepareIntent(launcher, cl, context, a.name);
		
		launcher.putExtra(IntentHelper.EXTRA_INJECT, false);
		context.startActivity(launcher);
	}

	private String toHostStubClassName(String superClassName) {
		if (mT2HMap.containsKey(superClassName)){
			return mT2HMap.get(superClassName);
		}
		
		return superClassName.replace("Target", "Stub");
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
