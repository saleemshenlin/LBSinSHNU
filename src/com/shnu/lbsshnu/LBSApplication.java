package com.shnu.lbsshnu;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;

import com.supermap.data.Environment;

public class LBSApplication extends Application {

	private final static String TAG = "LBSApplication";
	private static int screenWidth;
	private static int screenHeight;
	public static String sDcard;
	private static Context context;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setEnvironment();
		context=getApplicationContext();
		Log.i(TAG, "LBSApplication onCreate!");
		getScreenDesplay();
		Log.i(TAG, "LBSApplication getScreenDisplay height:" + screenHeight);
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		Log.e(TAG, "LBSApplication onTerminate");
	}

	/*
	 * ����supermap����
	 */
	private void setEnvironment() {
		sDcard = android.os.Environment.getExternalStorageDirectory()
				.toString();
		Environment.setLicensePath(sDcard + getString(R.string.license_path));
		Environment.setTemporaryPath(sDcard + getString(R.string.temp_path));
		Environment.setWebCacheDirectory(sDcard
				+ getString(R.string.cache_path));
		Environment.initialization(this);
		Log.i(TAG, "LBSApplication setEnvironment!");
	}

	/*
	 * ��ȡ��Ļ�ֱ���
	 */
	private void getScreenDesplay() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		setScreenWidth(dm.widthPixels);
		setScreenHeight(dm.heightPixels);

	}

	public static int getScreenWidth() {
		return screenWidth;
	}

	public static void setScreenWidth(float xdpi) {
		LBSApplication.screenWidth = (int) xdpi;
	}

	public static int getScreenHeight() {
		return screenHeight;
	}

	public static void setScreenHeight(float ydpi) {
		LBSApplication.screenHeight = (int) ydpi;
	}

	/*
	 * Dpת����
	 */
	public static int Dp2Px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/*
	 * ������λС��
	 */
	public static float twoDecimal(float value) {
		final float valueTwoDecimal = (float) (Math.round(value * 100)) / 100;// (�����100����2λС����,���Ҫ����λ,��4λ,��������100�ĳ�10000)
		return valueTwoDecimal;
	}

	/*
	 * �ж��Ƿ�����
	 */
	public static boolean isNetWork() {
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			// do something
			// ������
			return true;
		} else {
			// do something
			// ��������
			return false;
		}
	}
}
