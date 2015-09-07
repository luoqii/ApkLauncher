package com.example.apklauncher_app_osgi_felix_t_3;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import tutorial.example2.service.DictionaryService;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.apklauncher_osgi_felix_t_3.R;

public class DictionaryActivity extends BaseDictionaryActivity {
	
	private ServiceReference<?>[] mRefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CharSequence word = ((TextView)findViewById(R.id.word)).getText();
				
				for (ServiceReference<?> s : mRefs){
					DictionaryService d = (DictionaryService) m_context.getService(s);
					checkWord(d);
				}

			}
		});
		
		populateUi();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
//		populateUi();
	}
	
	protected void populateUi(){
		if (true){
//			return;
		}

		if (null == m_context){
			return;
		}
		
//		mBundleContext = FrameworkHelper.getInstance(null)
//				.getFramework()
//				.getBundleContext();
        try {
			mRefs = m_context.getServiceReferences(
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
