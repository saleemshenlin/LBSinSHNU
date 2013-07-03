package com.shnu.lbsshnu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.supermap.mapping.MapParameterChangedListener;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;

public class HomeActivity extends BaseActivity {
	private static final String TAG = "HomeActivity";
	Workspace mWorkspace;
	MapView mMapView;
	MapControl mMapControl;
	LocationByBaiduAPI baiduAPI = new LocationByBaiduAPI();
	BaiduLocationListener baiduLocationListener = new BaiduLocationListener();
	LocationClient locationClient;
	Point2D locationPoint2d;
	Point2D lastlocationPoint2d = new Point2D(0, 0);
	TrackingLayer mTrackingLayer;
	RelativeLayout locationImageView;
	TextView accuracyTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeactivity);
		new GetLocation().execute("");		
		locationClient = new LocationClient(getApplicationContext());
		locationClient.registerLocationListener(baiduLocationListener);
		setSliderActionBar();
		locationImageView = (RelativeLayout) findViewById(R.id.locationRelativeLayout);
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
				if (baiduAPI.isLocInMap(locationPoint2d, mMapView)) {
					baiduAPI.addCallOutBall(locationPoint2d, mMapView,
							getApplicationContext());
				} else {
					baiduAPI.addCallOutArrow(locationPoint2d, mMapView,
							getApplicationContext());
				}
				accuracyTextView = (TextView) findViewById(R.id.txtAccuracy);
				accuracyTextView.setText("�ҵ�λ��(���ȣ�"
						+ LBSApplication.twoDecimal(location.getRadius())
						+ "��)");
			}
			

			// Log.i(TAG, sb.toString());
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
		info.setServer(LBSApplication.sDcard + getString(R.string.data_path));
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
		mMapControl.setMapParamChangedListener(mapParameterChangedListener);
	}
	/*
	 * ������ͼ�����仯
	 */
	MapParameterChangedListener mapParameterChangedListener = new MapParameterChangedListener() {
		
		@Override
		public void scaleChanged(double scale) {
			addAccuracyBuffer(lastlocationPoint2d,(float)scale);
		}
		
		@Override
		public void boundsChanged(Point2D point2d) {
			// TODO Auto-generated method stub
			
		}
	};

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
	 * ���Ӷ�λ����buffer �뾶=����(��λ����)*��ͼscale*0.01
	 */
	public void addAccuracyBuffer(Point2D location, float radius) {
		clearTrackingLayer();
		double mapScale = mMapControl.getMap().getScale();
		GeoCircle accuracyBuffer = new GeoCircle(location, radius * mapScale
				* 0.01);
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

		RelativeLayout mapRelativeLayout = (RelativeLayout) findViewById(R.id.mapViewRelativeLayout);
		if (!isPopUp) {
			viewPopup(0, -LBSApplication.Dp2Px(this, 96), mapRelativeLayout);
			isPopUp = true;
		} else {
			viewPopup(-LBSApplication.Dp2Px(this, 96), 0, mapRelativeLayout);
			isPopUp = false;
		}

	}

	/*
	 * ��ͼ������
	 */
	private void viewPopup(final float p1, final float p2,
			final RelativeLayout view) {
		TranslateAnimation animation = new TranslateAnimation(0, 0, p1, p2);
		// animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setDuration(500);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				Log.i(TAG,
						"Left: " + view.getLeft() + "Top: " + view.getTop()
								+ "Right: " + view.getRight() + "Bottom: "
								+ view.getBottom());
				locationImageView.setEnabled(false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
				view.clearAnimation();
				view.getLayoutParams().height = view.getBottom()
						+ (int) (p2 - p1);
				view.setVisibility(View.VISIBLE);
				Log.i(TAG,
						"Left: " + view.getLeft() + "Top: " + view.getTop()
								+ "Right: " + view.getRight() + "Bottom: "
								+ view.getBottom());

				if (lastlocationPoint2d != null)
					mMapControl.getMap().setCenter(lastlocationPoint2d);
				locationImageView.setEnabled(true);
			}
		});
		view.startAnimation(animation);
	}
	
	/*
	 * ���߳�����λ�÷��񣬷�ֹ����ʱ�����������Ӧ
	 */
	class GetLocation extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... contexts) {
			baiduAPI.startLocate(locationClient);
			return null;
		}

	}

}
