package com.shnu.lbsshnu;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
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
	private static int screenWidth;
	private static int screenHeight;
	private static String sDcard;
	private static Context context;

	private static Point2D lastlocationPoint2d;
	private static float locationAccuracy;
	private static LocationByBaiduAPI locationApi = new LocationByBaiduAPI();
	private BaiduLocationListener baiduLocationListener = new BaiduLocationListener();
	private LocationClient locationClient;
	private static Workspace mWorkspace;
	private static MapView mMapView;
	private static MapControl mMapControl;
	private static TrackingLayer mTrackingLayer;
	private static Layers mlayers;
	public static boolean isChange;//
	Layer mWifiLayerS;// 小比例尺wifi层
	Layer mWifiLayerL;// 大比例尺wifi层

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
		LBSApplication.locationAccuracy = (float) 0;
		locationClient = new LocationClient(getApplicationContext());
		locationClient.registerLocationListener(baiduLocationListener);
		new GetLocation().execute("");
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
		ConnectivityManager cwjManager = (ConnectivityManager) context
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
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中 61 ： GPS定位结果 62 ： 扫描整合定位依据失败。此时定位结果无效。 63 ：
	 * 网络异常，没有成功向服务器发起请求。此时定位结果无效。 65 ： 定位缓存的结果。 66 ：
	 * 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果 67 ：
	 * 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
	 * 161： 表示网络定位结果 162~167： 服务端定位失败。
	 */
	private class BaiduLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			// StringBuffer sb = new StringBuffer(256);
			// sb.append("time : ");
			// sb.append(location.getTime());
			// sb.append("\nerror code : ");
			// sb.append(location.getLocType());
			// sb.append("\nlatitude : ");
			// sb.append(location.getLatitude());
			// sb.append("\nlontitude : ");
			// sb.append(location.getLongitude());
			// sb.append("\nradius : ");
			// sb.append(location.getRadius());
			// if (location.getLocType() == BDLocation.TypeGpsLocation) {
			// sb.append("\nspeed : ");
			// sb.append(location.getSpeed());
			// sb.append("\nsatellite : ");
			// sb.append(location.getSatelliteNumber());
			// } else if (location.getLocType() ==
			// BDLocation.TypeNetWorkLocation) {
			// sb.append("\naddr : ");
			// sb.append(location.getAddrStr());
			// }
			if (LBSApplication.lastlocationPoint2d.getX() != location
					.getLongitude()
					|| LBSApplication.lastlocationPoint2d.getY() != location
							.getLatitude()) {
				LBSApplication.lastlocationPoint2d = new Point2D(
						location.getLongitude(), location.getLatitude());
			}
			if (LBSApplication.getLocationAccuracy() != location.getRadius())
				LBSApplication.setLocationAccuracy(location.getRadius());
			// Log.i(TAG, sb.toString());
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

	}

	/*
	 * 多线程请求位置服务，防止请求时间过长而无相应
	 */
	class GetLocation extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... contexts) {
			getLocationApi().startLocate(locationClient);
			return null;
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

	public static float getLocationAccuracy() {
		return locationAccuracy;
	}

	public static void setLocationAccuracy(float value) {
		LBSApplication.locationAccuracy = value;
	}
}
