package com.shnu.lbsshnu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.Geometrist;
import com.supermap.data.Recordset;
import com.supermap.mapping.Layer;

public class Query {
	private static final String TAG = "Query";

	/*
	 * 地理编码实现
	 */
	public String geoCode() {
		Layer mlayer = null;
		mlayer = LBSApplication.getmMapControl().getMap().getLayers().get(15);
		String locationAddresString = "";
		GeoPoint mGeoPoint = new GeoPoint(
				LBSApplication.getLastlocationPoint2d());
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
		return locationAddresString;
	}

	/*
	 * 缓冲区查询
	 */
	public List<Place> queryByBuffer(GeoRegion buffer) {
		List<Place> locationPlaces = new ArrayList<Place>();
		Layer mlayer = null;
		mlayer = LBSApplication.getmMapControl().getMap().getLayers().get(14);
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
							new GeoPoint(LBSApplication
									.getLastlocationPoint2d()));
					String distance = LBSApplication
							.save2Point(distancef * 100000);
					Place placeItem = new Place(buildingNum, buildingName,
							distance);
					locationPlaces.add(placeItem);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		Collections.sort(locationPlaces);
		return locationPlaces;
	}

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
}
