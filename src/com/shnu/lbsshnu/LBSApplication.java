package com.shnu.lbsshnu;

import android.app.Application;
import android.util.DisplayMetrics;
import android.util.Log;

import com.supermap.data.Environment;

public class LBSApplication extends Application {

	private final static String TAG = "LBSApplication";
	private static int screenWidth;
	private static int screenHeight;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setEnvironment();
		Log.i(TAG, "LBSApplication onCreate!");
		getScreenDesplay();
		Log.i(TAG, "LBSApplication gerScreenDisplay");
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		Log.e(TAG, "LBSApplication onTerminate");
	}

	private void setEnvironment() {
		Environment.setLicensePath(getString(R.string.license_path));
		Environment.setTemporaryPath(getString(R.string.temp_path));
		Environment.setWebCacheDirectory(getString(R.string.cache_path));
		Environment.initialization(this);
		Log.i(TAG, "LBSApplication setEnvironment!");
	}

	private void getScreenDesplay() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		if (dm.xdpi < dm.ydpi) {
			setScreenWidth(dm.xdpi);
			setScreenHeight(dm.ydpi);
		} else {
			setScreenWidth(dm.ydpi);
			setScreenHeight(dm.xdpi);
		}

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

}
