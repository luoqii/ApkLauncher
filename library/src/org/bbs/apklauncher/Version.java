package org.bbs.apklauncher;

public class Version {
	public static String VERSION = "0.1";	
	
	public static boolean isNewer(String rVersion, String lVersion){
		boolean isNewer = false;
		
		return isNewer;
	}
	
	public static String toVersion(String versionStr){
		String v = versionStr.replace(".*\\(\\d+[.\\d+]*\\).*", "\1");
		return v;
	}
}
