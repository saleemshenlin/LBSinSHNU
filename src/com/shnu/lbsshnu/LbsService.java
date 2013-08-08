package com.shnu.lbsshnu;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 用于更新Widget的服务
 * 
 */
public class LbsService extends Service {
	/**
	 * 需要更新的intent的Action
	 */
	public static final String NEW_STATUS_INTENT = "com.shnu.lbsshnu.NEW_STATUS";
	static final String TAG = "LbsService";
	private static boolean isRun = false;
	/**
	 * 设置更新时间为1小时
	 */
	private final int DELAY = 3600000;
	private Updater updater;
	LbsApplication lbsApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		this.updater = new Updater();
		lbsApplication = (LbsApplication) getApplication();
		Log.e(TAG, "onCreate");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LbsService.setRunFlag(false);
		this.updater.interrupt();
		this.updater = null;
		Log.e(TAG, "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		LbsService.setRunFlag(true);
		if (!this.updater.isAlive()) {
			this.updater.start();
		}

		Log.e(TAG, "onStart");
		return START_STICKY;
	}

	public static boolean isRunFlag() {
		return isRun;
	}

	public static void setRunFlag(boolean runFlag) {
		LbsService.isRun = runFlag;
	}

	/**
	 * 每隔一个小时广播一次Widget更新
	 */
	private class Updater extends Thread {
		Intent intent;
		LbsService lbsService = LbsService.this;

		public Updater() {
			super("UpdaterService-Updater");
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			LbsService.setRunFlag(true);
			while (LbsService.isRunFlag()) {
				Log.d(TAG, "Updater running");
				try {
					intent = new Intent();
					intent.setAction(LbsService.NEW_STATUS_INTENT);
					lbsService.sendBroadcast(intent);
					Log.d(TAG, "Send Broadcast");
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					Log.e(TAG, e.toString() + " , " + e.getMessage());
				}
			}
		}
	}

}
