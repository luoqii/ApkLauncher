package org.bbs.apklauncher.emb;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;

public class Target_Service extends 
//Service
ContextWrapper
{
	private static final String TAG = Target_Service.class.getSimpleName();
	
    public static final int START_CONTINUATION_MASK = Service.START_CONTINUATION_MASK;
    public static final int START_STICKY_COMPATIBILITY = Service.START_STICKY_COMPATIBILITY;
    public static final int START_STICKY = Service.START_STICKY;
    public static final int START_NOT_STICKY = Service.START_NOT_STICKY;
    public static final int START_REDELIVER_INTENT = Service.START_REDELIVER_INTENT;
    public static final int START_FLAG_REDELIVERY = Service.START_FLAG_REDELIVERY;
    public static final int START_FLAG_RETRY = Service.START_FLAG_RETRY;

	private boolean mStartCompatibility;
	
	Host_Service mHostService;
	
	public Context getHostContext() {
		return mHostService.getHostContext();
	}
	
	public Target_Service(){
		super(null);
		
//        mStartCompatibility = getApplicationInfo().targetSdkVersion
//                < Build.VERSION_CODES.ECLAIR;
	}

	@Override
	protected void attachBaseContext(Context base) {
		mHostService = (Host_Service) base;
		super.attachBaseContext(base);
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
	}

	public void onStart(Intent intent, int startId) {
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return mStartCompatibility ? START_STICKY_COMPATIBILITY : START_STICKY;
	}

	public void onDestroy() {
	}

	public void onConfigurationChanged(Configuration newConfig) {
	}

	public void onLowMemory() {
	}

	public void onTrimMemory(int level) {
	}

	public boolean onUnbind(Intent intent) {
        return false;
	}

	public void onRebind(Intent intent) {
	}

	public void onTaskRemoved(Intent rootIntent) {
	}

	protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
	}
	
	public final void stopSelf() {
        stopSelf(-1);
    }

    public final void stopSelf(int startId) {
    	mHostService.stopSelf(startId);
    }
    
    public final boolean stopSelfResult(int startId) {
    	return mHostService.stopSelfResult(startId);
    }
    
    public final void setForeground(boolean isForeground) {
    	Log.w(TAG, "setForeground() is not defined for Service...");
//    	mHostService.setForeground(isForeground);
    }
    
    public final void startForeground(int id, Notification notification) {
    	mHostService.startForeground(id, notification);
    }
    
    public final void stopForeground(boolean removeNotification) {
    	mHostService.stopForeground(removeNotification);
    }
}
