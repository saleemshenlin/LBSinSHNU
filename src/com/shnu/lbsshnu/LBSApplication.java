package com.shnu.lbsshnu;

import java.text.DecimalFormat;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;

import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;

public class LBSApplication extends Application {

	private final static String TAG = "LBSApplication";
	private static int SCREENWIDTH;
	private static int SCREENHEIGHT;
	private static double SCREENDPI;
	private static String SDCARD;
	private static Context CONTEXT;
	private static LocationByBaiduAPI LOCATIONAPI = new LocationByBaiduAPI();
	private static Point2D LASTLOCATION;
	private static float LOCATIONACCUCRACY;
	private static Workspace mWorkspace;
	private static MapView mMapView;
	private static MapControl mMapControl;
	private static TrackingLayer mTrackingLayer;
	private static Layers mlayers;
	Layer mWifiLayerS;// 小比例尺wifi层
	Layer mWifiLayerL;// 大比例尺wifi层

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setEnvironment();
		CONTEXT = getApplicationContext();
		Log.i(TAG, "LBSApplication onCreate!");
		getScreenDesplay();
		Log.i(TAG, "LBSApplication getScreenDisplay height:" + SCREENHEIGHT);
		LBSApplication.LASTLOCATION = new Point2D();
		LBSApplication.LOCATIONACCUCRACY = (float) 10;
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
		SDCARD = android.os.Environment.getExternalStorageDirectory()
				.toString();
		Environment.setLicensePath(SDCARD + getString(R.string.license_path));
		Environment.setTemporaryPath(SDCARD + getString(R.string.temp_path));
		Environment.setWebCacheDirectory(SDCARD
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
		setScreenDPI(dm.densityDpi);
	}

	public static int getScreenWidth() {
		return SCREENWIDTH;
	}

	public static void setScreenWidth(float xdpi) {
		LBSApplication.SCREENWIDTH = (int) xdpi;
	}

	public static int getScreenHeight() {
		return SCREENHEIGHT;
	}

	public static void setScreenHeight(float ydpi) {
		LBSApplication.SCREENHEIGHT = (int) ydpi;
	}

	public static double getScreenDPI() {
		return SCREENDPI;
	}

	public static void setScreenDPI(double screenDPI) {
		LBSApplication.SCREENDPI = screenDPI;
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
		ConnectivityManager cwjManager = (ConnectivityManager) CONTEXT
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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

	/*
	 * 刷新地图
	 */
	public static void refreshMap() {
		mMapControl.getMap().refresh();
	}

	/*
	 * 清除上一次的定位buffer
	 */
	public static void clearTrackingLayer() {
		mTrackingLayer.clear();
		refreshMap();
	}

	/*
	 * 保留2位小数
	 */
	public static String save2Point(float value) {
		DecimalFormat df = new java.text.DecimalFormat("#0.00");
		return df.format(value);
	}

	public static LocationByBaiduAPI getLocationApi() {
		return LOCATIONAPI;
	}

	public static void setLocationApi(LocationByBaiduAPI locationApi) {
		LBSApplication.LOCATIONAPI = locationApi;
	}

	public static Layers getMlayers() {
		return mlayers;
	}

	public static void setMlayers(Layers mlayers) {
		LBSApplication.mlayers = mlayers;
	}

	public static Context getContext() {
		return CONTEXT;
	}

	public static String getSdCard() {
		return SDCARD;
	}

	public static Workspace getmWorkspace() {
		return mWorkspace;
	}

	public static void setmWorkspace(Workspace mWorkspace) {
		LBSApplication.mWorkspace = mWorkspace;
	}

	public static MapView getmMapView() {
		return mMapView;
	}

	public static void setmMapView(MapView mMapView) {
		LBSApplication.mMapView = mMapView;
	}

	public static MapControl getmMapControl() {
		return mMapControl;
	}

	public static void setmMapControl(MapControl mMapControl) {
		LBSApplication.mMapControl = mMapControl;
	}

	public static TrackingLayer getmTrackingLayer() {
		return mTrackingLayer;
	}

	public static void setmTrackingLayer(TrackingLayer mTrackingLayer) {
		LBSApplication.mTrackingLayer = mTrackingLayer;
	}

	public static Point2D getLastlocationPoint2d() {
		return LASTLOCATION;
	}

	public static void setLastlocationPoint2d(Point2D point) {
		LBSApplication.LASTLOCATION = point;
	}

	public static float getLocationAccuracy() {
		return LOCATIONACCUCRACY;
	}

	public static void setLocationAccuracy(float value) {
		LBSApplication.LOCATIONACCUCRACY = value;
	}

}
