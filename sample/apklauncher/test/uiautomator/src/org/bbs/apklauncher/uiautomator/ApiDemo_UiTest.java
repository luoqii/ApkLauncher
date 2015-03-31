package org.bbs.apklauncher.uiautomator;

import com.android.uiautomator.core.UiCollection;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

public class ApiDemo_UiTest extends BaseUiAutomatorTestCase {
    private static final String TAG = ApiDemo_UiTest.class.getSimpleName();
    private UiObject mTmpUiObject;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        enter2Home();
        enter2App();
    }

	private void enter2Home() {
		UiObject o = new UiObject(new UiSelector().className(A_TEXTVIEW).text("AkpLauncher"));
		
		int retry = 5;
		while (!o.exists() && retry > 0) {
			pressBack();
			o = new UiObject(new UiSelector().className(A_TEXTVIEW).text("AkpLauncher"));
			
			retry = retry - 1;
		}
		
		assertUiExist(o);
	}

	private void enter2App() {
		UiObject o = new UiObject(new UiSelector().className(A_TEXTVIEW).text("AkpLauncher"));
		waitForIdle();
		assertUiExist(o);
		if (o.exists()){
			o = new UiObject(new UiSelector().className(A_TEXTVIEW).textContains("com.example.android.apis.ApiDemo"));
			try {
				o.click();
			} catch (UiObjectNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			waitForIdle();
		}
	}
	
	public void testApiDemo_smoking() throws UiObjectNotFoundException{
		logD(TAG, "testApiDemo. ");
		
		int i = 0;
		while ( i < 1000) {
			int step = gotoReallApp();
			pressBack(step);
			i+=1;
		}
	}
	
	int gotoReallApp() throws UiObjectNotFoundException{
		int step = 0;
		UiObject o = new UiObject(new UiSelector().className(A_TEXTVIEW).text("API Demos"));
		waitForIdle();
		while (o.exists()) {
			assertUiExist(o);

			UiCollection c = new UiCollection(new UiSelector().className(A_LISTVIEW)
					//				.fromParent(new UiSelector().className(A_LISTVIEW))
					);
			int count = c.getChildCount();
			logD(TAG, "child count: " + count);

			o = c.getChildByInstance(new UiSelector().className(A_TEXTVIEW), nextApp(step, count));
			o.clickAndWaitForNewWindow();
			
			step = step +1;
		}
		
		logD(TAG, "step: " + step);
		return step;
	}

	private int nextApp(int step, int count) {
		int next = (int) (Math.random() * 100 % count);
		
		logD(TAG, "nextApp: " + next + " step: " + step + " count: " + count);
		return next;
	}

}
