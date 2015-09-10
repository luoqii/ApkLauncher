package com.example.apklauncher_app_osgi_felix_t_3;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import android.os.AsyncTask;
import android.util.Log;

public class Activator implements BundleActivator {

	private static final String TAG = Activator.class.getSimpleName();
	public static BundleContext sContext;

	@Override
	public void start(BundleContext arg0) throws Exception {
		sContext = arg0;
		Log.d(TAG, "classloader: " + getClass().getClassLoader());

	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		sContext = arg0;
	}

}
