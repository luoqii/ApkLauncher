package com.example.apklauncher_mulit_plugin;

import java.io.File;
import java.util.List;

import org.bbs.android.commonlib.ExceptionCatcher;
import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkLauncherConfig;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.emb.Host_Application;

import android.app.DownloadManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;

import com.umeng.update.net.DownloadingService;

public class App extends
//Application
Host_Application 
{
	private static final String TAG = App.class.getSimpleName();
	public static final String APK_LAUNCHER_DIR = "apklauncher";
	
	public App(){
		super();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ExceptionCatcher.attachExceptionHandler(this);
		
		ApplicationInfo info = null;
		try {
			info = getPackageManager().getApplicationInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, info.toString());
		
		File apkDir = null;
//		apkDir = getDir(APK_LAUNCHER_DIR, 0);
		apkDir = new File(Environment.getExternalStorageDirectory(), "apk");

		ApkLauncherConfig.setDebug(true);
		ApkLauncher apk = ApkLauncher.getInstance();
		apk.init(this, ApkLauncher.ASSET_PLUGIN_DIR, true);
//		apk.setT2HMap(ApkLauncher.TKey.LIST_ACTIVITY, "org.bbs.apklauncher.demo.ListActivity");
		
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
//		ApkPackageManager.getInstance().scanApkDir(apkDir, true, ".*_c[a|b]\\.apk");
		List<File> files = ApkPackageManager.getInstance().extractApkFromAsset(getResources().getAssets(), 
				"sdcard/Download", 
				new File(Environment.getExternalStorageDirectory(), "Download"));
		
		// make Downloads app happy
		DownloadManager dl = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		for (File f : files){
			dl.addCompletedDownload(f.getName(), f.getName(), true, "application/X-apk", f.getPath(), f.length(), false);
		}
	}
	
	
}
