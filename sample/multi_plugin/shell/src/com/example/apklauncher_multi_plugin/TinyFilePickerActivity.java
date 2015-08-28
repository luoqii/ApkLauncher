package com.example.apklauncher_multi_plugin;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TinyFilePickerActivity extends Activity {

	public static final String EXTRA_DIR = ".EXTRA_DIR";
	private ListView mListV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("TinyFilePicker");
		mListV = new ListView(this);
		mListV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				File f = (File) mListV.getAdapter().getItem(position);
				
				Intent data = new Intent();
				data.setData(Uri.parse("file://" + f.getPath()));
				setResult(RESULT_OK, data);
				finish();
			}
		});
		setContentView(mListV);
		

		String dir = getIntent().getStringExtra(EXTRA_DIR);
		File[] files = new File(dir).listFiles();
		
		mListV.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, files));
	}
	
}
