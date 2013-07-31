package com.shnu.lbsshnu;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
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

public class LbsApplication extends Application {

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
	private static int bufferQueryCode = 1;
	private static boolean isLocateStart = false;
	private static LocationClient locationClient;
	private static String queryString = "";
	public static String QUERY_WITH_LOCATION_FLAG = "QUERY_WITH_LOCATION";

	Layer mWifiLayerS;// С������wifi��
	Layer mWifiLayerL;// �������wifi��

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setEnvironment();
		CONTEXT = getApplicationContext();
		Log.i(TAG, "LBSApplication onCreate!");
		getScreenDesplay();
		Log.i(TAG, "LBSApplication getScreenDisplay height:" + SCREENHEIGHT);
		LbsApplication.LASTLOCATION = new Point2D();
		LbsApplication.LOCATIONACCUCRACY = (float) 10;
		importDataFromXML();
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
	 * ��ȡ��Ļ�ֱ���
	 */
	private void getScreenDesplay() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		setScreenWidth(dm.widthPixels);
		setScreenHeight(dm.heightPixels);
		setScreenDPI(dm.densityDpi);
	}

	/*
	 * Dpת����
	 */
	public static int Dp2Px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/*
	 * �ж��Ƿ�����
	 */
	public static boolean isNetWork() {
		ConnectivityManager cwjManager = (ConnectivityManager) CONTEXT
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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

	/*
	 * �ж�GPS�Ƿ��
	 */
	public static boolean isGPSOpen() {
		LocationManager locationManager = ((LocationManager) getContext()
				.getSystemService(Context.LOCATION_SERVICE));
		boolean isOpen = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return isOpen;
	}

	/*
	 * ˢ�µ�ͼ
	 */
	public static void refreshMap() {
		if (mMapControl != null) {
			mMapControl.getMap().refresh();
		}

	}

	/*
	 * ������ٲ�
	 */
	public static void clearTrackingLayer() {
		if (mTrackingLayer != null) {
			mTrackingLayer.clear();
		}
		refreshMap();
	}

	/*
	 * ������ע
	 */
	public static void clearCallout() {
		if (mMapView != null) {
			mMapView.removeAllCallOut();
		}
	}

	/*
	 * ����2λС��
	 */
	public static String save2Point(float value) {
		DecimalFormat df = new java.text.DecimalFormat("#.00");
		return df.format(value) + "";
	}

	/*
	 * ����FileIO��������
	 */
	private void importDataFromXML() {
		FileIO fileIO = new FileIO();
		fileIO.getDateFromXML();
	}

	/*
	 * ��������
	 */
	public static void startServices(Context context) {
		if (!LbsService.isRunFlag()) {
			context.startService(new Intent(getContext(), LbsService.class));
		}
	}

	/*
	 * �����������
	 */
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

	/*
	 * �ж���������Ƿ��
	 */
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
		LbsApplication.LOCATIONAPI = locationApi;
	}

	public static Layers getMlayers() {
		return mlayers;
	}

	public static void setMlayers(Layers mlayers) {
		LbsApplication.mlayers = mlayers;
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
		LbsApplication.mWorkspace = mWorkspace;
	}

	public static MapView getmMapView() {
		return mMapView;
	}

	public static void setmMapView(MapView mMapView) {
		LbsApplication.mMapView = mMapView;
	}

	public static MapControl getmMapControl() {
		return mMapControl;
	}

	public static void setmMapControl(MapControl mMapControl) {
		LbsApplication.mMapControl = mMapControl;
	}

	public static TrackingLayer getmTrackingLayer() {
		return mTrackingLayer;
	}

	public static void setmTrackingLayer(TrackingLayer mTrackingLayer) {
		LbsApplication.mTrackingLayer = mTrackingLayer;
	}

	public static Point2D getLastlocationPoint2d() {
		return LASTLOCATION;
	}

	public static void setLastlocationPoint2d(Point2D point) {
		LbsApplication.LASTLOCATION = point;
	}

	public static float getLocationAccuracy() {
		return LOCATIONACCUCRACY;
	}

	public static void setLocationAccuracy(float value) {
		LbsApplication.LOCATIONACCUCRACY = value;
	}

	public static int getScreenWidth() {
		return SCREENWIDTH;
	}

	public static void setScreenWidth(float xdpi) {
		LbsApplication.SCREENWIDTH = (int) xdpi;
	}

	public static int getScreenHeight() {
		return SCREENHEIGHT;
	}

	public static void setScreenHeight(float ydpi) {
		LbsApplication.SCREENHEIGHT = (int) ydpi;
	}

	public static double getScreenDPI() {
		return SCREENDPI;
	}

	public static void setScreenDPI(double screenDPI) {
		LbsApplication.SCREENDPI = screenDPI;
	}

	public static ActivityData getActivityData() {
		activityData = new ActivityData(getContext());
		return activityData;
	}

	public static int getRequestCode() {
		return requestCode;
	}

	public static void setRequestCode(int requestCode) {
		LbsApplication.requestCode = requestCode;
	}

	public static int getBufferQueryCode() {
		return bufferQueryCode;
	}

	public static void setBufferQueryCode(int bufferQueryCode) {
		LbsApplication.bufferQueryCode = bufferQueryCode;
	}

	public static LocationClient getLocationClient() {
		return locationClient;
	}

	public static void setLocationClient(Context context) {
		LocationClient locationClient = new LocationClient(context);
		LbsApplication.locationClient = locationClient;
	}

	public static String getQueryString() {
		return queryString;
	}

	public static void setQueryString(String queryString) {
		LbsApplication.queryString = queryString;
	}

	public static boolean isLocateStart() {
		return isLocateStart;
	}

	public static void setLocateStart(boolean isStart) {
		LbsApplication.isLocateStart = isStart;
	}

}
