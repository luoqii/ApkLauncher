package org.bbs.apklauncher;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Build;
import android.util.Log;

/**
 * control target context's file related stuff.
 *
 */
abstract public class FileContext extends 
ContextWrapper 
//ContextThemeWrapper
{

	private static final String TAG = FileContext.class.getSimpleName();
//	 file related method.	
	private static final String[] EMPTY_FILE_LIST = {};

	public static /*final*/ boolean ENBABLE_FILE = ApkLauncherConfig.DEBUG && true;
	private static final boolean DEBUG_FILE = ENBABLE_FILE && true;
	private Object mSync = new Object();
	private File mPreferencesDir;
	private File mFilesDir;
    private File mCacheDir;
    private File mCodeCacheDir;
    private File mDatabasesDir;
	private Map<String, HashMap<String, Object>> sSharedPrefs;

	abstract public String getTargetPackageName();
	
	public FileContext(Context base) {
		super(base);
	}	
	
	// the root dir fot all file.
	private File getDataDirFile(){
		return ApkPackageManager.getInstance().getAppDataDir(getTargetPackageName());
	}
	
    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
    	if (!ENBABLE_FILE) {
    		return super.getSharedPreferences(name, mode);
    	}
        Object sp;
        synchronized (FileContext.class) {
            if (sSharedPrefs == null) {
                sSharedPrefs = new HashMap<String, HashMap<String, Object>>();
            }

            final String packageName = getPackageName();
            HashMap<String, Object> packagePrefs = sSharedPrefs.get(packageName);
            if (packagePrefs == null) {
                packagePrefs = new HashMap<String, Object>();
                sSharedPrefs.put(packageName, packagePrefs);
            }

            // At least one application in the world actually passes in a null
            // name.  This happened to work because when we generated the file name
            // we would stringify it to "null.xml".  Nice.
            if (getApplicationInfo().targetSdkVersion <
                    Build.VERSION_CODES.KITKAT) {
                if (name == null) {
                    name = "null";
                }
            }

            sp = packagePrefs.get(name);
            if (sp == null) {
                File prefsFile = getSharedPrefsFile(name);
                sp = SharedPrefImplCompat.newObject(prefsFile, mode);
                packagePrefs.put(name, sp);
                
        		if (DEBUG_FILE){
        			Log.d(TAG, "SharedPreferences(). name: " + name + " pref: " + sp);
        		}
                return (SharedPreferences) sp;
            }
        }
        if ((mode & Context.MODE_MULTI_PROCESS) != 0 ||
            getApplicationInfo().targetSdkVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
            // If somebody else (some other process) changed the prefs
            // file behind our back, we reload it.  This has been the
            // historical (if undocumented) behavior.
        	SharedPrefImplCompat.startReloadIfChangedUnexpectedly(sp);
        }
        
		if (DEBUG_FILE){
			Log.d(TAG, "SharedPreferences(). name: " + name + " pref: " + sp);
		}
        return (SharedPreferences) sp;
    }
	
    public File getSharedPrefsFile(String name) {
        File file =  makeFilename(getPreferencesDir(), name + ".xml");
        if (DEBUG_FILE){
        	Log.d(TAG, "getSharedPrefsFile. name: " + name + " file: " + file);
        }
        return file;
    }

    private File makeFilename(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            File file = new File(base, name);
            if (DEBUG_FILE){
            	Log.d(TAG, "makeFilename. base: " + base + " name: " + name + " file: " + file);
            }
            return file;
        }
        throw new IllegalArgumentException(
                "File " + name + " contains a path separator");
    }

    private File getPreferencesDir() {
        synchronized (mSync) {
            if (mPreferencesDir == null) {
                mPreferencesDir = new File(getDataDirFile(), "shared_prefs");
            }
            if (DEBUG_FILE){
            	Log.d(TAG, "getPreferencesDir. file: " + mPreferencesDir);
            }
            return mPreferencesDir;
        }
    }
	
    @Override
    public File getDir(String name, int mode) {
    	if (!ENBABLE_FILE) {
    		return super.getDir(name, mode);
    	}
        name = "app_" + name;
        File file = makeFilename(getDataDirFile(), name);
        if (!file.exists()) {
            file.mkdir();
//            setFilePermissionsFromMode(file.getPath(), mode,
//                    FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH);
        }
        if (DEBUG_FILE){
        	Log.d(TAG, "getDir. name: " + name + " mode: " + mode + " file: " + file);
        }
        return file;
    }
    
    @Override
    public File getFilesDir() {
    	if (!ENBABLE_FILE) {
    		return super.getFilesDir();
    	}
        synchronized (mSync) {
            if (mFilesDir == null) {
                mFilesDir = new File(getDataDirFile(), "files");
            }
            if (DEBUG_FILE){
            	Log.d(TAG, "getFilesDir dir: " + mFilesDir);
            }
            return createFilesDirLocked(mFilesDir);
        }
    }
    // Common-path handling of app data dir creation
    private static File createFilesDirLocked(File file) {
        if (!file.exists()) {
            if (!file.mkdirs()) {
                if (file.exists()) {
                    // spurious failure; probably racing with another process for this app
                    return file;
                }
                Log.w(TAG, "Unable to create files subdir " + file.getPath());
                return null;
            }
//            FileUtils.setPermissions(
//                    file.getPath(),
//                    FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH,
//                    -1, -1);
        }
        return file;
    }
    
    @Override
    public File getFileStreamPath(String name) {
    	if (!ENBABLE_FILE) {
    		return super.getFileStreamPath(name);
    	}
        return makeFilename(getFilesDir(), name);
    }

    @Override
    public String[] fileList() {
    	if (!ENBABLE_FILE) {
    		return super.fileList();
    	}
        final String[] list = getFilesDir().list();
        return (list != null) ? list : EMPTY_FILE_LIST;
    }
    
    @Override
    public File getCacheDir() {
    	if (!ENBABLE_FILE) {
    		return super.getCacheDir();
    	}
        synchronized (mSync) {
            if (mCacheDir == null) {
                mCacheDir = new File(getDataDirFile(), "cache");
            }
            if (DEBUG_FILE) {
            	Log.d(TAG, "getCacheDir. file: " + mCacheDir);
            }
            return createFilesDirLocked(mCacheDir);
        }
    }

    @SuppressLint("NewApi")
	@Override
    public File getCodeCacheDir() {
    	if (!ENBABLE_FILE) {
    		return super.getCodeCacheDir();
    	}
        synchronized (mSync) {
            if (mCodeCacheDir == null) {
                mCodeCacheDir = new File(getDataDirFile(), "code_cache");
            }
            if (DEBUG_FILE) {
            	Log.d(TAG, "getCodeCacheDir. file: " + mCodeCacheDir);
            }
            return createFilesDirLocked(mCodeCacheDir);
        }
    }
    
