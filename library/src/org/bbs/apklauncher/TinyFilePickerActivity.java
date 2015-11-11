package org.bbs.apklauncher;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * <pre>
 * keep this class as simple as possible.
 * and do not depends other res/code to make that
 * everyone can only  COPY this class and 
 * it worked perfectly.
 * 
 * after user pick one file, resultIntent.getData().getPath() 
 * contain absolute picked file path.
 * e.g.:
 * file:///path/to/file
 * 
 * @author bysong
 *
 */
public class TinyFilePickerActivity extends Activity {
	
	public static final String EXTRA_DIR = "TinyFilePickerActivity.EXTRA_DIR";
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
		if (TextUtils.isEmpty(dir)) {
			dir = Environment.getExternalStorageDirectory().getPath();
		}
		File[] files = new File(dir).listFiles();
		
		mListV.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, files));
	}
	
}
