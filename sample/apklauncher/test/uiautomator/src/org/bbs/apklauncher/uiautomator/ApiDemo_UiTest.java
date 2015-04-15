package org.bbs.apklauncher.uiautomator;

import java.util.ArrayList;
import java.util.List;

import com.android.uiautomator.core.UiCollection;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

public class ApiDemo_UiTest extends BaseUiAutomatorTestCase {
    private static final String TEXT_API_DEMOS = "API Demos";
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
	
	public void testApiDemo_tranverse() throws UiObjectNotFoundException{
		logD(TAG, "testApiDemo_tranverse. ");
		
		List<String> ignoreLables = new ArrayList<String>();
		ignoreLables.add("Action Bar Usage");
		UiNode top = new UiNode("TOP");
		gatherAllUiNode(top, true);
		tranverse(top, 1, ignoreLables);
	}
	
	void tranverse(UiNode uiTree, int deep, List<String> ignoreLables) throws UiObjectNotFoundException{
		logD(TAG, "tranverse. uiTree: " + uiTree + " deep: " + deep);
		String path = uiTree.label;
		UiNode p = uiTree.parent;
		while (p != null) {
			path = path + "->" + p.label;
			p = p.parent;
		}
		logD(TAG, "path: " + path);
		
		uiTree.finished = true;
		for (UiNode n : uiTree.children) {
			if (!n.finished){
				uiTree.finished = false;
				break;
			}
		}
		
		if (uiTree.finished){
			pressBack();
			return;
		} else {
			for (UiNode n : uiTree.children) {
				
				waitForIdle();
				UiObject o = new UiObject(new UiSelector().description("Xapidemo"));
				if (!o.exists()){
					uiTree.finished = true;
				} else 	if (!n.finished){
					if (shouldIgnore(ignoreLables, n.label)) {
						n.finished = true;
						continue;
					} else {
						findNodeAndClick(n);
						o = new UiObject(new UiSelector().description("Xapidemo"));
						UiObject listO = new UiObject(new UiSelector().className(A_LISTVIEW));
						if (!o.exists() ) {
							n.finished = true;
							pressBack();
						} else {
							gatherAllUiNode(n, true);
							tranverse(n, deep + 1, ignoreLables);
						}
					}
				}
			}
			
			uiTree.finished = true;
			pressBack();
		}
	}

	private boolean shouldIgnore(List<String> ignoreLables, String text) {
//		logD(TAG, "text: " + text);
		return ignoreLables.contains(text);
	}

	private void findNodeAndClick(final UiNode n) throws UiObjectNotFoundException {
//		logD(TAG, "findNodeAndClick, n: " + n);
		String label = n.label;

		UiNodeAction a = new UiNodeAction(){

			@Override
			public boolean handle(UiObject o) {
				String label;
				try {
					label = o.getText();
//					logD(TAG, "findNodeAndClick, label: " + label);
					if (n.label.equalsIgnoreCase(label)){
						logD(TAG, "clickAndWaitForNewWindow. n: " + o.getText());
						o.clickAndWaitForNewWindow();
						return true;
					}
				} catch (UiObjectNotFoundException e) {
					e.printStackTrace();
				}
				return false;
			}
		};

		tranverseCurrentUi(a);
	}

	private void gatherAllUiNode(final UiNode uiTree, boolean scrollToHeadAfter)
			throws UiObjectNotFoundException {
//		logD(TAG, "gatherAllUiNode, scrollToHeadAfter: " + scrollToHeadAfter + " uiTree: " + uiTree);
		
		UiNodeAction a = new UiNodeAction(){

			@Override
			public boolean handle(UiObject o) {
				String label;
				try {
					label = o.getText();
					uiTree.addChild(label);
				} catch (UiObjectNotFoundException e) {
					e.printStackTrace();
				}
				return false;
			}
		};
		
		tranverseCurrentUi(a);
	}
	
	int gotoReallApp() throws UiObjectNotFoundException{
		logD(TAG, "gotoReallApp. ");
		
		int step = 0;
		UiObject o = new UiObject(new UiSelector().className(A_TEXTVIEW).text(TEXT_API_DEMOS));
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
	
	private void tranverseCurrentUi(UiNodeAction action)
			throws UiObjectNotFoundException {
//		logD(TAG, "tranverseCurrentUi. action: " + action);
		UiSelector selector = new UiSelector().className(A_LISTVIEW);
		UiCollection collection = new UiCollection(selector);
		int count = collection.getChildCount();
		String firstText = "";
		String lastText = "";
//		logD(TAG, "tranverseCurrentUi. count : " + count);
		boolean handled = true;
		for (int i = 0 ; i < count ; i++) {
			UiObject o = collection.getChildByInstance(new UiSelector().className(A_TEXTVIEW),i);
			String text = o.getText();
			handled = action.handle(o);
//			logD(TAG, "tranverseCurrentUi. handled : " + handled + " o: " + o);
			if (handled) {
				return;
			}
			
			if (i==0) {
				firstText = text;
			}
			
			lastText = text;
		}
	
//		logD(TAG, "tranverseCurrentUi. firstText : " + firstText);
		String tempFirstText = "";
		int maxTry = 1;
		do {
			UiScrollable scrollable = new UiScrollable(selector);
			scrollable.setAsVerticalList();
			scrollable.scrollForward();
			
			collection = new UiCollection(selector);
			count = collection.getChildCount();
//			logD(TAG, "tranverseCurrentUi. count : " + count);
			boolean startHandle = false;
			for (int i = 0 ; i < count ; i++) {
				UiObject o = collection.getChildByInstance(new UiSelector().className(A_TEXTVIEW),i);
				String text = o.getText();			
				if (startHandle) {
					handled = action.handle(o);
//					logD(TAG, "tranverseCurrentUi. handled : " + handled + " o: " + o);
					if (handled) {
						return;
					}
				} else if (lastText.equals(text)) {
					startHandle = true;
				}
				
				if(i == 0) {
					tempFirstText = text;
				}
			}
	
//			logD(TAG, "tranverseCurrentUi. tempFirstText : " + tempFirstText);
			
			maxTry--;
		} while (! firstText.equalsIgnoreCase(tempFirstText) && maxTry > 0);
		
		new UiScrollable(selector).scrollToBeginning(100);
	}

	/**
	 * @author luoqii
	 *
	 */
	interface UiNodeAction{
		
		/**
		 * @param o
		 * @return false to go on next node.
		 */
		public boolean handle(UiObject o);
	}
	
	class UiNode {
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
				e.parent = this;
//				logD(TAG, this + " add " + label + " as child");
				children.add(e);
			}
		}

		@Override
		public String toString() {
			return label + "[" + children.size() + "]";
		}
		
		
	}
}
