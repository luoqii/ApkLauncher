package org.bbs.apklauncher.emb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Set;

import android.app.Application;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

/**
 * < 5.0 Intent.putSerizable() do not support classloader.
 * 
 * Created by bysong
 * 
 */
public class IntentHelper extends android.content.Intent {
	
	public static final String TAG = IntentHelper.class.getSimpleName();
	
	/**
	 * type {@link boolean}
	 */
	public static final String EXTRA_INJECT = android.content.Intent.class.getName() + ".EXTRA_INJECT";
	
	private android.content.Intent mTarget;
	
	public IntentHelper(){
		mTarget = new android.content.Intent();
	}

	public IntentHelper(android.content.Intent target) {
		mTarget = target;
	}

	public IntentHelper(Context context,
			Class clazz) {
		mTarget = new android.content.Intent(context, clazz);
	}

	public IntentHelper(String action) {
		mTarget = new Intent(action);
	}

	public IntentHelper(String action, Uri uri) {
		mTarget = new Intent(action, uri);
	}

	@Override
	public Object clone() {
		return mTarget.clone();
	}

	@Override
	public android.content.Intent cloneFilter() {
		return mTarget.cloneFilter();
	}

	@Override
	public String getAction() {
		return mTarget.getAction();
	}

	@Override
	public Uri getData() {
		return mTarget.getData();
	}

	@Override
	public String getDataString() {
		return mTarget.getDataString();
	}

	@Override
	public String getScheme() {
		return mTarget.getScheme();
	}

	@Override
	public String getType() {
		return mTarget.getType();
	}

	@Override
	public String resolveType(Context context) {
		return mTarget.resolveType(context);
	}

	@Override
	public String resolveType(ContentResolver resolver) {
		return mTarget.resolveType(resolver);
	}

	@Override
	public String resolveTypeIfNeeded(ContentResolver resolver) {
		return mTarget.resolveTypeIfNeeded(resolver);
	}

	@Override
	public boolean hasCategory(String category) {
		return mTarget.hasCategory(category);
	}

	@Override
	public Set<String> getCategories() {
		return mTarget.getCategories();
	}

	@Override
	public android.content.Intent getSelector() {
		return mTarget.getSelector();
	}

	@Override
	public ClipData getClipData() {
		return mTarget.getClipData();
	}

	@Override
	public void setExtrasClassLoader(ClassLoader loader) {
		mTarget.setExtrasClassLoader(loader);
	}

	@Override
	public boolean hasExtra(String name) {
		return mTarget.hasExtra(name);
	}

	@Override
	public boolean hasFileDescriptors() {
		return mTarget.hasFileDescriptors();
	}

	@Override
	public boolean getBooleanExtra(String name, boolean defaultValue) {
		return mTarget.getBooleanExtra(name, defaultValue);
	}

	@Override
	public byte getByteExtra(String name, byte defaultValue) {
		return mTarget.getByteExtra(name, defaultValue);
	}

	@Override
	public short getShortExtra(String name, short defaultValue) {
		return mTarget.getShortExtra(name, defaultValue);
	}

	@Override
	public char getCharExtra(String name, char defaultValue) {
		return mTarget.getCharExtra(name, defaultValue);
	}

	@Override
	public int getIntExtra(String name, int defaultValue) {
		return mTarget.getIntExtra(name, defaultValue);
	}

	@Override
	public long getLongExtra(String name, long defaultValue) {
		return mTarget.getLongExtra(name, defaultValue);
	}

	@Override
	public float getFloatExtra(String name, float defaultValue) {
		return mTarget.getFloatExtra(name, defaultValue);
	}

	@Override
	public double getDoubleExtra(String name, double defaultValue) {
		return mTarget.getDoubleExtra(name, defaultValue);
	}

	@Override
	public String getStringExtra(String name) {
		return mTarget.getStringExtra(name);
	}

	@Override
	public CharSequence getCharSequenceExtra(String name) {
		return mTarget.getCharSequenceExtra(name);
	}

