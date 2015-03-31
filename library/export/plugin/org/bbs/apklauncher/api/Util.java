package org.bbs.apklauncher.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;

public class Util {
	private static final String TAG = Util.class.getSimpleName();
	
	public static Object getTargetActivity(Activity a) {
		Object o = null;
		try {
			Method m = a.getClass().getMethod("getTargetActivity", (Class[])null);
			o = m.invoke(a, (Object[]) null);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return o;
	}
	
	@SuppressLint("NewApi")
	public static Object getTargetActivityFormFrag(android.app.Fragment f) {
		Object o = null;
		Activity a = f.getActivity();
		
		return getTargetActivity(a);
	}
	
	public static Object getTargetActivityFormFrag_V4(android.support.v4.app.Fragment f) {
		Object o = null;
		Activity a = f.getActivity();
		
		return getTargetActivity(a);
	}
}
