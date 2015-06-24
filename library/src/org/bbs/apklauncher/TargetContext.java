package org.bbs.apklauncher;

import java.io.File;

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
	
//	 file-related.	
//	private static final String[] EMPTY_FILE_LIST = {};
//	private Object mSync = new Object();
//	private File mPreferencesDir;
//	private File mFilesDir;
//    private File mCacheDir;
//    private File mCodeCacheDir;
//    private File mDatabasesDir;
//	private File getDataDirFile(){
//		return ApkPackageManager.getInstance().getAppDataDir(mPackageName);
//	}
//	
//	@Override
//	public SharedPreferences getSharedPreferences(String name, int mode) {
//		SharedPreferences pref =  super.getSharedPreferences(name, mode);
//		
//		Log.d(TAG, "SharedPreferences(). name: " + name + " pref: " + pref);
//		return pref;
//	}
//	
//    public File getSharedPrefsFile(String name) {
//        return makeFilename(getPreferencesDir(), name + ".xml");
//    }
//
//    private File makeFilename(File base, String name) {
//        if (name.indexOf(File.separatorChar) < 0) {
//            return new File(base, name);
//        }
//        throw new IllegalArgumentException(
//                "File " + name + " contains a path separator");
//    }
//
//    private File getPreferencesDir() {
//        synchronized (mSync) {
//            if (mPreferencesDir == null) {
//                mPreferencesDir = new File(getDataDirFile(), "shared_prefs");
//            }
//            return mPreferencesDir;
//        }
//    }
//	
//    @Override
//    public File getDir(String name, int mode) {
//        name = "app_" + name;
//        File file = makeFilename(getDataDirFile(), name);
//        if (!file.exists()) {
//            file.mkdir();
////            setFilePermissionsFromMode(file.getPath(), mode,
////                    FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH);
//        }
//        return file;
//    }
//    
//    @Override
//    public File getFilesDir() {
//        synchronized (mSync) {
//            if (mFilesDir == null) {
//                mFilesDir = new File(getDataDirFile(), "files");
//            }
//            return createFilesDirLocked(mFilesDir);
//        }
//    }
//    // Common-path handling of app data dir creation
//    private static File createFilesDirLocked(File file) {
//        if (!file.exists()) {
//            if (!file.mkdirs()) {
//                if (file.exists()) {
//                    // spurious failure; probably racing with another process for this app
//                    return file;
//                }
//                Log.w(TAG, "Unable to create files subdir " + file.getPath());
//                return null;
//            }
////            FileUtils.setPermissions(
////                    file.getPath(),
////                    FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH,
////                    -1, -1);
//        }
//        return file;
//    }
//    
//    @Override
//    public File getFileStreamPath(String name) {
//        return makeFilename(getFilesDir(), name);
//    }
//
//    @Override
//    public String[] fileList() {
//        final String[] list = getFilesDir().list();
//        return (list != null) ? list : EMPTY_FILE_LIST;
//    }
//    
//    @Override
//    public File getCacheDir() {
//        synchronized (mSync) {
//            if (mCacheDir == null) {
//                mCacheDir = new File(getDataDirFile(), "cache");
//            }
//            return createFilesDirLocked(mCacheDir);
//        }
//    }
//
//    @Override
//    public File getCodeCacheDir() {
//        synchronized (mSync) {
//            if (mCodeCacheDir == null) {
//                mCodeCacheDir = new File(getDataDirFile(), "code_cache");
//            }
//            return createFilesDirLocked(mCodeCacheDir);
//        }
//    }
//    
//    @Override
//    public String[] databaseList() {
//        final String[] list = getDatabasesDir().list();
//        return (list != null) ? list : EMPTY_FILE_LIST;
//    }
//
//
//    private File getDatabasesDir() {
//        synchronized (mSync) {
//            if (mDatabasesDir == null) {
//                mDatabasesDir = new File(getDataDirFile(), "databases");
//            }
//            if (mDatabasesDir.getPath().equals("databases")) {
//                mDatabasesDir = new File("/data/system");
//            }
//            return mDatabasesDir;
//        }
//    }
//    
//    private File validateFilePath(String name, boolean createDirectory) {
//        File dir;
//        File f;
//
//        if (name.charAt(0) == File.separatorChar) {
//            String dirPath = name.substring(0, name.lastIndexOf(File.separatorChar));
//            dir = new File(dirPath);
//            name = name.substring(name.lastIndexOf(File.separatorChar));
//            f = new File(dir, name);
//        } else {
//            dir = getDatabasesDir();
//            f = makeFilename(dir, name);
//        }
//
//        if (createDirectory && !dir.isDirectory() && dir.mkdir()) {
////            FileUtils.setPermissions(dir.getPath(),
////                FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH,
////                -1, -1);
//        }
//
//        return f;
//    }
//    
//    @Override
//    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory,
//            DatabaseErrorHandler errorHandler) {
//        File f = validateFilePath(name, true);
//        int flags = SQLiteDatabase.CREATE_IF_NECESSARY;
//        if ((mode & MODE_ENABLE_WRITE_AHEAD_LOGGING) != 0) {
//            flags |= SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;
//        }
//        SQLiteDatabase db = SQLiteDatabase.openDatabase(f.getPath(), factory, flags, errorHandler);
//        setFilePermissionsFromMode(f.getPath(), mode, 0);
//        return db;
//    }
//    
//    static void setFilePermissionsFromMode(String name, int mode,
//            int extraPermissions) {
////        int perms = FileUtils.S_IRUSR|FileUtils.S_IWUSR
////            |FileUtils.S_IRGRP|FileUtils.S_IWGRP
////            |extraPermissions;
////        if ((mode&MODE_WORLD_READABLE) != 0) {
////            perms |= FileUtils.S_IROTH;
////        }
////        if ((mode&MODE_WORLD_WRITEABLE) != 0) {
////            perms |= FileUtils.S_IWOTH;
////        }
////        if (DEBUG) {
////            Log.i(TAG, "File " + name + ": mode=0x" + Integer.toHexString(mode)
////                  + ", perms=0x" + Integer.toHexString(perms));
////        }
////        FileUtils.setPermissions(name, perms, -1, -1);
//    }
//
//    @Override
//    public boolean deleteDatabase(String name) {
//        try {
//            File f = validateFilePath(name, false);
//            return /*SQLiteDatabase.*/deleteDatabase(f);
//        } catch (Exception e) {
//        }
//        return false;
//    }
//    
//    //SQLiteDatabase
//    public static boolean deleteDatabase(File file) {
//        if (file == null) {
//            throw new IllegalArgumentException("file must not be null");
//        }
//
//        boolean deleted = false;
//        deleted |= file.delete();
//        deleted |= new File(file.getPath() + "-journal").delete();
//        deleted |= new File(file.getPath() + "-shm").delete();
//        deleted |= new File(file.getPath() + "-wal").delete();
//
//        File dir = file.getParentFile();
//        if (dir != null) {
//            final String prefix = file.getName() + "-mj";
//            File[] files = dir.listFiles(new FileFilter() {
//                @Override
//                public boolean accept(File candidate) {
//                    return candidate.getName().startsWith(prefix);
//                }
//            });
//            if (files != null) {
//                for (File masterJournal : files) {
//                    deleted |= masterJournal.delete();
//                }
//            }
//        }
//        return deleted;
//    }
//
//    @Override
//    public File getDatabasePath(String name) {
//        return validateFilePath(name, false);
//    }


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