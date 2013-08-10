package com.shnu.lbsshnu;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * ��LbsService ����ÿ��һСʱ����Widget�ķ���
 * 
 */
public class LbsService extends Service {
	/**
	 * ��Ҫ���µ�intent��Action
	 */
	public static final String NEW_STATUS_INTENT = "com.shnu.lbsshnu.NEW_STATUS";
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾLbsService
	 */
	private static final String TAG = "LbsService";
	/**
	 * ����һ������,�����жϷ����Ƿ��
	 */
	private static boolean isRun = false;
	/**
	 * ����һ������,�������ø���ʱ��Ϊ1Сʱ
	 */
	private final int DELAY = 3600000;
	/**
	 * ʵ��һ��Updater
	 */
	private Updater updater;

	/**
	 * ����LbsSrvice<br>
	 * 1)��ʼ��Updater<br>
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
	 * ���ٷ���
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
	 * ��������<br>
	 * 1)����RunFlagΪ����״̬<br>
	 * 2)��������<br>
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
	 * ��ȡ������״̬
	 * 
	 * @return boolean �����Ƿ���
	 */
	public static boolean isRunFlag() {
		return isRun;
	}

	/**
	 * ���÷�����״̬
	 * 
	 * @param runFlag
	 *            �Ƿ���
	 */
	public static void setRunFlag(boolean runFlag) {
		LbsService.isRun = runFlag;
	}

	/**
	 * ��Updater <br>
	 * ����ÿ��һ��Сʱ�㲥һ��Widget����
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
