package com.shnu.lbsshnu;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.MapParameterChangedListener;
import com.supermap.mapping.MapView;

/**
 * 类HomeActivity<br>
 * 是应用的主界面，以地图为主体，用于将Event信息和查询结果信息在地图上显示。
 */
public class HomeActivity extends BaseActivity {
	/**
	 * 定义一个标签,在LogCat内表示HomeActivity
	 */
	private static final String TAG = "HomeActivity";
	/*
	 * 定义一个常数,用于记录“再点一次退出应用”两次点击的时间
	 */
	private long exitTime = 0;
	/**
	 * 实例一个mHandler
	 */
	private Handler mHandler;
	/**
	 * 实例一个mDrawPointAndBuffer
	 */
	private DrawPointAndBuffer mDrawPointAndBuffer;
	/**
	 * 实例一个LocationListener,并初始化
	 */
	LocationListener mLocationListener = new LocationListener();
	/**
	 * 实例一个TextView,用于显示定位精度
	 */
	private TextView txtAaccuracy;
	/**
	 * 实例一个TextView,用于显示反向地理编码结果
	 */
	private TextView txtGeocode;
	/**
	 * 实例一个Button,用于点击进入Event详细页
	 */
	private Button btnDetail;
	/**
	 * 实例一个ProgressBar,用于显示地图读取时显示进度条
	 */
	private ProgressBar prbMapLoad;

	/**
	 * 创建HomeActivity<br>
	 * 1)调用initLocationAPi(),初始化LocationAPI;<br>
	 * 2)调用initView(),初始化View;<br>
	 * 3)异步加载地图;<br>
	 * 4)开启LbsService;
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeactivity_view);
		initLocationAPI();
		initView();
		new initMapData().execute();
		LbsApplication.startServices(this);
	}

	/**
	 * 外部进入HomeActivity<br>
	 * 1)判断Slider是否已打开，若打开则关闭<br>
	 * 2)判断是否是查询状态，如果不是初始化MainActionbar，反之初始化ResultActionbar<br>
	 * 3)判断是否要进行查询结果定位,如果需要调用locateResultInMap()<br>
	 * 4)判断是否要Event定位,如果需要调用locateEventInMap()<br>
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (!mSimpleSideDrawer.isClosed()) {
			mSimpleSideDrawer.toggleRightDrawer();
		}
		if (!isSearch) {
			lnlMainActionbar.removeAllViews();
			View.inflate(this, R.layout.main_actionbar, lnlMainActionbar);
			rllLocation.setVisibility(View.VISIBLE);
			initActionbarAndSlider();
		} else {
			lnlMainActionbar.removeAllViews();
			View.inflate(this, R.layout.query_result_actionbar,
					lnlMainActionbar);
			rllLocation.setVisibility(View.GONE);
			initResultActionbar(true);
		}
		if (!events.isEmpty() && isSearch) {
			LbsApplication.getmMapView().removeAllCallOut();
			locateResultInMap(events);
		}
		if (hasDetail) {
			locateEventInMap(mEvent);
		}
		LbsApplication.refreshMap();
		Log.d(TAG, "on resume!");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 用于接收startActivityForResult的反馈<br>
	 * 根据RequestCode (GET_EVENT/GET_QUERY),处理反馈结果<br>
	 * GET_EVENT:调用设置hasDetail为true;<br>
	 * GET_QUERY:为调用locateResultInMap()做准备;
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			if (requestCode == LbsApplication.GET_EVENT) {
				if (resultCode == LbsApplication.GET_EVENT) {
					Bundle bundle = data.getExtras();
					mEvent = bundle.getParcelable("activity");
					hasDetail = true;
				}
			}
			if (requestCode == LbsApplication.GET_QUERY) {
				Bundle bundle = data.getExtras();
				if (events.isEmpty()) {
					events = bundle.getParcelableArrayList("results");
				}
			}
		}
	}

	/**
	 * 用于"再按一次后退键退出程序"<br>
	 * 当按两次back键两次按下的时间>2s,退出应用<br>
	 */
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

