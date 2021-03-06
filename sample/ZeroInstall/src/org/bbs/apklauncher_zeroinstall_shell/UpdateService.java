package org.bbs.apklauncher_zeroinstall_shell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import orb.bbs.apklauncher.zeroinstall.shell.R;

import org.bbs.android.commonlib.ActivityUtil;
import org.bbs.apklauncher.AndroidUtil;
import org.bbs.apklauncher.ApkPackageManager;
import org.bbs.apklauncher.Version;
import org.bbs.apkparser.PackageInfoX;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
		Log.d(TAG, "checkUpdate");
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
        				if (null == pInfo ) {
        					Log.e(TAG, "null packageInfo, return;");
        				}
        				if ((Version.isNewer(pInfo.versionName, updateInfo.version)
        						|| Version.isSame(pInfo.versionName, updateInfo.version))) {
        					Log.i(TAG, "our versionName    : " + pInfo.versionName);
        					Log.i(TAG, "upgrade versionName: " + updateInfo.version);
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

		UpdateConfig.setUpdateCheck(false);
		UpdateConfig.setDebug(true);
        UpdateConfig.setDeltaUpdate(false);
        UpdateConfig.setUpdateOnlyWifi(false);
        UpdateConfig.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        // umeng will auto update this, shit!!!!
        UmengUpdateAgent.update(this);
//        UmengUpdateAgent.silentUpdate(this);
	}

	class Donwloader extends AsyncTask<UpdateResponse, Integer, File>{
		private UpdateResponse mUpdateRes;

		@Override
		protected File doInBackground(UpdateResponse... params) {
			if (null != params && params.length > 0){
				mUpdateRes = params[0];
				Log.d(TAG, "try download file: " + mUpdateRes.path);
				
				try {
					URLConnection u = new URL(mUpdateRes.path).openConnection();
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
		
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(File result) {
			super.onPostExecute(result);
			
			if (result != null){
				String uStr = "app has upgraded. v" + mUpdateRes.version;
				ActivityUtil.toast(getApplicationContext(), uStr, Toast.LENGTH_LONG);
				
				Notification n = new Notification();
				Notification.Builder b = new Notification.Builder(getApplicationContext());
				b.setSmallIcon(R.drawable.ic_launcher);
				b.setContentText(uStr);
				b.setContentTitle("Upgrade");
//				Intent intent = new Intent(getApplicationContext(), );				
//				PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//				b.addAction(R.drawable.tb_munion_icon, "restart", pIntent);
				
//				b.addAction(action)
				
				((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).notify(R.layout.activity_main, b.build());
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
