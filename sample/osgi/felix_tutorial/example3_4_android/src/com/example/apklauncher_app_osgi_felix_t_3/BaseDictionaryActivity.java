package com.example.apklauncher_app_osgi_felix_t_3;

import org.bbs.apklauncher.api.Base_Activity;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import tutorial.example2.service.DictionaryService;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.apklauncher_osgi_felix_t_3.R;

public class BaseDictionaryActivity extends Base_Activity {
	
	// copied form felix
	protected BundleContext m_context;
	protected View mCheck;
	protected TextView mStatus;
	protected TextView mWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dictionary);

		m_context = Activator.sContext;
		mWord = (TextView)findViewById(R.id.word);
		mStatus = (TextView) findViewById(R.id.status);
		mCheck = findViewById(R.id.check);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	protected void checkWord(DictionaryService s) {
		mStatus.setText(s.checkWord(mWord.getText().toString()) ? "Correct. "  : "Incorrect.");
	}
	


}
