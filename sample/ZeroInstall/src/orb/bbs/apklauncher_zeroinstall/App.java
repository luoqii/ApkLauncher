package orb.bbs.apklauncher_zeroinstall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.emb.Host_Application;

import android.content.SharedPreferences;
import android.content.res.AssetManager;

public class App extends Host_Application {
	private static final String PREF_EXTRACT_APK = "extract_apk";
	private static final String PERF_KEY_APK_HAS_SCANNED = "apk_has_scanned";
	private static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		
		ApkPackageManager am = ApkPackageManager.getInstance();
		am.init(this);
		ApkPackageManager.UpdateUtil update = new ApkPackageManager.UpdateUtil("version_id");
		if (update.isAppUpdate(this) || update.isFirstUsage(this)) {
			doScanApk(am);
		} else {
			reScanApkIfNecessary(am);
		}
		update.updateVersion(this);
	}

	private void reScanApkIfNecessary(ApkPackageManager am) {
		SharedPreferences s = getSharedPreferences(PREF_EXTRACT_APK, 0);
		boolean scanned = s.getBoolean(PERF_KEY_APK_HAS_SCANNED, false);
		if (!scanned) {
			doScanApk(am);
		}
	}

	private void doScanApk(ApkPackageManager am) {
		File tempApkDir = getDir("temp_apk", 0);
		extractApkFromAsset("plugin", tempApkDir.getPath());
		am.scanApkDir(tempApkDir);		

		SharedPreferences s = getSharedPreferences(PREF_EXTRACT_APK, 0);
		s.edit().putBoolean(PERF_KEY_APK_HAS_SCANNED, true).commit();
	}

	private void extractApkFromAsset(String srcDir, String destDir) {
		AssetManager am = getResources().getAssets();
		try {
			for (String fp : am.list(srcDir)) {
				AndroidUtil.copyStream(am.open(srcDir + "/" + fp), new FileOutputStream(new File(destDir, fp)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
