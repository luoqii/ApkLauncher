package org.bbs.apklauncher.uiautomator;

import java.util.ArrayList;
import java.util.List;

import com.android.uiautomator.core.UiCollection;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

public class ApiDemo_UiTest extends BaseUiAutomatorTestCase {
    private static final String TAG = ApiDemo_UiTest.class.getSimpleName();
    private UiObject mTmpUiObject;
    
    private List<List<UiNode>> mUiTree = new ArrayList<List<UiNode>>();
    
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
		logD(TAG, "testApiDemo_smoking. ");
		
		int i = 0;
		while ( i < 1000) {
			int step = gotoReallApp();
			pressBack(step);
			i+=1;
		}
	}	
	
	public void _testApiDemo_tranverse() throws UiObjectNotFoundException{
		logD(TAG, "testApiDemo_tranverse. ");
		
		UiNode top = new UiNode("TOP");
		gatherAllUiNode(top);
		tranverse(top);
	}
	
	void tranverse(UiNode uiTree) throws UiObjectNotFoundException{
		uiTree.finished = true;
		for (UiNode n : uiTree.children) {
			if (!n.finished){
				uiTree.finished = false;
				break;
			}
		}
		if (uiTree.finished){
			return;
		} else {
			for (UiNode n : uiTree.children) {
				
				UiObject o = new UiObject(new UiSelector().className(A_TEXTVIEW).text("API Demos"));
				waitForIdle();
				if (!o.exists()){
					uiTree.finished = true;
				}
				if (!n.finished){
					tranverse(n);
				}
			}
		}
	}

	private void gatherAllUiNode(UiNode uiTree)
			throws UiObjectNotFoundException {
		UiSelector s = new UiSelector().className(A_LISTVIEW);
		UiCollection c = new UiCollection(s);
		int count = c.getChildCount();
		String firstText = "";
		for (int i = 0 ; i < count ; i++) {
			UiObject o = c.getChildByInstance(new UiSelector().className(A_TEXTVIEW),i);
			String text = o.getText();
			uiTree.addChild(text);
			
			if (i==0) {
				firstText = text;
			}
		}
		
		String temp = "";
		do {
			UiScrollable scrollable = new UiScrollable(s);
			scrollable.scrollForward(1);

			for (int i = 0 ; i < count ; i++) {
				UiObject o = c.getChildByInstance(new UiSelector().className(A_TEXTVIEW),i);
				String text = o.getText();
				uiTree.addChild(text);
				temp = text;
			}
		} while (! firstText.equalsIgnoreCase(temp));
	}
	
	int gotoReallApp() throws UiObjectNotFoundException{
		logD(TAG, "gotoReallApp. ");
		
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
		
//		logD(TAG, "step: " + step);
		return step;
	}

	private int nextApp(int step, int count) {
		int next = (int) (Math.random() * 100 % count);
		
		logD(TAG, "nextApp: " + next + " step: " + step + " count: " + count);
		return next;
	}
	
	static class UiNode {
		public UiNode parent;
		public List<UiNode> children = new ArrayList<UiNode>();
		public boolean hasMoreChild;
		public String label;
		public boolean finished;
		
		public UiNode(String label){
			this.label = label;
		}
		
		public void addChild(String label){
			boolean found = false;
			for (UiNode c : children) {
				if (label.equalsIgnoreCase(c.label)){
					found = true;
					break;
				}
			}
			
			if (!found) {
				UiNode e = new UiNode(label);
				children.add(e);
			}
		}
	}
}
