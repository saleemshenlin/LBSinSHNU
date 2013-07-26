package com.shnu.lbsshnu;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.baidu.location.LocationClient;
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
	private static ActivityData activityData;
	private static int requestCode = 0;
	private static boolean isStart = false;
	private static LocationClient locationClient;
	private static boolean isSearch = false;
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
		importDataFromXML();
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

	/*
	 * Dp转像素
	 */
	public static int Dp2Px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/*
	 * 
	 * /* 判断是否联网
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
	 * 清除跟踪层
	 */
	public static void clearTrackingLayer() {
		mTrackingLayer.clear();
		refreshMap();
	}

	/*
	 * 清除点标注
	 */
	public static void clearCallout() {
		LBSApplication.mMapView.removeAllCallOut();
	}

	/*
	 * 保留2位小数
	 */
	public static String save2Point(float value) {
		DecimalFormat df = new java.text.DecimalFormat("#0.00");
		return df.format(value);
	}

	/*
	 * 调用FileIO导入数据
	 */
	private void importDataFromXML() {
		FileIO fileIO = new FileIO();
		fileIO.getDateFromXML();
	}
	
	@SuppressWarnings("static-access")
	public static void hideIme(Activity context) {
		if (context == null)
			return;
		final View v = context.getWindow().peekDecorView();
		if (v != null && v.getWindowToken() != null) {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	@SuppressWarnings("static-access")
	public static boolean isImeShow(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(context.INPUT_METHOD_SERVICE);
		return imm.isActive();
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

	public static ActivityData getActivityData() {
		activityData = new ActivityData(getContext());
		return activityData;
	}

	public static int getRequestCode() {
		return requestCode;
	}

	public static void setRequestCode(int requestCode) {
		LBSApplication.requestCode = requestCode;
	}

	public static LocationClient getLocationClient() {
		return locationClient;
	}

	public static void setLocationClient(Context context) {
		LocationClient locationClient = new LocationClient(context);
		LBSApplication.locationClient = locationClient;
	}

	public static boolean isStart() {
		return isStart;
	}

	public static void setStart(boolean isStart) {
		LBSApplication.isStart = isStart;
	}

	public static boolean isSearch() {
		return isSearch;
	}

	public static void setSearch(boolean isSearch) {
		LBSApplication.isSearch = isSearch;
	}
}
