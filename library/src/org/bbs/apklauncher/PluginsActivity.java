package org.bbs.apklauncher;

import org.bbs.apkparser.PackageInfoX;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PluginsActivity extends Activity {
	private static final int MENU_PICK = R.layout.apklauncher_activity_plugins;
	private static final String TAG = PluginsActivity.class.getSimpleName();
	
	private ActivityInfoX mDeleteAct;
	private ActionMode mActionMode;
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.apklauncher_plugins_context_menu, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        int itemId = item.getItemId();
			if (itemId == R.id.delete) {
				deletePlguin();
				mode.finish(); // Action picked, so close the CAB
				return true;
			} else {
				return false;
			}
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.apklauncher_activity_plugins);
		populateUi();
		
//		getActionBar().get
	}
	
	protected void deletePlguin() {
		ApkPackageManager.getInstance().deleteApk(mDeleteAct.mPackageInfo);
		populateUi();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
//		populateUi();
	}

	private void populateUi() {
		ApkPackageManager apks = ApkPackageManager.getInstance();
		ListAdapter adapter = new ArrayAdapter<PackageInfoX.ActivityInfoX>(this, android.R.layout.simple_list_item_1, apks.getLauncherActivityInfo()){
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View v =  super.getView(position, convertView, parent);
				
				v.setTag(getItem(position));
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						PackageInfoX.ActivityInfoX a = (ActivityInfoX) v.getTag();
						onAppClick(a);
					}
				});
				v.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						mActionMode = startActionMode(mActionModeCallback);
						mDeleteAct = getItem(position);
						return true;
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

				onAppClick(a);
			}
		});
		mListView.setEmptyView(findViewById(android.R.id.empty));;
	}	
		
	protected void onAppClick(ActivityInfoX info){
		
		Log.d(TAG, "onClick. activity: " + info);

		ApkLauncher.getInstance().startActivity(PluginsActivity.this, info);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// XXX not work, why???
//		menu.add(Menu.NONE, MENU_PICK, 0, "pick plugin");
		getMenuInflater().inflate(R.menu.apklauncher_plugin, menu);
//		return true;
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// XXX not work, why???
//		if (MENU_PICK == item.getItemId()){
		if (item.getItemId() == R.id.pick) {
			Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
			pick.setType("*/*");
//			Intent pick = new Intent(Intent.ACTION_PICK);			
			startActivityForResult(pick, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (0 == requestCode && RESULT_OK == resultCode){
			Uri uri = data.getData();
			Log.d(TAG, "uri: " + uri);
			if ("file".equals(uri.getScheme())){
				String apkPath = uri.getPath();
				ApkPackageManager.getInstance().parseApkFile(apkPath);
				
				populateUi();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
