package org.bbs.apklauncher.emb;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.res.Resources;
import android.content.res.Resources.Theme;

@SuppressLint("NewApi")
public class Target_Application extends Application
{
	private static final String TAG = Target_Application.class.getSimpleName();
	
	@Override
	public Theme getTheme() {
		return getBaseContext().getTheme();
	}

	public Resources getResources() {
		return getBaseContext().getResources();
	}	
}
