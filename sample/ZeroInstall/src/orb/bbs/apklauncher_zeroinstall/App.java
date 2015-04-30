package orb.bbs.apklauncher_zeroinstall;

import java.io.File;
import java.io.IOException;

import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.emb.Host_Application;

import android.content.res.AssetManager;

public class App extends Host_Application {
	private static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		
		ApkPackageManager am = ApkPackageManager.getInstance();
		am.init(this, null);
		File pluginDir = am.getPluginDir();
		extractApkFromAsset("plugin", pluginDir.getPath());
		am.init(this, pluginDir);
	}

	private void extractApkFromAsset(String srcDir, String destDir) {
		AssetManager am = getResources().getAssets();
		try {
			for (String fp : am.list(srcDir)) {
				AndroidUtil.copyFile(src, dest);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
