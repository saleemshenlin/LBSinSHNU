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
 * ��HomeActivity<br>
 * ��Ӧ�õ������棬�Ե�ͼΪ���壬���ڽ�Event��Ϣ�Ͳ�ѯ�����Ϣ�ڵ�ͼ����ʾ��
 */
public class HomeActivity extends BaseActivity {
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾHomeActivity
	 */
	private static final String TAG = "HomeActivity";
	/*
	 * ����һ������,���ڼ�¼���ٵ�һ���˳�Ӧ�á����ε����ʱ��
	 */
	private long exitTime = 0;
	/**
	 * ʵ��һ��mHandler
	 */
	private Handler mHandler;
	/**
	 * ʵ��һ��mDrawPointAndBuffer
	 */
	private DrawPointAndBuffer mDrawPointAndBuffer;
	/**
	 * ʵ��һ��LocationListener,����ʼ��
	 */
	LocationListener mLocationListener = new LocationListener();
	/**
	 * ʵ��һ��TextView,������ʾ��λ����
	 */
	private TextView txtAaccuracy;
	/**
	 * ʵ��һ��TextView,������ʾ������������
	 */
	private TextView txtGeocode;
	/**
	 * ʵ��һ��Button,���ڵ������Event��ϸҳ
	 */
	private Button btnDetail;
	/**
	 * ʵ��һ��ProgressBar,������ʾ��ͼ��ȡʱ��ʾ������
	 */
	private ProgressBar prbMapLoad;