//// external file 
//    @Override
//    public File getExternalCacheDir() {
//        // Operates on primary external storage
//        return getExternalCacheDirs()[0];
//    }
//
//    @Override
//    public File[] getExternalCacheDirs() {
//        synchronized (mSync) {
//            if (mExternalCacheDirs == null) {
//                mExternalCacheDirs = Environment.buildExternalStorageAppCacheDirs(getPackageName());
//            }
//
//            // Create dirs if needed
//            return ensureDirsExistOrFilter(mExternalCacheDirs);
//        }
//    }
//
//    @Override
//    public File[] getExternalMediaDirs() {
//        synchronized (mSync) {
//            if (mExternalMediaDirs == null) {
//                mExternalMediaDirs = Environment.buildExternalStorageAppMediaDirs(getPackageName());
//            }
//
//            // Create dirs if needed
//            return ensureDirsExistOrFilter(mExternalMediaDirs);
//        }
//    }
    
    @Override
    public String[] databaseList() {
    	if (!ENBABLE_FILE) {
    		return super.databaseList();
    	}
    	
        final String[] list = getDatabasesDir().list();
        return (list != null) ? list : EMPTY_FILE_LIST;
    }


    private File getDatabasesDir() {
        synchronized (mSync) {
            if (mDatabasesDir == null) {
                mDatabasesDir = new File(getDataDirFile(), "databases");
            }
            if (mDatabasesDir.getPath().equals("databases")) {
                mDatabasesDir = new File("/data/system");
            }
            return mDatabasesDir;
        }
    }
    
    private File validateFilePath(String name, boolean createDirectory) {
        File dir;
        File f;

        if (name.charAt(0) == File.separatorChar) {
            String dirPath = name.substring(0, name.lastIndexOf(File.separatorChar));
            dir = new File(dirPath);
            name = name.substring(name.lastIndexOf(File.separatorChar));
            f = new File(dir, name);
        } else {
            dir = getDatabasesDir();
            f = makeFilename(dir, name);
        }

        if (createDirectory && !dir.isDirectory() && dir.mkdir()) {
//            FileUtils.setPermissions(dir.getPath(),
//                FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH,
//                -1, -1);
        }

        return f;
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory,
            DatabaseErrorHandler errorHandler) {
    	if (!ENBABLE_FILE) {
    		return super.openOrCreateDatabase(name, mode, factory, errorHandler);
    	}
    	
        File f = validateFilePath(name, true);
        int flags = SQLiteDatabase.CREATE_IF_NECESSARY;
        if ((mode & MODE_ENABLE_WRITE_AHEAD_LOGGING) != 0) {
            flags |= SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;
        }
        SQLiteDatabase db = SQLiteDatabaseCompat.openDatabase(f.getPath(), factory, flags, errorHandler);
        setFilePermissionsFromMode(f.getPath(), mode, 0);
        return db;
    }
    
    static void setFilePermissionsFromMode(String name, int mode,
            int extraPermissions) {
//        int perms = FileUtils.S_IRUSR|FileUtils.S_IWUSR
//            |FileUtils.S_IRGRP|FileUtils.S_IWGRP
//            |extraPermissions;
//        if ((mode&MODE_WORLD_READABLE) != 0) {
//            perms |= FileUtils.S_IROTH;
//        }
//        if ((mode&MODE_WORLD_WRITEABLE) != 0) {
//            perms |= FileUtils.S_IWOTH;
//        }
//        if (DEBUG) {
//            Log.i(TAG, "File " + name + ": mode=0x" + Integer.toHexString(mode)
//                  + ", perms=0x" + Integer.toHexString(perms));
//        }
//        FileUtils.setPermissions(name, perms, -1, -1);
    }

    @Override
    public boolean deleteDatabase(String name) {
    	if (!ENBABLE_FILE) {
    		return super.deleteDatabase(name);
    	}
    	
        try {
            File f = validateFilePath(name, false);
            return SQLiteDatabaseCompat.deleteDatabase(f);
        } catch (Exception e) {
        }
        return false;
    }
    
    

    @Override
    public File getDatabasePath(String name) {
    	if (!ENBABLE_FILE) {
    		return super.getDatabasePath(name);
    	}
    	
        return validateFilePath(name, false);
    }

	static class SharedPrefImplCompat {

		public static void startReloadIfChangedUnexpectedly(Object sp) {
			try {
				Method m = Class.forName("android.app.SharedPreferencesImpl").getDeclaredMethod("startReloadIfChangedUnexpectedly", new Class[]{});
				m.setAccessible(true);	
				m.invoke(sp, new Object[]{});
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public static Object newObject(File prefsFile, int mode) {
			try {
				Constructor<?> c = Class.forName("android.app.SharedPreferencesImpl").getDeclaredConstructor(new Class[]{File.class, int.class});
				c.setAccessible(true);	
				return c.newInstance(new Object[]{prefsFile, mode});
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}		
	}
	
	static class SQLiteDatabaseCompat {

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public static SQLiteDatabase openDatabase(String path,
				CursorFactory factory, int flags,
				DatabaseErrorHandler errorHandler) {
			try {
				Method m = Class.forName("android.database.sqlite.SQLiteDatabase")
					.getDeclaredMethod("openDatabase", 
							new Class[]{String.class, CursorFactory.class, int.class, DatabaseErrorHandler.class});
				m.setAccessible(true);
				return (SQLiteDatabase) m.invoke(null, new Object[]{path, factory, flags, errorHandler});
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		//copied form SQLiteDatabase
//	    public static SQLiteDatabase openDatabaseX(String path, CursorFactory factory, int flags,
//	            DatabaseErrorHandler errorHandler) {
//	        SQLiteDatabase db = new SQLiteDatabase(path, flags, factory, errorHandler);
//	        db.open();
//	        return db;
//	    }
	    public static boolean deleteDatabase(File file) {
	    	
	        if (file == null) {
	            throw new IllegalArgumentException("file must not be null");
	        }

	        boolean deleted = false;
	        deleted |= file.delete();
	        deleted |= new File(file.getPath() + "-journal").delete();
	        deleted |= new File(file.getPath() + "-shm").delete();
	        deleted |= new File(file.getPath() + "-wal").delete();

	        File dir = file.getParentFile();
	        if (dir != null) {
	            final String prefix = file.getName() + "-mj";
	            File[] files = dir.listFiles(new FileFilter() {
	                @Override
	                public boolean accept(File candidate) {
	                    return candidate.getName().startsWith(prefix);
	                }
	            });
	            if (files != null) {
	                for (File masterJournal : files) {
	                    deleted |= masterJournal.delete();
	                }
	            }
	        }
	        return deleted;
	    }
		
	}
}