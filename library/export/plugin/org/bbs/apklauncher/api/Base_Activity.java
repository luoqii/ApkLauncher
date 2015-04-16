package org.bbs.apklauncher.api;

import org.bbs.apklauncher.emb.auto_gen.StubBase_Activity;
import org.bbs.apklauncher.emb.auto_gen.StubBase_Activity;
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
import org.bbs.apklauncher.emb.auto_gen.Target_FragmentActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_ListActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_PreferenceActivity;
import org.bbs.apklauncher.emb.auto_gen.Target_TabActivity;

public class Base_Activity extends Target_Activity {
	
	private static final String TAG = Base_Activity.class.getSimpleName();
	
	@Override
	public StubBase_Activity getHostActivity() {
		return super.getHostActivity();
	}
	
	public int getHostIdentifier(String name, String defType, String defPackage){
		int resId = -1;
		resId = org.bbs.apklauncher.emb.Util.getContextImpl(this).getResources().getIdentifier(name, defType, defPackage);
		
		return resId;
	}	
}
