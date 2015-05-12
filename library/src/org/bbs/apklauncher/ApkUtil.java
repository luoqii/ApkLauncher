package org.bbs.apklauncher;

import org.bbs.apklauncher.api.ExportApi;
import org.bbs.apklauncher.emb.IntentHelper;
import org.bbs.apklauncher.emb.auto_gen.Stub_Activity;
import org.bbs.apkparser.PackageInfoX;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import dalvik.system.DexClassLoader;

@ExportApi
public class ApkUtil {

	public static void startActivity(Context context, PackageInfoX.ActivityInfoX a) {
		if (null == a) {
			throw new RuntimeException("activity info in null");
		}
		
		ClassLoader cl = ApkPackageManager.getClassLoader(a.applicationInfo.packageName);
		String superClassName = LoadedApk.getActivitySuperClassName(cl, a.name);
		Intent launcher = new Intent();

        // inject and replace with our component.
		String comClassName = superClassName.replace("Target", "Stub");
		ComponentName com= new ComponentName(context.getPackageName(), comClassName);
		launcher.setComponent(com);
		launcher.putExtra(Stub_Activity.EXTRA_COMPONENT_CLASS_NAME, a.name);
		
		launcher.putExtra(IntentHelper.EXTRA_INJECT, false);
		context.startActivity(launcher);
	}
}
