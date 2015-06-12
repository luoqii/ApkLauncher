package org.bbs.apklauncher;

import java.io.NotSerializableException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

/**
 * when bundle resource is ready, return this, otherwise, return normally.
 * @author bysong
 *
 */
public class TargetContext extends 
ContextWrapper 
//ContextThemeWrapper
{

	private static final String TAG = TargetContext.class.getSimpleName();
	
	private static final boolean ENALBE_SERVICE = true;
	
	private String mPackageName;
	private Resources mResource;
	private ClassLoader mClassLoader;
	private ClassLoader mMergedClassLoader;
	private PackageManager mPackageManager;
	private Context mAppContext;
	private Theme mTargetTheme;
	private int mTargetThemeId;
	private Activity mHostActivity;

	private LayoutInflater mInflater;

	private String mCookie;
	
	public TargetContext(Context base) {
		super(base);
//		this(base, 0);
	}
	
	public void setCookie(String cookie){
		mCookie = cookie;
	}
	public String getCookie() {
		return mCookie;
	}

//	public LazyContext(Context base, int themeResId) {
//		super(base, themeResId);
//	}
	
	public static void bundleReadyX(TargetContext LazyContext, Bundle bundle, Resources res, String packageName) {
		// trivas build error
//		LazyContext.mClassLoader = bundle.adapt(BundleWiring.class).getClassLoader();
//		LazyContext.mResource = res;
//		mPackageName = packageName;
		
		notSupported();
	}	
	
	public void packageManagerReady(PackageManager pm) {
		mPackageManager = pm;
	}
	
	public void applicationContextReady(Context appContext){
		mAppContext = appContext;
	}
	
	public void packageNameReady(String packageName) {
		mPackageName = packageName;
	}
	
	public void themeReady(int theme) {
		mTargetThemeId = theme;
	}
	
	public void resReady(Resources res) {
		mResource = res;
	}
	
	public void classLoaderReady(ClassLoader classloader) {
		mClassLoader = classloader;
	}
	
	
	
	//------------------------------------------------------------------
	
	@Override
	public Theme getTheme() {
		Theme theme = null;
		if (null != mTargetTheme) {
			theme = mTargetTheme;
		}
		if (mResource != null) {
			if (mTargetThemeId > 0) {
				if (mTargetTheme == null) {
					mTargetTheme = mResource.newTheme();
				}
				mTargetTheme.applyStyle(mTargetThemeId, true);

				theme = mTargetTheme;
			}
		}
		
		if (theme == null) {
			theme = super.getTheme();
		}
		
//		Log.d(TAG, "getTheme(). theme: " + theme);
		return theme;
		
	}

	@Override
	public String getPackageName() {
		String pName = doGetPackageName();
//		Log.d(TAG, "getPackageName(). packageName: " + pName);
		
		return pName;
	}	
	
	private String doGetPackageName() {
//		new Exception("stack info").printStackTrace();
		if (!TextUtils.isEmpty(mPackageName)) {
			return mPackageName;
		}
		return super.getPackageName();
	}	
	
	@Override	
	public Resources getResources() {
		Resources res = null;
		if (null == mResource) {
			res = super.getResources();
		} else {
			res = mResource;
		}
		
//		Log.d(TAG, "getResources(). res: " + res);
		return res;
	}
	
	@Override
	public AssetManager getAssets() {
		if (null != mResource) {
			if (mResource instanceof ResourcesMerger) {
				Resources r = ((ResourcesMerger)mResource).mFirst;
				
				return getAsset(r);
			}
		}
		return super.getAssets();
	}
	
	private AssetManager getAsset(Resources r) {
		return (AssetManager) ReflectUtil.getFiledValue(Resources.class, r, "mAssets");
	}

	@Override
	public ClassLoader getClassLoader() {
		ClassLoader cl = super.getClassLoader();
		if (mClassLoader != null) {
			if (mMergedClassLoader == null) {
				mMergedClassLoader = new MergedClassLoader(cl, mClassLoader);
			}
			
			cl = mMergedClassLoader;
		}
		
		return cl;
	}
	
	@Override
	public PackageManager getPackageManager() {
		PackageManager pm =  doGetPackageManager();
//		Log.d(TAG, "pm: " + pm);
			
		return pm;
	}	
	
	private PackageManager doGetPackageManager() {
//		new Exception("stack info").printStackTrace();
		if (mPackageManager != null) {
			return mPackageManager;
		}

		return super.getPackageManager();
	}
	
	@Override
	public Context getApplicationContext() {
		if (mAppContext != null) {
			return mAppContext;
		}
		return super.getApplicationContext();
	}
	
	@Override
	public SharedPreferences getSharedPreferences(String name, int mode) {
		SharedPreferences pref =  super.getSharedPreferences(name, mode);
		
		Log.d(TAG, "SharedPreferences(). name: " + name + " pref: " + pref);
		return pref;
	}
	
	@Override
	public ComponentName startService(Intent service) {
		if (ENALBE_SERVICE) {
			ApkLauncher.getInstance().onProcessIntent(service, mClassLoader, getBaseContext());
			return super.startService(service);
		} else {
			Log.w(TAG, "startService not implemented.");
			return null;
		}
	}

	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		if (ENALBE_SERVICE) {
			ApkLauncher.getInstance().onProcessIntent(service, mClassLoader, getBaseContext());
			return super.bindService(service, conn, flags);
		} else {
			Log.w(TAG, "bindService not implemented.");
			return false;
		}
	}

	@Override
	public boolean stopService(Intent service) {
		if (ENALBE_SERVICE) {
			ApkLauncher.getInstance().onProcessIntent(service, mClassLoader, getBaseContext());
			return super.stopService(service);
		} else {
			Log.w(TAG, "stopService not implemented.");
			return false;
		}
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		if (ENALBE_SERVICE) {
			super.unbindService(conn);
		} else {
			Log.w(TAG, "unbindService not implemented.");
		}
	}
	
	@Override 
	public Object getSystemService(String name) {
		// adjust layout inflater
//        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
//            if (mInflater == null) {
//                mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
//            }
//            return mInflater;
//        }
        return getBaseContext().getSystemService(name);
    }
	
	static void notSupported() {
		throw new RuntimeException("not supported.");
	}

	class MergedAssetManager 
//	extends AssetManager 
	{
		
	}
	
	class MergedClassLoader extends ClassLoader {
		private ClassLoader mMajor;
		private ClassLoader mMinor;

		MergedClassLoader(ClassLoader major, ClassLoader minor) {
			mMajor = major;
			mMinor = minor;
		}
		
		@Override
		protected Class<?> loadClass(String className, boolean resolve)
				throws ClassNotFoundException {
			try {
				return mMajor.loadClass(className);
			} catch (Exception e) {
				return mMinor.loadClass(className);
			}
		}
		
	}
}