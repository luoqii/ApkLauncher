package org.bbs.apklauncher;

import org.bbs.apklauncher.api.ExportApi;

import android.util.Log;

/**
 * @author bysong
 *
 */
@ExportApi
public class ApkLauncherConfig {
	private static boolean DEBUG;
	private static int LOG_LEVEL;

	public static void setDebug(boolean debug){
		DEBUG = debug;
	}
		
	/**
	 * @param logLevel
	 * 
	 * @see {@link Log#VERBOSE}
	 * @see {@link Log#INFO}
	 * @see {@link Log#WARN}
	 * @see {@link Log#ERROR}
	 * @see {@link Log#ASSERT}
	 */
	public static void setLogLevel(int logLevel){
		LOG_LEVEL = logLevel;
	}
}
