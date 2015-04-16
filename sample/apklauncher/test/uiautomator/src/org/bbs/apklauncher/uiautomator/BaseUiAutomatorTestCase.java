package org.bbs.apklauncher.uiautomator;

import java.io.File;

import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiWatcher;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;


/**
 * 
 * NOTE: you should always wiat for idle for some code working correctly.
 * @author bysong
 *
 */
public class BaseUiAutomatorTestCase extends UiAutomatorTestCase {
	public static final int IDLE_TIMEOUT = 7 * 1000;

	private static final String TAG = BaseUiAutomatorTestCase.class.getSimpleName();
	
    public static final String A_BUTTON = Button.class.getName();
    public static final String A_IMAGE_BUTTON = ImageButton.class.getName();
    public static final String A_TEXTVIEW = TextView.class.getName();
    public static final String A_RELATIVE_LAYOUT = RelativeLayout.class.getName();
    public static final String A_LISTVIEW = ListView.class.getName();
    public static final String A_LENEARLAYOUT = LinearLayout.class.getName();

	protected static final String WATCHER_DISMISS_NPE = "WATCHER_DISMISS_NPE";
	protected static final String WATCHER_WAIT_ANR = "WATCHER_WAIT_ANR";
	protected static final boolean DEBUG_WATCHER = false;
    
