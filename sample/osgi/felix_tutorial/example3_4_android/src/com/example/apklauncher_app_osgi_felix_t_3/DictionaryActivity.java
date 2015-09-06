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

public class DictionaryActivity extends Base_Activity {
	
	private ServiceReference<?>[] mRefs;
	private BundleContext mBundleContext;
	private View mCheck;
	private TextView mStatus;
	private TextView mWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dictionary);
		
		mWord = (TextView)findViewById(R.id.word);
		mStatus = (TextView) findViewById(R.id.status);
		mCheck = findViewById(R.id.check);
		mCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CharSequence word = ((TextView)findViewById(R.id.word)).getText();
				
				for (ServiceReference<?> s : mRefs){
					DictionaryService d = (DictionaryService) mBundleContext.getService(s);
					mStatus.setText(d.checkWord(mWord.getText().toString()) ? "Correct. "  : "Incorrect.");
				}

			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		populateUi();
	}
	
	void populateUi(){
		if (true){
//			return;
		}

		mBundleContext = Activator.sContext;
		if (null == mBundleContext){
			return;
		}
		
//		mBundleContext = FrameworkHelper.getInstance(null)
//				.getFramework()
//				.getBundleContext();
        try {
			mRefs = mBundleContext.getServiceReferences(
                DictionaryService.class.getName(),
//			        "tutorial.example2.service.DictionaryService",
			        "(Language=*)");
			if (mRefs != null && mRefs.length > 0){
				mCheck.setEnabled(true);
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
