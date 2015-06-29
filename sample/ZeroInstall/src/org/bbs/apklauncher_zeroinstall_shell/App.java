package org.bbs.apklauncher_zeroinstall_shell;

import orb.bbs.apklauncher.zeroinstall.shell.BuildConfig;

import org.bbs.android.commonlib.ExceptionCatcher;
import org.bbs.android.commonlib.Version;
import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.TargetContext;
import org.bbs.apklauncher.emb.Host_Application;

public class App extends Host_Application {
	private static final String PREF_EXTRACT_APK = "extract_apk";
	private static final String PERF_KEY_APK_HAS_SCANNED = "apk_has_scanned";
	private static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		
		ExceptionCatcher.attachExceptionHandler(this);
		Version version = Version.getInstance();
		version.init(this);
		
        ApkLauncher apkLauncher = ApkLauncher.getInstance();
        apkLauncher.init(this,"plugin",
                BuildConfig.DEBUG
//                && false
                );
	}

}
