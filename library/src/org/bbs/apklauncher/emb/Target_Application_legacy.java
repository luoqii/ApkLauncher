package org.bbs.apklauncher.emb;

import android.annotation.SuppressLint;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.Application.OnProvideAssistDataListener;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;

@SuppressLint("NewApi")
public class Target_Application_legacy extends 
//Application
ContextWrapper
{
	private Host_Application mHostApplication;
	
	public Target_Application_legacy(Context base) {
		super(base);
	}	
	public Target_Application_legacy() {
		super(null);
	}
	
	public Host_Application getHostApplication() {
		return mHostApplication;
	}
	
	protected void attachBaseContext(Context base) {
//		mHostApplication = (Host_Application) base;
		super.attachBaseContext(base);
	}
	
	void setHostApplication(Host_Application app) {
		mHostApplication = app;
	}

	public void onCreate() {
	}

	public void onTerminate() {
	}

	public void onConfigurationChanged(Configuration newConfig) {
	}

	public void onLowMemory() {
	}

	public void onTrimMemory(int level) {
	}

	public void registerComponentCallbacks(ComponentCallbacks callback) {
		mHostApplication.registerComponentCallbacks(callback);
	}

	public void unregisterComponentCallbacks(ComponentCallbacks callback) {
		mHostApplication.unregisterComponentCallbacks(callback);
	}

	public void registerActivityLifecycleCallbacks(
			ActivityLifecycleCallbacks callback) {
		mHostApplication.registerActivityLifecycleCallbacks(callback);
	}

	public void unregisterActivityLifecycleCallbacks(
			ActivityLifecycleCallbacks callback) {
		mHostApplication.unregisterActivityLifecycleCallbacks(callback);
	}

	public void registerOnProvideAssistDataListener(
			OnProvideAssistDataListener callback) {
		mHostApplication.registerOnProvideAssistDataListener(callback);
	}

	public void unregisterOnProvideAssistDataListener(
			OnProvideAssistDataListener callback) {
		mHostApplication.unregisterOnProvideAssistDataListener(callback);
	}
	
}
