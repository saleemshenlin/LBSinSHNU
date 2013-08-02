package com.shnu.lbsshnu;

import android.content.Context;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.supermap.data.Color;
import com.supermap.data.GeoCircle;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.mapping.MapView;

public class LocationAPI {
	private final static String TAG = "BaiduAPI";

	/*
	 * ������ز���
	 */
	private void setLocationOption(LocationClient locationClient) {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // ��gps_mGpsCheck.isChecked()
		option.setCoorType("gcj02"); // ������������gcj02
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setScanSpan(1000);
		if (LbsApplication.isNetWork()) {
			option.setPriority(LocationClientOption.NetWorkFirst);
		} else {
			option.setPriority(LocationClientOption.GpsFirst);
		}
		option.disableCache(true);
		locationClient.setLocOption(option);
	}

	/*
	 * ������λ
	 */
	public void startLocate(LocationClient locationClient) {
		if (!LbsApplication.isLocateStart()) {
			setLocationOption(locationClient);
			locationClient.start();
			// Log.d(TAG, "version:" + locationClient.getVersion());
			Log.d(TAG, "start to locate");
			LbsApplication.setLocateStart(true);
		}
	}

	/*
	 * �رն�λ
	 */
	public void stopLocate(LocationClient locationClient) {
		if (LbsApplication.isLocateStart()) {
			locationClient.stop();
			Log.d(TAG, "stop to locate");
			LbsApplication.setLocateStart(false);
		}
	}

	/*
	 * �ڵ�ͼ�ϻ���λ��;���buffer,��Χ�뾶=����*5.577531914893617E-4����ͼ������mapscale��*0.02
	 */
	public void drawLocationPoint(Point2D location, MapView mMapView,
			Context context, float radius) {
		LbsApplication.clearTrackingLayer();
		// double mapScale =
		// LbsApplication.getmMapControl().getMap().getScale();
		GeoCircle mGeoCircle = new GeoCircle(location,
				radius * 5.577531914893617E-4 * 0.02);
		GeoRegion mGeoRegion = new GeoCircle(location,
				15 * 5.577531914893617E-4 * 0.005).convertToRegion(100);
		GeoStyle mGeoStyle = new GeoStyle();
		GeoStyle mGeoStyle_P = new GeoStyle();
		mGeoStyle.setFillOpaqueRate(10);
		mGeoStyle.setLineSymbolID(0);
		mGeoStyle.setLineWidth(0.5);
		mGeoStyle.setLineColor(new Color(0, 153, 204));
		mGeoStyle_P.setFillForeColor(new Color(0, 153, 204));
		mGeoStyle_P.setLineSymbolID(5);
		mGeoRegion.setStyle(mGeoStyle_P);
		mGeoCircle.setStyle(mGeoStyle);
		LbsApplication.getmTrackingLayer().add(mGeoCircle, "accuracyBuffer");
		LbsApplication.getmTrackingLayer().add(mGeoRegion, "geopoint");
		LbsApplication.refreshMap();
	}

	/*
	 * �ж��Ƿ��ڵ�ͼ��Χ�� ��: 121.412490774567; ��: 31.1651384499396; ��: 121.426210646701;
	 * ��: 31.1566896665659; ��: 0.01371987213399; ��: 0.00844878337370147
	 */
	public boolean isLocInMap(Point2D point2d, MapView mMapView) {
		Rectangle2D rcMap = new Rectangle2D(121.412490774567, 31.1566896665659,
				121.426210646701, 31.165138449939);
		if (point2d.getX() < rcMap.getLeft()
				|| point2d.getX() > rcMap.getRight()) {
			return false;
		}
		if (point2d.getY() < rcMap.getBottom()
				|| point2d.getY() > rcMap.getTop()) {
			return false;
		}
		return true;
	}
}
