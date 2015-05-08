package org.bbs.apklauncher.demo;
import java.util.ArrayList;
import java.util.List;

import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.ApkUtil;
import org.bbs.apklauncher.emb.IntentHelper;
import org.bbs.apklauncher.emb.LoadedApk;
import org.bbs.apklauncher.emb.auto_gen.Stub_Activity;
import org.bbs.apkparser.PackageInfoX;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;
import org.bbs.apkparser.PackageInfoX.IntentFilterX;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import dalvik.system.DexClassLoader;

public class ApkLuncherActivity extends Activity {
	private static final String TAG = ApkLuncherActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_apk_launcher);
		
		ApkPackageManager apks = ApkPackageManager.getInstance();
		ListAdapter adapter = new ArrayAdapter<PackageInfoX.ActivityInfoX>(this, android.R.layout.simple_list_item_1, parseLauncher(apks.getAllApks())){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v =  super.getView(position, convertView, parent);
				
				v.setTag(getItem(position));
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						PackageInfoX.ActivityInfoX a = (ActivityInfoX) v.getTag();
						
						Log.d(TAG, "onClick. activity: " + a);
						
						ApkUtil.startActivity(ApkLuncherActivity.this, a);
					}
				});
				return v ;
			}
		};
		ListView mListView = (ListView)findViewById(R.id.apk_container);
		mListView.setAdapter(adapter);;
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				PackageInfoX.ActivityInfoX a = (ActivityInfoX) view.getTag();
				
				Log.d(TAG, "onClick. activity: " + a);

				ApkUtil.startActivity(ApkLuncherActivity.this, a);
				
			}
		});
		mListView.setEmptyView(findViewById(android.R.id.empty));;
	}	

	private List<PackageInfoX.ActivityInfoX> parseLauncher(List<PackageInfoX> ms) {
		List<PackageInfoX.ActivityInfoX> launchers = new ArrayList<PackageInfoX.ActivityInfoX>();
		for (PackageInfoX m : ms) {
			
			if (m.activities != null) {
				for (ActivityInfo a : m.activities) {
					PackageInfoX.ActivityInfoX aX = (ActivityInfoX) a;
					if (aX.mIntentFilters != null) {
						for (IntentFilterX i : aX.mIntentFilters) {
							if (i.hasAction(IntentHelper.ACTION_MAIN) && i.hasCategory(IntentHelper.CATEGORY_LAUNCHER)) {
								launchers.add(aX);
								break;
							}
						}
					}
				}
			}
		}
		return launchers;
	}
	
}
