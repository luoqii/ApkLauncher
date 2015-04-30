package orb.bbs.apklauncher_zeroinstall;

import orb.bbs.apklauncher.zeroinstall.shell.R;

import org.bbs.apklauncher.api.Base_Activity;

import android.os.Bundle;

public class MainActivity extends Base_Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	}
}
