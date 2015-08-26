package com.example.apklauncher_mulit_plugin;

import org.bbs.apklauncher.PluginsActivity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateConfig;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends PluginsActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		UpdateConfig.setDebug(true);
		UmengUpdateAgent.update(this);
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
