package com.shnu.lbsshnu;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Point2D;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapParameterChangedListener;
import com.supermap.mapping.MapView;

public class HomeActivity extends BaseActivity {
	private static final String TAG = "HomeActivity";
	private DrawPointAndBuffer drawPointAndBuffer;
	private LocationListener baiduLocationListener = new LocationListener();
	private Query queryViaSuperMap;
	private RelativeLayout mapRelativeLayout;
	private RelativeLayout locationImageView;
	private TextView accuracyTextView;
	private TextView geoCodeTextView;
	private Button detailButton;
	private ActivityClass activity;
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeactivity);
		initLocationAPi();
		drawPointAndBuffer = new DrawPointAndBuffer();
		initView();
		openData();
		LbsApplication.startServices(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.getParcelable("activity") != null) {
				activity = bundle.getParcelable("activity");
			}
			activityLocate(activity);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!simpleSideDrawer.isClosed()) {
			simpleSideDrawer.toggleRightDrawer();
		}
		if (!flagSearch) {
			actionbarView.removeAllViews();
			View.inflate(this, R.layout.actionbar, actionbarView);
			initMainBar();
		} else {
			actionbarView.removeAllViews();
			View.inflate(this, R.layout.resultbar, actionbarView);
			initResultBar("map");
		}
		if (!results.isEmpty() && !isSearch) {
			LbsApplication.getmMapView().removeAllCallOut();
			resultLocate(results);
		}
		if (hasDetail) {
			activityLocate(activity);
		}
		LbsApplication.refreshMap();
		Log.d(TAG, "on resume!");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			if (requestCode == 0) {
				if (resultCode == 0) {
					Bundle bundle = data.getExtras();
					activity = bundle.getParcelable("activity");
					hasDetail = true;
				}
			}
			if (requestCode == 1) {
				Bundle bundle = data.getExtras();
				if (results.isEmpty()) {
					results = bundle.getParcelableArrayList("results");
				}
			}
		}
	}

	/*
	 * 初始化启动 定位api
	 */
	private void initLocationAPi() {
		LbsApplication.setLocationClient(getApplicationContext());
		LbsApplication.getLocationClient().registerLocationListener(
				baiduLocationListener);
		if (LbsApplication.isNetWork() || LbsApplication.isGPSOpen()) {
			LbsApplication.getLocationApi().startLocate(
					LbsApplication.getLocationClient());
		}
	}

	/*
	 * 初始化View
	 */
	private void initView() {
		handler = new Handler();
		locationImageView = (RelativeLayout) findViewById(R.id.locationRelativeLayout);
		mapRelativeLayout = (RelativeLayout) findViewById(R.id.mapViewRelativeLayout);
		actionbarView = (LinearLayout) findViewById(R.id.actionbar);
		locationImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLocated(false);
			}
		});
		accuracyTextView = (TextView) findViewById(R.id.txtAccuracy);
		geoCodeTextView = (TextView) findViewById(R.id.txtAddress);
		detailButton = (Button) findViewById(R.id.btnDetail);
		initMainBar();
	}

	/*
	 * 从SDcard添加底图数据
	 */
	private void openData() {
		// 打开工作空间
		LbsApplication.setmWorkspace(new Workspace());
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(LbsApplication.getSdCard()
				+ getString(R.string.data_path));
		info.setType(WorkspaceType.SMWU);
		LbsApplication.getmWorkspace().open(info);

		LbsApplication.setmMapView((MapView) findViewById(R.id.mapView));
		LbsApplication.setmMapControl(LbsApplication.getmMapView()
				.getMapControl());

		LbsApplication.getmMapControl().getMap()
				.setWorkspace(LbsApplication.getmWorkspace());
		LbsApplication.getmMapControl().getMap()
				.setMapDPI(LbsApplication.getScreenDPI());
		String mapName = LbsApplication.getmWorkspace().getMaps().get(0);
		Log.i(TAG, "add Map: " + mapName);
		LbsApplication.getmMapControl().getMap().open(mapName);
		LbsApplication.setMlayers(LbsApplication.getmMapControl().getMap()
				.getLayers());
		lbsApplication.mWifiLayerS = LbsApplication.getMlayers().get(2);
		lbsApplication.mWifiLayerL = LbsApplication.getMlayers().get(3);
		lbsApplication.mWifiLayerL.setVisible(false);
		lbsApplication.mWifiLayerS.setVisible(false);
		LbsApplication.getmMapControl().getMap().setScale(1 / 1200);
		LbsApplication.getmMapControl().getMap().setAntialias(true);
		LbsApplication
				.getmMapControl()
				.getMap()
				.setLockedViewBounds(
						new Rectangle2D(121.412490774567, 31.1566896665659,
								121.426210646701, 31.1651384499396));
		LbsApplication.getmMapControl().getMap().setViewBoundsLocked(true);
		LbsApplication.setmTrackingLayer(LbsApplication.getmMapControl()
				.getMap().getTrackingLayer());
		LbsApplication.getmMapControl().setMapParamChangedListener(
				mapParameterChangedListener);
		Log.i(TAG, "Max:"
				+ LbsApplication.getmMapControl().getMap().getMaxScale()
				+ " Min:"
				+ LbsApplication.getmMapControl().getMap().getMinScale()
				+ " Dpi:"
				+ LbsApplication.getmMapControl().getMap().getMapDPI());
		LbsApplication.refreshMap();
	}

	/*
	 * 监听地图参数变化
	 */
	MapParameterChangedListener mapParameterChangedListener = new MapParameterChangedListener() {

		@Override
		public void scaleChanged(double scale) {
			Log.i(TAG, "Scale:" + scale);
			LbsApplication.refreshMap();
		}

		@Override
		public void boundsChanged(Point2D point2d) {
			LbsApplication.refreshMap();

		}
	};

	/*
	 * 定位后操作
	 */
	private void onLocated(boolean hasDetail) {

		if (!hasDetail) {
			detailButton.setVisibility(View.GONE);
		}
		if (!isPopUp) {
			if (LbsApplication.isLocateStart()) {
				new GeoCoding().execute();
				accuracyTextView.setText("我的位置(精度:"
						+ LbsApplication.save2Point(LbsApplication
								.getLocationAccuracy()) + "米)");
				geoCodeTextView.setText("上海师范大学");
				locationViewPopup(0, -LbsApplication.Dp2Px(this, 50),
						mapRelativeLayout);
				isPopUp = true;
				if (LbsApplication.getLastlocationPoint2d() != null)
					LbsApplication.getmMapControl().getMap()
							.setCenter(LbsApplication.getLastlocationPoint2d());
			} else {
				Toast.makeText(this, "请先开启定位功能，才能获取当前位置", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			LbsApplication.clearCallout();
			locationViewPopup(-LbsApplication.Dp2Px(this, 50), 0,
					mapRelativeLayout);
			isPopUp = false;
			hasDetail = false;
		}
		Log.d(TAG, "locationPoint2d:"
				+ LbsApplication.getLastlocationPoint2d().getX() + " , "
				+ LbsApplication.getLastlocationPoint2d().getY());
	}

	/*
	 * 定位详细框上移
	 */
	private void locationViewPopup(final float p1, final float p2,
			final RelativeLayout view) {
		TranslateAnimation animation = new TranslateAnimation(0, 0, p1, p2);
		animation.setInterpolator(new AnticipateOvershootInterpolator());
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
			} catch (Exception e) {
				Log.e(TAG, e.toString());
				return null;
			}
			return "DrawPointAndBuffer ok";
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
				LbsApplication.getLocationApi().drawLocationPoint(
						LbsApplication.getLastlocationPoint2d(),
						LbsApplication.getmMapView(),
						LbsApplication.getContext(),
						LbsApplication.getLocationAccuracy());
			} catch (Exception e) {
				Log.e(TAG, e.toString());
				Log.e(TAG, "locationPoint2d:"
						+ LbsApplication.getLastlocationPoint2d().getX()
						+ " , "
						+ LbsApplication.getLastlocationPoint2d().getY()
						+ " , " + LbsApplication.getLocationAccuracy());
			}
		}
	};

	/*
	 * Geocoding 防止等待查询结果而未响应
	 */
	private class GeoCoding extends AsyncTask<String, Integer, String> {
		HomeActivity homeActivity = HomeActivity.this;

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			homeActivity.geoCodeTextView.setText("正在获取中……");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			homeActivity.geoCodeTextView.setText("上海师范大学" + result);
		}

		@Override
		protected String doInBackground(String... params) {
			String queryResult = "";
			queryViaSuperMap = new Query();
			try {
				queryViaSuperMap = new Query();
				queryResult = queryViaSuperMap.geoCode();
			} catch (Exception e) {
				Log.e(TAG + " GeoCoding", e.toString());
				return null;
			}
			return queryResult;
		}
	}

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中 61 ： GPS定位结果 62 ： 扫描整合定位依据失败。此时定位结果无效。 63 ：
	 * 网络异常，没有成功向服务器发起请求。此时定位结果无效。 65 ： 定位缓存的结果。 66 ：
	 * 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果 67 ：
	 * 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
	 * 161： 表示网络定位结果 162~167： 服务端定位失败。
	 */
	public class LocationListener implements BDLocationListener {
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
			if (LbsApplication.getLastlocationPoint2d().getX() != location
					.getLongitude()
					|| LbsApplication.getLastlocationPoint2d().getY() != location
							.getLatitude()) {
				LbsApplication.setLastlocationPoint2d(new Point2D(location
						.getLongitude(), location.getLatitude()));
				LbsApplication.setLocationAccuracy(location.getRadius());
				if (!LbsApplication.getLocationApi().isLocInMap(
						LbsApplication.getLastlocationPoint2d(),
						LbsApplication.getmMapView())) {
					LbsApplication.setLocateStart(false);
					locationSwitch.setChecked(false);
				} else {
					drawPointAndBuffer = new DrawPointAndBuffer();
					drawPointAndBuffer.execute("");
				}
				Log.i(TAG, sb.toString());
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次后退键退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * 活动详情定位
	 */
	private void activityLocate(final ActivityClass activity) {
		hasDetail = false;
		Layer mLayer = null;
		mLayer = LbsApplication.getmMapControl().getMap().getLayers().get(14);
		DatasetVector mDatasetVector = (DatasetVector) mLayer.getDataset();
		try {
			QueryParameter parameter = new QueryParameter();
			parameter
					.setAttributeFilter("Id=" + activity.getActivityBuilding());
			parameter.setCursorType(CursorType.STATIC);

			Recordset mRecordset = mDatasetVector.query(parameter);
			mRecordset.moveFirst();
			Point2D mPoint2d = mRecordset.getGeometry().getInnerPoint();
			CallOut mCallOut = new CallOut(this);
			mCallOut.setStyle(CalloutAlignment.BOTTOM);
			mCallOut.setCustomize(true);
			ImageView image = new ImageView(this);
			mCallOut.setLocation(mPoint2d.getX(), mPoint2d.getY());
			switch (activity.getActivityType()) {
			case 1:
				image.setBackgroundResource(R.drawable.ic_play_pin);
				break;
			case 2:
				image.setBackgroundResource(R.drawable.ic_mic_pin);
				break;
			case 3:
				image.setBackgroundResource(R.drawable.ic_book_pin);
				break;
			default:
				break;
			}
			mCallOut.setContentView(image);
			detailButton.setVisibility(View.VISIBLE);
			if (!isPopUp) {
				LbsApplication.clearCallout();
				locationViewPopup(0, -LbsApplication.Dp2Px(this, 50),
						mapRelativeLayout);
				isPopUp = true;
			}
			if (activity.getActivityName().length() > 13) {
				accuracyTextView.setText(activity.getActivityName().substring(
						0, 12)
						+ "...");
			} else {
				accuracyTextView.setText(activity.getActivityName());
			}
			if (activity.getActivitySpeakerTitle().length() > 10) {
				geoCodeTextView.setText(activity.getActivitySpeaker() + ", "
						+ activity.getActivitySpeakerTitle().substring(0, 10)
						+ "...");
			} else {
				geoCodeTextView.setText(activity.getActivitySpeaker() + ", "
						+ activity.getActivitySpeakerTitle());
			}
			detailButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					locationViewPopup(-LbsApplication.Dp2Px(
							LbsApplication.getContext(), 50), 0,
							mapRelativeLayout);
					isPopUp = false;
					LbsApplication.clearCallout();
					LbsApplication.refreshMap();
					Intent intent = new Intent(LbsApplication.getContext(),
							ActivityListView.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable("activity", activity);
					intent.putExtras(bundle);
					startActivityForResult(intent,
							LbsApplication.getRequestCode());
					HomeActivity.this.overridePendingTransition(
							R.anim.in_right2left, R.anim.out_left2right);
				}
			});
			LbsApplication.getmMapControl().getMap().setCenter(mPoint2d);
			LbsApplication.getmMapView().addCallout(mCallOut);
			LbsApplication.refreshMap();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	public void resultLocate(List<Result> results) {
		isSearch = true;
		Layer mLayer = null;
		mLayer = LbsApplication.getmMapControl().getMap().getLayers().get(14);
		DatasetVector mDatasetVector = (DatasetVector) mLayer.getDataset();
		try {
			for (Result result : results) {
				QueryParameter parameter = new QueryParameter();
				parameter.setAttributeFilter("Id=" + result.getId());
				parameter.setCursorType(CursorType.STATIC);
				Recordset mRecordset = mDatasetVector.query(parameter);
				mRecordset.moveFirst();
				Point2D mPoint2d = mRecordset.getGeometry().getInnerPoint();
				String resultName = result.getId() + "";
				CallOut mCallOut = new CallOut(this);
				mCallOut.setStyle(CalloutAlignment.BOTTOM);
				mCallOut.setCustomize(true);
				ImageView image = new ImageView(this);
				image.setBackgroundResource(R.drawable.ic_unselect_pin);
				mCallOut.setLocation(mPoint2d.getX(), mPoint2d.getY());
				mCallOut.setContentView(image);
				LbsApplication.getmMapView().addCallout(mCallOut, resultName);
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		LbsApplication.refreshMap();
	}

}