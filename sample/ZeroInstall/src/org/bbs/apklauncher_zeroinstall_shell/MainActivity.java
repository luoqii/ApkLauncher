package org.bbs.apklauncher_zeroinstall_shell;

import org.bbs.apklauncher.SingleLauncherActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends SingleLauncherActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent update = new Intent(this, UpdateService.class);
		startService(update);
	}
}