	@Override
	public <T extends Parcelable> T getParcelableExtra(String name) {
//		notSupported();
//		
		return mTarget.getParcelableExtra(name);
		
//		Log.d(TAG, "getParcelableExtra(). name: " + name);
//		T value = null;
//		if (mTarget.getBooleanExtra(EXTRA_INJECT, false) == false){
//			value =  mTarget.getParcelableExtra(name);
//		} else {
//			String fName = null;
//			fName = mTarget.getStringExtra(name);
//			if (!TextUtils.isEmpty(fName)) {
//				value =  (T) PersistentObject.getsInstance().getObject(fName);
//			}
//		}
//
//		Log.d(TAG, "getParcelableExtra(). name: " + name + " value: " + value);
//		return value;
	}

	@Override
	public Parcelable[] getParcelableArrayExtra(String name) {
		notSupported();
		
		return mTarget.getParcelableArrayExtra(name);
	}

	@Override
	public <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(
			String name) {
		notSupported();
		
		return mTarget.getParcelableArrayListExtra(name);
	}

	@Override
	public Serializable getSerializableExtra(String name) {
		Log.d(TAG, "getSerializableExtra(). name: " + name);

		String fName = mTarget.getStringExtra(name);
		Serializable value = null;
		if (!TextUtils.isEmpty(fName)) {
			value = (Serializable) PersistentObject.getsInstance().getObject(fName);
		}
		Log.d(TAG, "getSerializableExtra(). name: " + name + " value: " + value);
		
		return value;
				
//		return mTarget.getSerializableExtra(name);
	}

	@Override
	public ArrayList<Integer> getIntegerArrayListExtra(String name) {
		return mTarget.getIntegerArrayListExtra(name);
	}

	@Override
	public ArrayList<String> getStringArrayListExtra(String name) {
		return mTarget.getStringArrayListExtra(name);
	}

	@Override
	public ArrayList<CharSequence> getCharSequenceArrayListExtra(String name) {
		return mTarget.getCharSequenceArrayListExtra(name);
	}

	@Override
	public boolean[] getBooleanArrayExtra(String name) {
		return mTarget.getBooleanArrayExtra(name);
	}

	@Override
	public byte[] getByteArrayExtra(String name) {
		return mTarget.getByteArrayExtra(name);
	}

	@Override
	public short[] getShortArrayExtra(String name) {
		return mTarget.getShortArrayExtra(name);
	}

	@Override
	public char[] getCharArrayExtra(String name) {
		return mTarget.getCharArrayExtra(name);
	}

	@Override
	public int[] getIntArrayExtra(String name) {
		return mTarget.getIntArrayExtra(name);
	}

	@Override
	public long[] getLongArrayExtra(String name) {
		return mTarget.getLongArrayExtra(name);
	}

	@Override
	public float[] getFloatArrayExtra(String name) {
		return mTarget.getFloatArrayExtra(name);
	}

	@Override
	public double[] getDoubleArrayExtra(String name) {
		return mTarget.getDoubleArrayExtra(name);
	}

	@Override
	public String[] getStringArrayExtra(String name) {
		return mTarget.getStringArrayExtra(name);
	}

	@Override
	public CharSequence[] getCharSequenceArrayExtra(String name) {
		return mTarget.getCharSequenceArrayExtra(name);
	}

	@Override
	public Bundle getBundleExtra(String name) {
		return mTarget.getBundleExtra(name);
	}

	@Override
	public Bundle getExtras() {
		return mTarget.getExtras();
	}

	@Override
	public int getFlags() {
		return mTarget.getFlags();
	}

	@Override
	public String getPackage() {
		return mTarget.getPackage();
	}

	@Override
	public ComponentName getComponent() {
		return mTarget.getComponent();
	}

	@Override
	public Rect getSourceBounds() {
		return mTarget.getSourceBounds();
	}

	@Override
	public ComponentName resolveActivity(PackageManager pm) {
		return mTarget.resolveActivity(pm);
	}

	@Override
	public ActivityInfo resolveActivityInfo(PackageManager pm, int flags) {
		return mTarget.resolveActivityInfo(pm, flags);
	}

	@Override
	public android.content.Intent setAction(String action) {
		 mTarget.setAction(action);
		 
		 return this;
	}

	@Override
	public android.content.Intent setData(Uri data) {
		 mTarget.setData(data);
		 
		 return this;
	}

