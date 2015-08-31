package com.example.apklauncher_app;

import java.io.File;

import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.ApkLauncher.TKey;
import org.bbs.apklauncher.emb.Host_Application;
import org.bbs.apklauncher.osgi.bundlemanager.FrameworkHelper;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

import android.app.Application;
import android.os.Environment;

public class App extends 
//Application
Host_Application  {
	private static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		
		ApkLauncher.getInstance().setT2HMap(TKey.ACTIVITY, MyStub.class.getName());;
		ApkPackageManager.getInstance().init(this, "non-exist", false);
		Framework fm = FrameworkHelper.getInstance(getApplicationContext()).getFramework();

		AndroidUtil.extractAssetFile(getAssets(), "auto_extracted_2_sdcard", new File(Environment.getExternalStorageDirectory(), "bundle"));
		File autoInstallDir = getDir("bundle", MODE_PRIVATE);
//		autoInstallDir = Environment.getExternalStorageDirectory();
		AndroidUtil.extractAssetFile(getAssets(), "auto_install_bundle", autoInstallDir);
		for (String f : autoInstallDir.list()){
			try {
				fm.getBundleContext().installBundle("file://" + autoInstallDir.getPath() + "/" + f).start();
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
