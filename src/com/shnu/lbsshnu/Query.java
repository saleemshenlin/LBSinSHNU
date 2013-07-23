package com.shnu.lbsshnu;

import android.util.Log;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoPoint;
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
		GeoPoint mGeoPoint = new GeoPoint(LBSApplication.getLastlocationPoint2d());
		// GeoPoint point = new GeoPoint(121.416781751312,
		// 31.1617360260643);
		DatasetVector mDatasetVector = (DatasetVector) mlayer.getDataset();
		System.out.println(mlayer.getName());
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
}
