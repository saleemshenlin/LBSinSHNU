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

/**
 * 
 * ��LbsApplication<br>
 * ���ڴ洢ȫ�ֱ����ͷ���
 * 
 */
public class LbsApplication extends Application {
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾLBSApplication
	 */
	private final static String TAG = "LBSApplication";
	/**
	 * ����һ������,���ڱ�ʾ��Ļ���
	 */
	private static int SCREENWIDTH;
	/**
	 * ����һ������,���ڱ�ʾ��Ļ�߶�
	 */
	private static int SCREENHEIGHT;
	/**
	 * ����һ������,���ڱ�ʾDPI
	 */
	private static double SCREENDPI;
	/**
	 * ����һ������,���ڱ�ʾ������
	 */
	private static Context CONTEXT;
	/**
	 * ����һ������,���ڱ�ʾ��λ����
	 */
	private static float LOCATIONACCUCRACY;
	/**
	 * ����һ������,���ڱ�ʾ��λ�Ƿ���
	 */
	private static boolean isLocateStart = false;
	/**
	 * ����һ������,���ڱ�ʾEvent�Ƿ񱻹�ע
	 */
	private static boolean isEventLike = false;
	/**
	 * ����һ������,���ڱ�ʾ��ѯ����
	 */
	private static String queryString = "";
	/**
	 * ����һ������,������ʾ�Ƿ����ڻ�������ѯ
	 */
	public static boolean isQueryViaLocation = false;
	/**
	 * ����һ������,���ڱ�ʾRequestCode������Event�ڵ�ͼ�϶�λ
	 */
	public static int GET_EVENT = 0;
	/**
	 * ����һ������,���ڱ�ʾRequestCode������ ��ѯ����ڵ�ͼ�϶�λ
	 */
	public static int GET_QUERY = 1;
	/**
	 * ʵ��һ��LOCATIONAPI,����ʼ��
	 */
	private static LocationAPI mLocationAPI = new LocationAPI();
	/**
	 * ʵ��һ��Point2D,���ڱ�ʾ���¶�λ�ص�
	 */
	private static Point2D mPoint2d;
	/**
	 * ʵ��һ��Workspace
	 */
	private static Workspace mWorkspace;
	/**
	 * ʵ��һ��MapView
	 */
	private static MapView mMapView;
	/**
	 * ʵ��һ��MapControl
	 */
	private static MapControl mMapControl;
	/**
	 * ʵ��һ��TrackingLayer
	 */
	private static TrackingLayer mTrackingLayer;
	/**
	 * ʵ��һ��Layers
	 */
	private static Layers mlayers;
	/**
	 * ʵ��һ��mEventData
	 */
	private static EventData mEventData;
	/**
	 * ʵ��һ��LocationClient
	 */
	private static LocationClient locationClient;
	/**
	 * ʵ��һ��Layer,��ʾС������wifi��
	 */
	Layer mWifiLayerS;
	/**
	 * ʵ��һ��Layer,��ʾ�������wifi��
	 */
	Layer mWifiLayerL;

	/**
	 * ����LbsApplication<br>
	 * 1)��ȡ������,��ֵ��CONTEXT<br>
	 * 2)��ȡ��Ļ�ֱ���<br>
	 * 3)��ʼ��SuperMap����<br>
	 * 4)��ʼ��mPoint2d��LOCATIONACCUCRACY<br>
	 * 
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CONTEXT = getApplicationContext();
		Log.i(TAG, "LBSApplication onCreate!");
		getScreenDesplay();
		initEnvironment();
		Log.i(TAG, "LBSApplication getScreenDisplay height:" + SCREENHEIGHT);
		LbsApplication.mPoint2d = new Point2D();
		LbsApplication.LOCATIONACCUCRACY = (float) 10;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		Log.e(TAG, "LBSApplication onTerminate");
	}

	/**
	 * ����Dpת����
	 * 
	 * @param context
	 *            ������
	 * @param dp
	 *            DIP
	 * @return int PX
	 */
	public static int Dp2Px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * �����ж��Ƿ�����
	 * 
	 * @return boolean �Ƿ����
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

	/**
	 * �����ж�GPS�Ƿ��
	 * 
	 * @return boolean �Ƿ����
	 */
	public static boolean isGPSOpen() {
		LocationManager locationManager = ((LocationManager) getContext()
				.getSystemService(Context.LOCATION_SERVICE));
		boolean isOpen = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return isOpen;
	}

	/**
	 * ����ˢ�µ�ͼ
	 */
	public static void refreshMap() {
		if (mMapControl != null) {
			mMapControl.getMap().refresh();
		}

	}

	/**
	 * ����������ٲ�
	 */
	public static void clearTrackingLayer() {
		if (mTrackingLayer != null) {
			mTrackingLayer.clear();
		}
		refreshMap();
	}

	/**
	 * ����������ע
	 */
	public static void clearCallout() {
		if (mMapView != null) {
			mMapView.removeAllCallOut();
		}
	}

	/**
	 * ���ڱ���2λС��
	 * 
	 * @param value
	 *            ԭʼֵ
	 * @return String ����2λС�����ַ���
	 */
	public static String save2Point(float value) {
		DecimalFormat df = new java.text.DecimalFormat("#.00");
		return df.format(value) + "";
	}

	/**
	 * ���ڿ�������
	 * 
	 * @param context
	 *            ������
	 */
	public static void startServices(Context context) {
		if (!LbsService.isRunFlag()) {
			context.startService(new Intent(getContext(), LbsService.class));
		}
	}

	/**
	 * ���������������
	 * 
	 * @param activity
	 *            ��ǰActivity
	 */
	@SuppressWarnings("static-access")
	public static void hideIme(Activity activity) {
		if (activity == null)
			return;
		final View v = activity.getWindow().peekDecorView();
		if (v != null && v.getWindowToken() != null) {
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(activity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	/**
	 * �����ж���������Ƿ��
	 * 
	 * @param context
	 *            ������
	 * @return boolean �Ƿ��
	 */
	@SuppressWarnings("static-access")
	public static boolean isImeShow(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(context.INPUT_METHOD_SERVICE);
		return imm.isActive();
	}

	public static LocationAPI getLocationApi() {
		return mLocationAPI;
	}

	public static void setLocationApi(LocationAPI locationApi) {
		LbsApplication.mLocationAPI = locationApi;
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
		return mPoint2d;
	}

	public static void setLastlocationPoint2d(Point2D point) {
		LbsApplication.mPoint2d = point;
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

	public static EventData getEventData() {
		mEventData = new EventData(getContext());
		return mEventData;
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

	public static boolean isActivityLike() {
		return isEventLike;
	}

	public static void setActivityLike(boolean isActivityLike) {
		LbsApplication.isEventLike = isActivityLike;
	}

	/**
	 * ���ڻ�ȡ��Ļ�ֱ���
	 */
	private void getScreenDesplay() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		setScreenWidth(dm.widthPixels);
		setScreenHeight(dm.heightPixels);
		setScreenDPI(dm.densityDpi);
	}

	/**
	 * ��������SuperMap����
	 */
	private void initEnvironment() {
		Environment.setLicensePath(LbsApplication.getContext()
				.getExternalFilesDir(getString(R.string.license_path))
				.toString());
		Environment.setTemporaryPath(LbsApplication.getContext()
				.getExternalFilesDir(getString(R.string.temp_path)).toString());
		Environment
				.setWebCacheDirectory(LbsApplication.getContext()
						.getExternalFilesDir(getString(R.string.cache_path))
						.toString());
		Environment.initialization(LbsApplication.getContext());
	}

}
