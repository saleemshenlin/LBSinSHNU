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
	 * 设置supermap环境
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
	 * 获取屏幕分别率
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
	 * Dp转像素
	 */
	public static int Dp2Px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/*
	 * 保留两位小数
	 */
	public static float twoDecimal(float value) {
		final float valueTwoDecimal = (float) (Math.round(value * 100)) / 100;// (这里的100就是2位小数点,如果要其它位,如4位,这里两个100改成10000)
		return valueTwoDecimal;
	}

	/*
	 * 判断是否联网
	 */
	public static boolean isNetWork() {
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			// do something
			// 能联网
			return true;
		} else {
			// do something
			// 不能联网
			return false;
		}
	}
}
