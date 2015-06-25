package com.example.apklauncher_app_intnet_helper;

import org.bbs.apklauncher.api.Base_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.apklauncher_app_intent_helper.R;

public class ResultActivity extends Base_Activity {
	private static final String TAG = ResultActivity.class.getSimpleName();

	public static final String EXTRA_SERIABLE = "EXTRA_SERIABLE";
	public static final String EXTRA_PARCEL = "EXTRA_PARCEL";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		int pa = -1;
//		pa = ((MainActivity.P)intent.getParcelableExtra(EXTRA_PARCEL)).a;
		int sa = -1;
		sa = ((MainActivity.S)intent.getSerializableExtra(EXTRA_SERIABLE)).a;
		
		CharSequence text;
		text = "pa: " + pa;
		text = text + "\nsa: " + sa;
		((TextView)findViewById(R.id.text1)).setText(text);
		findViewById(R.id.button).setVisibility(View.INVISIBLE);
	}
}
