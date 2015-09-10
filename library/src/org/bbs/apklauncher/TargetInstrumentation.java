package org.bbs.apklauncher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bbs.apklauncher.ReflectUtil.ActivityReflectUtil;

import android.R.array;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TargetInstrumentation extends InstrumentationWrapper {
	private static final String TAG = TargetInstrumentation.class.getSimpleName();
	private CallBack mCallback;
	private Handler mUiHandler;

	public TargetInstrumentation(Instrumentation base, Handler uiHandler){
		super(base);
		mUiHandler = uiHandler;
	}
	
	public void setCallBack(CallBack callback) {
		mCallback = callback;
	}
	
	public boolean processIntent(Intent intent) {
		if (null != mCallback) {
			return mCallback.onProcessIntent(intent);
		}
		
		return true;
	}
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public void disableActivityTransition(final Activity target) {
		Log.w(TAG, "disableActivityTransition now, need to be done in the future.");
		mUiHandler.post(new Runnable() {
			
			@Override
			public void run() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
					target.overridePendingTransition(0, 0);
				}
			}
		});
	}

	public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
    	processIntent(intent);
    	
    	ActivityResult r = ReflectUtil.execStartActivity(getBase(), who, contextThread, token, target,
				intent, requestCode, options);
    	
    	disableActivityTransition(target);
    	// FIXME activity transition
//		((Activity)who).overridePendingTransition(0, 0);
		
		return r;
	}
	
	public void execStartActivities(Context who, IBinder contextThread,
            IBinder token, Activity target, Intent[] intents, Bundle options) {
    	for (Intent intent: intents) {
    		processIntent(intent);
    	}
        
    	ReflectUtil.execStartActivities(getBase(), who, contextThread, token, target, intents, options, ReflectUtil.myUseId());

    	disableActivityTransition(target);
    }

	public void execStartActivitiesAsUser(Context who, IBinder contextThread,
            IBinder token, Activity target, Intent[] intents, Bundle options,
            int userId) {
    	for (Intent intent: intents) {
    		processIntent(intent);
    	}
    	
    	ReflectUtil.execStartActivitiesAsUser(getBase(), who, contextThread, token, target,
    			intents, options, userId);

    	disableActivityTransition(target);
    }
	
    @SuppressLint("NewApi")
	public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Fragment target,
            Intent intent, int requestCode, Bundle options) {
    	processIntent(intent);
    	ActivityResult result = ReflectUtil.execStartActivity(getBase(), who, contextThread, token, target, 
    			intent, requestCode, options);

    	disableActivityTransition(target.getActivity());
    	
    	return result;
    }

	public static void injectInstrumentation(Object activity, CallBack callback) {
		Instrumentation intr = (Instrumentation) ActivityReflectUtil.getFiledValue(Activity.class, activity, "mInstrumentation");
		Field f = ActivityReflectUtil.getFiled(Activity.class, activity, "mInstrumentation");
		TargetInstrumentation wrapper = new TargetInstrumentation(intr, new Handler());
		wrapper.setCallBack(callback);
		ActivityReflectUtil.setField(activity, f, wrapper);
	}

	public static interface CallBack {
		public boolean onProcessIntent(Intent intent);
	}

	static class ReflectUtil {

		public static ActivityResult execStartActivity(Object receiver,
				Context who, IBinder contextThread, IBinder token,
				Activity target, Intent intent, int requestCode, Bundle options) {
			try {
				// EmbeddedActivityAgent.ActivityReflectUtil.dumpMethod(Instrumentation.class,
				// "execStartActivity");
				Method m = Instrumentation.class.getDeclaredMethod(
						"execStartActivity", new Class[] { Context.class,
								IBinder.class, IBinder.class, Activity.class,
								Intent.class,
								// int.class VS Integer.class
								int.class, Bundle.class });
				m.setAccessible(true);
				return (ActivityResult) m.invoke(receiver, new Object[] { who,
						contextThread, token, target, intent, requestCode,
						options });
			} catch (Exception e) {
				ActivityReflectUtil.dumpMethod(Instrumentation.class,
						"execStartActivity");
				throw new RuntimeException("error in execStartActivity.", e);
			}
		}

		public static void execStartActivitiesAsUser(Instrumentation receiver,
				Context who, IBinder contextThread, IBinder token,
				Activity target, Intent[] intents, Bundle options, int userId) {
			try {
				// EmbeddedActivityAgent.ActivityReflectUtil.dumpMethod(Instrumentation.class,
				// "execStartActivitiesAsUser");
				Method m = Instrumentation.class.getDeclaredMethod(
						"execStartActivitiesAsUser", new Class[] {
								Context.class, IBinder.class, IBinder.class,
								Activity.class, Intent.class, Bundle.class,
								// int.class VS Integer.class
								int.class });
				m.setAccessible(true);
				m.invoke(receiver, new Object[] { who, contextThread, token,
						target, intents, options, userId });
			} catch (Exception e) {
				ActivityReflectUtil.dumpMethod(Instrumentation.class,
						"execStartActivitiesAsUser");
				throw new RuntimeException(
						"error in execStartActivitiesAsUser.", e);
			}
		}

		@SuppressLint("NewApi")
		public static ActivityResult execStartActivity(
				Instrumentation receiver, Context who, IBinder contextThread,
				IBinder token, Fragment target, Intent intent, int requestCode,
				Bundle options) {
			try {
				// EmbeddedActivityAgent.ActivityReflectUtil.dumpMethod(Instrumentation.class,
				// "execStartActivity");
				Method m = Instrumentation.class.getDeclaredMethod(
						"execStartActivity", new Class[] { Context.class,
								IBinder.class, IBinder.class, Fragment.class,
								// int.class VS Integer.class
								int.class, Bundle.class });
				m.setAccessible(true);
				return (ActivityResult) m.invoke(receiver, new Object[] { who,
						contextThread, token, target, intent, requestCode,
						options });
			} catch (Exception e) {
				ActivityReflectUtil.dumpMethod(Instrumentation.class,
						"execStartActivity");
				throw new RuntimeException("error in execStartActivity.", e);
			}
		}

		public static Object execStartActivities(Instrumentation receiver,
				Context who, IBinder contextThread, IBinder token,
				Activity target, Intent[] intents, Bundle options, int myUseId) {
			try {
				// EmbeddedActivityAgent.ActivityReflectUtil.dumpMethod(Instrumentation.class,
				// "execStartActivities");
				Method m = Instrumentation.class.getDeclaredMethod(
						"execStartActivities", new Class[] { Context.class,
								IBinder.class, IBinder.class, Activity.class,
								array.class, Bundle.class,
								// int.class VS Integer.class
								int.class });
				m.setAccessible(true);
				return (ActivityResult) m.invoke(receiver,
						new Object[] { who, contextThread, token, target,
								intents, options, myUseId });
			} catch (Exception e) {
				ActivityReflectUtil.dumpMethod(Instrumentation.class,
						"execStartActivities");
				throw new RuntimeException("error in execStartActivities.", e);
			}
		}

		public static int myUseId() {
			int userId = -1;
			try {
				Method myUserIdM = Class.forName("android.os.UserHandle")
						.getDeclaredMethod("myUseId", (Class[]) null);
				myUserIdM.setAccessible(true);
				userId = ((Integer) myUserIdM.invoke(null, (Object[]) null));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return userId;
		}
	}
	
	

}