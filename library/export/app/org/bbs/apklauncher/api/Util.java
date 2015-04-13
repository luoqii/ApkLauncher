package org.bbs.apklauncher.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

public class Util {
	private static final String TAG = Util.class.getSimpleName();
	
	public static Object getTargetActivity(Context a) {
		
		return a;
	}
	
	@SuppressLint("NewApi")
	public static Object getTargetActivityFormFrag(android.app.Fragment f) {
		Activity a = f.getActivity();
		
		return a;
	}
	
	public static Object getTargetActivityFormFrag_V4(android.support.v4.app.Fragment f) {
		Activity a = f.getActivity();
		
		return a;
	}
}
