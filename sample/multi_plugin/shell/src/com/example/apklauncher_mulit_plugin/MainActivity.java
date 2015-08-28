package com.example.apklauncher_mulit_plugin;

import java.io.File;
import java.util.List;

import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.PluginsActivity;
import org.bbs.apklauncher.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;

import com.example.apklauncher_multi_plugin.TinyFilePickerActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateConfig;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends PluginsActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private File mDestDir;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		UpdateConfig.setDeltaUpdate(false);
		UpdateConfig.setDebug(true);
		UmengUpdateAgent.update(this);
		
		mDestDir = new File(Environment.getExternalStorageDirectory(), "Download");
		mDestDir = getDir("sdcard", Context.MODE_WORLD_READABLE);
		ApkPackageManager.getInstance().extractApkFromAsset(getResources().getAssets(), 
				"sdcard/Download", 
				mDestDir);
		ApkPackageManager.getInstance().extractApkFromAsset(getResources().getAssets(), 
				ApkLauncher.ASSET_PLUGIN_DIR, 
				mDestDir);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.pick) {
			Intent pick = new Intent(this, TinyFilePickerActivity.class);
			pick.putExtra(TinyFilePickerActivity.EXTRA_DIR, mDestDir.getPath());
			startActivityForResult(pick, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