	/**
	 * 用于初始化启动 LocationAPI<br>
	 * 判断网络和GPS是否打开，若打开则开启定位功能
	 */
	private void initLocationAPI() {
		LbsApplication.setLocationClient(getApplicationContext());
		LbsApplication.getLocationClient().registerLocationListener(
				mLocationListener);
		if (LbsApplication.isNetWork() || LbsApplication.isGPSOpen()) {
			LbsApplication.getLocationApi().startLocate(
					LbsApplication.getLocationClient());
		}
	}

	/**
	 * 用于初始化View
	 */
	private void initView() {
		mHandler = new Handler();
		rllLocation = (RelativeLayout) findViewById(R.id.rllLocation);
		rllMapView = (RelativeLayout) findViewById(R.id.rllMapView);
		lnlMainActionbar = (LinearLayout) findViewById(R.id.lnlActionbar);
		prbMapLoad = (ProgressBar) findViewById(R.id.prbLoadMap);
		rllLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLocated();
			}
		});
		txtAaccuracy = (TextView) findViewById(R.id.txtLocationAccuracy);
		txtGeocode = (TextView) findViewById(R.id.txtLocationAddress);
		btnDetail = (Button) findViewById(R.id.btnDetail);
		initActionbarAndSlider();
	}

	/**
	 * 用于点击定位按钮进行定位和反向地理编码的查询，移动LocationDetail显示内容<br>
	 * 任何触发上移LocationDetail,此方法下移LocationDetail 具体方法如下:<br>
	 * 1)在定位时，不显示详细按钮<br>
	 * 2)判读LocationDetail是否popup<br>
	 * 3)如果处于关闭状态,则打开异步进行GeoCoding(),上移LocationDetail; <br>
	 * 4)如果处于打开状态,则清空地图Callout,下移LocationDetail<br>
	 */
	private void onLocated() {
		btnDetail.setVisibility(View.GONE);
		if (!isPopUp) {
			if (LbsApplication.isLocateStart()) {
				new InitGeoCoding().execute();
				txtAaccuracy.setText("我的位置(精度:"
						+ LbsApplication.save2Point(LbsApplication
								.getLocationAccuracy()) + "米)");
				txtGeocode.setText("上海师范大学");
				mQuery.moveLocationDetail(0, -LbsApplication.Dp2Px(this, 50),
						rllMapView, rllLocation);
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
			mQuery.moveLocationDetail(-LbsApplication.Dp2Px(this, 50), 0,
					rllMapView, rllLocation);
			isPopUp = false;
			hasDetail = false;
		}
		Log.d(TAG, "locationPoint2d:"
				+ LbsApplication.getLastlocationPoint2d().getX() + " , "
				+ LbsApplication.getLastlocationPoint2d().getY());
	}

	/**
	 * 用于将根据Event所发生的位置在地图上定位，并在LocationDetail里显示Event的具体信息<br>
	 * 具体方法如下:<br>
	 * 1)清空Callout;<br>
	 * 2)通过Query.getCallOutViaBuildingId()获取CallOut;<br>
	 * 3)上移LocationDetail,显示Event内容,超出字数限制用"..."代替;<br>
	 * 4)设置btnDetail的onClick事件;<br>
	 * 5)显示Callout并居中
	 * 
	 * @param mEvent
	 *            传入Event参数
	 */
	private void locateEventInMap(final Event mEvent) {
		hasDetail = false;
		LbsApplication.clearCallout();
		CallOut mCallOut = mQuery.getCallOutViaBuildingId(
				mEvent.getEventBuilding(), mEvent.getEventType(), this);
		if (mCallOut != null) {
			Point2D mPoint2d = new Point2D(mCallOut.getLocationX(),
					mCallOut.getLocationY());
			btnDetail.setVisibility(View.VISIBLE);
			if (!isPopUp) {
				mQuery.moveLocationDetail(0, -LbsApplication.Dp2Px(this, 50),
						rllMapView, rllLocation);
				isPopUp = true;
			}
			if (mEvent.getEventName().length() > 13) {
				txtAaccuracy.setText(mEvent.getEventName().substring(0, 12)
						+ "...");
			} else {
				txtAaccuracy.setText(mEvent.getEventName());
			}
			if (mEvent.getEventSpeakerTitle().length() > 10) {
				txtGeocode.setText(mEvent.getEventSpeaker() + ", "
						+ mEvent.getEventSpeakerTitle().substring(0, 10)
						+ "...");
			} else {
				txtGeocode.setText(mEvent.getEventSpeaker() + ", "
						+ mEvent.getEventSpeakerTitle());
			}
			btnDetail.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(LbsApplication.getContext(),
							EventListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable("activity", mEvent);
					intent.putExtras(bundle);
					startActivityForResult(intent, LbsApplication.GET_EVENT);
					HomeActivity.this.overridePendingTransition(
							R.anim.anim_in_right2left,
							R.anim.anim_out_left2right);
				}
			});
			LbsApplication.getmMapControl().getMap().setCenter(mPoint2d);
			LbsApplication.getmMapView().addCallout(mCallOut);
			LbsApplication.refreshMap();
		}

	}

	/**
	 * 用于将查询结果在地图上定位<br>
	 * 具体方法如下:<br>
	 * 1)遍历整个List<Event>; <br>
	 * 2)调用Query.getCallOutViaBuildingId(),获取Callout; <br>
	 * 3)显示Callout并居中
	 * 
	 * @param events
	 *            传入查询结果list
	 */
	private void locateResultInMap(List<Event> events) {
		isSearch = true;
		LbsApplication.clearCallout();
		try {
			for (Event event : events) {
				CallOut mCallOut = mQuery.getCallOutViaBuildingId(
						event.getEventBuilding(), 4, this);
				LbsApplication.getmMapView().addCallout(mCallOut);
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		Point2D mPoint2d = LbsApplication.getLastlocationPoint2d();
		LbsApplication.getmMapControl().getMap().setCenter(mPoint2d);
		LbsApplication.refreshMap();
	}

	/**
	 * 类LocationListener<br>
	 * 用于监听，又新位置的时候，格式化成字符串输出到屏幕中<br>
	 * 61 ： GPS定位结果 <br>
	 * 62 ： 扫描整合定位依据失败。此时定位结果无效。<br>
	 * 63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。 <br>
	 * 65 ： 定位缓存的结果。 <br>
	 * 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果<br>
	 * 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果 <br>
	 * 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果<br>
	 * 161： 表示网络定位结果<br>
	 * 162~167： 服务端定位失败<br>
	 * 1)通过定位API获取经纬度坐标和定位精度<br>
	 * 2)更新LastlocationPoint2d和LocationAccuracy<br>
	 * 3)更新完执行DrawPointAndBuffer通过定位API获取经纬度坐标和定位精度<br>
	 * 4)更新LastlocationPoint2d和LocationAccuracy<br>
	 * 5)调用LocationAPI.isLocInMap如果不在地图范围内，则自动关闭定位<br>
	 * 6)更新完执行DrawPointAndBuffer
	 * 
	 */
	private class LocationListener implements BDLocationListener {
		/**
		 * 用于获取经纬度坐标
		 */
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer mStringBuffer = new StringBuffer(256);
			mStringBuffer.append("time : ");
			mStringBuffer.append(location.getTime());
			mStringBuffer.append("\nerror code : ");
			mStringBuffer.append(location.getLocType());
			mStringBuffer.append("\nlatitude : ");
			mStringBuffer.append(location.getLatitude());
			mStringBuffer.append("\nlontitude : ");
			mStringBuffer.append(location.getLongitude());
			mStringBuffer.append("\nradius : ");
			mStringBuffer.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				mStringBuffer.append("\nspeed : ");
				mStringBuffer.append(location.getSpeed());
				mStringBuffer.append("\nsatellite : ");
				mStringBuffer.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				mStringBuffer.append("\naddr : ");
				mStringBuffer.append(location.getAddrStr());
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
					swtLocation.setChecked(false);
				} else {
					mDrawPointAndBuffer = new DrawPointAndBuffer();
					mDrawPointAndBuffer.execute("");
				}
				Log.i(TAG, mStringBuffer.toString());
			}
		}

		/**
		 * 用于在线进行反向地理编码(无实用)
		 */
		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

	}

	/**
	 * 类DrawPointAndBuffer<br>
	 * 
	 * 用于多线程绘制定位点和定位精度缓冲区 <br>
	 * 1)调用LocationAPI.drawLocation()方法绘制<br>
	 * 2)使用runnable进行UI交互<br>
	 */
	private class DrawPointAndBuffer extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... contexts) {
			try {
				mHandler.post(mRunnable);
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
			Log.d(TAG, result);
		}

	}

	/**
	 * 类OpenMapData<br>
	 * 用于避免屏幕黑屏,多线程加载离线地图，<br>
	 * 1)加载地图,并显示提示"正在努力加载地图中..."和加载进度条<br>
	 * 2)加载完后,判断是否从Widget有Event要定位,如果有调用locateEventInMap()执行定位<br>
	 */
	private class initMapData extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// 从Widget定位入口进入时，在加载完地图时再执行定位操作
			Bundle mBundle = getIntent().getExtras();
			if (mBundle != null) {
				if (mBundle.getParcelable("activity") != null) {
					mEvent = mBundle.getParcelable("activity");
					if (mEvent != null)
						locateEventInMap(mEvent);
				}

			}
			prbMapLoad.setVisibility(View.GONE);
			Toast.makeText(HomeActivity.this, result, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			Toast.makeText(HomeActivity.this, "正在努力加载地图中...",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected String doInBackground(String... params) {
			publishProgress(1);
			LbsApplication.setmWorkspace(new Workspace());
			WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
			info.setServer(LbsApplication
					.getContext()
					.getExternalFilesDir(
							getString(R.string.data_path)
									+ getString(R.string.data_name)).toString());
			info.setType(WorkspaceType.SMWU);
			LbsApplication.getmWorkspace().open(info);
			LbsApplication.setmMapView((MapView) findViewById(R.id.mapView));
			LbsApplication.setmMapControl(LbsApplication.getmMapView()
					.getMapControl());

			LbsApplication.getmMapControl().getMap()
					.setWorkspace(LbsApplication.getmWorkspace());
			LbsApplication.getmMapControl().getMap()
					.setMapDPI(LbsApplication.getScreenDPI());
			Log.d(TAG, "add Map: " + LbsApplication.getmWorkspace());
			String strMapName = LbsApplication.getmWorkspace().getMaps().get(0);
			Log.d(TAG, "add Map: " + strMapName);
			LbsApplication.getmMapControl().getMap().open(strMapName);
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
					mMapParameterChangedListener);
			Log.i(TAG, "Max:"
					+ LbsApplication.getmMapControl().getMap().getMaxScale()
					+ " Min:"
					+ LbsApplication.getmMapControl().getMap().getMinScale()
					+ " Dpi:"
					+ LbsApplication.getmMapControl().getMap().getMapDPI());
			LbsApplication.refreshMap();
			return "地图加载完毕,开始体验";
		}

	}

	/**
	 * 类InitGeoCoding<br>
	 * 用于多线程查询反向地理编码，防止因查询延迟和LocationDetail框popup发生卡顿<br>
	 * 调用Query.geoCode()实现;
	 */
	private class InitGeoCoding extends AsyncTask<String, Integer, String> {
		HomeActivity homeActivity = HomeActivity.this;

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			homeActivity.txtGeocode.setText("正在获取中……");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			homeActivity.txtGeocode.setText("上海师范大学" + result);
		}

		@Override
		protected String doInBackground(String... params) {
			publishProgress(1);
			String strResult = "";
			try {
				strResult = mQuery.geoCode();
			} catch (Exception e) {
				Log.e(TAG + " GeoCoding", e.toString());
				return null;
			}
			return strResult;
		}
	}

	/**
	 * 定义一个Runnable,用于UI交互,绘制
	 */
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				LbsApplication.getLocationApi().drawLocation(
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

	/**
	 * 定义一个MapParameterChangedListener,用于监听地图变化
	 */
	MapParameterChangedListener mMapParameterChangedListener = new MapParameterChangedListener() {

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

}