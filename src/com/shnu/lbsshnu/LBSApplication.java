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
	private static int screenWidth;
	private static int screenHeight;
	private static double screenDPI;
	private static String sDcard;
	private static Context context;
	private static LocationByBaiduAPI locationApi = new LocationByBaiduAPI();
	private static Point2D lastlocationPoint2d;
	private static float locationAccuracy;
	private static Workspace mWorkspace;
	private static MapView mMapView;
	private static MapControl mMapControl;
	private static TrackingLayer mTrackingLayer;
	private static Layers mlayers;
	Layer mWifiLayerS;// С������wifi��
	Layer mWifiLayerL;// �������wifi��

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setEnvironment();
		context = getApplicationContext();
		Log.i(TAG, "LBSApplication onCreate!");
		getScreenDesplay();
		Log.i(TAG, "LBSApplication getScreenDisplay height:" + screenHeight);
		LBSApplication.lastlocationPoint2d = new Point2D();
		LBSApplication.locationAccuracy = (float) 10;
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
		setScreenDPI(dm.densityDpi);
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

	public static double getScreenDPI() {
		return screenDPI;
	}

	public static void setScreenDPI(double screenDPI) {
		LBSApplication.screenDPI = screenDPI;
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
		ConnectivityManager cwjManager = (ConnectivityManager) context
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
	 * ˢ�µ�ͼ
	 */
	public static void refreshMap() {
		mMapControl.getMap().refresh();
	}

	/*
	 * �����һ�εĶ�λbuffer
	 */
	public static void clearTrackingLayer() {
		mTrackingLayer.clear();
		refreshMap();
	}

	/*
	 * ����2λС��
	 */
	public static String save2Point(float value) {
		DecimalFormat df = new java.text.DecimalFormat("#0.00");
		return df.format(value);
	}

	public static LocationByBaiduAPI getLocationApi() {
		return locationApi;
	}

	public static void setLocationApi(LocationByBaiduAPI locationApi) {
		LBSApplication.locationApi = locationApi;
	}

	public static Layers getMlayers() {
		return mlayers;
	}

	public static void setMlayers(Layers mlayers) {
		LBSApplication.mlayers = mlayers;
	}

	public static Context getContext() {
		return context;
	}

	public static String getSdCard() {
		return sDcard;
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
		return lastlocationPoint2d;
	}

	public static void setLastlocationPoint2d(Point2D point) {
		LBSApplication.lastlocationPoint2d = point;
	}

	public static float getLocationAccuracy() {
		return locationAccuracy;
	}

	public static void setLocationAccuracy(float value) {
		LBSApplication.locationAccuracy = value;
	}

}
