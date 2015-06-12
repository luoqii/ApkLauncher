package org.bbs.apklauncher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

public class TargetFirstClassLoader extends 
ClassLoader
//DexClassLoader
{
	private static final String TAG = TargetFirstClassLoader.class.getSimpleName();
	public static /*final*/ boolean DEBUG = false;
	
	private DexFile mHostDexFile;
	private DexFile mTargetDexFile;
	private int mLevel;

	public TargetFirstClassLoader(String dexPath, String optimizedDirectory,
			String libraryPath, ClassLoader parent, String targetPackageName, Context hostContext) {
//		super(dexPath, optimizedDirectory, libraryPath, parent);
		super(parent);
		
		try {
			String hostApkPath = hostContext.getApplicationInfo().publicSourceDir;
			String hostPName = hostContext.getPackageName();
			mHostDexFile = DexFile.loadDex(hostApkPath, optimizedDirectory + "/" + hostPName + ".dex", 0);

			mTargetDexFile = DexFile.loadDex(dexPath, optimizedDirectory + "/" + targetPackageName + ".dex", 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected Class<?> loadClass(String className, boolean resolve)
			throws ClassNotFoundException {
		if (DEBUG) {
			mLevel++;
			Log.d(TAG, makePrefix(mLevel) + className);
		}
		Class<?> c = null;
		if (c == null ) {
			c = mTargetDexFile.loadClass(className, this);

			if (c != null) {
//				Log.d(TAG, "/\\/\\/\\" + className + " ==> " + c.getClassLoader());
			}
		}
		if (c == null){
			c = mHostDexFile.loadClass(className, this);
			if (c != null) {
//				Log.d(TAG, "/\\ /\\ /\\" + className + " ==> " + c.getClassLoader());
			}
		}
		
		if (c == null) {
			c = super.loadClass(className, resolve);			
		}
		
		if (resolve){
			resolveClass(c);
		}

		if (DEBUG) {
			if (c != null) {
				Log.d(TAG, makePrefix(mLevel) + className + " ==> " + c.getClassLoader());
				mLevel--;
			}
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