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

	/**
	 * ��������������λ�õ�ʱ�򣬸�ʽ�����ַ������������Ļ�� 61 �� GPS��λ��� 62 �� ɨ�����϶�λ����ʧ�ܡ���ʱ��λ�����Ч�� 63 ��
	 * �����쳣��û�гɹ���������������󡣴�ʱ��λ�����Ч�� 65 �� ��λ����Ľ���� 66 ��
	 * ���߶�λ�����ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ�� 67 ��
	 * ���߶�λʧ�ܡ�ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ�� 68 �� ��������ʧ��ʱ�����ұ������߶�λʱ��Ӧ�ķ��ؽ��
	 * 161�� ��ʾ���綨λ��� 162~167�� ����˶�λʧ�ܡ�
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
	 * ���߳�����λ�÷��񣬷�ֹ����ʱ�����������Ӧ
	 */
	class GetLocation extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... contexts) {
			getLocationApi().startLocate(locationClient);
			return null;
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
