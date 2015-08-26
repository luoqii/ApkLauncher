package org.bbs.apklauncher.demo;

import java.io.File;

import org.bbs.android.commonlib.ExceptionCatcher;
import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkLauncherConfig;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.emb.Host_Application;

import android.os.Environment;

public class App extends 
//Application
Host_Application 
{
	private static final String TAG = App.class.getSimpleName();
	
	public App(){
		super();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ExceptionCatcher.attachExceptionHandler(this);
		
		File apkDir = null;
		apkDir = new File(Environment.getExternalStorageDirectory(), "apk");

		ApkLauncherConfig.setDebug(true);
		ApkLauncher apk = ApkLauncher.getInstance();
		apk.init(this, ApkLauncher.ASSET_PLUGIN_DIR, true);
		apk.setT2HMap(ApkLauncher.TKey.LIST_ACTIVITY, "org.bbs.apklauncher.demo.ListActivity");
		
//		ApkPackageManager.getInstance().setClassLoaderFactory(new ClassLoaderFactory() {
//			
//			@Override
//			public ClassLoader createClassLoader(ApkPackageManager apkPackageManager,
//					Context baseContext, String apkPath, String libPath,
//					String targetPackageName) {
//				String optPath =  apkPackageManager.getOptDir().getPath();
//				ClassLoader cl = new DexClassLoader(apkPath, optPath, libPath, baseContext.getClassLoader());
//				cl = new TargetClassLoader(apkPath, optPath, libPath, baseContext.getClassLoader(), targetPackageName, baseContext);
//				
//				return cl;
//			}
//		});
		ApkPackageManager.getInstance().scanApkDir(apkDir);
	}
	
	
}
