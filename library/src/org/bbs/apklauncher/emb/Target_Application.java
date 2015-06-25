package org.bbs.apklauncher.emb;

import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ViewCreater;
import org.bbs.apklauncher.emb.IntentHelper.PersistentObject;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;

@SuppressLint("NewApi")
public class Target_Application extends Application
{
	private static final String TAG = Target_Application.class.getSimpleName();
	private LayoutInflater mInflater;
	private ClassLoader mTargetClassLoader;
	private ClassLoader mHostClassLoader;
	
	@Override
	public void onCreate() {
		super.onCreate();

		PersistentObject.getsInstance().init(this, getClassLoader());
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		mHostClassLoader = AndroidUtil.getContextImpl(this).getClassLoader();
	}
	
	@Override
	public Context getApplicationContext() {
		return getBaseContext().getApplicationContext();
	}
	
	@Override
	public Theme getTheme() {
		return getBaseContext().getTheme();
	}

	public Resources getResources() {
		return getBaseContext().getResources();
	}	
	
	void attachTargetClassLoader(ClassLoader classLoader){
		mTargetClassLoader = classLoader;
	}
	
	// can merge with TargetContext ??? 
	@Override 
	public Object getSystemService(String name) {
		// adjust layout inflater
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                mInflater = ((LayoutInflater) getBaseContext().getSystemService(name)).cloneInContext(this);
                mInflater.setFactory(new Factory() {
					
					@Override
					public View onCreateView(String name, Context context, AttributeSet attrs) {
						return ViewCreater.onCreateView(name, context, attrs, 
								mHostClassLoader,
								mTargetClassLoader,
								this);
					}
				});
            }
            return mInflater;
        }
        
        return getBaseContext().getSystemService(name);
    }
}
