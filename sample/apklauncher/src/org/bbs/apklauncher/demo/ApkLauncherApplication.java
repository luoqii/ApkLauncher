package org.bbs.apklauncher.demo;

import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import org.bbs.apklauncher.InstalledAPks;
import org.bbs.apklauncher.emb.Host_Application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class ApkLauncherApplication extends 
//Application
Host_Application 
{
	private static final String TAG = ApkLauncherApplication.class.getSimpleName();
	public static final String APK_LAUNCHER_DIR = "apklauncher";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		attachExceptionHandler();
		
		File apkDir = getDir(APK_LAUNCHER_DIR, 0);
		apkDir = new File(Environment.getExternalStorageDirectory(), "apk");
		
		Log.d(TAG, "apkDir: " + apkDir);
		InstalledAPks apks = InstalledAPks.getInstance();
		apks.init(this, apkDir);
	}
	
//	public void attachBundleAplication(Application app, Context baseContext){
//		ReflectUtil.ApplicationUtil.callAttach(app, baseContext);
//		
//		callStubOnCreate(app);
//		
//		mAgents.add(app);
//	}
	
	@Override
	public Context getApplicationContext() {
		// TODO Auto-generated method stub
		return super.getApplicationContext();
	}
	
	
	private void attachExceptionHandler() {
		final UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@SuppressLint("WorldReadableFiles")
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				PrintStream writer;
				File crashFile = null;
				String name = "00_" + getPackageName() + "_crash.log.txt";
				try {
					crashFile = getFileStreamPath(name);
					crashFile.delete();
					openFileOutput(name, Context.MODE_WORLD_READABLE);
					crashFile = getFileStreamPath(name);
					crashFile.createNewFile();

					writer = new PrintStream(crashFile);
					writer.append("crash at: " + new Date().toString());
					writer.append("\n");
					writer.flush();

					ex.printStackTrace(writer);
					ex.printStackTrace();
					writer.flush();
					writer.close();

					Intent view = new Intent(Intent.ACTION_VIEW);
					view.setDataAndType(Uri.fromFile(crashFile), "text/*");
					view.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					view.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					view.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(view);
				} catch (Exception e) {
					e.printStackTrace();
				}

				defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
			}
		});

		if (false) {
			npe();
		}
	}

	void npe() {
		String nullStr = null;
		if (nullStr.length() > 0) {
		}
	}
	
}
