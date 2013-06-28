package com.shnu.lbsshnu;

import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapParameterChangedListener;
import com.supermap.mapping.MapView;

public class HomeActivity extends BaseActivity {
	private static final String TAG = "HomeActivity";
	Workspace mWorkspace;
	MapView mMapView;
	LocationByBaiduAPI baiduAPI = new LocationByBaiduAPI();
	LocationClient locationClient;
	Point2D locationPoint2d;
	public BaiduLocationListener baiduLocationListener = new BaiduLocationListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeactivity);
		locationClient = new LocationClient(getApplicationContext());
		locationClient.registerLocationListener(this.baiduLocationListener);
		baiduAPI.startLocate(locationClient);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		openData();
	}

	protected void onDestroy() {
		super.onDestroy();
		mMapView.getMapControl().removeMapParamChangedListener(
				mapParameterChangedListener);
	}

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中 61 ： GPS定位结果 62 ： 扫描整合定位依据失败。此时定位结果无效。 63 ：
	 * 网络异常，没有成功向服务器发起请求。此时定位结果无效。 65 ： 定位缓存的结果。 66 ：
	 * 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果 67 ：
	 * 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
	 * 161： 表示网络定位结果 162~167： 服务端定位失败。
	 */
	public class BaiduLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			locationPoint2d = new Point2D(location.getLongitude(),
					location.getLatitude());
			if (baiduAPI.isLocInMap(locationPoint2d, mMapView)) {
				baiduAPI.addCallOutBall(locationPoint2d, mMapView,
						getApplicationContext());
			} else {
				baiduAPI.addCallOutArrow(locationPoint2d, mMapView,
						getApplicationContext());
			}
			Log.i(TAG, sb.toString());
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

	}

	/*
	 * 从SDcard添加底图数据
	 */
	private void openData() {
		// 打开工作空间
		mWorkspace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(getString(R.string.data_path));
		info.setType(WorkspaceType.SMWU);
		mWorkspace.open(info);

		mMapView = (MapView) findViewById(R.id.mapView);
		MapControl mapControl = mMapView.getMapControl();

		mapControl.getMap().setWorkspace(mWorkspace);

		String mapName = mWorkspace.getMaps().get(0);
		Log.i(TAG, "add Map: " + mapName);
		mapControl.getMap().open(mapName);
		mapControl.getMap().refresh();

		mapControl.setMapParamChangedListener(mapParameterChangedListener);
	}

	private final MapParameterChangedListener mapParameterChangedListener = new MapParameterChangedListener() {

		@Override
		public void scaleChanged(double arg0) {
			// updateLocation();
		}

		@Override
		public void boundsChanged(Point2D arg0) {
			// updateLocation();
		}
	};

}
