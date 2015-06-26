package org.bbs.apklauncher;

import android.util.Log;

public class Version {
	private static final String TAG = Version.class.getSimpleName();
	public static String VERSION = "0.1";	
	
	public static boolean isNewer(String otherVersion){
		return isNewer(VERSION, otherVersion);
	}

	public static boolean isSame(String rVersion, String lVersion){
		return extractVersion(rVersion).equals(extractVersion(lVersion));
	}

	public static boolean isNewer(String rVersion, String lVersion){
		return isNewerRaw(extractVersion(rVersion), extractVersion(lVersion));
	}
	
	public static boolean isNewerRaw(String rVersion, String lVersion){
		boolean isNewer = false;
		if (!isValid(rVersion)) {
			throw new IllegalArgumentException("invalid version: " + rVersion);
		} else if (!isValid(lVersion)) {
			throw new IllegalArgumentException("invalid version: " + lVersion);
		} else {
			final String[] rV = rVersion.split("\\.");
			final String[] lV = lVersion.split("\\.");
			final int rL = rV.length;
			final int lL = lV.length;
			int i = 0;
			for (; i < Math.min(rL, lL); i++) {
				int r = Integer.parseInt(rV[i]);
				int l = Integer.parseInt(lV[i]);
				Log.d(TAG, "r: " + r + " l: " + l);
				if (r > l){
					isNewer = true;
					break;
				} else if (r < l) {
					isNewer = false;
					break;
				}
			}
			if (i == Math.min(rL, lL) && rL > lL) {
				isNewer = true;
			}
		}
		
		return isNewer;
	}
	
	public static boolean isValid(String versionStr){
		// TODO regex
//		return versionStr.matches("^[\\.0-9]*&");
		
		final int L = versionStr.length();
		for (int i = 0 ; i < L ; i ++) {
			char c = versionStr.charAt(i);
			if (c == '.' || (c >= '0' && c <= '9')) {
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	public static String extractVersion(String versionStr){
		// TODO extract by Regex
		String v = versionStr.replaceAll(".*(\\d+[^a]*).*", "aa\\1bb\\2");
		
		final int L = versionStr.length();
		v = "";
		boolean hit = false;
		for (int i = 0 ; i < L ; i ++) {
			char c = versionStr.charAt(i);
			if (c == '.' || (c >= '0' && c <= '9')) {
				hit = true;
				v = v + c;
			} else {
				if (hit) {
					break;
				}
			}
		}
		
		return v;
	}
}
