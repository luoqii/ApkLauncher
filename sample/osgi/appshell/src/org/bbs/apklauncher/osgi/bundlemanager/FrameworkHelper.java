package org.bbs.apklauncher.osgi.bundlemanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.knopflerfish.framework.Debug;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

import android.content.Context;
import android.util.Log;

public class FrameworkHelper {
	private static final String TAG = FrameworkHelper.class.getSimpleName();

	public static final String HEADER_CONTRIBUTE_ANDROID_COMPONENT = "X-contribute-android-component";
	public static final String HEADER_REQUIRED_RESOURCE_BUNDLE = "X-Required-Resource-Bundle";

	private static final String OSGI_BUNDLE_DIR = "osgi_bundle";
	private static final String OSGI_BUNDLE_CACHE_DIR = "osgi_bundlecache";

	private static final String ANDROID_PACKAGE_FOR_BOOT_DELEGATION = 
			// android framework package
			"android.*,"
			+ "javax.crypto.*,"
			+ "junit.framework.*,"
			+ "junit.runner.*,"
			+ "org.apache.http.*,"
			+ "org.json.*,"
			+ "org.w3c.dom.*,"
			+ "org.xml.sax.*,"
			+ "org.xmlpull.*"
			;

//	private static final String ASSERT_PRELOAD_BUNDLE_DIR = "felix/preloadbundle";
//	private static final String ASSERT_AUTO_EXTRACT_DIR = "autoExtract";

	private static FrameworkHelper sInstance;
	private Framework mFramework;
	private String mCacheDir;
	private String mBundleDir;

	private FrameworkHelper(Context context) {
		mCacheDir = context.getDir(OSGI_BUNDLE_CACHE_DIR,
				Context.MODE_WORLD_WRITEABLE).toString();
		mBundleDir = context.getDir(OSGI_BUNDLE_DIR,
				Context.MODE_WORLD_WRITEABLE).toString();
		;
//		extractPreloadBundle(context);
//		extractAssets(context);

		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(Constants.FRAMEWORK_STORAGE, mCacheDir);
		configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				ANDROID_PACKAGES_FOR_EXPORT_EXTRA);
		configMap.put(Constants.FRAMEWORK_BOOTDELEGATION, ANDROID_PACKAGE_FOR_BOOT_DELEGATION);
//		configMap.put(Constants.frame, value)

		// configMap.put(FelixConstants.LOG_LEVEL_PROP, 4 + "");

//		mFramework = new FrameworkFactory().newFramework(configMap);
		configMap.put(Debug.CLASSLOADER_PROP, "true");
//		configMap.put(Debug.RESOLVER_PROP, "true");
		mFramework = new org.knopflerfish.framework.FrameworkFactoryImpl().newFramework(configMap);

		Log.d(TAG, "init & start osgi.");
		try {
			mFramework.init();
			BundleContext bContext = mFramework.getBundleContext();
			OsgiUtil.dumpProperties(bContext);
			Bundle[] bundles = bContext.getBundles();

			// for re-deploy bundle.
			for (Bundle b : bundles) {
				if (0 != b.getBundleId()) {
					b.uninstall();
				}
			}

			mFramework.start();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG,
				"OSGi framework running, state: "
						+ OsgiUtil.bundleState2Str(mFramework.getState()));

		registerListener(mFramework);

		Bundle[] bundles = mFramework.getBundleContext().getBundles();
		for (Bundle b : bundles) {
			Log.d(TAG,
					"b: " + b.getBundleId() + " "
							+ OsgiUtil.bundleState2Str(b.getState()));
		}
//		mFramework.getBundleContext().getb
	}

	public Framework getFramework() {
		return mFramework;
	}

	void registerListener(Framework f) {
		f.getBundleContext().addBundleListener(new BundleListener() {

			@Override
			public void bundleChanged(BundleEvent e) {
				Log.d(TAG, "bundleChanged. event:" + e);

			}
		});
	}

