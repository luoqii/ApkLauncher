package org.bbs.apklauncher.api;

import android.app.Activity;
import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;

import org.bbs.apklauncher.emb.IntentHelper;

public class Base_TabActivity extends TabActivity {
	
	private static final String TAG = Base_TabActivity.class.getSimpleName();
	private Intent mIntent;
	
	//@Override
	public Base_TabActivity getHostActivity() {
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
