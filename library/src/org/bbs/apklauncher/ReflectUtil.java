package org.bbs.apklauncher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class ReflectUtil {
		private static final String TAG = ReflectUtil.class.getSimpleName();

		public static void dumpMethod(Class clazz, String methodName){
			Method[] methods = clazz.getDeclaredMethods();
			for (Method m: methods) {
				if (m.getName().startsWith(methodName)) {
					Log.d(TAG, "method: " + m);
//					Log.d(TAG, "method name: " + m.getName());
//					Log.d(TAG, "paramter: [");
//					Class<?>[] parameterTypes = m.getParameterTypes();
//					for (Class p : parameterTypes) {
//						Log.d(TAG, "" + p.getCanonicalName());
//					}
//					Log.d(TAG, "]");
				}
			}
		}
		
		public static void dumpField(Class clazz, String fieldName){
			Field[] fs = clazz.getDeclaredFields();
			for (Field f: fs) {
				if (f.getName().startsWith(fieldName)) {
					Log.d(TAG, "method name: " + f.getName());
					Log.d(TAG, "]");
				}
			}
		}

		public static void copyFields(Class clazz, String[] fields, Object host, Activity target) {
			for (String f : fields) {
				Field declaredField = null;
				try {
					declaredField = clazz.getDeclaredField(f);
					setField(target, declaredField, getFiledValue(clazz, host, f));
				} catch (Exception e) {
					throw new RuntimeException("setField(). field: " + declaredField, e);
				}
			}
		}

		public static  void setField(Object object, Field field, Object value) {
		    field.setAccessible(true);
		    try {
		        field.set(object, value);
		    } catch (Exception e) {
				throw new RuntimeException("setField(). field: " + field, e);
		    }
		}
		
		public static  Field getFiled(Class clazz, Object object, String fieldName) {
			try {
	            Field declaredField = clazz.getDeclaredField(fieldName);
	            
	            return declaredField;
			} catch (Exception e) {
				throw new RuntimeException("getFiled(). fieldName: " + fieldName, e);
			}
		}
		
		public static void copyAllFields(Class clazz, Object host, Object target) {
			while (clazz != null) {
				for (Field f : clazz.getDeclaredFields()) {
					f.setAccessible(true);
					try {
						f.set(target, f.get(host));
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				clazz = clazz.getSuperclass();
			}
	}

		public static  Object getFiledValue(Class clazz, Object object, String fieldName) {
		        Object f = null;
		        try {
		            Field declaredField = getFiled(clazz, object, fieldName);
		            declaredField.setAccessible(true);
		            f = declaredField.get(object);
		            
		            return f;
		        } catch (Exception e) {
					throw new RuntimeException("getFiledValue(). class: " + clazz + " object: " + object + " field: " + fieldName, e);
		        }
		    }
		
		public static class ApplicationUtil {		
			public static void callAttach(Application app, Context baseContext){
				try {
					Method m = Class.forName("android.app.Application").getDeclaredMethod("attach", new Class[]{Context.class});
					m.setAccessible(true);
					m.invoke(app, new Object[]{baseContext});
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			public static void copyFields(Application host, Application target) {
			    String[] fields = new String[] {
			            "mMainThread",
			            "mInstrumentation",
			            "mToken",
			            "mIdent",
			            "mApplication",
			            "mIntent",
			            "mActivityInfo",
			            "mTitle",
			            "mParent",
			            "mEmbeddedID",
			            "mLastNonConfigurationInstances",
			            "mFragments",// java.lang.IllegalStateException
			                         // FragmentManagerImpl.moveToState
			            "mWindow",
			            "mWindowManager",
			            "mCurrentConfig"
			    };
			    copyFields(Application.class, fields, host, target);
			}
		
			public static void copyFields(Class clazz, String[] fields, Application host, Application target) {
		
			    try {
			        for (String f : fields) {
			            Field declaredField = clazz.getDeclaredField(f);
			            setField(target, declaredField, getFiledValue(Class.forName("android.app.Application"), host, f));
			        }
			    } catch (NoSuchFieldException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			    } catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		/**
		 *  keep function name consistency with {@link Activity}
		 *  
		 * @author bysong
		 *
		 */
		public static class ActivityReflectUtil extends ReflectUtil {
			public static void onClick(Activity activity, View view){
				try {
					Method m = Activity.class.getDeclaredMethod("onClick", new Class[]{View.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{view});
				} catch (Exception e) {
					throw new RuntimeException("error in onCreate", e);
				}
			}
			
			public static void copyBaseContext(Activity target,
					Context base) {
			    try {
					Field f = ContextWrapper.class.getDeclaredField("mBase");
					f.setAccessible(true);
					f.set(target, base);
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			public static void copyNewResouce(Activity target, Resources source) {
			    try {
					Field f = ContextThemeWrapper.class.getDeclaredField("mResources");
					f.setAccessible(true);
					f.set(target, source);
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			public static void onCreate(Activity activity, Bundle savedInstanceState){
				try {
					Method m = Activity.class.getDeclaredMethod("onCreate", new Class[]{Bundle.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{savedInstanceState});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onCreate", e);
				}
			}
			@SuppressLint("NewApi")
			public static void onCreate(Activity activity, Bundle savedInstanceState,
					PersistableBundle persistentState){
				try {
					Method m = Activity.class.getDeclaredMethod("onCreate", new Class[]{Bundle.class, PersistableBundle.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{savedInstanceState,persistentState});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onCreate", e);
				}
			}
			
			public static void onPostCreate(Activity activity,
					Bundle savedInstanceState) {		
				try {
					Method m = Activity.class.getDeclaredMethod("onPostCreate", new Class[]{Bundle.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{savedInstanceState});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onCreate", e);
				}
			}
			@SuppressLint("NewApi")
			public static void onPostCreate(Activity activity,
					Bundle savedInstanceState, PersistableBundle persistentState) {
				try {
					Method m = Activity.class.getDeclaredMethod("onPostCreate", new Class[]{Bundle.class, PersistableBundle.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{savedInstanceState, persistentState});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onCreate", e);
				}
			}
			public static void onResume(Activity activity){
				try {
					Method m = Activity.class.getDeclaredMethod("onResume", (Class[]) null);
					m.setAccessible(true);
					m.invoke(activity, (Object[]) null);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onResume", e);
				}
			}
			
			public static void onPostResume(Activity activity){
				try {
					Method m = Activity.class.getDeclaredMethod("onPostResume", (Class[]) null);
					m.setAccessible(true);
					m.invoke(activity, (Object[]) null);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onPause", e);
				}
			}
			
			public static void onPause(Activity activity){
				try {
					Method m = Activity.class.getDeclaredMethod("onPause", (Class[]) null);
					m.setAccessible(true);
					m.invoke(activity, (Object[]) null);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onPause", e);
				}
			}
			
			public static void onRestoreInstanceState(Activity activity,
					Bundle savedInstanceState) {
				try {
					Method m = Activity.class.getDeclaredMethod("onRestoreInstanceState", new Class[]{Bundle.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[] {savedInstanceState});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onPause", e);
				}
			}			
			@SuppressLint("NewApi")
			public static void onRestoreInstanceState(Activity activity,
					Bundle savedInstanceState, PersistableBundle persistentState) {
				try {
					Method m = Activity.class.getDeclaredMethod("onRestoreInstanceState", new Class[]{Bundle.class, PersistableBundle.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[] {savedInstanceState, persistentState});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onPause", e);
				}
			}
			public static void onRestart(Activity activity) {
				try {
					Method m = Activity.class.getDeclaredMethod("onRestart", (Class[]) null);
					m.setAccessible(true);
					m.invoke(activity, (Object[]) null);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onPause", e);
				}
			}
			public static void onStart(Activity activity) {
				try {
					Method m = Activity.class.getDeclaredMethod("onStart", (Class[])null);
					m.setAccessible(true);
					m.invoke(activity, (Object[])null);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onStop", e);
				}
			}
			public static void onStop(Activity activity) {
				try {
					Method m = Activity.class.getDeclaredMethod("onStop", (Class[])null);
					m.setAccessible(true);
					m.invoke(activity, (Object[])null);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onStop", e);
				}
			}
			public static boolean isValidFragment(Activity activity, String fragmentName) {
				try {
					Method m = Activity.class.getDeclaredMethod("isValidFragment", new Class[]{String.class});
					m.setAccessible(true);
					return (Boolean) m.invoke(activity, new Object[]{fragmentName});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in isValidFragment", e);
				}
			}
			
			public static void onDestroy(Activity activity){
				try {
					Method m = Activity.class.getDeclaredMethod("onDestroy", (Class[])null);
					m.setAccessible(true);
					m.invoke(activity, (Object[])null);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onDestroy", e);
				}
			}	

			public static void onContextMenuClosed(Activity activity,
					Menu menu) {
				try {
					Method m = Activity.class.getDeclaredMethod("onContextMenuClosed", new Class[]{Menu.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{menu});
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}

			public static boolean onContextItemSelected(Activity activity,
					MenuItem item) {
				try {
					Method m = Activity.class.getDeclaredMethod("onContextItemSelected", new Class[]{MenuItem.class});
					m.setAccessible(true);
					return (Boolean) m.invoke(activity, new Object[]{item});
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				return false;
			}

			public static void onCreateContextMenu(Activity activity,
					ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				try {
					Method m = Activity.class.getDeclaredMethod("onCreateContextMenu", new Class[]{ContextMenu.class, View.class, ContextMenuInfo.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{menu, v, menuInfo});
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}

			public static void onActivityResult(Activity activity, int arg0,
					int arg1, Intent arg2) {
				try {
					Method m = Activity.class.getDeclaredMethod("onActivityResult", new Class[]{int.class, int.class, Intent.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{arg0, arg1, arg2});
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}

			public static boolean onOptionsItemSelected(Activity activity, MenuItem item) {
				try {
					Method m = Activity.class.getDeclaredMethod("onOptionsItemSelected", new Class[]{MenuItem.class});
					m.setAccessible(true);
					return (Boolean) m.invoke(activity, new Object[]{item});
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}

			public static boolean onCreateOptionsMenu(Activity activity, Menu menu) {
				try {
					Method m = Activity.class.getDeclaredMethod("onCreateOptionsMenu", new Class[]{Menu.class});
					return (Boolean) m.invoke(activity, new Object[]{menu});
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}

			public static Dialog onCreateDialog(Activity activity, int id, Bundle args) {
				try {
					Method m = Activity.class.getDeclaredMethod("onCreateDialog", new Class[]{ int.class, Bundle.class});
					m.setAccessible(true);
					return (Dialog) m.invoke(activity, new Object[]{id, args});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onCreateDialog", e);
				}
			}
			public static Dialog onCreateDialog(Activity activity, int id) {
				try {
					Method m = Activity.class.getDeclaredMethod("onCreateDialog", new Class[]{ int.class});
					m.setAccessible(true);
					return (Dialog) m.invoke(activity, new Object[]{id});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onCreateDialog", e);
				}
			}
			
			public static void onPrepareDialog(Activity activity, int id, Dialog dialog) {
				try {
					Method m = Activity.class.getDeclaredMethod("onPrepareDialog", new Class[]{ int.class, Dialog.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{id, dialog});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onPrepareDialog", e);
				}
			}

			public static void onPrepareDialog(Activity activity, int id,
					Dialog dialog, Bundle args) {
				try {
					Method m = Activity.class.getDeclaredMethod("onPrepareDialog", new Class[]{ int.class, Dialog.class, Bundle.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{id, dialog, args});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onPrepareDialog", e);
				}
				
			}

			public static void attach(Activity hostActivity,
					Activity embeddedActivity) {
				try {
//					dumpMethod(Activity.class,"attach");
					Class<Object>[] parameters = new Class[]{Context.class, Class.forName("android.app.ActivityThread"),
							Class.forName("android.app.Instrumentation"), 
							Class.forName("android.os.IBinder"), 
							// not Integer.class
							int.class, 
							Application.class,
							Intent.class, 
							Class.forName("android.content.pm.ActivityInfo"),
//							Class.forName("android.os.IBinder"),
							CharSequence.class, 
							Activity.class, 
							String.class,
							Class.forName("android.app.Activity$NonConfigurationInstances"),
							Configuration.class,
							Class.forName("com.android.internal.app.IVoiceInteractor")
							};
					Method m = Activity.class.getDeclaredMethod("attach", parameters);
					m.setAccessible(true);
					Object[] args = new Object[]{
							hostActivity.getBaseContext(),
							getFiledValue(Activity.class, hostActivity, "mMainThread"),
							getFiledValue(Activity.class, hostActivity, "mInstrumentation"),
							getFiledValue(Activity.class, hostActivity, "mToken"),
							getFiledValue(Activity.class, hostActivity, "mIdent"),	
							getFiledValue(Activity.class, hostActivity, "mApplication"),
							getFiledValue(Activity.class, hostActivity, "mIntent"),
							getFiledValue(Activity.class, hostActivity, "mActivityInfo"),
							getFiledValue(Activity.class, hostActivity, "mTitle"),
							getFiledValue(Activity.class, hostActivity, "mParent"),
							getFiledValue(Activity.class, hostActivity, "mEmbeddedID"),
							getFiledValue(Activity.class, hostActivity, "mLastNonConfigurationInstances"),
							getFiledValue(Activity.class, hostActivity, "mCurrentConfig"),
							null};
					m.invoke(embeddedActivity, args );
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			public static void attachBaseContext(Object Object,
					Context context) {
				try {
					Method m = ContextWrapper.class.getDeclaredMethod("attachBaseContext", new Class[]{Context.class});
					m.setAccessible(true);
					m.invoke(Object, new Object[]{context});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in attachBaseContext", e);
				}
				
			}

			public static void copyFields(Activity host, Activity target) {
			    String[] fields = new String[] {
			            "mMainThread",
			            "mInstrumentation",
			            "mToken",
			            "mIdent",
			            "mApplication",
			            "mIntent",
			            "mActivityInfo",
			            "mTitle",
			            "mParent",
			            "mEmbeddedID",
			            "mLastNonConfigurationInstances",
			            "mFragments",// java.lang.IllegalStateException
			                         // FragmentManagerImpl.moveToState
			            "mWindow",
			            "mWindowManager",
			            "mCurrentConfig"
			    };
			    copyFields(Activity.class, fields, host, target);
			    
			    fields = new String[] {
			    		"mThemeResource",
			    		"mTheme",
			    		"mInflater",
			    		"mOverrideConfiguration",
			    		"mResources"
			    };
			    copyFields(ContextThemeWrapper.class, fields, host, target);
			    
			    fields = new String[] {
			    		"mBase",
			    };
//			    copyFields(ContextWrapper.class, fields, host, target);
			    
			    // bundle should user getLayoutInflator always.
			    try {
				    LayoutInflater in = LayoutInflater.from(host);
				    
					Field inflator = Class.forName("com.android.internal.policy.impl.PhoneWindow").getDeclaredField("mLayoutInflater");
					inflator.setAccessible(true);
					Object winF = getFiledValue(Activity.class, host, "mWindow");
					inflator.set(winF, in);
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}



			public static boolean onMenuItemSelected(Activity activity,
					int featureId, MenuItem menu) {
				try {
					Method m = Activity.class.getDeclaredMethod("onMenuItemSelected", new Class[]{ int.class, Menu.class});
					m.setAccessible(true);
					return (Boolean) m.invoke(activity, new Object[]{featureId, menu});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onCreateDialog", e);
				}
			}

			public static boolean onPrepareOptionsPanel(Activity activity,
					View view, Menu menu) {
				try {
					Method m = Activity.class.getDeclaredMethod("onPrepareOptionsPanel", new Class[]{ View.class, Menu.class});
					m.setAccessible(true);
					return (Boolean) m.invoke(activity, new Object[]{view, menu});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onCreateDialog", e);
				}
			}

			public static View onPreparePonCreatePanelViewanel(
					Activity activity, int featureId) {
				try {
					Method m = Activity.class.getDeclaredMethod("onPreparePonCreatePanelViewanel", new Class[]{ int.class});
					m.setAccessible(true);
					return (View) m.invoke(activity, new Object[]{featureId});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onCreateDialog", e);
				}
			}

			public static void setActivityApplication(Object activity,
					Application app) {
				try {
					Field field = Activity.class.getDeclaredField("mApplication");
					ReflectUtil.ActivityReflectUtil.setField(activity, field, app);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in setApplictivity.", e);
				}
			}
			
			public static void setServiceApplication(Service service,
					Application app) {
				try {
					Field field = Service.class.getDeclaredField("mApplication");
					ReflectUtil.ActivityReflectUtil.setField(service, field, app);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in setApplictivity.", e);
				}
			}
			
			public static void setBaseContext(Activity mTargetActivity,
					Context baseContext) {
				try {
					Field field = ContextWrapper.class.getDeclaredField("mBase");
					ReflectUtil.ActivityReflectUtil.setField(mTargetActivity, field, baseContext);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in setBaseContext.", e);
				}
			}

			public static void setResource(Activity mTargetActivity,
					ResourcesMerger mResourceMerger) {
				try {
					Field field = ContextThemeWrapper.class.getDeclaredField("mResources");
					ReflectUtil.ActivityReflectUtil.setField(mTargetActivity, field, mResourceMerger);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in setBaseContext.", e);
				}
			}
			
			public static void setWindowContext(Window window,
					Context baseContext) {
				try {
					Field field = window.getClass().getDeclaredField("Window");
					ReflectUtil.ActivityReflectUtil.setField(window, field, baseContext);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in setWindowContext.", e);
				}
			}

			public static void onTitleChanged(Activity activity,
					CharSequence title, int color) {
				try {
					Method m = Activity.class.getDeclaredMethod("onTitleChanged", new Class[]{CharSequence.class, int.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{title, color});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onTitleChanged", e);
				}
			}

			public static void onUserLeaveHint(Activity activity) {
				try {
					Method m = Activity.class.getDeclaredMethod("onUserLeaveHint", new Class[]{});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onUserLeaveHint", e);
				}
			}
			
			public static void performCreate(Activity activity, Bundle icicle) {
				try {
					Method m = Activity.class.getDeclaredMethod("performCreate", new Class[]{Bundle.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{icicle});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in performCreate", e);
				}
			}

			public static void onNewIntent(Activity activity,
					Intent intent) {
				try {
					Method m = Activity.class.getDeclaredMethod("onNewIntent", new Class[]{Intent.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{intent});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onNewIntent", e);
				}
			}

			public static void onSaveInstanceState(
					Activity activity, Bundle outState) {
				try {
					Method m = Activity.class.getDeclaredMethod("onSaveInstanceState", new Class[]{Bundle.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{outState});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onSaveInstanceState", e);
				}
			}

			@SuppressLint("NewApi")
			public static void onSaveInstanceState(Activity activity,
					Bundle outState, PersistableBundle outPersistentState) {
				try {
					Method m = Activity.class.getDeclaredMethod("onSaveInstanceState", new Class[]{Bundle.class, PersistableBundle.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{outState, outPersistentState});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onSaveInstanceState", e);
				}
			}

			public static void onApplyThemeResource(
					Activity activity, Theme theme, int resid,
					boolean first) {
				try {
					Method m = Activity.class.getDeclaredMethod("onApplyThemeResource", new Class[]{Theme.class, int.class, int.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{theme, resid, first});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onApplyThemeResource", e);
				}
			}

			public static void onChildTitleChanged(
					Activity activity, Activity childActivity,
					CharSequence title) {
				try {
					Method m = Activity.class.getDeclaredMethod("onChildTitleChanged", new Class[]{Activity.class, CharSequence.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{childActivity, title});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onChildTitleChanged", e);
				}
			}

			public static void onResumeFragments(
					FragmentActivity activity) {
				try {
					Method m = FragmentActivity.class.getDeclaredMethod("onResumeFragments", (Class[])null);
					m.setAccessible(true);
					m.invoke(activity, new Object[]{(Object[]) null});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onResumeFragments", e);
				}
			}

			public static void onListItemClick(
					ListActivity activity, ListView l, View v,
					int position, long id) {
				try {
					Method m = ListActivity.class.getDeclaredMethod("onListItemClick", new Class[]{ListView.class, View.class, int.class, long.class});
					m.setAccessible(true);
					m.invoke(activity, new Object[]{new Object[]{l, v, position, id}});
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("error in onListItemClick", e);
				}
			}

		}
		
		public static class ResourceUtil {
			public static int selectDefaultTheme(Resources res, int curTheme, int targetSdkVersion) {
				try {
					Method m = Class.forName(Resources.class.getCanonicalName()).getDeclaredMethod("selectDefaultTheme", 
							new Class[]{int.class, int.class});
					return (Integer) m.invoke(res, new Object[]{curTheme, targetSdkVersion});
				} catch (NoSuchMethodError e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return -1;
			}
		}

		public static Drawable loadItemIcon(PackageManager object,
				PackageItemInfo itemInfo, ApplicationInfo appInfo) {
			try {
				Method m = object.getClass().getDeclaredMethod("loadItemIcon", new Class[]{PackageItemInfo.class, ApplicationInfo.class});
				return (Drawable) m.invoke(object, new Object[]{itemInfo, appInfo});
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}
	}