//	private void extractAssets(Context context) {
//		try {
//			AssetManager assetsM = context.getResources().getAssets();
//			String[] files = assetsM.list(ASSERT_AUTO_EXTRACT_DIR);
//			for (String aFile : files) {
//				String assertFile = ASSERT_AUTO_EXTRACT_DIR + "/" + aFile;
//				Log.d(TAG, "prepare bundle: " + aFile);
//				InputStream in = assetsM.open(assertFile);
//				String bFile = "/sdcard/autoextract/" + aFile;
//				File f = new File(bFile);
//				f.getParentFile().mkdirs();
//				OutputStream out =
//				// context.openFileOutput(mBundleDir + "/" + aFile, 0);
//
//				new FileOutputStream(bFile);
//
//				int byteCount = 8096;
//				byte[] buffer = new byte[byteCount];
//				int count = 0;
//				while ((count = in.read(buffer, 0, byteCount)) != -1) {
//					out.write(buffer, 0, count);
//				}
//
//				in.close();
//				out.close();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	private void extractPreloadBundle(Context context) {
//		try {
//			AssetManager assetsM = context.getResources().getAssets();
//			String[] files = assetsM.list(ASSERT_PRELOAD_BUNDLE_DIR);
//			for (String aFile : files) {
//				String assertFile = ASSERT_PRELOAD_BUNDLE_DIR + "/" + aFile;
//				Log.d(TAG, "prepare bundle: " + aFile);
//				InputStream in = assetsM.open(assertFile);
//				String bFile = mBundleDir + "/" + aFile;
//				if (aFile.endsWith("apk")) {
//					bFile = bFile + ".jar";
//				}
//				OutputStream out =
//				// context.openFileOutput(mBundleDir + "/" + aFile, 0);
//
//				new FileOutputStream(bFile);
//
//				int byteCount = 8096;
//				byte[] buffer = new byte[byteCount];
//				int count = 0;
//				while ((count = in.read(buffer, 0, byteCount)) != -1) {
//					out.write(buffer, 0, count);
//				}
//
//				in.close();
//				out.close();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public static FrameworkHelper getInstance(Context context) {
		if (null == sInstance) {
			sInstance = new FrameworkHelper(context);
		}
		return sInstance;
	}

	
	static String[] sExportedPackages_Android = new String[]{
		// android
		"android",
		"android.app",
		//"android.app.Application,"
		"android.content",
		//"android.content.Context,"
		//"android.content.ContextWrapper,"
		"android.content.res",
		"android.content.pm",
		"android.database",
		"android.database.sqlite",
		"android.graphics",
		"android.graphics.drawable",
		"android.graphics.glutils",
		"android.hardware",
		"android.location",
		"android.media",
		"android.net",
		"android.opengl",
		"android.os",
		"android.os.bundle",
		"android.provider",
		"android.sax",
		"android.speech.recognition",
		"android.telephony",
		"android.telephony.gsm",
		"android.text",
		"android.text.method",
		"android.text.style",
		"android.text.util",
		"android.util",
		"android.view",
		"android.view.animation",
		"android.webkit",
		"android.widget",		
		// JAVAx
		"javax.crypto",
		"javax.crypto.interfaces",
		"javax.crypto.spec",
		"javax.microedition.khronos.opengles",
		"javax.net",
		"javax.net.ssl",
		"javax.security.auth",
		"javax.security.auth.callback",
		"javax.security.auth.login",
		"javax.security.auth.x500",
		"javax.security.cert",
		"javax.sound.midi",
		"javax.sound.midi.spi",
		"javax.sound.sampled",
		"javax.sound.sampled.spi",
		"javax.sql",
		"javax.xml.parsers",
		// JUNIT
		"junit.extensions",
		"junit.framework",
		// APACHE
		"org.apache.commons.codec",
		"org.apache.commons.codec.binary",
		"org.apache.commons.codec.language",
		"org.apache.commons.codec.net",
		"org.apache.commons.httpclient",
		"org.apache.commons.httpclient.auth",
		"org.apache.commons.httpclient.cookie",
		"org.apache.commons.httpclient.methods",
		"org.apache.commons.httpclient.methods.multipart",
		"org.apache.commons.httpclient.params",
		"org.apache.commons.httpclient.protocol",
		"org.apache.commons.httpclient.util",
		// OTHERS
		"org.bluez",
		"org.json",
		"org.w3c.dom",
		"org.xml.sax",
		"org.xml.sax.ext", 
		"org.xml.sax.helpers",		
		"org.xmlpull.v1",
		"org.xmlpull.v1.sax2"
	};
	
	static String[] sExportedPackages_Android_v4 = new String[]{
		"android.support.annotation",
		"android.support.v4.accessibilityserivce",
		"android.support.v4.app",
		"android.support.v4.content",
		"android.support.v4.database",
		"android.support.v4.graphics",
		"android.support.v4.hardware.display",
		"android.support.v4.internal.view",
		"android.support.v4.media",
		"android.support.v4.net",
		"android.support.v4.os",
		"android.support.v4.print",
		"android.support.v4.provider",
		"android.support.v4.speech.tts",
		"android.support.v4.text",
		"android.support.v4.util",
		"android.support.v4.view",
		"android.support.v4.view.accessibility",
		"android.support.v4.widget", 
	};
	
	static String[] sExportedPackages_app = new String[]{
		"org.bbs.apklauncher.emb",
		"org.bbs.apklauncher.emb.auto_gen",
	};

	private static /*final*/ String ANDROID_PACKAGES_FOR_EXPORT_EXTRA = "";
	static {
		List<String> exportedPackages = new ArrayList<String>();
		exportedPackages = toList(sExportedPackages_app);
		exportedPackages.addAll(toList(sExportedPackages_Android));
		exportedPackages.addAll(toList(sExportedPackages_Android_v4));
		for (String pkg: exportedPackages){
			ANDROID_PACKAGES_FOR_EXPORT_EXTRA += pkg + ",";
		}
		ANDROID_PACKAGES_FOR_EXPORT_EXTRA = ANDROID_PACKAGES_FOR_EXPORT_EXTRA.substring(0, ANDROID_PACKAGES_FOR_EXPORT_EXTRA.length() - 1);
	}
	
	static List<String> toList(String[] array){
		List<String> l = new ArrayList<>();
		for (String s : array){
			l.add(s);
		}
		return l;
	}

}
