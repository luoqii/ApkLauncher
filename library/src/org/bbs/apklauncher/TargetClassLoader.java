package org.bbs.apklauncher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

class TargetClassLoader extends DexClassLoader {
	private static final String TAG = TargetClassLoader.class.getSimpleName();
	
	private DexFile mDexFile;

	public TargetClassLoader(String dexPath, String optimizedDirectory,
			String libraryPath, ClassLoader parent, Context hostContext) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
		
		 try {
			String hostApkPath = hostContext.getApplicationInfo().publicSourceDir;
			String hostPName = hostContext.getPackageName();
			mDexFile = DexFile.loadDex(hostApkPath, optimizedDirectory + "/" + hostPName + ".dex", 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Class<?> loadClass(String className, boolean resolve)
			throws ClassNotFoundException {
		Class<?> c = null;
		if (shouldLoad(className)){
			c = mDexFile.loadClass(className, this);
			Log.d(TAG, "" + className + " ==> " + c.getClassLoader());
		}
		if (c == null ) {
			c = super.loadClass(className, resolve);
		}
		
		return c;
	}

	private boolean shouldLoad(String className) {
		return sList.contains(className)
//				|| className.matches("android\\.support.*")
				;
	}
	
	static List<String> sList = new ArrayList<String>();
	static {
		sList.add(Activity.class.getName());
	}
	
}