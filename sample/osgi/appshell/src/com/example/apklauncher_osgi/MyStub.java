package com.example.apklauncher_osgi;

import org.bbs.apklauncher.emb.auto_gen.Stub_Activity;
import org.bbs.apklauncher.osgi.bundlemanager.FrameworkHelper;
import org.bbs.apklauncher.osgi.bundlemanager.OsgiUtil;
import org.osgi.framework.Bundle;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyStub extends Stub_Activity {
	private static final String TAG = MyStub.class.getSimpleName();
	
//	protected ClassLoader createTargetClassLoader(Context hostBaseContext, Intent intent) {
//		Bundle targetBundle = FrameworkHelper.getInstance(null).getFramework().getBundleContext().getBundle(mBundleId);
//		return OsgiUtil.getBundleClassLoader(targetBundle);
//	}

}
