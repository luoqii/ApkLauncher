package org.bbs.apklauncher;

import org.bbs.apklauncher.api.ExportApi;

import android.util.Log;

/**
 * @author bysong
 *
 */
@ExportApi
public class ApkLauncherConfig {
	static boolean DEBUG;
	static int LOG_LEVEL;
	
	public static /*final*/ boolean ENALBE_SERVICE = true;

	public static void setDebug(boolean debug){
		DEBUG = debug;
	}
	
	public static boolean debug(){
		return DEBUG;
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
