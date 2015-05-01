package org.bbs.apklauncher.demo;

import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import org.bbs.android.commonlib.ExceptionCatcher;
import org.bbs.apklauncher.ApkPackageManager;
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
		
		ExceptionCatcher.attachExceptionHandler(this);
		
		File apkDir = getDir(APK_LAUNCHER_DIR, 0);
		apkDir = new File(Environment.getExternalStorageDirectory(), "apk");
		
		Log.d(TAG, "apkDir: " + apkDir);
		ApkPackageManager apks = ApkPackageManager.getInstance();
		apks.init(this);
		apks.scanApkDir(apkDir);
	}
	
	
}
