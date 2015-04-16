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
        
        registerWatcher(WATCHER_DISMISS_NPE, sWatcherDismissNpe);
        registerWatcher(WATCHER_WAIT_ANR, sWatchWaitAnr);
        
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
		//[TOP]->[App]->[Action Bar]->[Action Provider]
		ignoreLables.add("Action Bar Usage");
		//[TOP]->[App]->[Activity]
		ignoreLables.add("Custom Title");
		//[TOP]->[App]->[Activity]
		ignoreLables.add("Persistent State");
		//[TOP]->[App]->[Alarm]
		ignoreLables.add("Alarm Service");
		ignoreLables.add("Voice Recognition");
		//[TOP]->[Graphics]
		ignoreLables.add("BitmapPixels");
		//[TOP]->[Media]
		ignoreLables.add("AudioFx");
		ignoreLables.add("VideoView");
		//[TOP]->[NFC]
		ignoreLables.add("ForegroundDispatch");
		//[TOP]->[OS]
		ignoreLables.add("SMS Messaging");
		ignoreLables.add("KeyStore");
		
		List<String> goPath = new ArrayList<String>();
		goPath.add("App");
		goPath.add("Service");
		gotoNode(goPath);
		int skip = 0;
		
		UiNode top = new UiNode("TOP");
		gatherAllUiNode(top, true);
		
		for (int i = 0; i < skip ; i++) {
			UiNode c = top.children.get(i);
			c.finished = true;
			
			logD(TAG, "ignore n: " + c);
		}
		
		tranverse(top, 1, ignoreLables);
	}
	
	void gotoNode(List<String> nodePath) {
		logD(TAG, "gotoNode: " + toPathStringS(nodePath));
		
		TestCondition condition = new PathCondition(nodePath.size());
		Runnable action = new PathAction(nodePath);
		whileDoAction(condition, action);;
	}
	
	void tranverse(UiNode uiTree, int deep, List<String> ignoreLables) throws UiObjectNotFoundException{
		logD(TAG, makePrefix(deep) + "tranverse. deep: " + deep + " uiTree: " + uiTree);
		String path = "[" + uiTree.label + "]";
		List<UiNode> nodePath = new ArrayList<ApiDemo_UiTest.UiNode>();
		UiNode p = uiTree.parent;
		nodePath.add(uiTree);
		while (p != null) {
			path = path + "->" + "[" + p.label + "]";
			nodePath.add(p);
			p = p.parent;
		}
		path = "";
		path = toPathString(nodePath);
		logD(TAG, makePrefix(deep) + "tranverse. path: " + path);
		
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
			int i = 0;
			for (UiNode n : uiTree.children) {
				i++;
				waitForIdle();
				UiObject o = new UiObject(new UiSelector().description("Xapidemo"));
				if (!o.exists()){
					uiTree.finished = true;
				} else 	if (!n.finished){
					if (shouldIgnore(ignoreLables, n.label)) {
						n.finished = true;
						String label = n.label;
						label = makePrefix(deep) + " X tranverse. ingore this node. label: " + label;
						logD(TAG, label);
						continue;
					} else {
						o = findNode(n);
					    waitForExists(o);
						String label = o.getText();
//						label = String.format(makePrefix(deep) + " %1$" + i + " tranverse. label: " + label, i);
						label = makePrefix(deep) + " " + i + " tranverse. label: " + label;
						logD(TAG, label);
						o.clickAndWaitForNewWindow();
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

			logD(TAG, makePrefix(deep) + " @ finish. n: " + uiTree);
			uiTree.finished = true;
			pressBack();
		}
	}

	private String toPathString(List<UiNode> nodePath) {
		String path = "";
		int S = nodePath.size();
		for (int i = S -1 ; i >= 0 ; i--) {
			path = path + "[" + nodePath.get(i).label + "]->";
		}
		path = path.replaceAll("->$", "");
		return path;
	}
	
	private String toPathStringS(List<String> nodePath) {
		String path = "";
		int S = nodePath.size();
		for (int i = 0 ; i < S ; i++) {
			path = path + "[" + nodePath.get(i) + "]->";
		}
		path = path.replaceAll("->$", "");
		return path;
	}
	
	String makePrefix(int deep){
		String prefix = "";
		for (int i = 0 ; i < deep ; i++) {
			prefix += "  ";
		}
		prefix = prefix.length() / 2 + prefix;
		return prefix;
	}

	private boolean shouldIgnore(List<String> ignoreLables, String text) {
//		logD(TAG, "text: " + text);
		return ignoreLables.contains(text);
	}

	private UiObject findNode(final UiNode n) throws UiObjectNotFoundException {
//		logD(TAG, "findNodeAndClick, n: " + n);

		NodeAction a = new NodeAction(n);
		tranverseCurrentUi(a);
		
		return a.obj;
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
		UiScrollable scrollable = new UiScrollable(selector);
		scrollable.setAsVerticalList();
		scrollable.scrollToBeginning(10);
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
		boolean scrollToEnd = false;
		do {
			scrollable = new UiScrollable(selector);
			scrollToEnd = !scrollable.scrollForward();
			
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
		} while (! firstText.equalsIgnoreCase(tempFirstText) && !scrollToEnd);
		
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
	
	class NodeAction implements UiNodeAction {
		
		public UiNode n;
		public UiObject obj;
		public NodeAction(UiNode n){
			this.n = n;
		}
		@Override
		public boolean handle(UiObject o) {
			String label;
			try {
				label = o.getText();
//				logD(TAG, "findNodeAndClick, label: " + label);
				if (n.label.equalsIgnoreCase(label)){
//					logD(TAG, "clickAndWaitForNewWindow. n: " + o.getText());
//					o.clickAndWaitForNewWindow();
					obj = o;
					return true;
				}
			} catch (UiObjectNotFoundException e) {
				e.printStackTrace();
			}
			return false;
		}
		
	}
	
	class PathCondition extends BaseCondition<Integer> {

		private int run;

		public PathCondition(int size) {
			super(size);
			
			run = 0;
		}

		@Override
		public boolean evalute() {

			boolean going = doEvalute();
			logD(TAG, "going: " + going);
			return going;
		}
		
		public boolean doEvalute() {
			
			UiObject o = new UiObject(new UiSelector().description("Xapidemo"));
			waitForExists(o);
			if ( o.exists() && run < get()) {
				run++;
				return true;
			} else {
				run++;
				return false;
			}
			
		}
		
	}

	class PathAction extends BaseAction<List<String>>{
	
		private int run;
	
		public PathAction(List<String> t) {
			super(t);
			
			run = 0;
		}
		
		@Override
		public void run() {
			logD(TAG, "run: " + run);
			if (get().size() > run){
				UiNode n = new UiNode(get().get(run));
				try {
					UiObject node = findNode(n);
					waitForExists(node);
					logD(TAG, "click n: " + node);
					node.clickAndWaitForNewWindow();
				} catch (UiObjectNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			run++;
		}
		
	}
}
