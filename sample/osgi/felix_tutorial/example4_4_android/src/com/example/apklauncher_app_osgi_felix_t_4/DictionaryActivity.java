package com.example.apklauncher_app_osgi_felix_t_4;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import tutorial.example2.service.DictionaryService;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.apklauncher_app_osgi_felix_t_3.BaseDictionaryActivity;
import com.example.apklauncher_osgi_felix_t_4.R;

public class DictionaryActivity extends BaseDictionaryActivity implements ServiceListener {
	
		private ServiceReference m_ref;
		private DictionaryService m_dictionary;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(com.example.apklauncher_app_osgi_felix_t_4.R.layout.fixed_id_layout);
		}

		@Override
		protected void onResume() {
		super.onResume();
		
		mCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkWord();
			}
		});
		
		//http://felix.apache.org/documentation/tutorials-examples-and-presentations/apache-felix-osgi-tutorial/apache-felix-tutorial-example-4.html
	      // We synchronize while registering the service listener and
        // performing our initial dictionary service lookup since we
        // don't want to receive service events when looking up the
        // dictionary service, if one exists.
		synchronized (this)
		{

			try {
				// Listen for events pertaining to dictionary services.
				m_context.addServiceListener(this,
						"(&(objectClass=" + DictionaryService.class.getName() + ")" +
						"(Language=*))");
				
				// Query for any service references matching any language.
	            ServiceReference[] refs = m_context.getServiceReferences(
	                DictionaryService.class.getName(), "(Language=*)");

	            // If we found any dictionary services, then just get
	            // a reference to the first one so we can use it.
	            if (refs != null)
	            {
	                m_ref = refs[0];
	                m_dictionary = (DictionaryService) m_context.getService(m_ref);
	            }
	            
	            if (null != m_dictionary){
	            	mCheck.setEnabled(true);
	            }
			} catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void checkWord() {
		if (null != m_dictionary){
			checkWord(m_dictionary);
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		   String[] objectClass =
		            (String[]) event.getServiceReference().getProperty("objectClass");

		        // If a dictionary service was registered, see if we
		        // need one. If so, get a reference to it.
		        if (event.getType() == ServiceEvent.REGISTERED)
		        {
		            if (m_ref == null)
		            {
		                // Get a reference to the service object.
		                m_ref = event.getServiceReference();
		                m_dictionary = (DictionaryService) m_context.getService(m_ref);
		            }
		        }
		        // If a dictionary service was unregistered, see if it
		        // was the one we were using. If so, unget the service
		        // and try to query to get another one.
		        else if (event.getType() == ServiceEvent.UNREGISTERING)
		        {
		            if (event.getServiceReference() == m_ref)
		            {
		                // Unget service object and null references.
		                m_context.ungetService(m_ref);
		                m_ref = null;
		                m_dictionary = null;

		                // Query to see if we can get another service.
		                ServiceReference[] refs = null;
		                try
		                {
		                    refs = m_context.getServiceReferences(
		                        DictionaryService.class.getName(), "(Language=*)");
		                }
		                catch (InvalidSyntaxException ex)
		                {
		                    // This will never happen.
		                }
		                if (refs != null)
		                {
		                    // Get a reference to the first service object.
		                    m_ref = refs[0];
		                    m_dictionary = (DictionaryService) m_context.getService(m_ref);
		                }
		            }
		        }
		
	}
	
}
