package org.bbs.apklauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.bbs.apklauncher.api.ExportApi;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;
import org.bbs.apkparser.PackageInfoX.ServiceInfoX;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

@ExportApi
public class AndroidUtil {
	private static final String TAG = AndroidUtil.class.getSimpleName();	
	
	public static String getInstallApkPath(Context context, String packageName) {
		String path = "";
		try {
			ApplicationInfo pInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
			path = pInfo.sourceDir;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return path;
	}
	
	public static void copyFile(File src, File dest) {
		if (null == src || null == dest) {
			return;
		}
		
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			
			copyStream(in, out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void copyStream(InputStream in, OutputStream out){
		try {
			int byteCount = 1024 * 1024;
			byte[] buffer = new byte[byteCount];
			int count = 0;
			while ((count = in.read(buffer, 0, byteCount)) != -1){
				out.write(buffer, 0, count);
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void extractZipEntry(ZipFile zipFile, String entryName, File destDir) {
		if (zipFile == null || entryName == null || destDir == null) return;
		Log.d(TAG, "zipFile: " + zipFile + " entryName: " + entryName);
		
		destDir.getParentFile().mkdirs();
		ZipEntry zE = zipFile.getEntry(entryName);
		
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		if (entries.hasMoreElements()) {
			ZipEntry nextElement = entries.nextElement();
//			Log.d(TAG, "ze: " + nextElement);
		}
		
		try {
			InputStream in = null;
//			in = zipFile.getInputStream(zE);
			in = new FileInputStream(new File(zipFile.getName()));
			ZipInputStream zIn = new ZipInputStream(in);
			try {
				ZipEntry ze;
				while ((ze = zIn.getNextEntry()) != null) {
//					Log.d(TAG, "ze: " + ze.getName() );
					String zpath = ze.getName();
					String zPPath = new File(zpath).getParent();
//					Log.d(TAG, "zPPath: " + zPPath);
					if (!entryName.equals(zPPath) || ze.isDirectory()) {
						continue;
					}
//					Log.d(TAG, "ze: " + ze);
					String name = ze.getName().substring(entryName.length());
					
					File destFile = new File(destDir, name);
					if (destFile.exists()){
						destFile.delete();
						destFile.createNewFile();
					}
					destFile.getParentFile().mkdirs();
					FileOutputStream fout = new FileOutputStream(destFile);
					byte[] buffer = new byte[1024];
					int count;
					while ((count = zIn.read(buffer)) != -1) {
						fout.write(buffer, 0, count);
					}
					fout.flush();
					fout.close();
					Log.d(TAG, "extract zipEntry: " + ze.getName() + " to " + destFile.getPath());
				}
			} finally {
				zIn.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// TODO
		public static final String toMemoryLevel(int level) {
			String levelStr = "";
			levelStr = level + "";
			
			return levelStr;
		}


		
	    public static Context getContextImpl(Context context) {
	        Context nextContext;
	        while ((context instanceof ContextWrapper) &&
	                (nextContext=((ContextWrapper)context).getBaseContext()) != null) {
	            context = nextContext;
	        }
	        return (Context)context;
	    }
}
