package com.example.apklauncher_app_intent_helper.test;

import com.example.apklauncher_app_intent_helper.R;
import com.example.apklauncher_app_intnet_helper.MainActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;

public class IntentTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mMain;
	private View mButton;

	public IntentTest(Class<MainActivity> activityClass) {
		super(activityClass);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mMain = getActivity();
		mButton = mMain.findViewById(R.id.button);
	}
	
	public void testPreconditions(){
		assertNotNull(mButton);
//		assertTrue(ViewAsserts.);
	}
	
	public void testClick(){
		
	}

}
