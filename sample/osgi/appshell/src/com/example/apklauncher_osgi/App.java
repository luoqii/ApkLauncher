package com.example.apklauncher_osgi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkLauncher.OnProcessIntent;
import org.bbs.apklauncher.ApkLauncher.TKey;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.ApkUtil;
import org.bbs.apklauncher.ResourcesMerger;
import org.bbs.apklauncher.TargetClassLoaderCreator;
import org.bbs.apklauncher.TargetClassLoaderCreator.Factory;
import org.bbs.apklauncher.emb.Host_Application;
import org.bbs.apklauncher.osgi.bundlemanager.FrameworkHelper;
import org.bbs.apklauncher.osgi.bundlemanager.OsgiUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.startlevel.StartLevel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class App extends 
//Application
Host_Application  {
	private static final String TAG = App.class.getSimpleName();
	protected static final String EXTRA_BUNDLE_ID = TAG + ".EXTRA_BUNDLE_ID";
	private Framework mFrameWork;

	@Override
	public void onCreate() {
		super.onCreate();
		
//		ApkLauncher.getInstance().setT2HMap(TKey.ACTIVITY, MyStub.class.getName());;
		ApkPackageManager.getInstance().init(this, "non-exist", false);
		mFrameWork = FrameworkHelper.getInstance(getApplicationContext()).getFramework();

		AndroidUtil.extractAssetFile(getAssets(), "auto_extracted_2_sdcard", new File(Environment.getExternalStorageDirectory(), "bundle"));
		File autoInstallDir = getDir("bundle", MODE_PRIVATE);
//		autoInstallDir = Environment.getExternalStorageDirectory();
		AndroidUtil.extractAssetFile(getAssets(), "auto_install_bundle", autoInstallDir);
		for (String f : autoInstallDir.list()){
			try {
				mFrameWork.getBundleContext().installBundle("file://" + autoInstallDir.getPath() + "/" + f).start();
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ApkLauncher.getInstance().setOnProcessIntentCallback(new OnProcessIntent() {
			
			@Override
			public boolean onProcessStartActivityIntent(Intent intent,
					ClassLoader targetClassLoader, Context hostContext) {
				Log.i(TAG, "processIntent. intent: " + intent);
				List<ResolveInfo> acts = ApkPackageManager.getInstance().queryIntentActivities(intent, 0);
				if (acts.size() > 0) {
					Log.i(TAG, "intent matchs a installed plugin.");
					ActivityInfo aInfo = acts.get(0).activityInfo;
					// may be we need a new classloader.
					//						targetClassLoader = ApkPackageManager.getInstance().createClassLoader(hostContext, ((ActivityInfoX)aInfo).mPackageInfo);
					String location = "file://" + aInfo.applicationInfo.publicSourceDir;
					Bundle b = FrameworkHelper.getInstance(null).getFramework().getBundleContext().getBundle(location);
					targetClassLoader = OsgiUtil.getBundleClassLoader(b);
					intent.putExtra(EXTRA_BUNDLE_ID, b.getBundleId());
					ApkLauncher.getInstance().prepareIntent(intent, targetClassLoader, hostContext, aInfo.name);
				} else {
					Log.w(TAG, "can not handle intent:  "  + intent);
				}		
				return true;
			}
		});
		
		TargetClassLoaderCreator.setFactory(new Factory() {
			
			@Override
			public ClassLoader onCreateTargetClassLoader(Context hostBaseContext,
					Intent intent) {
				long bundleId = intent.getLongExtra(EXTRA_BUNDLE_ID, -1);
				Bundle targetBundle = FrameworkHelper.getInstance(null).getFramework().getBundleContext().getBundle(bundleId);
				ClassLoader classloader = OsgiUtil.getBundleClassLoader(targetBundle);
				Log.d(TAG, "targetClassloader: " + classloader);				
				return classloader;
			}

			@Override
			public Resources onCreateTargetResources(Context hostBaseContext,
					Intent intent) {
				long bundleId = intent.getLongExtra(EXTRA_BUNDLE_ID, -1);
				Bundle targetBundle = FrameworkHelper.getInstance(null).getFramework().getBundleContext().getBundle(bundleId);
				BundleWiring bwing = targetBundle.adapt(BundleWiring.class);
				List<Bundle> resourceBundles = new ArrayList<Bundle>();
				for (BundleWire wire: bwing.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE)) {
					String packagee = (String) wire.getCapability().getAttributes().get(BundleRevision.PACKAGE_NAMESPACE);
					if (packagee.startsWith("resource")) {
						Bundle b = wire.getProviderWiring().getBundle();
						resourceBundles.add(b);
						Log.d(TAG, "package: " + packagee + " bundle: " + b);
					}
				}
				if (resourceBundles.size() > 0){
					resourceBundles.remove(targetBundle);
					resourceBundles.add(0, targetBundle);
					Resources res = new ResourcesMerger(ApkUtil.loadApkResource(resourceBundles.get(0).getLocation(), hostBaseContext),
							                            ApkUtil.loadApkResource(resourceBundles.get(1).getLocation(), hostBaseContext));
					for (int i = 2 ; i < resourceBundles.size(); i++) {
						res = new ResourcesMerger(ApkUtil.loadApkResource(resourceBundles.get(i).getLocation(), hostBaseContext),res);
					}

					return res;
				}

				BundleWire bw = targetBundle.adapt(BundleWire.class);
				if (bw != null) {
					BundleRequirement req = bw.getRequirement();
					for (String key : req.getAttributes().keySet()) {
						Object v = req.getAttributes().get(key);
						Log.d(TAG, "key: " + key + " value: " + v);
					}
				}
				String name = targetBundle.getHeaders().get(FrameworkHelper.HEADER_REQUIRED_RESOURCE_BUNDLE);
				if (!TextUtils.isEmpty(name)){
					Bundle b = OsgiUtil.getBundleBySymblicName(targetBundle.getBundleContext(), name);
				}
				return null;
			}
		});
		
		setStartLevel();
	}
	
	void setStartLevel (){
		StartLevel sl = (StartLevel) mFrameWork.getBundleContext().getService(mFrameWork.getBundleContext().getServiceReference(StartLevel.class));
		sl.setStartLevel(100);
	}
}
