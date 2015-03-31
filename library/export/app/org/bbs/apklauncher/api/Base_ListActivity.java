package org.bbs.apklauncher.api;

import android.app.Activity;
import android.app.ListActivity;
import android.app.TabActivity;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;

public class Base_ListActivity extends ListActivity {
	
	private static final String TAG = Base_ListActivity.class.getSimpleName();
	
	//@Override
	public Base_ListActivity getHostActivity() {
		return this;
	}
}
