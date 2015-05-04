package org.bbs.apklauncher_zeroinstall_shell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bbs.android.commonlib.ActivityUtil;
import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apkparser.PackageInfoX;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateConfig;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class UpdateService extends Service {

    private final String TAG = UpdateService.class.getSimpleName();

	protected ApkDonwloadMonitor mMonitor;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
	    checkUpdate();
	}

	private void checkUpdate() {
		UpdateConfig.setDebug(true);
        UpdateConfig.setDeltaUpdate(false);
        UmengUpdateAgent.setDownloadListener( new UmengDownloadListener(){

			@Override
			public void OnDownloadEnd(int arg0, String arg1) {
				Log.d(TAG, "OnDownloadEnd. arg0: " + arg0 + " arg1: " + arg1);
				if (UpdateStatus.DOWNLOAD_COMPLETE_FAIL == arg0 ) {
					mMonitor.onError();
				}
			}

			@Override
			public void OnDownloadStart() {
				Log.d(TAG, "OnDownloadStart. ");
				
			}

			@Override
			public void OnDownloadUpdate(int arg0) {
				Log.d(TAG, "OnDownloadUpdate. arg0: " + arg0);
				
			}});
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
//                        UmengUpdateAgent.showUpdateDialog(MainActivity.this, updateInfo);
                		
//                    	if (null != mMonitor) {
//                    		mMonitor.onError();
//                    	} 
//                		mMonitor = new ApkDonwloadMonitor(UpdateService.this, updateInfo);
//                        mMonitor.start();

        				PackageInfoX pInfo = ApkPackageManager.getInstance().getPackageInfo("com.example.apklauncher_zero_install");
        				int code = toCode(updateInfo.version);
        				if (code <= pInfo.versionCode) {
        					Log.i(TAG, "our versionCode    : " + pInfo.versionCode);
        					Log.i(TAG, "upgrade versionCode: " + code);
        					Log.i(TAG, "ignore this upgrade.");
        					return ;
        				}
                    	new Donwloader().execute(updateInfo);
                        break;
                    case UpdateStatus.No: // has no update
                        Toast.makeText(UpdateService.this, "没有更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.NoneWifi: // none wifi
                        Toast.makeText(UpdateService.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.Timeout: // java.lang.Stringtime out
                        Toast.makeText(UpdateService.this, "超时", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
           
        });
        
//        UpdateConfig.setUpdateOnlyWifi(true);
        UpdateConfig.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateAutoPopup(false);
//        UmengUpdateAgent.update(this);
        UmengUpdateAgent.silentUpdate(this);
	}
	
	int toCode(String versionName){
		int code = -1;
		String[] ver = versionName.split("\\.");
		code = Integer.parseInt(ver[0]) * 10000;
		code += Integer.parseInt(ver[1]) * 100;
		if (ver.length > 2){
			code += Integer.parseInt(ver[2]);
		}
		return code;
	}
	
	class Donwloader extends AsyncTask<UpdateResponse, Integer, File>{
		
		

		@Override
		protected File doInBackground(UpdateResponse... params) {
			if (null != params && params.length > 0){
				UpdateResponse r = params[0];
				
				try {
					URLConnection u = new URL(r.path).openConnection();
					InputStream in = u.getInputStream();
					
					File outF = new File(ApkPackageManager.getInstance().getAutoUpdatePluginDir() + "/lasted.apk.donwload");
					if (outF.exists()){
						outF.delete();
					}
					FileOutputStream out = new FileOutputStream(outF);
					final int LEN = 1024 * 1024;
					byte[] buffer = new byte[LEN];
					int count = -1;
					while ((count = in.read(buffer, 0, LEN)) != -1){
						out.write(buffer, 0, count);
					}
					File destFile = new File(ApkPackageManager.getInstance().getAutoUpdatePluginDir() + "/lasted.apk");
					if (destFile.exists()){
						destFile.delete();
					}
					outF.renameTo(destFile);
					out.flush();
					out.close();
					in.close();
					
					return destFile;
				} catch (MalformedURLException e) {
					Log.e(TAG,"",  e);
				} catch (IOException e) {
					Log.e(TAG,"",  e);
				}	
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(File result) {
			super.onPostExecute(result);
			
			if (result != null){
				ActivityUtil.toast(getApplicationContext(), "app has updated", Toast.LENGTH_LONG);
				Log.d(TAG, "apk downlod successed. file: " + result);
			} else {
				Log.d(TAG, "apk downlod error. file: " + result);
			}
		}
	}
		
    class ApkDonwloadMonitor extends Thread {
		private final  UpdateResponse mInfo;
        private final Context mContext;
		private boolean mError;

        public ApkDonwloadMonitor(Context context, UpdateResponse updateInfo) {
            mContext = context;
            mInfo = updateInfo;
        }
        
        public void onError(){
        	mError = true;
        }

        @Override
        public void run() {
            super.run();

            UmengUpdateAgent.downloadedFile(UpdateService.this, mInfo);

            int loop = 0;
            while (UmengUpdateAgent.downloadedFile(mContext, mInfo) == null && !mError) {
                try {
                    sleep(500);
                    Log.d(TAG, "sleep... " + loop);
                    loop++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            File apkFile = UmengUpdateAgent.downloadedFile(mContext, mInfo);
            if (null != apkFile) {
                apkFileReady(apkFile);
            } else {
            	Log.e(TAG, "error on download file: " + apkFile);
            }
        }

        private void apkFileReady(File apkFile) {
            Log.d(TAG, "apkFileReady: apkFile:" + apkFile);
            
            File update = new File(ApkPackageManager.getInstance().getAutoUpdatePluginDir(), "latest.apk");
            AndroidUtil.copyFile(apkFile, update);
            Log.d(TAG, "apkFileReady: update:" + update);
        }

    }
}
