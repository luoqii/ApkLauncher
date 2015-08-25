package org.bbs.apklauncher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bbs.apklauncher.ReflectUtil.ActivityReflectUtil;

import android.R.array;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * 
 * inspired by {@link ContextWrapper}
 * 
 * @author bysong
 *
 */
public class InstrumentationWrapper extends Instrumentation {
		private static final String TAG = InstrumentationWrapper.class.getSimpleName();

		private static final boolean LOG_CALL = ApkLauncherConfig.DEBUG && true;
		
		private Instrumentation mBase;
		
		public InstrumentationWrapper(Instrumentation base){
			mBase = base;
		}
		
		public Instrumentation getBase(){
			return mBase;
		}
				
		// call base method instead.
		@Override
		public void onCreate(Bundle arguments) {
			// TODO Auto-generated method stub
			mBase.onCreate(arguments);
		}

		@Override
		public void start() {
			// TODO Auto-generated method stub
			mBase.start();
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			mBase.onStart();
		}

		@Override
		public boolean onException(Object obj, Throwable e) {
			// TODO Auto-generated method stub
			return mBase.onException(obj, e);
		}

		@Override
		public void sendStatus(int resultCode, Bundle results) {
			// TODO Auto-generated method stub
			mBase.sendStatus(resultCode, results);
		}

		@Override
		public void finish(int resultCode, Bundle results) {
			// TODO Auto-generated method stub
			mBase.finish(resultCode, results);
		}

		@Override
		public void setAutomaticPerformanceSnapshots() {
			// TODO Auto-generated method stub
			mBase.setAutomaticPerformanceSnapshots();
		}

		@Override
		public void startPerformanceSnapshot() {
			// TODO Auto-generated method stub
			mBase.startPerformanceSnapshot();
		}

		@Override
		public void endPerformanceSnapshot() {
			// TODO Auto-generated method stub
			mBase.endPerformanceSnapshot();
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			mBase.onDestroy();
		}

		@Override
		public Context getContext() {
			// TODO Auto-generated method stub
			return mBase.getContext();
		}

		@Override
		public ComponentName getComponentName() {
			// TODO Auto-generated method stub
			return mBase.getComponentName();
		}

		@Override
		public Context getTargetContext() {
			// TODO Auto-generated method stub
			return mBase.getTargetContext();
		}

		@Override
		public boolean isProfiling() {
			// TODO Auto-generated method stub
			return mBase.isProfiling();
		}

		@Override
		public void startProfiling() {
			// TODO Auto-generated method stub
			mBase.startProfiling();
		}

		@Override
		public void stopProfiling() {
			// TODO Auto-generated method stub
			mBase.stopProfiling();
		}

		@Override
		public void setInTouchMode(boolean inTouch) {
			// TODO Auto-generated method stub
			mBase.setInTouchMode(inTouch);
		}

		@Override
		public void waitForIdle(Runnable recipient) {
			// TODO Auto-generated method stub
			mBase.waitForIdle(recipient);
		}

		@Override
		public void waitForIdleSync() {
			// TODO Auto-generated method stub
			mBase.waitForIdleSync();
		}

		@Override
		public void runOnMainSync(Runnable runner) {
			// TODO Auto-generated method stub
			mBase.runOnMainSync(runner);
		}

		@Override
		public Activity startActivitySync(Intent intent) {
			// TODO Auto-generated method stub
			return mBase.startActivitySync(intent);
		}

		@Override
		public void addMonitor(ActivityMonitor monitor) {
			// TODO Auto-generated method stub
			mBase.addMonitor(monitor);
		}

		@Override
		public ActivityMonitor addMonitor(IntentFilter filter,
				ActivityResult result, boolean block) {
			// TODO Auto-generated method stub
			return mBase.addMonitor(filter, result, block);
		}

		@Override
		public ActivityMonitor addMonitor(String cls, ActivityResult result,
				boolean block) {
			// TODO Auto-generated method stub
			return mBase.addMonitor(cls, result, block);
		}

