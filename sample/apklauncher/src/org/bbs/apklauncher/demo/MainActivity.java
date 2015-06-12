package org.bbs.apklauncher.demo;
import java.util.ArrayList;
import java.util.List;

import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.ApkUtil;
import org.bbs.apklauncher.emb.IntentHelper;
import org.bbs.apkparser.PackageInfoX;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;
import org.bbs.apkparser.PackageInfoX.IntentFilterX;

import android.app.Activity;
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

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_apk_launcher);
		
		ApkPackageManager apks = ApkPackageManager.getInstance();
		ListAdapter adapter = new ArrayAdapter<PackageInfoX.ActivityInfoX>(this, android.R.layout.simple_list_item_1, apks.getLauncherActivityInfo()){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v =  super.getView(position, convertView, parent);
				
				v.setTag(getItem(position));
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						PackageInfoX.ActivityInfoX a = (ActivityInfoX) v.getTag();
						
						Log.d(TAG, "onClick. activity: " + a);
						
						ApkLauncher.getInstance().startActivity(MainActivity.this, a);
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

				ApkLauncher.getInstance().startActivity(MainActivity.this, a);
				
			}
		});
		mListView.setEmptyView(findViewById(android.R.id.empty));;
	}
	
}
