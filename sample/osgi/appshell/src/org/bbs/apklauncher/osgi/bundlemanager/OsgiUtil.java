package org.bbs.apklauncher.osgi.bundlemanager;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import android.text.TextUtils;
import android.util.Log;

public class OsgiUtil {
    private static final String TAG = OsgiUtil.class.getSimpleName();
	static Map<Integer, String> sStateMap = new HashMap<Integer, String>();
    static {
        sStateMap.put(org.osgi.framework.Bundle.ACTIVE,      "ACTIVE");
        sStateMap.put(org.osgi.framework.Bundle.INSTALLED,   "INSTALLED");
        sStateMap.put(org.osgi.framework.Bundle.RESOLVED,    "RESOLVED");
        sStateMap.put(org.osgi.framework.Bundle.STARTING,    "STARTING");
        sStateMap.put(org.osgi.framework.Bundle.STOPPING,    "STOPPING");
        sStateMap.put(org.osgi.framework.Bundle.UNINSTALLED, "UNINSTALLED");
    }
    public static String bundleState2Str(int state){
        String str = state + " [unknown state]";
        if (sStateMap.containsKey(state)){
            str = sStateMap.get(state);
        }
        return str;
    }
    
    public static Bundle getBundleBySymblicName(BundleContext context, String name){
    	for (Bundle b : context.getBundles()){
    		if (name.equals(b.getHeaders().get(Constants.BUNDLE_SYMBOLICNAME))) {
    			return b;
    		}
    	}
    	
    	return null;
    }
    
    public static String getName(org.osgi.framework.Bundle b) {
    	String name = "";
    	name = b.getSymbolicName();
    	if (TextUtils.isEmpty(name)) {
    		name = b.getLocation();
    	}
    	
    	return name;
    }
    
    public static String[] HEADER_ALL = new String[]{};
    public static String[] HEADER_OSGI = new String[]{
    	org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME,
    	org.osgi.framework.Constants.BUNDLE_VERSION,
    	org.osgi.framework.Constants.IMPORT_PACKAGE,
    	org.osgi.framework.Constants.EXPORT_PACKAGE,
    };
    public static String getHeader(org.osgi.framework.Bundle b, String[] headers) {
    	String header = "";
    	for (String h : headers) {
    		Dictionary<String, String> d = b.getHeaders(h);
    		Enumeration<String> keys = d.keys();
    		while (keys.hasMoreElements()) {
    			String key = keys.nextElement();
    			if (key.equals(h)) {
    				header += h + ":" + d.get(key) + "\n";
    				break;
    			}
    			
    		}
    	}
    	if (null == header || header.length() == 0) {
    		header = toString(b.getHeaders());
    	}
    	
    	return header;
    }
	
	public static String toString(Dictionary<String, String> d) {
		String str = "";
		Enumeration<String> e = d.keys();
		while (e != null && e.hasMoreElements()) {
			String key = e.nextElement();
			str += key + ": " + d.get(key) + "\n";
		}
		
		return str;
	}
	
	public static ClassLoader getBundleClassLoader(Bundle bundle) {
		String activator = bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_ACTIVATOR);		
		try {
			return bundle.loadClass(activator).getClassLoader();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void dumpProperties(BundleContext context){
		Log.d(TAG, Constants.FRAMEWORK_VERSION + " " + context.getProperty(Constants.FRAMEWORK_VERSION));
		Log.d(TAG, Constants.FRAMEWORK_VENDOR + " " + context.getProperty(Constants.FRAMEWORK_VENDOR));
		Log.d(TAG, Constants.FRAMEWORK_LANGUAGE + " " + context.getProperty(Constants.FRAMEWORK_LANGUAGE));
		Log.d(TAG, Constants.FRAMEWORK_EXECUTIONENVIRONMENT + " " + context.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
		Log.d(TAG, Constants.FRAMEWORK_PROCESSOR + " " + context.getProperty(Constants.FRAMEWORK_PROCESSOR));
		Log.d(TAG, Constants.FRAMEWORK_OS_NAME + " " + context.getProperty(Constants.FRAMEWORK_OS_NAME));
		Log.d(TAG, Constants.FRAMEWORK_OS_VERSION + " " + context.getProperty(Constants.FRAMEWORK_OS_VERSION));
		// optional
		Log.d(TAG, Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION + " " + context.getProperty(Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION));
		Log.d(TAG, Constants.SUPPORTS_FRAMEWORK_EXTENSION + " " + context.getProperty(Constants.SUPPORTS_FRAMEWORK_EXTENSION));
		Log.d(TAG, Constants.SUPPORTS_FRAMEWORK_FRAGMENT + " " + context.getProperty(Constants.SUPPORTS_FRAMEWORK_FRAGMENT));
		Log.d(TAG, Constants.SUPPORTS_FRAMEWORK_REQUIREBUNDLE + " " + context.getProperty(Constants.SUPPORTS_FRAMEWORK_REQUIREBUNDLE));
//		Log.d(TAG, Constants.FR + " " + context.getProperty(Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION));
	}
}
