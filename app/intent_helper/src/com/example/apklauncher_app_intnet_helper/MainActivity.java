package com.example.apklauncher_app_intnet_helper;

import java.io.Serializable;

import org.bbs.apklauncher.api.Base_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.apklauncher_app_intent_helper.R;

public class MainActivity extends Base_Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new org.bbs.apklauncher.emb.IntentHelper(MainActivity.this, ResultActivity.class);
				// FIXME we just can put Serializable object to intent.
				//				intent.putExtra(ResultActivity.EXTRA_PARCEL, new P(1));
				intent.putExtra(ResultActivity.EXTRA_SERIABLE, new S(1));
				startActivity(intent);
			}
		});
	}
	
	public static class S implements Serializable {
		public int a;
		
		public S(int p){
			a = p;
		}
	}
	
	public static class P implements Parcelable 
//	,Serializable
	{
		
		public int a;
		
		public P(int p){
			a = p;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		public void writeToParcel(Parcel out, int flags) {
	         out.writeInt(a);
	     }

	     public static final Parcelable.Creator<P> CREATOR
	             = new Parcelable.Creator<P>() {
	         public P createFromParcel(Parcel in) {
	             return new P(in);
	         }

	         public P[] newArray(int size) {
	             return new P[size];
	         }
	     };
	     
	     private P(Parcel in) {
	         a = in.readInt();
	     }
		
	}
}
