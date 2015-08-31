package com.example.apklauncher_app;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.osgi.bundlemanager.FrameworkHelper;
import org.bbs.apklauncher.osgi.bundlemanager.OsgiUtil;
import org.bbs.apkparser.PackageInfoX;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;
import org.osgi.framework.launch.Framework;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.apklauncher_osgi.R;

public class MainActivity extends Activity {
	private static final String HEADER_CONTRIBUTE_ANDROID_COMPONENT = "X-contribute-android-component";
	protected static final String TAG = MainActivity.class.getSimpleName();
	private ListView mList;
	private Framework mFm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mList = (ListView)findViewById(R.id.list);
		mList.setEmptyView(findViewById(R.id.empty));
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				org.osgi.framework.Bundle b = (org.osgi.framework.Bundle) parent.getAdapter().getItem(position);
				String apkPath = b.getLocation();
				Log.d(TAG, "classloader: " + b.getClass().getClassLoader());
				ApkPackageManager pm = ApkPackageManager.getInstance();

				// not support file:// schema ???
				if (apkPath.startsWith("file://")) {
					apkPath = apkPath.substring("file://".length());
				}
				PackageInfoX info = pm.parseApkFile(apkPath);
				List<ActivityInfoX> aInfo = pm.getLauncherActivityInfo(info.packageName);
				ApkLauncher.getInstance().startActivity(MainActivity.this, aInfo.get(0));
				MyStub.mBundleId = b.getBundleId();
			}
		});
		
		mFm = FrameworkHelper.getInstance(null).getFramework();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		populateUi();
	}
	
	void populateUi(){
		org.osgi.framework.Bundle[] bundles = mFm.getBundleContext().getBundles();
		List<org.osgi.framework.Bundle> bList = new ArrayList<>();
		for (org.osgi.framework.Bundle b : bundles){
			Dictionary<String, String> h = b.getHeaders();
			if (h != null 
					&& h.get(HEADER_CONTRIBUTE_ANDROID_COMPONENT) != null
					) {
				bList.add(b);
			}
		}
		
		ArrayAdapter<org.osgi.framework.Bundle> adapter = new ArrayAdapter<org.osgi.framework.Bundle>(this, android.R.layout.simple_list_item_1, bList){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView  t = new TextView(MainActivity.this);
				org.osgi.framework.Bundle b = getItem(position);
				CharSequence text = OsgiUtil.getName(b) + " " 
						+ b.getBundleId() + " "
						+ OsgiUtil.bundleState2Str(b.getState());
				t.setText(text);
				return t;
//				return super.getView(position, convertView, parent);
			}
		};
		mList.setAdapter(adapter);
	}
}
