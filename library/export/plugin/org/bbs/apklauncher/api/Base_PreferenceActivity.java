package org.bbs.apklauncher.api;

import org.bbs.apklauncher.AndroidUtil;

import org.bbs.apklauncher.emb.auto_gen.StubBase_Activity;
import org.bbs.apklauncher.emb.auto_gen.StubBase_ActionBarActivity;
import org.bbs.apklauncher.emb.auto_gen.StubBase_ActivityGroup;
import org.bbs.apklauncher.emb.auto_gen.StubBase_ExpandableListActivity;
import org.bbs.apklauncher.emb.auto_gen.StubBase_FragmentActivity;
import org.bbs.apklauncher.emb.auto_gen.StubBase_ListActivity;
import org.bbs.apklauncher.emb.auto_gen.StubBase_PreferenceActivity;
import org.bbs.apklauncher.emb.auto_gen.StubBase_TabActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_Activity;
import org.bbs.apklauncher.emb.auto_gen.Target_FragmentActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_ListActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_PreferenceActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_TabActivity;
//do NOT edit this file, auto-generated.
import org.bbs.apklauncher.emb.auto_gen.Target_Activity;
import org.bbs.apklauncher.emb.auto_gen.Target_ActionBarActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_ActivityGroup;
import org.bbs.apklauncher.emb.auto_gen.Target_ExpandableListActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_FragmentActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_ListActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_PreferenceActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_TabActivity;

public class Base_PreferenceActivity extends Target_PreferenceActivity {
	
	private static final String TAG = Base_PreferenceActivity.class.getSimpleName();
	
	@Override
	public StubBase_PreferenceActivity getHostActivity() {
		return super.getHostActivity();
	}
//do NOT edit this file, auto-generated.
	
	public int getHostIdentifier(String name, String defType, String defPackage){
		int resId = -1;
		resId = AndroidUtil.getContextImpl(this).getResources().getIdentifier(name, defType, defPackage);
		
		return resId;
	}	
}
