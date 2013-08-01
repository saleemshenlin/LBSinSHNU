package com.shnu.lbsshnu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.Geometrist;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Layer;

public class Query {
	private static final String TAG = "Query";

	/*
	 * 地理编码实现
	 */
	public String geoCode() {
		Layer mlayer = null;
		mlayer = LbsApplication.getmMapControl().getMap().getLayers().get(15);
		String locationAddresString = "";
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
				locationAddresString = " , "
						+ mRecordset.getFieldValue("Name").toString();
				break;
			}
		}
		// 释放资源
		Log.i(TAG, locationAddresString);
		mRecordset.dispose();
		mGeoPoint.dispose();
		return locationAddresString;
	}

	/*
	 * 缓冲区查询
	 */
	public List<Place> queryByBuffer(GeoRegion buffer) {
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
				boolean isTrue = Geometrist.canContain(buffer,
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

	public void addPhotoBubble(DatasetVector mDatasetVector,
			final Context context) {
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
						imgPopup(context, id);
					}
				});
				image.setBackgroundResource(R.drawable.ic_img_pin);
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

	/*
	 * 缓冲区查询结果的实体类
	 */
	class Place implements Comparable<Place> {
		int buildingNum;
		String buildingName;
		String distance;

		Place(int buildingNum, String buildingName, String distance) {
			this.buildingName = buildingName;
			this.buildingNum = buildingNum;
			this.distance = distance;
		}

		@Override
		public int compareTo(Place place) {
			// TODO Auto-generated method stub
			return this.distance.compareTo(place.distance);
		}
	}

	@SuppressWarnings("static-access")
	private void imgPopup(Context context, int id) {
		try {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.popupphoto, null);
			View rootView = layoutInflater.inflate(R.layout.homeactivity, null);
			final PopupWindow popupWindow = new PopupWindow(view,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			LinearLayout linearLayout = (LinearLayout) view
					.findViewById(R.id.popupPhoto);
			switch (id) {
			case 1:
				linearLayout.setBackgroundResource(R.drawable.photo_1);
				break;
			case 2:
				linearLayout.setBackgroundResource(R.drawable.photo_2);
				break;
			case 3:
				linearLayout.setBackgroundResource(R.drawable.photo_3);
				break;
			case 4:
				linearLayout.setBackgroundResource(R.drawable.photo_4);
				break;
			case 5:
				linearLayout.setBackgroundResource(R.drawable.photo_5);
				break;
			}
			popupWindow.setBackgroundDrawable(new BitmapDrawable(context
					.getResources()));
			popupWindow.showAtLocation(rootView, Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.setAnimationStyle(R.anim.popupanimation);
			ColorDrawable dw = new ColorDrawable(-00000);
			popupWindow.setBackgroundDrawable(dw);
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.update();
		} catch (Exception e) {
			Log.e("imgPopup", e.toString());
		}
	}
}
