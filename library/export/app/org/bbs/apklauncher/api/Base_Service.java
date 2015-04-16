package org.bbs.apklauncher.api;

import android.app.Service;

public abstract class Base_Service extends Service {
	
	private static final String TAG = Base_Service.class.getSimpleName();
	
	public int getHostIdentifier(String name, String defType, String defPackage){
		int resId = -1;
		resId = getResources().getIdentifier(name, defType, defPackage);
		
		return resId;
	}
}
