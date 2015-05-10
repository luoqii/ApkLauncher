package org.bbs.apklauncher.api;

import android.app.Activity;
import android.app.ListActivity;
import android.app.TabActivity;
import android.app.ExpandableListActivity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.preference.PreferenceActivity;
//do NOT edit this file, auto-generated.
import android.support.v4.app.FragmentActivity;
import android.app.ActionBarActivity;

import org.bbs.apklauncher.emb.IntentHelper;

public class Base_FragmentActivity extends FragmentActivity {
	
	private static final String TAG = Base_FragmentActivity.class.getSimpleName();
	private Intent mIntent;

	public int getHostIdentifier(String name, String defType, String defPackage){
		int resId = -1;
		resId = getResources().getIdentifier(name, defType, defPackage);
		
		return resId;
	}
		
//do NOT edit this file, auto-generated.
	//@Override
	public Base_FragmentActivity getHostActivity() {
		return this;
	}
	
	//@Override
	public Intent getIntent() {
		if (null == mIntent) {
			mIntent = new IntentHelper(super.getIntent());
		}
		return mIntent;
		
//		return super.getIntent();
	}
}
