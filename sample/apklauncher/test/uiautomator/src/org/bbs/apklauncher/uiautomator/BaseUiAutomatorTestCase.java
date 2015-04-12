package org.bbs.apklauncher.uiautomator;

import android.os.RemoteException;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class BaseUiAutomatorTestCase extends UiAutomatorTestCase {
    public static final String A_BUTTON = "android.widget.Button";
    public static final String A_IMAGE_BUTTON = "android.widget.ImageButton";
    public static final String A_RELATIVE_LAYOUT = "android.widget.RelativeLayout";
    public static final String A_TEXTVIEW = "android.widget.TextView";
    public static final String A_LISTVIEW = "android.widget.ListView";
    
    protected UiDevice mDevice;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        mDevice = UiDevice.getInstance();
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
    
    public void waitForIdle() {
        mDevice.waitForIdle();
    }  
    
    public void safeWaitForIdle(){
    	waitForIdle(3 * 1000);
    }
    
    public void waitForIdle(long timeout) {
        mDevice.waitForIdle(timeout);
    }
    
    public void wakeUp() throws RemoteException {
	    mDevice.wakeUp();
	}
    
    public boolean waitForWindowUpdate(String packageName, long timeout){
    	return mDevice.waitForWindowUpdate(packageName, timeout);
    }

    // assert
	protected void assertUiExist(UiObject ui) {
        assertTrue("no such ui: " + ui.getSelector(), ui.exists());
    }
    
    protected void assertUiNotExist(UiObject ui) {
        assertTrue("have ui: " + ui.getSelector(), !ui.exists());
    }

    // log
    protected void logD(String tag, String string) {
        System.out.println(tag + ": " + string); 
    }  
    protected void logE(String tag, String string) {
        System.err.println(tag + ": " + string); 
    }
	
}
