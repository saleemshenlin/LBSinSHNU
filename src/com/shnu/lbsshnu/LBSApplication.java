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
 * 类LbsApplication<br>
 * 用于存储全局变量和方法
 * 
 */
public class LbsApplication extends Application {
	/**
	 * 定义一个标签,在LogCat内表示LBSApplication
	 */
	private final static String TAG = "LBSApplication";
	/**
	 * 定义一个常数,用于表示屏幕宽度
	 */
	private static int SCREENWIDTH;
	/**
	 * 定义一个常数,用于表示屏幕高度
	 */
	private static int SCREENHEIGHT;
	/**
	 * 定义一个常数,用于表示DPI
	 */
	private static double SCREENDPI;
	/**
	 * 定义一个常量,用于表示上下文
	 */
	private static Context CONTEXT;
	/**
	 * 定义一个常数,用于表示定位精度
	 */
	private static float LOCATIONACCUCRACY;
	/**
	 * 定义一个常量,用于表示定位是否开启
	 */
	private static boolean isLocateStart = false;
	/**
	 * 定义一个常量,用于表示Event是否被关注
	 */
	private static boolean isEventLike = false;
	/**
	 * 定义一个常量,用于表示查询内容
	 */
	private static String queryString = "";
	/**
	 * 定义一个常量,用来表示是否属于缓冲区查询
	 */
	public static boolean isQueryViaLocation = false;
	/**
	 * 定义一个常数,用于表示RequestCode，代表Event在地图上定位
	 */
	public static int GET_EVENT = 0;
	/**
	 * 定义一个常数,用于表示RequestCode，代表 查询结果在地图上定位
	 */
	public static int GET_QUERY = 1;
	/**
	 * 实例一个LOCATIONAPI,并初始化
	 */
	private static LocationAPI mLocationAPI = new LocationAPI();
	/**
	 * 实例一个Point2D,用于表示最新定位地点
	 */
	private static Point2D mPoint2d;
	/**
	 * 实例一个Workspace
	 */
	private static Workspace mWorkspace;
	/**
	 * 实例一个MapView
	 */
	private static MapView mMapView;
	/**
	 * 实例一个MapControl
	 */
	private static MapControl mMapControl;
	/**
	 * 实例一个TrackingLayer
	 */
	private static TrackingLayer mTrackingLayer;
	/**
	 * 实例一个Layers
	 */
	private static Layers mlayers;
	/**
	 * 实例一个mEventData
	 */
	private static EventData mEventData;
	/**
	 * 实例一个LocationClient
	 */
	private static LocationClient locationClient;
	/**
	 * 实例一个Layer,表示小比例尺wifi层
	 */
	Layer mWifiLayerS;
	/**
	 * 实例一个Layer,表示大比例尺wifi层
	 */
	Layer mWifiLayerL;

	/**
	 * 创建LbsApplication<br>
	 * 1)获取上下文,赋值个CONTEXT<br>
	 * 2)获取屏幕分辨率<br>
	 * 3)初始化SuperMap环境<br>
	 * 4)初始化mPoint2d和LOCATIONACCUCRACY<br>
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
	 * 用于Dp转像素
	 * 
	 * @param context
	 *            上下文
	 * @param dp
	 *            DIP
	 * @return int PX
	 */
	public static int Dp2Px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * 用于判断是否联网
	 * 
	 * @return boolean 是否存在
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

	/**
	 * 用于判断GPS是否打开
	 * 
	 * @return boolean 是否存在
	 */
	public static boolean isGPSOpen() {
		LocationManager locationManager = ((LocationManager) getContext()
				.getSystemService(Context.LOCATION_SERVICE));
		boolean isOpen = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return isOpen;
	}

	/**
	 * 用于刷新地图
	 */
	public static void refreshMap() {
		if (mMapControl != null) {
			mMapControl.getMap().refresh();
		}

	}

	/**
	 * 用于清除跟踪层
	 */
	public static void clearTrackingLayer() {
		if (mTrackingLayer != null) {
			mTrackingLayer.clear();
		}
		refreshMap();
	}

	/**
	 * 用于清除点标注
	 */
	public static void clearCallout() {
		if (mMapView != null) {
			mMapView.removeAllCallOut();
		}
	}

	/**
	 * 用于保留2位小数
	 * 
	 * @param value
	 *            原始值
	 * @return String 保留2位小数的字符串
	 */
	public static String save2Point(float value) {
		DecimalFormat df = new java.text.DecimalFormat("#.00");
		return df.format(value) + "";
	}

	/**
	 * 用于开启服务
	 * 
	 * @param context
	 *            上下文
	 */
	public static void startServices(Context context) {
		if (!LbsService.isRunFlag()) {
			context.startService(new Intent(getContext(), LbsService.class));
		}
	}

	/**
	 * 用于隐藏虚拟键盘
	 * 
	 * @param activity
	 *            当前Activity
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
	 * 用于判断虚拟键盘是否打开
	 * 
	 * @param context
	 *            上下文
	 * @return boolean 是否打开
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
	 * 用于获取屏幕分别率
	 */
	private void getScreenDesplay() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		setScreenWidth(dm.widthPixels);
		setScreenHeight(dm.heightPixels);
		setScreenDPI(dm.densityDpi);
	}

	/**
	 * 用于设置SuperMap环境
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
