package org.bbs.apklauncher.emb;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class ViewCreater {
	private static final String TAG = ViewCreater.class.getSimpleName();
	// phonelayoutinfaltor
	private static final String[] sClassPrefixList = { 
			"android.view.",
			"android.widget.",
			"android.webkit.", 
			"android.app." };

	public static View onCreateView(String name, Context context,
			AttributeSet attrs, ClassLoader classLoader, final Object object
//			.final Class clazz
			) {

		View v = null;
		if (name.contains(".")) {
			v = createView(name, context, attrs, classLoader);
		} else {
			for (String prefix : sClassPrefixList) {
				v = createView(prefix + name, context, attrs, classLoader);
				if (null != v) {
					break;
				}
			}
		}

		if (null != v) {
			int attributeCount = attrs.getAttributeCount();
			for (int i = 0; i < attributeCount; i++) {
				String attName = attrs.getAttributeName(i);
//				Log.d(TAG, "attName: " + attName);
				if ("onClick".equals(attName)) {
					final String handlerName = attrs.getAttributeValue(i);
					if (!TextUtils.isEmpty(handlerName)) {
						Log.d(TAG, "re-adjust onClick listener. handlerName: " + handlerName);
						v.setOnClickListener(new OnClickListener() {
							private Method mHandler;

							@Override
							public void onClick(View v) {
								if (mHandler == null) {
									try {
										mHandler = object
												.getClass()
												.getMethod(handlerName,
														View.class);
									} catch (NoSuchMethodException e) {
										int id = v.getId();
										String idText = id == View.NO_ID ? ""
												: " with id '"
														+ v.getContext()
																.getResources()
																.getResourceEntryName(
																		id)
														+ "'";
										throw new IllegalStateException(
												"Could not find a method "
														+ handlerName
														+ "(View) in the activity "
														+ object
														+ " for onClick handler"
														+ " on view "
														+ v.getClass() + idText,
												e);
									}
								}

								try {
									mHandler.invoke(object, v);
								} catch (IllegalAccessException e) {
									throw new IllegalStateException(
											"Could not execute non "
													+ "public method of the activity",
											e);
								} catch (InvocationTargetException e) {
									throw new IllegalStateException(
											"Could not execute "
													+ "method of the activity",
											e);
								}
							}
						});
					}
				}
			}
		}

//		Log.d(TAG, "onCreateView(). name: " + name + " v: " + v);
		return v;
	}

	public static View createView(String className, Context context,
			AttributeSet attrs, ClassLoader classLoader) {
		try {
			Class clazz = classLoader.loadClass(className);
			Constructor construtor = clazz.getConstructor(new Class[] {
					Context.class, AttributeSet.class });
			return (View) construtor
					.newInstance(new Object[] { context, attrs });
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return null;
	}

}
