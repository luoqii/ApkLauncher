package org.bbs.apklauncher.emb;

import org.bbs.apklauncher.TargetContext;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

/**
 * 
 * provide consistent interface with {@link PendingIntent}
 * @author bysong
 *
 */
public class PendingIntentHelper 
//	extends PendingIntent 
	{
	private static final String TAG = PendingIntentHelper.class.getSimpleName();
	private static final String COOKIE = TAG;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static PendingIntent 	getActivities(Context context, int requestCode, Intent[] intents, int flags) {
		Context injectContext = (context);
		return PendingIntent.getActivities(injectContext, requestCode, intents, flags);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static PendingIntent 	getActivities(Context context, int requestCode, Intent[] intents, int flags, Bundle options){
		Context injectContext = (context);
		return PendingIntent.getActivities(injectContext, requestCode, intents, flags, options);
	}
	
	public static PendingIntent 	getActivity(Context context, int requestCode, Intent intent, int flags) {
		Context injectContext = (context);
		 return PendingIntent.getActivity(injectContext, requestCode, intent, flags);
	 }
	 
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static PendingIntent 	getActivity(Context context, int requestCode, Intent intent, int flags, Bundle options){
		Context injectContext = (context);
		 return PendingIntent.getActivity(injectContext, requestCode, intent, flags, options);
	 }
	 
	public static PendingIntent 	getBroadcast(Context context, int requestCode, Intent intent, int flags){
		Context injectContext = (context);
		 return PendingIntent.getBroadcast(injectContext, requestCode, intent, flags);
	 }
	
	public static PendingIntent 	getService(Context context, int requestCode, Intent intent, int flags) {
		Context injectContext = (context);
		return PendingIntent.getService(injectContext, requestCode, intent, flags);
	}
	
	static Context parseContext(Context context){
		Context c = null;
		if (context instanceof TargetContext 
				&& !COOKIE.equals(((TargetContext) c).getCookie())) {
			 c = context;
		} else {
			TargetContext t = new TargetContext(context);
			t.setCookie(COOKIE);
			t.packageNameReady(getContextImpl(context).getPackageName());
			
			c = t;
		}
		
		return c;
	}
	
    public static Context getContextImpl(Context context) {
        Context nextContext;
        while ((context instanceof ContextWrapper) &&
                (nextContext=((ContextWrapper)context).getBaseContext()) != null) {
            context = nextContext;
        }
        return (Context)context;
    }

	
//	public static PendingIntent 	readPendingIntentOrNullFromParcel(Parcel in) {
//		return PendingIntent.readPendingIntentOrNullFromParcel(in);
//	}
}