	@Override
	public android.content.Intent setDataAndNormalize(Uri data) {
		 mTarget.setDataAndNormalize(data);
		 
		 return this;
	}

	@Override
	public android.content.Intent setType(String type) {
		mTarget.setType(type);
		 
		 return this;
	}

	@Override
	public android.content.Intent setTypeAndNormalize(String type) {
		 mTarget.setTypeAndNormalize(type);
		 
		 return this;
	}

	@Override
	public android.content.Intent setDataAndType(Uri data, String type) {
		mTarget.setDataAndType(data, type);
		 
		 return this;
	}

	@Override
	public android.content.Intent setDataAndTypeAndNormalize(Uri data, String type) {
		 mTarget.setDataAndTypeAndNormalize(data, type);
		 
		 return this;
	}

	@Override
	public android.content.Intent addCategory(String category) {
		 mTarget.addCategory(category);
		 
		 return this;
	}

	@Override
	public void removeCategory(String category) {
		mTarget.removeCategory(category);
	}

	@Override
	public void setSelector(android.content.Intent selector) {
		mTarget.setSelector(selector);
	}

	@Override
	public void setClipData(ClipData clip) {
		mTarget.setClipData(clip);
	}

	@Override
	public android.content.Intent putExtra(String name, boolean value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, byte value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, char value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, short value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, int value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, long value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, float value) {
		mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, double value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, String value) {
		mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, CharSequence value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, Parcelable value) {
		mTarget.putExtra(name, value);
//			Log.d(TAG, "putExtra(). name: " + name + " value: " + value);
//			if (! (value instanceof Serializable)) {
//				throw new RuntimeException("input paramater must be Serializable. value: " + value);
//			}
//			File f = PersistentObject.getsInstance().saveObject((Serializable) value);
//			if (f != null) {
//				mTarget.putExtra(name, f.getName());
//			}
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, Parcelable[] value) {
		 mTarget.putExtra(name, value);

		 notSupported();
		 
		 return this;
	}

	@Override
	public android.content.Intent putParcelableArrayListExtra(String name,
			ArrayList<? extends Parcelable> value) {
		 mTarget.putParcelableArrayListExtra(name, value);

		 notSupported();
		 
		 return this;
	}

