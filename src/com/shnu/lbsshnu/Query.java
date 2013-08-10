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
 * 类Query<br>
 * 用于在各类有关地图和Event的查询
 */
public class Query {
	/**
	 * 定义一个标签,在LogCat内表示EventData
	 */
	private static final String TAG = "Query";
	/**
	 * 用于缓存区查询时，表示半径 100米
	 */
	private static final int QUERY_RADIUS = 100;
	/**
	 * 用于实例化接口OnFragmengToActivityListener
	 */
	public OnFragmengToActivityListener onFragmengToActivityListener;
	/**
	 * 用于实例化类EventProvider
	 */
	private EventProvider mEventProvider = new EventProvider();
	/**
	 * 用于实例化类Event
	 */
	private Event mEvent = null;

	/**
	 * 用于HomeActivity中的反向地里编码查询<br>
	 * 具体实现方法：<br>
	 * 1)获取当前坐标<br>
	 * 2)遍历图层通过Geometrist.canContain判断是否包含当前位置 <br>
	 * 3)返回结果位置的Name字段信息
	 * 
	 * @return String 返回结果位置的Name字段信息
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
		// 释放资源
		Log.i(TAG, strAddress);
		mRecordset.dispose();
		mGeoPoint.dispose();
		return strAddress;
	}

	/**
	 * 用于查询缓冲区内的的结果<br>
	 * 具体实现方法：<br>
	 * 1)获取缓冲区 GeoRegion geoRegion<br>
	 * 2)遍历图层，通过Geometrist.canContain缓冲区结果对比<br>
	 * 3)将结果按与当前位置的距离从小到大排列存放到List<Place><br>
	 * 4)返回List<Place><br>
	 * 
	 * @param geoRegion
	 *            已经绘制好的缓冲区
	 * @return List<Place> 缓冲区查询结果
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
	 * 用于初始化美丽校园层的CallOut<br>
	 * 具体实现方法：<br>
	 * 1)遍历图层,获取点的Geometry<br>
	 * 2)对每一个点赋予Callout,并添加onClick事件,让使用者点击CallOut能够弹出图片<br>
	 * 
	 * @param context
	 *            上下文
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
	 * 用于通过LocationDetail的上下移动,显示其内容<br>
	 * 具体实现方法：<br>
	 * 1)通过传入的参数使用 实例一个TranslateAnimation<br>
	 * 2)设置TranslateAnimation动画为AnticipateOvershootInterpolator()<br>
	 * 3)设置TranslateAnimation动画的时间为500ms<br>
	 * 4)设置TranslateAnimation动画过程<br>
	 * 4)执行TranslateAnimation<br>
	 * 
	 * @param fromY
	 *            移动前位置
	 * @param toY
	 *            移动后位置
	 * @param mapView
	 *            需要移动的view,程序中是指地图展示RelativeLayout
	 * @param locate
	 *            控制定位按钮RelativeLayout rllLocation在移动过程中不能点击
	 */
	public void moveLocationDetail(final float fromY, final float toY,
			final RelativeLayout mapView, final RelativeLayout locate) {
		TranslateAnimation animation = new TranslateAnimation(0, 0, fromY, toY);
		// 移动动画
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
	 * 用于初始化Event在地图上定位的CallOut<br>
	 * 具体实现方法：<br>
	 * 1)根据传入BuildingId,遍历地图,获取相应的Geometry<br>
	 * 2)根据点的Geometry,初始化CallOut<br>
	 * 3)根据传入的Type,给每个CallOut附上不同的图片<br>
	 * 4)返回CallOut<br>
	 * 
	 * @param BuildingId
	 *            Event的发生位置
	 * @param Type
	 *            Event的类型
	 * @param context
	 *            上下文
	 * @return Callout 返回Callout
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
	 * 用于绘制缓冲区<br>
	 * 具体方法如下:<br>
	 * 1)根据位置和半径QUERY_RADIUS绘制缓冲区<br>
	 * 2)返回缓冲区<br>
	 * 
	 * @return GeoRegion 缓冲区
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
	 * 用于设置查询的排序条件<br>
	 * 按Event发生时间的升序排序 <br>
	 * 
	 * @return String 排序条件
	 */
	public String getSortOrder() {
		String strSQL = null;
		strSQL = EventData.C_DATE + " ASC";
		return strSQL;
	}

	/**
	 * 用于Event根据查询类型设置查询条件<br>
	 * 类型 0 学术讲座 1 电影演出 2 精品课程 3 我关注的<br>
	 * 时间设置为一周,从查询当天开始算<br>
	 * 
	 * @param intIndex
	 *            查询类型
	 * @return String 查询条件
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
	 * 用于设置在缓存区查询时的查询条件<br>
	 * 根据传入的mPlace(缓冲区查询的结果)和strQuery(用户输入的内容)进行查询<br>
	 * 时间设置为一周,从查询当天开始算<br>
	 * 
	 * @param mPlace
	 *            缓冲区查询的结果
	 * @param strQuery
	 *            用户输入的内容
	 * @return String 查询条件
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
	 * 用于设置在缓存区查询时的查询条件<br>
	 * 根据传入的num(Event类别)和strQuery(用户输入的内容)进行查询<br>
	 * 时间设置为一周,从查询当天开始算<br>
	 * 
	 * @param num
	 *            查询类别
	 * @param strQuery
	 *            用户输入的内容
	 * @return String 查询条件
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
	 * 用于初始化EventDetail的PopupWindow<br>
	 * 具体方法如下:<br>
	 * 1)首先初始化一个PopupWindow,宽度为屏幕的3/4,高度自适应<br>
	 * 2)通过bindPopupData方法绑数据<br>
	 * 3)设置背景，用于点击周围关闭弹出PopupWindow<br>
	 * 4)设置位置水平竖直居中<br>
	 * 5)设置出现消失动画<br>
	 * 6)更新PopupWindow<br>
	 * 
	 * @param context
	 *            上下文
	 * @param rootView
	 *            显示PopupWindow的父view
	 * @param id
	 *            Event的Id
	 * @param hasImgMapView
	 *            是否需要初始化Event定位按钮
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
	 * 接口OnFragmengToActivityListener<br>
	 * 用于EventListFragment和EventListView间交互数据<br>
	 * 当使用者在EventListFragment中的点击定位功能时<br>
	 * 能够将Event的信息传递给EventListView，并最终传递给HomeActivity进行定位。<br>
	 */
	public interface OnFragmengToActivityListener {
		public void onEventLocated(Event mEvent);
	}

	/**
	 * 类Place<br>
	 * 用于表示缓冲区查询结果实体类,与地图属性数据匹配<br>
	 * 参数：<br>
	 * intBuildingNum 查询结果位置的id。<br>
	 * strBuildingName 查询结果位置的名称。<br>
	 * strDistance 查询结果与当前位置的距离。<br>
	 */
	class Place implements Comparable<Place> {
		/**
		 * 用于表示查询结果位置的id。
		 */
		int intBuildingNum;
		/**
		 * 用于表示查询结果位置的名称。
		 */
		String strBuildingName;
		/**
		 * 用于表示查询结果与当前位置的距离。
		 */
		String strDistance;

		Place(int buildingNum, String buildingName, String distance) {
			this.setStrBuildingName(buildingName);
			this.setIntBuildingNum(buildingNum);
			this.strDistance = distance;
		}

		/**
		 * 用于查询结果按其与当前位置的距离间排序。
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
	 * 用于初始化美丽校园的PopupWindow<br>
	 * 具体方法如下:<br>
	 * 1)首先初始化一个PopupWindow,宽度、高度自适应<br>
	 * 2)通过传入的id匹配相应照片<br>
	 * 3)设置背景，用于点击周围关闭弹出PopupWindow<br>
	 * 4)设置位置水平竖直居中<br>
	 * 5)设置出现消失动画<br>
	 * 6)更新PopupWindow<br>
	 * 
	 * @param context
	 *            上下文
	 * @param id
	 *            Photo的id
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
	 * 用于给initPopupWindow()中的PopupWindow绑数据 具体方法如下:<br>
	 * 1)初始化PopupWindow中的各个View<br>
	 * 2)根据hasImgMapView是否需要初始化ImgMapView<br>
	 * 3)根据id查询获取并实例化一个Event<br>
	 * 4)给各个View绑定数据<br>
	 * 5)设置likeImageView的onClick事件,用于更新数据库发出广播更新Widget<br>
	 * 
	 * @param id
	 *            Event的id
	 * @param view
	 *            PopupWindow的view
	 * @param popupwindow
	 *            PopupWindow实例
	 * @param hasImgMapView
	 *            是否需要初始化Event定位按钮,当从EventDetail入口进入时需要,其他时候不需要.
	 * @param context
	 *            上下文
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
