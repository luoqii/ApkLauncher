package org.bbs.apklauncher.api;

import android.app.Activity;
import android.app.ListActivity;
import android.app.TabActivity;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;

public class Base_FragmentActivity extends FragmentActivity {
	
	private static final String TAG = Base_FragmentActivity.class.getSimpleName();
	
	//@Override
	public Base_FragmentActivity getHostActivity() {
		return this;
	}
}