		@Override
		public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
			// TODO Auto-generated method stub
			return mBase.checkMonitorHit(monitor, minHits);
		}

		@Override
		public Activity waitForMonitor(ActivityMonitor monitor) {
			// TODO Auto-generated method stub
			return mBase.waitForMonitor(monitor);
		}

		@Override
		public Activity waitForMonitorWithTimeout(ActivityMonitor monitor,
				long timeOut) {
			// TODO Auto-generated method stub
			return mBase.waitForMonitorWithTimeout(monitor, timeOut);
		}

		@Override
		public void removeMonitor(ActivityMonitor monitor) {
			// TODO Auto-generated method stub
			mBase.removeMonitor(monitor);
		}

		@Override
		public boolean invokeMenuActionSync(Activity targetActivity, int id,
				int flag) {
			// TODO Auto-generated method stub
			return mBase.invokeMenuActionSync(targetActivity, id, flag);
		}

		@Override
		public boolean invokeContextMenuAction(Activity targetActivity, int id,
				int flag) {
			// TODO Auto-generated method stub
			return mBase.invokeContextMenuAction(targetActivity, id, flag);
		}

		@Override
		public void sendStringSync(String text) {
			// TODO Auto-generated method stub
			mBase.sendStringSync(text);
		}

		@Override
		public void sendKeySync(KeyEvent event) {
			// TODO Auto-generated method stub
			mBase.sendKeySync(event);
		}

		@Override
		public void sendKeyDownUpSync(int key) {
			// TODO Auto-generated method stub
			mBase.sendKeyDownUpSync(key);
		}

		@Override
		public void sendCharacterSync(int keyCode) {
			// TODO Auto-generated method stub
			mBase.sendCharacterSync(keyCode);
		}

		@Override
		public void sendPointerSync(MotionEvent event) {
			// TODO Auto-generated method stub
			mBase.sendPointerSync(event);
		}

		@Override
		public void sendTrackballEventSync(MotionEvent event) {
			// TODO Auto-generated method stub
			mBase.sendTrackballEventSync(event);
		}

		@Override
		public Application newApplication(ClassLoader cl, String className,
				Context context) throws InstantiationException,
				IllegalAccessException, ClassNotFoundException {
			// TODO Auto-generated method stub
			return mBase.newApplication(cl, className, context);
		}

		@Override
		public void callApplicationOnCreate(Application app) {
			// TODO Auto-generated method stub
			mBase.callApplicationOnCreate(app);
		}

		@Override
		public Activity newActivity(Class<?> clazz, Context context, IBinder token,
				Application application, Intent intent, ActivityInfo info,
				CharSequence title, Activity parent, String id,
				Object lastNonConfigurationInstance) throws InstantiationException,
				IllegalAccessException {
			// TODO Auto-generated method stub
			return mBase.newActivity(clazz, context, token, application, intent, info,
					title, parent, id, lastNonConfigurationInstance);
		}

		@Override
		public Activity newActivity(ClassLoader cl, String className, Intent intent)
				throws InstantiationException, IllegalAccessException,
				ClassNotFoundException {
			// TODO Auto-generated method stub
			return mBase.newActivity(cl, className, intent);
		}

		@Override
		public void callActivityOnCreate(Activity activity, Bundle icicle) {
			if (LOG_CALL) {
				//----------123412341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnCreate().    activity: " + activity + " icicle: " + icicle);
			}
			mBase.callActivityOnCreate(activity, icicle);
		}

		@Override
		public void callActivityOnCreate(Activity activity, Bundle icicle,
				PersistableBundle persistentState) {
			if (LOG_CALL) {
				//----------123412341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnCreate().    activity: " + activity + " icicle: " + icicle
					+ " persistentState: " + persistentState);
			}
			mBase.callActivityOnCreate(activity, icicle, persistentState);
		}

		@Override
		public void callActivityOnDestroy(Activity activity) {
			if (LOG_CALL) {
				//----------123412341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnDestroy().   activity: " + activity);
			}
			mBase.callActivityOnDestroy(activity);
		}

		@Override
		public void callActivityOnRestoreInstanceState(Activity activity,
				Bundle savedInstanceState) {
			if (LOG_CALL) {
				//----------123412341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnRestoreInstanceState().  activity: " + activity 
						+ " savedInstanceState: " + savedInstanceState) ;
			}
			mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState);
		}

		@Override
		public void callActivityOnRestoreInstanceState(Activity activity,
				Bundle savedInstanceState, PersistableBundle persistentState) {
			if (LOG_CALL) {
				//----------123412341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnRestoreInstanceState().  activity: " + activity 
						+ " savedInstanceState: " + savedInstanceState
						+ " persistentState: " + persistentState) ;
			}
			mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState,
					persistentState);
		}

		@Override
		public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
			if (LOG_CALL) {
				//----------12341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnPostCreate().    activity: " + activity + " icicle: " + icicle);
			}
			mBase.callActivityOnPostCreate(activity, icicle);
		}

		@Override
		public void callActivityOnPostCreate(Activity activity, Bundle icicle,
				PersistableBundle persistentState) {
			if (LOG_CALL) {
				//----------12341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnPostCreate().    activity: " + activity + " icicle: " + icicle
					+ " persistentState: " + persistentState);
			}
			mBase.callActivityOnPostCreate(activity, icicle, persistentState);
		}

		@Override
		public void callActivityOnNewIntent(Activity activity, Intent intent) {
			if (LOG_CALL) {
				//----------12341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnNewIntent(). activity: " + activity
						+ " intent: " + intent);
			}
			mBase.callActivityOnNewIntent(activity, intent);
		}

		@Override
		public void callActivityOnStart(Activity activity) {
			if (LOG_CALL) {
				//----------12341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnStart(). activity: " + activity);
			}
			mBase.callActivityOnStart(activity);
		}

		@Override
		public void callActivityOnRestart(Activity activity) {
			if (LOG_CALL) {
				//----------12341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnRestart().   activity: " + activity);
			}
			mBase.callActivityOnRestart(activity);
		}

		@Override
		public void callActivityOnResume(Activity activity) {
			if (LOG_CALL) {
				//----------12341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnResume().    activity: " + activity);
			}
			mBase.callActivityOnResume(activity);
		}

		@Override
		public void callActivityOnStop(Activity activity) {
			if (LOG_CALL) {
				//----------12341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnStop().   activity: " + activity);
			}
			mBase.callActivityOnStop(activity);
		}

		@Override
		public void callActivityOnSaveInstanceState(Activity activity,
				Bundle outState) {
			if (LOG_CALL) {
				//----------12341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnSaveInstanceState(). activity: " + activity
						+ " outState: " + outState);
			}
			mBase.callActivityOnSaveInstanceState(activity, outState);
		}

		@Override
		public void callActivityOnSaveInstanceState(Activity activity,
				Bundle outState, PersistableBundle outPersistentState) {
			if (LOG_CALL) {
				//----------12341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnSaveInstanceState(). activity: " + activity
						+ " outState: " + outState
						+ " outPersistentState: " + outPersistentState);
			}
			mBase.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
		}

		@Override
		public void callActivityOnPause(Activity activity) {
			if (LOG_CALL) {
				//----------12341234123412341234123412341234123412341234
				Log.d(TAG, "callActivityOnPause(). activity: " + activity);
			}
			mBase.callActivityOnPause(activity);
		}

		@Override
		public void callActivityOnUserLeaving(Activity activity) {
			if (LOG_CALL) {
				Log.d(TAG, "callActivityOnUserLeaving(). activity: " + activity);
			}
			mBase.callActivityOnUserLeaving(activity);
		}

		@Override
		public void startAllocCounting() {
			// TODO Auto-generated method stub
			mBase.startAllocCounting();
		}

		@Override
		public void stopAllocCounting() {
			// TODO Auto-generated method stub
			mBase.stopAllocCounting();
		}

		@Override
		public Bundle getAllocCounts() {
			// TODO Auto-generated method stub
			return mBase.getAllocCounts();
		}

		@Override
		public Bundle getBinderCounts() {
			// TODO Auto-generated method stub
			return mBase.getBinderCounts();
		}

		@Override
		public UiAutomation getUiAutomation() {
			// TODO Auto-generated method stub
			return mBase.getUiAutomation();
		}
		


	}