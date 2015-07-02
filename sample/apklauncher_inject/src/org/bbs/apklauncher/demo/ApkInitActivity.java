package org.bbs.apklauncher.demo;
import org.bbs.apklauncher.demo_inject.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ApkInitActivity extends Activity {
	private static final String TAG = ApkInitActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_init_apk);
	}
	
	public void onClick(View view){
		Intent intent = new Intent();
		intent.setClassName(getPackageName(), App.TARGET_LAUNCHER_NAME);
		startActivity(intent);
	}
}
