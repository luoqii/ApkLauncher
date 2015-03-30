package org.bbs.apklauncher.emb;

import org.bbs.apklauncher.TargetContext;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("NewApi")
public abstract class Host_Service extends Service {
	private static final String TAG = Host_Service.class.getSimpleName();
	private static boolean DEBUG_LIEFT_CYCLE = true;
	private static boolean DEBUG_MEMORY = true;
	private static boolean DEBUG = true;
	
	Target_Service mTargetService;
	protected TargetContext mTargetContext;
	Context mRealBaseContext;
	private PackageManager mSysPm;

	@Override
	protected void attachBaseContext(Context newBase) {
		mRealBaseContext = newBase;
		mTargetContext = new TargetContext(newBase);
		super.attachBaseContext(mTargetContext);
		mSysPm = getPackageManager();
	}
	
	Context getHostContext() {
		return mRealBaseContext;
	}

	abstract protected void onPrepareServiceStub(Intent intent) ;
	
	@Override
	public IBinder onBind(Intent intent) {
		if (DEBUG) {
			Log.d(TAG, "onBind(). intent: " + intent);
		}
		onPrepareServiceStub(intent);
		return mTargetService.onBind(intent);
	}

	@Override
	public void onCreate() {
		if (DEBUG_LIEFT_CYCLE) {
			Log.d(TAG, "onCreate(). ");
		}
		super.onCreate();
		if (null != mTargetService) {
			mTargetService.onCreate();
		}
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		onPrepareServiceStub(intent);
		super.onStart(intent, startId);
		if (null != mTargetService) {
			mTargetService.onStart(intent, startId);
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onPrepareServiceStub(intent);
//		return super.onStartCommand(intent, flags, startId);
		return mTargetService.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		if (DEBUG_LIEFT_CYCLE) {
			Log.d(TAG, "onDestroy(). ");
		}
		super.onDestroy();
		if (null != mTargetService) {
			mTargetService.onDestroy();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (DEBUG) {
			Log.d(TAG, "onConfigurationChanged(). newConfig: " + newConfig);
		}
		super.onConfigurationChanged(newConfig);
		if (null != mTargetService) {
			mTargetService.onConfigurationChanged(newConfig);
		}
	}
	
	@Override
	public void onLowMemory() {
		if (DEBUG_MEMORY) {
			Log.d(TAG, "onLowMemory(). ");
		}
		super.onLowMemory();
		if (null != mTargetService) {
			mTargetService.onLowMemory();
		}
	}
	
	@Override
	public void onTrimMemory(int level) {
		if (DEBUG_MEMORY) {
			Log.d(TAG, "onTrimMemory(). level: " + Util.toMemoryLevel(level));
		}
		super.onTrimMemory(level);
		if (null != mTargetService) {
			mTargetService.onTrimMemory(level);
		}
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
//		return super.onUnbind(intent);
		if (DEBUG) {
			Log.d(TAG, "onUnbind(). intent: " + intent);
		}
		if (null != mTargetService) {
			return mTargetService.onUnbind(intent);
		} else {
			return false;
		}
	}
	
	
	@Override
	public void onRebind(Intent intent) {
		if (DEBUG) {
			Log.d(TAG, "onRebind(). intent: " + intent);
		}
		super.onRebind(intent);
		if (null != mTargetService) {
			mTargetService.onRebind(intent);
		}
	}
	
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		if (null != mTargetService) {
			mTargetService.onTaskRemoved(rootIntent);
		}
	}
}
