package com.shnu.lbsshnu;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 类LbsService 用于每隔一小时更新Widget的服务
 * 
 */
public class LbsService extends Service {
	/**
	 * 需要更新的intent的Action
	 */
	public static final String NEW_STATUS_INTENT = "com.shnu.lbsshnu.NEW_STATUS";
	/**
	 * 定义一个标签,在LogCat内表示LbsService
	 */
	private static final String TAG = "LbsService";
	/**
	 * 定义一个常量,用于判断服务是否打开
	 */
	private static boolean isRun = false;
	/**
	 * 定义一个常数,用于设置更新时间为1小时
	 */
	private final int DELAY = 3600000;
	/**
	 * 实例一个Updater
	 */
	private Updater updater;

	/**
	 * 创建LbsSrvice<br>
	 * 1)初始化Updater<br>
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		this.updater = new Updater();
		Log.e(TAG, "onCreate");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 销毁服务
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		LbsService.setRunFlag(false);
		this.updater.interrupt();
		this.updater = null;
		Log.e(TAG, "onDestroy");
	}

	/**
	 * 开启服务<br>
	 * 1)设置RunFlag为开启状态<br>
	 * 2)开启服务<br>
	 */
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

	/**
	 * 获取服务开启状态
	 * 
	 * @return boolean 服务是否开启
	 */
	public static boolean isRunFlag() {
		return isRun;
	}

	/**
	 * 设置服务开启状态
	 * 
	 * @param runFlag
	 *            是否开启
	 */
	public static void setRunFlag(boolean runFlag) {
		LbsService.isRun = runFlag;
	}

	/**
	 * 类Updater <br>
	 * 用于每隔一个小时广播一次Widget更新
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
