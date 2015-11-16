package org.bbs.apklauncher.osgi.bundlemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.apklauncher_osgi.R;

public class BundleDetailActivity extends FragmentActivity {
	public static final String EXTRA_BUNDLE_ID = "extra.bundle.id";
	private static final String TAG = BundleDetailActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bundle_detail);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Uri uri = intent.getData();
		String id = uri.getQueryParameter("id");
		Log.d(TAG, "bundle id: " + id);

		((BundleDetailFragment)getSupportFragmentManager().findFragmentById(R.id.listFragment)).showBundle(Long.parseLong(id));
	}
}
