package org.bbs.apklauncher.api;

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
import org.bbs.apklauncher.emb.auto_gen.Target_Activity;
import org.bbs.apklauncher.emb.auto_gen.Target_ActionBarActivity;
//do NOT edit this file, auto-generated.
import org.bbs.apklauncher.emb.auto_gen.Target_ActivityGroup;
import org.bbs.apklauncher.emb.auto_gen.Target_ExpandableListActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_FragmentActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_ListActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_PreferenceActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_TabActivity;

public class Base_ExpandableListActivity extends Target_ExpandableListActivity {
	
	private static final String TAG = Base_ExpandableListActivity.class.getSimpleName();
	
	@Override
	public StubBase_ExpandableListActivity getHostActivity() {
		return super.getHostActivity();
	}
	
	public int getHostIdentifier(String name, String defType, String defPackage){
//do NOT edit this file, auto-generated.
		int resId = -1;
		resId = org.bbs.apklauncher.emb.Util.getContextImpl(this).getResources().getIdentifier(name, defType, defPackage);
		
		return resId;
	}	
}