    protected UiWatcher sWatcherDismissNpe = new UiWatcher() {
			
			@Override
			public boolean checkForCondition() {
				if (DEBUG_WATCHER) {
					logD(TAG, WATCHER_DISMISS_NPE + " checkForCondition");
				}
				UiObject o = new UiObject(new UiSelector().textContains("已停止运行"));
				if (o.exists()){
					try {
						o = new UiObject(new UiSelector().textContains("确定"));
						if (o.exists()) {
							o.click();
						}
					} catch (UiObjectNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return false;
			}
		};

	protected UiWatcher sWatchWaitAnr = new UiWatcher() {
			
			@Override
			public boolean checkForCondition() {
				if (DEBUG_WATCHER) {
					logD(TAG, WATCHER_WAIT_ANR + " checkForCondition");
				}
				UiObject o = new UiObject(new UiSelector().textContains("无响应"));
				if (o.exists()){
					try {
						o = new UiObject(new UiSelector().textContains("等待"));
						if (o.exists()) {
							o.click();
						}
					} catch (UiObjectNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return false;
			}
		};
    
    protected UiDevice mDevice;
	protected UiObject tmpUiObject;
	protected UiObject uiOject;
	protected Configurator mConf;

	@Override
    protected void setUp() throws Exception {
        super.setUp();
        
        mDevice = UiDevice.getInstance();
        mConf = Configurator.getInstance();
        // use space not tab.
        logD(TAG, "ActionAcknowledgmentTimeout  : " + mConf.getActionAcknowledgmentTimeout());
        logD(TAG, "KeyInjectionDelay            : " + mConf.getKeyInjectionDelay());
        logD(TAG, "ScrollAcknowledgmentTimeout  : " + mConf.getScrollAcknowledgmentTimeout());
        logD(TAG, "WaitForIdleTimeout           : " + mConf.getWaitForIdleTimeout());
        logD(TAG, "WaitForSelectorTimeout       : " + mConf.getWaitForSelectorTimeout());
    }
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
    
    public void pressBack() {
        mDevice.pressBack();
        safeWaitForIdle();
    }   
    
    public void pressBack(int limit) {
        for (int i = 0 ; i < limit; i++) {
            mDevice.pressBack();
            safeWaitForIdle();
        }
    }
    
    public void pressDPadLeft() {
        mDevice.pressDPadLeft();
        safeWaitForIdle();
    }
    
    public void pressDPadRight() {
        mDevice.pressDPadRight();
        safeWaitForIdle();
    }
    
    public void pressDPadUp() {
        mDevice.pressDPadUp();
        safeWaitForIdle();
    }
    
    public void pressDPadDown() {
        mDevice.pressDPadDown();
        safeWaitForIdle();
    }
    
    public void pressDPadDown(int limit) {
        for (int i = 0 ; i < limit; i++) {
            mDevice.pressDPadDown();
            safeWaitForIdle();
        }
    }
    
    public void pressDPadUp(int limit) {
        for (int i = 0 ; i < limit; i++) {
            mDevice.pressDPadUp();
            safeWaitForIdle();
        }
    }
    
    public void pressDPadLeft(int limit) {
        for (int i = 0 ; i < limit; i++) {
            mDevice.pressDPadLeft();
            safeWaitForIdle();
        }
    }
    
    public void pressDPadRight(int limit) {
        for (int i = 0 ; i < limit; i++) {
            mDevice.pressDPadRight();
            safeWaitForIdle();
        }
    }
    
    public void pressDPadCenter(int limit) {
        for (int i = 0 ; i < limit; i++) {
            mDevice.pressDPadCenter();
            safeWaitForIdle();
        }
    }
    
    public void pressDPadCenter() {
        mDevice.pressDPadCenter();
        waitForIdle();
    }
    
    public void dpad(String key){
    	if (!TextUtils.isEmpty(key)){
        	key = key.toLowerCase();
        	final int L = key.length();
        	for (int i = 0 ; i < L ; i++) {
        		char c = key.charAt(i);
        		switch (c) {
        		case 'd' :
        			pressDPadDown();
        			break;
        		case 'u' :
        			pressDPadUp();
        			break;
        		case 'l' :
        			pressDPadLeft();
        			break;
        		case 'r' :
        			pressDPadRight();
        			break;
        		case 'c' :
        			pressDPadCenter();
        			break;
        			default:
        				logE(TAG, "unknown key: " + key);
        		}
        	}
    	} else {
    		logE(TAG, "input dpad key is empty");
    	}
    }
    
    public void waitForIdle() {
        mDevice.waitForIdle();
    }  
    
    public void safeWaitForIdle(){
    	waitForIdle(IDLE_TIMEOUT);
    }
    
    public void waitForIdle(long timeout) {
        mDevice.waitForIdle(timeout);
    }
    
    public void wakeUp() throws RemoteException {
	    mDevice.wakeUp();
	}
    public void registerWatcher (String name, UiWatcher watcher){
    	mDevice.registerWatcher(name, watcher);
    }
    public void removeWatcher (String name){
    	mDevice.removeWatcher(name);
    }
    public void resetWatcherTriggers (){
    	mDevice.resetWatcherTriggers();
    }
    public void runWatchers (){
    	mDevice.runWatchers();
    }
    
    public boolean waitForWindowUpdate(String packageName, long timeout){
    	return mDevice.waitForWindowUpdate(packageName, timeout);
    }
    
    public boolean takeScreenshot (File storePath) {
    	return mDevice.takeScreenshot(storePath);
    }
    
    public boolean takeScreenshot (File storePath, float scale, int quality) {
    	return mDevice.takeScreenshot(storePath, scale, quality);
    }
    
    ////////////////// assert ////////////////////////////
	protected void assertUiExist(UiObject ui) {
        assertTrue("no such ui: " + ui.getSelector(), ui.exists());
    }
    
    protected void assertUiNotExist(UiObject ui) {
        assertTrue("have ui: " + ui.getSelector(), !ui.exists());
    }

    protected void assertmpty(String text) {
    	assertTrue("text is not empty. text: " + text, TextUtils.isEmpty(text));
	}

    protected void assertNotEmpty(String text) {
    	assertTrue("text is empty. text: " + text, !TextUtils.isEmpty(text));
	}

    //////////////////////// log //////////////////////////
    protected void logD(String tag, String string) {
        System.out.println(tag + ": " + string); 
    }  
    protected void logE(String tag, String string) {
        System.err.println(tag + ": " + string); 
    }

	protected void waitForExists(UiObject videoAnchor) {
		videoAnchor.waitForExists(IDLE_TIMEOUT);
	}
	
	//////////////////////// util ////////////////////////
	public static void doActionUntil(Runnable action, TestCondition condition){
		doActionUntil(action, condition, 7);
	}
	public static void doActionUntil(Runnable action, TestCondition condition, int maxRetry){
		do {
			action.run();
		} while (condition.evalute());
	}
	public static void doActionUntil(WhileBlock whileBlock){
		do {
			whileBlock.run();
		} while (whileBlock.evalute());
	}	
	public static void whileDoAction(TestCondition condition, Runnable action){
		whileDoAction(condition, action, 7);
	}
	public static void whileDoAction(TestCondition condition, Runnable action, int maxRetry){
		while (condition.evalute()){
			action.run();
		}
	}
	public static void whileDoAction(WhileBlock whileBlock){
		while (whileBlock.evalute()){
			whileBlock.run();
		}
	}
	public static interface TestCondition {
		
		public boolean evalute();
	}
	public static interface WhileBlock extends TestCondition, Runnable {
		
	}
	public abstract class BaseAction<T> implements Runnable {

		private T obj;
		
		public BaseAction(T t) {
			this.obj = t;
		}
		
		public T get() {
			return obj;
		}
		
	}
	public abstract class BaseCondition<T> implements TestCondition {

		private T obj;
		
		public BaseCondition(T t) {
			this.obj = t;
		}
		
		public T get() {
			return obj;
		}
		
	}
	
	
}