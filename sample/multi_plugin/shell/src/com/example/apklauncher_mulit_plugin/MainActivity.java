package com.example.apklauncher_mulit_plugin;

import org.bbs.apklauncher.ApkLauncher;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.PluginsActivity;
import org.bbs.apkparser.PackageInfoX;
import org.bbs.apkparser.PackageInfoX.ActivityInfoX;

import android.annotation.TargetApi;
import android.os.Build;
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

import com.example.apklauncher_multi_plugin.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends PluginsActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	
}
