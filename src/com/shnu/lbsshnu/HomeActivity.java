package com.shnu.lbsshnu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.supermap.data.Color;
import com.supermap.data.GeoCircle;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;

public class HomeActivity extends BaseActivity {
	private static final String TAG = "HomeActivity";
	Workspace mWorkspace;
	MapView mMapView;
	MapControl mMapControl;
	LocationByBaiduAPI baiduAPI = new LocationByBaiduAPI();
	LocationClient locationClient;
	Point2D locationPoint2d;
	Point2D lastlocationPoint2d = new Point2D(0, 0);
	public BaiduLocationListener baiduLocationListener = new BaiduLocationListener();
	TrackingLayer mTrackingLayer;
	ImageView locationImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeactivity);
		locationClient = new LocationClient(getApplicationContext());
		locationClient.registerLocationListener(this.baiduLocationListener);
		baiduAPI.startLocate(locationClient);
		locationImageView = (ImageView) findViewById(R.id.actionLociton);
		locationImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLocated();
			}
		});

		openData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshMap();
	}

	protected void onDestroy() {
		super.onDestroy();
		mMapControl.getMap().close();
		mMapControl.getMap().dispose();
		mMapControl.dispose();
		mWorkspace.close();
		mWorkspace.dispose();
	}

	/**
	 * ��������������λ�õ�ʱ�򣬸�ʽ�����ַ������������Ļ�� 61 �� GPS��λ��� 62 �� ɨ�����϶�λ����ʧ�ܡ���ʱ��λ�����Ч�� 63 ��
	 * �����쳣��û�гɹ���������������󡣴�ʱ��λ�����Ч�� 65 �� ��λ����Ľ���� 66 ��
	 * ���߶�λ�����ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ�� 67 ��
	 * ���߶�λʧ�ܡ�ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ�� 68 �� ��������ʧ��ʱ�����ұ������߶�λʱ��Ӧ�ķ��ؽ��
	 * 161�� ��ʾ���綨λ��� 162~167�� ����˶�λʧ�ܡ�
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
			if (lastlocationPoint2d != locationPoint2d) {
				addAccuracyBuffer(locationPoint2d, location.getRadius());
				lastlocationPoint2d = locationPoint2d;
			}
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
	 * ��SDcard��ӵ�ͼ����
	 */
	private void openData() {
		// �򿪹����ռ�
		mWorkspace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(getString(R.string.data_path));
		info.setType(WorkspaceType.SMWU);
		mWorkspace.open(info);

		mMapView = (MapView) findViewById(R.id.mapView);
		mMapControl = mMapView.getMapControl();

		mMapControl.getMap().setWorkspace(mWorkspace);

		String mapName = mWorkspace.getMaps().get(0);
		Log.i(TAG, "add Map: " + mapName);
		mMapControl.getMap().open(mapName);
		mMapControl.getMap().setScale(1 / 1200);
		refreshMap();
		mTrackingLayer = mMapControl.getMap().getTrackingLayer();
	}

	/*
	 * ˢ�µ�ͼ
	 */
	private void refreshMap() {
		mMapControl.getMap().refresh();
	}

	/*
	 * �����һ�εĶ�λbuffer
	 */
	private void clearTrackingLayer() {
		mTrackingLayer.clear();
		refreshMap();
	}

	/*
	 * ���Ӷ�λ����buffer �뾶=����*��ͼscale*0.02
	 */
	public void addAccuracyBuffer(Point2D location, float radius) {
		clearTrackingLayer();
		double mapScale = mMapControl.getMap().getScale();
		GeoCircle accuracyBuffer = new GeoCircle(location, radius * mapScale
				* 0.02);
		GeoStyle geoStyle_R = new GeoStyle();
		geoStyle_R.setFillOpaqueRate(10);
		geoStyle_R.setLineSymbolID(0);
		geoStyle_R.setLineWidth(0.5);
		geoStyle_R.setLineColor(new Color(0, 153, 204));
		accuracyBuffer.setStyle(geoStyle_R);
		mTrackingLayer.add(accuracyBuffer, "");
		refreshMap();
	}

	/*
	 * ��λ�����
	 */
	private void onLocated() {
		if (lastlocationPoint2d != null)
			mMapControl.getMap().setCenter(lastlocationPoint2d);
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mapViewRelativeLayout);
		LayoutParams linearParams = (LayoutParams) relativeLayout
				.getLayoutParams();
		linearParams.height = LBSApplication.getScreenHeight();
		relativeLayout.setLayoutParams(linearParams);

	}
}
