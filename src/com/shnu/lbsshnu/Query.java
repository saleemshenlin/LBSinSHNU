package com.shnu.lbsshnu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supermap.analyst.BufferAnalystGeometry;
import com.supermap.analyst.BufferAnalystParameter;
import com.supermap.analyst.BufferEndType;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.Geometrist;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Layer;

/**
 * ��Query<br>
 * �����ڸ����йص�ͼ��Event�Ĳ�ѯ
 */
public class Query {
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾEventData
	 */
	private static final String TAG = "Query";
	/**
	 * ���ڻ�������ѯʱ����ʾ�뾶 100��
	 */
	private static final int QUERY_RADIUS = 100;
	/**
	 * ����ʵ�����ӿ�OnFragmengToActivityListener
	 */
	public OnFragmengToActivityListener onFragmengToActivityListener;
	/**
	 * ����ʵ������EventProvider
	 */
	private EventProvider mEventProvider = new EventProvider();
	/**
	 * ����ʵ������Event
	 */
	private Event mEvent = null;

	/**
	 * ����HomeActivity�еķ����������ѯ<br>
	 * ����ʵ�ַ�����<br>
	 * 1)��ȡ��ǰ����<br>
	 * 2)����ͼ��ͨ��Geometrist.canContain�ж��Ƿ������ǰλ�� <br>
	 * 3)���ؽ��λ�õ�Name�ֶ���Ϣ
	 * 
	 * @return String ���ؽ��λ�õ�Name�ֶ���Ϣ
	 */
	public String geoCode() {
		Layer mlayer = null;
		mlayer = LbsApplication.getmMapControl().getMap().getLayers().get(15);
		String strAddress = "";
		GeoPoint mGeoPoint = new GeoPoint(
				LbsApplication.getLastlocationPoint2d());
		DatasetVector mDatasetVector = (DatasetVector) mlayer.getDataset();
		Recordset mRecordset = mDatasetVector.getRecordset(false,
				CursorType.STATIC);
		for (int i = 0; i < mRecordset.getRecordCount(); i++) {
			if (i == 0) {
				mRecordset.moveFirst();
			} else {
				mRecordset.moveNext();
			}
			boolean isTrue = Geometrist.canContain(mRecordset.getGeometry(),
					mGeoPoint);
			if (isTrue) {
				strAddress = " , "
						+ mRecordset.getFieldValue("Name").toString();
				break;
			}
		}
		// �ͷ���Դ
		Log.i(TAG, strAddress);
		mRecordset.dispose();
		mGeoPoint.dispose();
		return strAddress;
	}

