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

public class LocationByBaiduAPI {
	private final static String TAG = "BaiduAPI";

	/*
	 * 设置相关参数
	 */
	private void setLocationOption(LocationClient locationClient) {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps_mGpsCheck.isChecked()
		option.setCoorType("gcj02"); // 设置坐标类型gcj02
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setScanSpan(1000);
		if (LbsApplication.isNetWork()) {
			option.setPriority(LocationClientOption.NetWorkFirst);
		}
		option.disableCache(true);
		locationClient.setLocOption(option);
	}

	/*
	 * 开启定位
	 */
	public void startLocate(LocationClient locationClient) {
		if (!LbsApplication.isStart()) {
			setLocationOption(locationClient);
			locationClient.start();
			// Log.d(TAG, "version:" + locationClient.getVersion());
			Log.d(TAG, "start to locate");
			LbsApplication.setStart(true);
		}
	}

	/*
	 * 关闭定位
	 */
	public void stopLocate(LocationClient locationClient) {
		if (LbsApplication.isStart()) {
			locationClient.stop();
			Log.d(TAG, "stop to locate");
			LbsApplication.setStart(false);
		}
	}

	/*
	 * 在地图上画定位点和精度范围半径=精度(单位：米)*地图scale*0.02
	 */
	public void drawLocationPoint(Point2D location, MapView mMapView,
			Context context, float radius) {
		LbsApplication.clearCallout();
		LbsApplication.clearTrackingLayer();
		double mapScale = LbsApplication.getmMapControl().getMap().getScale();
		GeoCircle mGeoCircle = new GeoCircle(location, radius * mapScale * 0.02);
		GeoRegion mGeoRegion = new GeoCircle(location, radius * mapScale
				* 0.005).convertToRegion(100);
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
	 * 判读是否在屏幕范围内
	 */
	public boolean isLocInMap(Point2D point2d, MapView mMapView) {
		Rectangle2D rcMap = mMapView.getMapControl().getMap().getViewBounds();
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
