package com.shnu.lbsshnu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoPoint;
import com.supermap.data.Geometrist;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapParameterChangedListener;
import com.supermap.mapping.MapView;

public class HomeActivity extends BaseActivity {
	private static final String TAG = "HomeActivity";
	private DrawPointAndBuffer drawPointAndBuffer;
	private BaiduLocationListener baiduLocationListener = new BaiduLocationListener();
	private LocationClient locationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLocationAPi();
		setContentView(R.layout.homeactivity);
		setSliderActionBar();
		drawPointAndBuffer = new DrawPointAndBuffer();
		initView();
		openData();
	}

	/*
	 * 初始化启动 定位api
	 */
	private void initLocationAPi() {
		locationClient = new LocationClient(getApplicationContext());
		locationClient.registerLocationListener(baiduLocationListener);
		LBSApplication.getLocationApi().startLocate(locationClient);

	}

	/*
	 * 初始化View
	 */
	private void initView() {
		handler = new Handler();
		locationImageView = (RelativeLayout) findViewById(R.id.locationRelativeLayout);
		locationImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLocated();
			}
		});
		accuracyTextView = (TextView) findViewById(R.id.txtAccuracy);
		addressTextView = (TextView) findViewById(R.id.txtAddress);
		setActivityRightSilder();
		setWifiLayer();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LBSApplication.refreshMap();
	}

	protected void onDestroy() {
		super.onDestroy();
		// LBSApplication.getmMapControl().getMap().close();
		// LBSApplication.getmMapControl().getMap().dispose();
		// LBSApplication.getmMapControl().dispose();
		// LBSApplication.getmWorkspace().close();
		// LBSApplication.getmWorkspace().dispose();
	}

	/*
	 * 从SDcard添加底图数据
	 */
	private void openData() {
		// 打开工作空间
		LBSApplication.setmWorkspace(new Workspace());
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(LBSApplication.getSdCard()
				+ getString(R.string.data_path));
		info.setType(WorkspaceType.SMWU);
		LBSApplication.getmWorkspace().open(info);

		LBSApplication.setmMapView((MapView) findViewById(R.id.mapView));
		LBSApplication.setmMapControl(LBSApplication.getmMapView()
				.getMapControl());

		LBSApplication.getmMapControl().getMap()
				.setWorkspace(LBSApplication.getmWorkspace());
		LBSApplication.getmMapControl().getMap()
				.setMapDPI(LBSApplication.getScreenDPI());
		String mapName = LBSApplication.getmWorkspace().getMaps().get(0);
		Log.i(TAG, "add Map: " + mapName);
		LBSApplication.getmMapControl().getMap().open(mapName);
		LBSApplication.setMlayers(LBSApplication.getmMapControl().getMap()
				.getLayers());
		lbsApplication.mWifiLayerS = LBSApplication.getMlayers().get(2);
		lbsApplication.mWifiLayerL = LBSApplication.getMlayers().get(3);
		lbsApplication.mWifiLayerL.setVisible(false);
		lbsApplication.mWifiLayerS.setVisible(false);
		LBSApplication.getmMapControl().getMap().setScale(1 / 1200);
		LBSApplication.getmMapControl().getMap().setAntialias(true);
		LBSApplication
				.getmMapControl()
				.getMap()
				.setLockedViewBounds(
						new Rectangle2D(121.412490774567, 31.1566896665659,
								121.426210646701, 31.1651384499396));
		LBSApplication.getmMapControl().getMap().setViewBoundsLocked(true);
		// 左: 121.412490774567; 上: 31.1651384499396; 右: 121.426210646701; 下:
		// 31.1566896665659; 宽: 0.01371987213399; 高: 0.00844878337370147

		LBSApplication.setmTrackingLayer(LBSApplication.getmMapControl()
				.getMap().getTrackingLayer());
		LBSApplication.getmMapControl().setMapParamChangedListener(
				mapParameterChangedListener);
		Log.i(TAG, "Max:"
				+ LBSApplication.getmMapControl().getMap().getMaxScale()
				+ " Min:"
				+ LBSApplication.getmMapControl().getMap().getMinScale()
				+ " Dpi:"
				+ LBSApplication.getmMapControl().getMap().getMapDPI());
		LBSApplication.refreshMap();
	}

	/*
	 * 监听地图参数变化
	 */
	MapParameterChangedListener mapParameterChangedListener = new MapParameterChangedListener() {

		@Override
		public void scaleChanged(double scale) {
			Log.i(TAG, "Scale:" + scale);
			// LBSApplication.getLocationApi().addAccuracyBuffer(
			// LBSApplication.getLastlocationPoint2d(), (float) scale);
			// LBSApplication.refreshMap();
		}

		@Override
		public void boundsChanged(Point2D point2d) {
			// TODO Auto-generated method stub

		}
	};

	/*
	 * 定位后操作
	 */
	private void onLocated() {

		RelativeLayout mapRelativeLayout = (RelativeLayout) findViewById(R.id.mapViewRelativeLayout);
		if (!isPopUp) {
			new GeoCoding().execute();
			accuracyTextView.setText("我的位置(精度:"
					+ LBSApplication.save2Point(LBSApplication
							.getLocationAccuracy()) + "米)");
			viewPopup(0, -LBSApplication.Dp2Px(this, 96), mapRelativeLayout);
			isPopUp = true;
		} else {
			viewPopup(-LBSApplication.Dp2Px(this, 96), 0, mapRelativeLayout);
			isPopUp = false;
		}
		Log.d(TAG, "locationPoint2d:"
				+ LBSApplication.getLastlocationPoint2d().getX() + " , "
				+ LBSApplication.getLastlocationPoint2d().getY());
	}

	/*
	 * 地图框上移
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

				if (LBSApplication.getLastlocationPoint2d() != null)
					LBSApplication.getmMapControl().getMap()
							.setCenter(LBSApplication.getLastlocationPoint2d());
				locationImageView.setEnabled(true);
			}
		});
		view.startAnimation(animation);
	}

	/*
	 * 线程绘制location point & 精度buffer
	 */
	private class DrawPointAndBuffer extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... contexts) {
			try {
				handler.post(runnableUi);
				return "DrawPointAndBuffer ok";
			} catch (Exception e) {
				Log.e(TAG, e.toString());
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.e(TAG, result);
		}

	}

	Runnable runnableUi = new Runnable() {

		@Override
		public void run() {
			try {
				LBSApplication.getLocationApi().addCallOutBall(
						LBSApplication.getLastlocationPoint2d(),
						LBSApplication.getmMapView(),
						LBSApplication.getContext());
				LBSApplication.getLocationApi().addAccuracyBuffer(
						LBSApplication.getLastlocationPoint2d(),
						LBSApplication.getLocationAccuracy());

			} catch (Exception e) {
				Log.e(TAG, e.toString());
				Log.e(TAG, "locationPoint2d:"
						+ LBSApplication.getLastlocationPoint2d().getX()
						+ " , "
						+ LBSApplication.getLastlocationPoint2d().getY()
						+ " , " + LBSApplication.getLocationAccuracy());
			}
		}
	};

	/*
	 * 位置显示 By 缓冲区查询 防止等待查询结果而未响应
	 */
	private class GeoCoding extends AsyncTask<String, Integer, String> {
		HomeActivity homeActivity = HomeActivity.this;

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			homeActivity.addressTextView.setText("正在获取中……");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			homeActivity.addressTextView.setText("上海师范大学" + result);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				Layer layer = null;
				layer = LBSApplication.getmMapControl().getMap().getLayers()
						.get(15);
				String locationAddresString = "";
				GeoPoint point = new GeoPoint(
						LBSApplication.getLastlocationPoint2d());
				// GeoPoint point = new GeoPoint(121.416781751312,
				// 31.1617360260643);
				DatasetVector datasetvector = (DatasetVector) layer
						.getDataset();
				System.out.println(layer.getName());
				Recordset recordset = datasetvector.getRecordset(false,
						CursorType.STATIC);
				for (int i = 0; i < recordset.getRecordCount(); i++) {
					if (i == 0) {
						recordset.moveFirst();
					} else {
						recordset.moveNext();
					}
					boolean isTrue = Geometrist.canContain(
							recordset.getGeometry(), point);
					if (isTrue) {
						locationAddresString = " , "
								+ recordset.getFieldValue("Name").toString();
						break;
					}
				}
				// 释放资源
				Log.i("geoCoding", locationAddresString);
				recordset.dispose();
				return locationAddresString;
			} catch (Exception e) {
				Log.e(TAG + " GeoCoding", e.toString());
				return null;
			}
		}
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
			if (LBSApplication.getLastlocationPoint2d().getX() != location
					.getLongitude()
					|| LBSApplication.getLastlocationPoint2d().getY() != location
							.getLatitude()) {
				LBSApplication.setLastlocationPoint2d(new Point2D(location
						.getLongitude(), location.getLatitude()));
				LBSApplication.setLocationAccuracy(location.getRadius());
				drawPointAndBuffer = new DrawPointAndBuffer();
				drawPointAndBuffer.execute("");
				Log.i(TAG, sb.toString());
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

	}
}