	@Override
	public android.content.Intent putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
		 mTarget.putIntegerArrayListExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putStringArrayListExtra(String name, ArrayList<String> value) {
		 mTarget.putStringArrayListExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putCharSequenceArrayListExtra(String name,
			ArrayList<CharSequence> value) {
		 mTarget.putCharSequenceArrayListExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, Serializable value) {
//			mTarget.putExtra(name, value);
		Log.d(TAG, "putExtra(). name: " + name + " value: " + value);
		File f = PersistentObject.getsInstance().saveObject(value);
		if (f != null) {
			mTarget.putExtra(name, f.getName());
		}
		 
		return this;
	}

	@Override
	public android.content.Intent putExtra(String name, boolean[] value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, byte[] value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, short[] value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, char[] value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, int[] value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, long[] value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, float[] value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, double[] value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, String[] value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtra(String name, CharSequence[] value) {
		return mTarget.putExtra(name, value);
	}

	@Override
	public android.content.Intent putExtra(String name, Bundle value) {
		 mTarget.putExtra(name, value);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtras(android.content.Intent src) {
		 mTarget.putExtras(src);
		 
		 return this;
	}

	@Override
	public android.content.Intent putExtras(Bundle extras) {
		 mTarget.putExtras(extras);
		 
		 return this;
	}

	@Override
	public android.content.Intent replaceExtras(android.content.Intent src) {
		 mTarget.replaceExtras(src);
		 
		 return this;
	}

	@Override
	public android.content.Intent replaceExtras(Bundle extras) {
		 mTarget.replaceExtras(extras);
		 
		 return this;
	}

	@Override
	public void removeExtra(String name) {
		mTarget.removeExtra(name);
	}

	@Override
	public android.content.Intent setFlags(int flags) {
		 mTarget.setFlags(flags);
		 
		 return this;
	}

	@Override
	public android.content.Intent addFlags(int flags) {
		 mTarget.addFlags(flags);
		 
		 return this;
	}

	@Override
	public android.content.Intent setPackage(String packageName) {
		 mTarget.setPackage(packageName);
		 
		 return this;
	}

	@Override
	public android.content.Intent setComponent(ComponentName component) {
		 mTarget.setComponent(component);
		 
		 return this;
	}

	@Override
	public android.content.Intent setClassName(Context packageContext, String className) {
		 mTarget.setClassName(packageContext, className);
		 
		 return this;
	}

	@Override
	public android.content.Intent setClassName(String packageName, String className) {
		 mTarget.setClassName(packageName, className);
		 
		 return this;
	}

	@Override
	public android.content.Intent setClass(Context packageContext, Class<?> cls) {
		 mTarget.setClass(packageContext, cls);
		 
		 return this;
	}

	@Override
	public void setSourceBounds(Rect r) {
		mTarget.setSourceBounds(r);
	}

	@Override
	public int fillIn(android.content.Intent other, int flags) {
		return mTarget.fillIn(other, flags);
	}

	@Override
	public boolean filterEquals(android.content.Intent other) {
		return mTarget.filterEquals(other);
	}

	@Override
	public int filterHashCode() {
		return mTarget.filterHashCode();
	}

	@Override
	public String toString() {
		return mTarget.toString();
	}

	@Override
	public String toURI() {
		return mTarget.toURI();
	}

	@Override
	public String toUri(int flags) {
		return mTarget.toUri(flags);
	}

	@Override
	public int describeContents() {
		return mTarget.describeContents();
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		mTarget.writeToParcel(out, flags);
	}

	@Override
	public void readFromParcel(Parcel in) {
		mTarget.readFromParcel(in);
	}
	
	void notSupported() {
		throw new RuntimeException("not supported.");
	}
	
	public static class PersistentObject {
		private static final String TAG = PersistentObject.class.getSimpleName();
	    private File mDir;
	    private ClassLoader mClassLoader;
		private boolean mHasInit;

	    private static PersistentObject sInstance;

	    /**
	     * 
	     * @return
		 * 
		 * @see #init(Application, ClassLoader)
	     */
	    public static PersistentObject getsInstance() {
	        if (null == sInstance) {
	            sInstance = new PersistentObject();
	        }

	        return sInstance;
	    }
	    
	    private PersistentObject(){
	    	
	    }

	    public void init(Application application, ClassLoader classLoader){
	        mDir = application.getDir("tmp_object", Context.MODE_WORLD_READABLE);
	        
	        for (File f : mDir.listFiles()){
	        	f.delete();
	        }
	        
	        mClassLoader = classLoader;
	        
	        mHasInit = true;
	    }

	    public File saveObject(Serializable s){
	    	makeSureInited();
	    	
	        File f = null;
	        try {
	            f = File.createTempFile(s.getClass().getName(), ".object", mDir);
	            Log.d(TAG, "saveObject. fileName: " + f.getPath() + " data: " + s);
	            ObjectOutputStream oop = new ObjectOutputStream(new FileOutputStream(f));
	            oop.writeObject(s);
	            oop.flush();
	            oop.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return f;
	    }

	    private void makeSureInited() {
	    	if (!mHasInit) {
	    		throw new RuntimeException("you must init " + TAG + " with init(Application, ClassLoader).");
	    	}
		}

		public Object getObject(String serialFile){
	    	makeSureInited();
	    	
	    	File file = new File(mDir, serialFile);
	        Log.d(TAG, "getObject. fileName: " + file.getPath());
	    	Object o = null;
	    	try {
	    		ObjectInputStream oin = new OIS(new FileInputStream(file));
	    		o = oin.readObject();
	    		oin.close();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} catch (ClassNotFoundException e) {
	    		e.printStackTrace();
	    	}

	    	return o;
	    }
	    
	    class OIS extends ObjectInputStream {

			public OIS(InputStream input) throws StreamCorruptedException,
					IOException {
				super(input);
			}
			
			@Override
			protected Class<?> resolveClass(ObjectStreamClass osClass)
					throws IOException, ClassNotFoundException {
				try {
					return super.resolveClass(osClass);
				} catch (ClassNotFoundException e){
					return mClassLoader.loadClass(osClass.getName());
				}
			}
	    	
	    }
	}
}