	/**
	 * ���ڲ�ѯ�������ڵĵĽ��<br>
	 * ����ʵ�ַ�����<br>
	 * 1)��ȡ������ GeoRegion geoRegion<br>
	 * 2)����ͼ�㣬ͨ��Geometrist.canContain����������Ա�<br>
	 * 3)��������뵱ǰλ�õľ����С�������д�ŵ�List<Place><br>
	 * 4)����List<Place><br>
	 * 
	 * @param geoRegion
	 *            �Ѿ����ƺõĻ�����
	 * @return List<Place> ��������ѯ���
	 */
	public List<Place> queryViaBuffer(GeoRegion geoRegion) {
		List<Place> locationPlaces = new ArrayList<Place>();
		Layer mlayer = null;
		mlayer = LbsApplication.getmMapControl().getMap().getLayers().get(14);
		try {
			DatasetVector mDatasetVector = (DatasetVector) mlayer.getDataset();
			Recordset mRecordset = mDatasetVector.getRecordset(false,
					CursorType.STATIC);
			for (int i = 0; i < mRecordset.getRecordCount(); i++) {
				if (i == 0) {
					mRecordset.moveFirst();
				} else {
					mRecordset.moveNext();
				}
				boolean isTrue = Geometrist.canContain(geoRegion,
						mRecordset.getGeometry());
				if (isTrue) {
					int buildingNum = mRecordset.getInt32("id");
					String buildingName = mRecordset.getFieldValue("Name")
							.toString();
					float distancef = (float) Geometrist.distance(
							mRecordset.getGeometry(),
							new GeoPoint(LbsApplication
									.getLastlocationPoint2d()));
					String distance = LbsApplication
							.save2Point(distancef * 100000);
					Place placeItem = new Place(buildingNum, buildingName,
							distance);
					locationPlaces.add(placeItem);
				}
			}
			mRecordset.dispose();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		Collections.sort(locationPlaces);
		return locationPlaces;
	}

	/**
	 * ���ڳ�ʼ������У԰���CallOut<br>
	 * ����ʵ�ַ�����<br>
	 * 1)����ͼ��,��ȡ���Geometry<br>
	 * 2)��ÿһ���㸳��Callout,�����onClick�¼�,��ʹ���ߵ��CallOut�ܹ�����ͼƬ<br>
	 * 
	 * @param context
	 *            ������
	 */
	public void initPhotoCallout(final Context context) {
		final DatasetVector mDatasetVector = (DatasetVector) LbsApplication
				.getmMapControl().getMap().getLayers().get(16).getDataset();
		try {
			Recordset mRecordset = mDatasetVector.getRecordset(false,
					CursorType.STATIC);
			mRecordset.moveFirst();
			for (int i = 0; i < mRecordset.getRecordCount(); i++) {
				Point2D mPoint2d = mRecordset.getGeometry().getInnerPoint();
				final int id = mRecordset.getInt16("PhotoId");
				CallOut mCallOut = new CallOut(context);
				mCallOut.setStyle(CalloutAlignment.BOTTOM);
				mCallOut.setCustomize(true);
				ImageView image = new ImageView(context);
				image.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d(TAG, "img onClick!");
						initPhotoPopupWindow(context, id);
					}
				});
				image.setBackgroundResource(R.drawable.ic_pin_photo);
				mCallOut.setLocation(mPoint2d.getX(), mPoint2d.getY());
				mCallOut.setContentView(image);
				LbsApplication.getmMapView().addCallout(mCallOut, id + "");
				mRecordset.moveNext();
			}
			mRecordset.dispose();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	/**
	 * ����ͨ��LocationDetail�������ƶ�,��ʾ������<br>
	 * ����ʵ�ַ�����<br>
	 * 1)ͨ������Ĳ���ʹ�� ʵ��һ��TranslateAnimation<br>
	 * 2)����TranslateAnimation����ΪAnticipateOvershootInterpolator()<br>
	 * 3)����TranslateAnimation������ʱ��Ϊ500ms<br>
	 * 4)����TranslateAnimation��������<br>
	 * 4)ִ��TranslateAnimation<br>
	 * 
	 * @param fromY
	 *            �ƶ�ǰλ��
	 * @param toY
	 *            �ƶ���λ��
	 * @param mapView
	 *            ��Ҫ�ƶ���view,��������ָ��ͼչʾRelativeLayout
	 * @param locate
	 *            ���ƶ�λ��ťRelativeLayout rllLocation���ƶ������в��ܵ��
	 */
	public void moveLocationDetail(final float fromY, final float toY,
			final RelativeLayout mapView, final RelativeLayout locate) {
		TranslateAnimation animation = new TranslateAnimation(0, 0, fromY, toY);
		// �ƶ�����
		animation.setInterpolator(new AnticipateOvershootInterpolator());
		animation.setDuration(500);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				locate.setEnabled(false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mapView.setVisibility(View.GONE);
				mapView.clearAnimation();
				mapView.getLayoutParams().height = mapView.getBottom()
						+ (int) (toY - fromY);
				mapView.setVisibility(View.VISIBLE);
				locate.setEnabled(true);
			}
		});
		mapView.startAnimation(animation);
	}

	/**
	 * ���ڳ�ʼ��Event�ڵ�ͼ�϶�λ��CallOut<br>
	 * ����ʵ�ַ�����<br>
	 * 1)���ݴ���BuildingId,������ͼ,��ȡ��Ӧ��Geometry<br>
	 * 2)���ݵ��Geometry,��ʼ��CallOut<br>
	 * 3)���ݴ����Type,��ÿ��CallOut���ϲ�ͬ��ͼƬ<br>
	 * 4)����CallOut<br>
	 * 
	 * @param BuildingId
	 *            Event�ķ���λ��
	 * @param Type
	 *            Event������
	 * @param context
	 *            ������
	 * @return Callout ����Callout
	 */
	public CallOut getCallOutViaBuildingId(int BuildingId, int Type,
			Context context) {
		CallOut mCallOut = new CallOut(context);
		;
		Layer mLayer = null;
		mLayer = LbsApplication.getmMapControl().getMap().getLayers().get(14);
		DatasetVector mDatasetVector = (DatasetVector) mLayer.getDataset();
		try {
			QueryParameter parameter = new QueryParameter();
			parameter.setAttributeFilter("Id=" + BuildingId);
			parameter.setCursorType(CursorType.STATIC);

			Recordset mRecordset = mDatasetVector.query(parameter);
			mRecordset.moveFirst();
			Point2D mPoint2d = mRecordset.getGeometry().getInnerPoint();
			mCallOut.setStyle(CalloutAlignment.BOTTOM);
			mCallOut.setCustomize(true);
			ImageView mImageView = new ImageView(context);
			mCallOut.setLocation(mPoint2d.getX(), mPoint2d.getY());
			switch (Type) {
			case 1:
				mImageView.setBackgroundResource(R.drawable.ic_pin_play);
				break;
			case 2:
				mImageView.setBackgroundResource(R.drawable.ic_pin_speech);
				break;
			case 3:
				mImageView.setBackgroundResource(R.drawable.ic_pin_course);
				break;
			case 4:
				mImageView.setBackgroundResource(R.drawable.ic_pin_info);
			default:
				break;
			}
			mCallOut.setContentView(mImageView);
			mRecordset.dispose();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return mCallOut;
	}

	/**
	 * ���ڻ��ƻ�����<br>
	 * ���巽������:<br>
	 * 1)����λ�úͰ뾶QUERY_RADIUS���ƻ�����<br>
	 * 2)���ػ�����<br>
	 * 
	 * @return GeoRegion ������
	 */
	public GeoRegion getQueryBuffer() {
		GeoRegion mGeoRegion = new GeoRegion();
		try {
			BufferAnalystParameter bufferAnalystParam = new BufferAnalystParameter();
			bufferAnalystParam.setEndType(BufferEndType.ROUND);
			bufferAnalystParam.setLeftDistance(QUERY_RADIUS);
			bufferAnalystParam.setRightDistance(QUERY_RADIUS);
			Geometry mGeometry = new GeoPoint(
					LbsApplication.getLastlocationPoint2d());
			PrjCoordSys mPrjCoordSys = LbsApplication.getmMapControl().getMap()
					.getPrjCoordSys();
			mGeoRegion = BufferAnalystGeometry.createBuffer(mGeometry,
					bufferAnalystParam, mPrjCoordSys);
		} catch (Exception e) {
			Log.e("addQueryBuffer", e.toString());
		}
		return mGeoRegion;
	}

	/**
	 * �������ò�ѯ����������<br>
	 * ��Event����ʱ����������� <br>
	 * 
	 * @return String ��������
	 */
	public String getSortOrder() {
		String strSQL = null;
		strSQL = EventData.C_DATE + " ASC";
		return strSQL;
	}

	/**
	 * ����Event���ݲ�ѯ�������ò�ѯ����<br>
	 * ���� 0 ѧ������ 1 ��Ӱ�ݳ� 2 ��Ʒ�γ� 3 �ҹ�ע��<br>
	 * ʱ������Ϊһ��,�Ӳ�ѯ���쿪ʼ��<br>
	 * 
	 * @param intIndex
	 *            ��ѯ����
	 * @return String ��ѯ����
	 */
	public String getSectionViaType(int intIndex) {
		String strSQL;
		switch (intIndex) {
		case 0:
			strSQL = EventData.C_DATE + " > (SELECT DATE('now')) and "
					+ EventData.C_DATE
					+ " < (SELECT DATE('now', '+7 day')) and "
					+ EventData.C_TIME + " > (SELECT TIME('now')) and "
					+ EventData.C_TYPE + " = 2";
			return strSQL;
		case 1:
			strSQL = EventData.C_DATE + " > (SELECT DATE('now')) and "
					+ EventData.C_DATE
					+ " < (SELECT DATE('now', '+7 day')) and "
					+ EventData.C_TIME + " > (SELECT TIME('now')) and "
					+ EventData.C_TYPE + " = 1";
			return strSQL;
		case 2:
			strSQL = EventData.C_DATE + " > (SELECT DATE('now')) and "
					+ EventData.C_DATE
					+ " < (SELECT DATE('now', '+7 day')) and "
					+ EventData.C_TIME + " > (SELECT TIME('now')) and "
					+ EventData.C_TYPE + " = 3";
			return strSQL;
		case 3:
			strSQL = EventData.C_DATE + " > (SELECT DATE('now')) and "
					+ EventData.C_ISLIKE + " = 1";
			return strSQL;
		default:
			return null;
		}
	}

	/**
	 * ���������ڻ�������ѯʱ�Ĳ�ѯ����<br>
	 * ���ݴ����mPlace(��������ѯ�Ľ��)��strQuery(�û����������)���в�ѯ<br>
	 * ʱ������Ϊһ��,�Ӳ�ѯ���쿪ʼ��<br>
	 * 
	 * @param mPlace
	 *            ��������ѯ�Ľ��
	 * @param strQuery
	 *            �û����������
	 * @return String ��ѯ����
	 */
	public String getQuerySection(Place mPlace, String strQuery) {
		String strSQL = null;
		strSQL = EventData.C_BUILDING + " = " + mPlace.intBuildingNum + " and ";
		strSQL = strSQL + EventData.C_DATE + " > (SELECT DATE('now')) and "
				+ EventData.C_DATE + " < (SELECT DATE('now', '+7 day')) and ";
		strSQL = strSQL + "( " + EventData.C_NAME + " Like '%" + strQuery
				+ "%' OR " + EventData.C_SPEAKER + " Like '%" + strQuery
				+ "%' OR " + EventData.C_SPEAKERTITLE + " Like '%" + strQuery
				+ "%' OR " + EventData.C_DESCRIPTION + " Like '%" + strQuery
				+ "%' )";
		return strSQL;
	}

	/**
	 * ���������ڻ�������ѯʱ�Ĳ�ѯ����<br>
	 * ���ݴ����num(Event���)��strQuery(�û����������)���в�ѯ<br>
	 * ʱ������Ϊһ��,�Ӳ�ѯ���쿪ʼ��<br>
	 * 
	 * @param num
	 *            ��ѯ���
	 * @param strQuery
	 *            �û����������
	 * @return String ��ѯ����
	 */
	public String getQuerySection(int intNum, String strQuery) {
		String sql = null;
		if (intNum == 0) {
			sql = EventData.C_ISLIKE + " = 1";
		} else {
			sql = EventData.C_TYPE + " = " + intNum + " and ";
			sql = sql + EventData.C_DATE + " > (SELECT DATE('now')) and "
					+ EventData.C_DATE
					+ " < (SELECT DATE('now', '+7 day')) and ";
			sql = sql + "( " + EventData.C_NAME + " Like '%" + strQuery
					+ "%' OR " + EventData.C_SPEAKER + " Like '%" + strQuery
					+ "%' OR " + EventData.C_SPEAKERTITLE + " Like '%"
					+ strQuery + "%' OR " + EventData.C_DESCRIPTION
					+ " Like '%" + strQuery + "%' )";
		}
		return sql;
	}

	/**
	 * ���ڳ�ʼ��EventDetail��PopupWindow<br>
	 * ���巽������:<br>
	 * 1)���ȳ�ʼ��һ��PopupWindow,���Ϊ��Ļ��3/4,�߶�����Ӧ<br>
	 * 2)ͨ��bindPopupData����������<br>
	 * 3)���ñ��������ڵ����Χ�رյ���PopupWindow<br>
	 * 4)����λ��ˮƽ��ֱ����<br>
	 * 5)���ó�����ʧ����<br>
	 * 6)����PopupWindow<br>
	 * 
	 * @param context
	 *            ������
	 * @param rootView
	 *            ��ʾPopupWindow�ĸ�view
	 * @param id
	 *            Event��Id
	 * @param hasImgMapView
	 *            �Ƿ���Ҫ��ʼ��Event��λ��ť
	 */
	@SuppressWarnings({ "static-access" })
	public void initPopupwindows(Context context, View rootView, final long id,
			boolean hasImgMapView) {
		try {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.detail_popup, null);
			final PopupWindow popupWindow = new PopupWindow(view,
					LbsApplication.getScreenWidth() * 3 / 4,
					LayoutParams.WRAP_CONTENT, true);
			bindPopupData(id, view, popupWindow, hasImgMapView, context);
			ColorDrawable dw = new ColorDrawable(-00000);
			popupWindow.setBackgroundDrawable(dw);
			popupWindow.showAtLocation(rootView, Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.setAnimationStyle(R.style.popupAnimation);
			popupWindow.update();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	/**
	 * �ӿ�OnFragmengToActivityListener<br>
	 * ����EventListFragment��EventListView�佻������<br>
	 * ��ʹ������EventListFragment�еĵ����λ����ʱ<br>
	 * �ܹ���Event����Ϣ���ݸ�EventListView�������մ��ݸ�HomeActivity���ж�λ��<br>
	 */
	public interface OnFragmengToActivityListener {
		public void onEventLocated(Event mEvent);
	}

	/**
	 * ��Place<br>
	 * ���ڱ�ʾ��������ѯ���ʵ����,���ͼ��������ƥ��<br>
	 * ������<br>
	 * intBuildingNum ��ѯ���λ�õ�id��<br>
	 * strBuildingName ��ѯ���λ�õ����ơ�<br>
	 * strDistance ��ѯ����뵱ǰλ�õľ��롣<br>
	 */
	class Place implements Comparable<Place> {
		/**
		 * ���ڱ�ʾ��ѯ���λ�õ�id��
		 */
		int intBuildingNum;
		/**
		 * ���ڱ�ʾ��ѯ���λ�õ����ơ�
		 */
		String strBuildingName;
		/**
		 * ���ڱ�ʾ��ѯ����뵱ǰλ�õľ��롣
		 */
		String strDistance;

		Place(int buildingNum, String buildingName, String distance) {
			this.setStrBuildingName(buildingName);
			this.setIntBuildingNum(buildingNum);
			this.strDistance = distance;
		}

		/**
		 * ���ڲ�ѯ��������뵱ǰλ�õľ��������
		 */
		@Override
		public int compareTo(Place place) {
			// TODO Auto-generated method stub
			return this.strDistance.compareTo(place.strDistance);
		}

		public void setIntBuildingNum(int intBuildingNum) {
			this.intBuildingNum = intBuildingNum;
		}

		public void setStrBuildingName(String strBuildingName) {
			this.strBuildingName = strBuildingName;
		}
	}

	/**
	 * ���ڳ�ʼ������У԰��PopupWindow<br>
	 * ���巽������:<br>
	 * 1)���ȳ�ʼ��һ��PopupWindow,��ȡ��߶�����Ӧ<br>
	 * 2)ͨ�������idƥ����Ӧ��Ƭ<br>
	 * 3)���ñ��������ڵ����Χ�رյ���PopupWindow<br>
	 * 4)����λ��ˮƽ��ֱ����<br>
	 * 5)���ó�����ʧ����<br>
	 * 6)����PopupWindow<br>
	 * 
	 * @param context
	 *            ������
	 * @param id
	 *            Photo��id
	 */
	@SuppressWarnings("static-access")
	private void initPhotoPopupWindow(Context context, int id) {
		try {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.photo_popup, null);
			View rootView = layoutInflater.inflate(R.layout.homeactivity_view,
					null);
			final PopupWindow popupWindow = new PopupWindow(view,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			LinearLayout linearLayout = (LinearLayout) view
					.findViewById(R.id.lnlPhotoPopup);
			switch (id) {
			case 1:
				linearLayout.setBackgroundResource(R.drawable.img_photo_one);
				break;
			case 2:
				linearLayout.setBackgroundResource(R.drawable.img_photo_two);
				break;
			case 3:
				linearLayout.setBackgroundResource(R.drawable.img_photo_three);
				break;
			case 4:
				linearLayout.setBackgroundResource(R.drawable.img_photo_four);
				break;
			case 5:
				linearLayout.setBackgroundResource(R.drawable.img_photo_five);
				break;
			}
			ColorDrawable dw = new ColorDrawable(-00000);
			popupWindow.setBackgroundDrawable(dw);
			popupWindow.showAtLocation(rootView, Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.setAnimationStyle(R.style.popupAnimation);
			popupWindow.update();
		} catch (Exception e) {
			Log.e("imgPopup", e.toString());
		}
	}

	/**
	 * ���ڸ�initPopupWindow()�е�PopupWindow������ ���巽������:<br>
	 * 1)��ʼ��PopupWindow�еĸ���View<br>
	 * 2)����hasImgMapView�Ƿ���Ҫ��ʼ��ImgMapView<br>
	 * 3)����id��ѯ��ȡ��ʵ����һ��Event<br>
	 * 4)������View������<br>
	 * 5)����likeImageView��onClick�¼�,���ڸ������ݿⷢ���㲥����Widget<br>
	 * 
	 * @param id
	 *            Event��id
	 * @param view
	 *            PopupWindow��view
	 * @param popupwindow
	 *            PopupWindowʵ��
	 * @param hasImgMapView
	 *            �Ƿ���Ҫ��ʼ��Event��λ��ť,����EventDetail��ڽ���ʱ��Ҫ,����ʱ����Ҫ.
	 * @param context
	 *            ������
	 */
	private void bindPopupData(final long id, View view,
			final PopupWindow popupwindow, boolean hasImgMapView,
			final Context context) {
		Cursor detailCursor = null;
		try {
			TextView txtDes = (TextView) view
					.findViewById(R.id.txtEventDecription);
			TextView txtTitle = (TextView) view
					.findViewById(R.id.txtEventTitle);
			TextView txtSpeak = (TextView) view
					.findViewById(R.id.txtEventSpeaker);
			TextView txtSpeakTitle = (TextView) view
					.findViewById(R.id.txtEventSpeakerTitle);
			TextView txtDate = (TextView) view.findViewById(R.id.txtEventDate);
			TextView txtPlace = (TextView) view
					.findViewById(R.id.txtEventPlace);
			ImageView mapImageView = (ImageView) view
					.findViewById(R.id.imgEventMap);
			if (hasImgMapView) {
				mapImageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						popupwindow.dismiss();
						onFragmengToActivityListener.onEventLocated(mEvent);
					}
				});
			} else {
				mapImageView.setVisibility(View.GONE);
			}
			final Uri queryUri = Uri.parse(EventProvider.CONTENT_URI.toString()
					+ "/" + id);
			detailCursor = mEventProvider.query(queryUri, null, null, null,
					getSortOrder());
			if (detailCursor.moveToFirst()) {
				mEvent = new Event(detailCursor);
				txtTitle.setText(mEvent.getEventName());
				txtSpeak.setText(mEvent.getEventSpeaker());
				txtSpeakTitle.setText(mEvent.getEventSpeakerTitle());
				txtDate.setText(mEvent.getEventDate() + " "
						+ mEvent.getEventTime());
				txtPlace.setText(mEvent.getEventLocation());
				txtDes.setText(mEvent.getEventDescription());
				if (detailCursor.getInt(detailCursor
						.getColumnIndex(EventData.C_ISLIKE)) == 1) {
					mEvent.setEventIsLike(true);
				} else {
					mEvent.setEventIsLike(false);
				}

				final ImageView likeImageView = (ImageView) view
						.findViewById(R.id.imgEventLike);
				if (mEvent.isEventIsLike()) {
					likeImageView.setImageDrawable(context.getResources()
							.getDrawable(R.drawable.ic_action_like));
					LbsApplication.setActivityLike(true);
				}
				likeImageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (LbsApplication.isActivityLike()) {
							LbsApplication.setActivityLike(false);
							likeImageView.setImageDrawable(context
									.getResources().getDrawable(
											R.drawable.ic_action_unlike));
							ContentValues values = new ContentValues();
							values.put(EventData.C_ISLIKE, 0);
							int num = mEventProvider.update(queryUri, values,
									null, null);
							Log.d(TAG, num + " rows changed");
						} else {
							LbsApplication.setActivityLike(true);
							likeImageView.setImageDrawable(context
									.getResources().getDrawable(
											R.drawable.ic_action_like));
							ContentValues values = new ContentValues();
							values.put(EventData.C_ISLIKE, 1);
							int num = mEventProvider.update(queryUri, values,
									null, null);
							Log.d(TAG, num + " rows changed");
						}
						Intent intent = new Intent();
						intent.setAction(LbsService.NEW_STATUS_INTENT);
						context.sendBroadcast(intent);
					}
				});
				txtDes.setMovementMethod(ScrollingMovementMethod.getInstance());
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			if (detailCursor != null) {
				detailCursor.close();
			}
			LbsApplication.getEventData().closeDatabase();
		}
	}

}