	/**
	 * ����HomeActivity<br>
	 * 1)����initLocationAPi(),��ʼ��LocationAPI;<br>
	 * 2)����initView(),��ʼ��View;<br>
	 * 3)�첽���ص�ͼ;<br>
	 * 4)����LbsService;
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
	 * �ⲿ����HomeActivity<br>
	 * 1)�ж�Slider�Ƿ��Ѵ򿪣�������ر�<br>
	 * 2)�ж��Ƿ��ǲ�ѯ״̬��������ǳ�ʼ��MainActionbar����֮��ʼ��ResultActionbar<br>
	 * 3)�ж��Ƿ�Ҫ���в�ѯ�����λ,�����Ҫ����locateResultInMap()<br>
	 * 4)�ж��Ƿ�ҪEvent��λ,�����Ҫ����locateEventInMap()<br>
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
	 * ���ڽ���startActivityForResult�ķ���<br>
	 * ����RequestCode (GET_EVENT/GET_QUERY),���������<br>
	 * GET_EVENT:��������hasDetailΪtrue;<br>
	 * GET_QUERY:Ϊ����locateResultInMap()��׼��;
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
	 * ����"�ٰ�һ�κ��˼��˳�����"<br>
	 * ��������back�����ΰ��µ�ʱ��>2s,�˳�Ӧ��<br>
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ�κ��˼��˳�����",
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
	 * ���ڳ�ʼ������ LocationAPI<br>
	 * �ж������GPS�Ƿ�򿪣�����������λ����
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
	 * ���ڳ�ʼ��View
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
	 * ���ڵ����λ��ť���ж�λ�ͷ���������Ĳ�ѯ���ƶ�LocationDetail��ʾ����<br>
	 * �κδ�������LocationDetail,�˷�������LocationDetail ���巽������:<br>
	 * 1)�ڶ�λʱ������ʾ��ϸ��ť<br>
	 * 2)�ж�LocationDetail�Ƿ�popup<br>
	 * 3)������ڹر�״̬,����첽����GeoCoding(),����LocationDetail; <br>
	 * 4)������ڴ�״̬,����յ�ͼCallout,����LocationDetail<br>
	 */
	private void onLocated() {
		btnDetail.setVisibility(View.GONE);
		if (!isPopUp) {
			if (LbsApplication.isLocateStart()) {
				new InitGeoCoding().execute();
				txtAaccuracy.setText("�ҵ�λ��(����:"
						+ LbsApplication.save2Point(LbsApplication
								.getLocationAccuracy()) + "��)");
				txtGeocode.setText("�Ϻ�ʦ����ѧ");
				mQuery.moveLocationDetail(0, -LbsApplication.Dp2Px(this, 50),
						rllMapView, rllLocation);
				isPopUp = true;
				if (LbsApplication.getLastlocationPoint2d() != null)
					LbsApplication.getmMapControl().getMap()
							.setCenter(LbsApplication.getLastlocationPoint2d());
			} else {
				Toast.makeText(this, "���ȿ�����λ���ܣ����ܻ�ȡ��ǰλ��", Toast.LENGTH_SHORT)
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
	 * ���ڽ�����Event��������λ���ڵ�ͼ�϶�λ������LocationDetail����ʾEvent�ľ�����Ϣ<br>
	 * ���巽������:<br>
	 * 1)���Callout;<br>
	 * 2)ͨ��Query.getCallOutViaBuildingId()��ȡCallOut;<br>
	 * 3)����LocationDetail,��ʾEvent����,��������������"..."����;<br>
	 * 4)����btnDetail��onClick�¼�;<br>
	 * 5)��ʾCallout������
	 * 
	 * @param mEvent
	 *            ����Event����
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
	 * ���ڽ���ѯ����ڵ�ͼ�϶�λ<br>
	 * ���巽������:<br>
	 * 1)��������List<Event>; <br>
	 * 2)����Query.getCallOutViaBuildingId(),��ȡCallout; <br>
	 * 3)��ʾCallout������
	 * 
	 * @param events
	 *            �����ѯ���list
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
	 * ��LocationListener<br>
	 * ���ڼ���������λ�õ�ʱ�򣬸�ʽ�����ַ����������Ļ��<br>
	 * 61 �� GPS��λ��� <br>
	 * 62 �� ɨ�����϶�λ����ʧ�ܡ���ʱ��λ�����Ч��<br>
	 * 63 �� �����쳣��û�гɹ���������������󡣴�ʱ��λ�����Ч�� <br>
	 * 65 �� ��λ����Ľ���� <br>
	 * 66 �� ���߶�λ�����ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��<br>
	 * 67 �� ���߶�λʧ�ܡ�ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ�� <br>
	 * 68 �� ��������ʧ��ʱ�����ұ������߶�λʱ��Ӧ�ķ��ؽ��<br>
	 * 161�� ��ʾ���綨λ���<br>
	 * 162~167�� ����˶�λʧ��<br>
	 * 1)ͨ����λAPI��ȡ��γ������Ͷ�λ����<br>
	 * 2)����LastlocationPoint2d��LocationAccuracy<br>
	 * 3)������ִ��DrawPointAndBufferͨ����λAPI��ȡ��γ������Ͷ�λ����<br>
	 * 4)����LastlocationPoint2d��LocationAccuracy<br>
	 * 5)����LocationAPI.isLocInMap������ڵ�ͼ��Χ�ڣ����Զ��رն�λ<br>
	 * 6)������ִ��DrawPointAndBuffer
	 * 
	 */
	private class LocationListener implements BDLocationListener {
		/**
		 * ���ڻ�ȡ��γ������
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
		 * �������߽��з���������(��ʵ��)
		 */
		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

	}

	/**
	 * ��DrawPointAndBuffer<br>
	 * 
	 * ���ڶ��̻߳��ƶ�λ��Ͷ�λ���Ȼ����� <br>
	 * 1)����LocationAPI.drawLocation()��������<br>
	 * 2)ʹ��runnable����UI����<br>
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
	 * ��OpenMapData<br>
	 * ���ڱ�����Ļ����,���̼߳������ߵ�ͼ��<br>
	 * 1)���ص�ͼ,����ʾ��ʾ"����Ŭ�����ص�ͼ��..."�ͼ��ؽ�����<br>
	 * 2)�������,�ж��Ƿ��Widget��EventҪ��λ,����е���locateEventInMap()ִ�ж�λ<br>
	 */
	private class initMapData extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// ��Widget��λ��ڽ���ʱ���ڼ������ͼʱ��ִ�ж�λ����
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
			Toast.makeText(HomeActivity.this, "����Ŭ�����ص�ͼ��...",
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
			return "��ͼ�������,��ʼ����";
		}

	}

	/**
	 * ��InitGeoCoding<br>
	 * ���ڶ��̲߳�ѯ���������룬��ֹ���ѯ�ӳٺ�LocationDetail��popup��������<br>
	 * ����Query.geoCode()ʵ��;
	 */
	private class InitGeoCoding extends AsyncTask<String, Integer, String> {
		HomeActivity homeActivity = HomeActivity.this;

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			homeActivity.txtGeocode.setText("���ڻ�ȡ�С���");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			homeActivity.txtGeocode.setText("�Ϻ�ʦ����ѧ" + result);
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
	 * ����һ��Runnable,����UI����,����
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
	 * ����һ��MapParameterChangedListener,���ڼ�����ͼ�仯
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