package org.bbs.apklauncher;

import android.util.Log;
import dalvik.system.DexClassLoader;

public class LogClassLoader extends DexClassLoader {
	private static final String TAG = LogClassLoader.class.getSimpleName();
	private static final boolean LOG = true;
	private int mLevel;

	public LogClassLoader(ClassLoader parent) {
		super("", "/sdcard/", "", parent);
	}
	
	@Override
	protected Class<?> loadClass(String className, boolean resolve)
			throws ClassNotFoundException {
		if (LOG) {
			mLevel++;
			Log.d(TAG, makePrefix(mLevel) + className);
		}
		
		Class<?> c = super.loadClass(className, resolve);
		
		if (LOG) {
			Log.d(TAG, makePrefix(mLevel) + className + " ==> " + (c == null ? "NaN" : c.getClassLoader()));
			mLevel--;
		}
		
		return c;
	}
	
	String makePrefix(int level){
		String str = "";
		for (int i = 1; i < level; i++){
			str += " ";
		}
		return str;
//		return  (level == 1 ? "\n" : "") + level;
	}
}
