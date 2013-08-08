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
	private final static String TAG = "LocationAPI";

	/**
	 * 开启定位 判断是否打开 若关闭则打开
	 * 
	 * @param locationClient
	 *            定位客户端
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

	/**
	 * 关闭定位 判断是否关闭 若打开则关闭
	 * 
	 * @param locationClient
	 *            定位客户端
	 */
	public void stopLocate(LocationClient locationClient) {
		if (LbsApplication.isLocateStart()) {
			locationClient.stop();
			Log.d(TAG, "stop to locate");
			LbsApplication.setLocateStart(false);
		}
	}

	/**
	 * 在地图上画定位点和精度buffer,范围半径=精度*5.577531914893617E-4（地图第三级mapscale）*0.02
	 * 
	 * @param location
	 *            定位点坐标
	 * @param mMapView
	 *            地图mapView
	 * @param context
	 * @param radius
	 *            定位精度
	 */
	public void drawLocation(Point2D location, MapView mMapView,
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

	/**
	 * 判读是否在地图范围内 左: 121.412490774567; 上: 31.1651384499396; 右: 121.426210646701;
	 * 下: 31.1566896665659; 宽: 0.01371987213399; 高: 0.00844878337370147
	 * 如果不在地图范围内，则自动关闭定位
	 * 
	 * @param point2d
	 * @param mMapView
	 * @return boolean
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

	/**
	 * 用来设置定位SDK的定位方式。 disableCache //true表示禁用缓存定位，false表示启用缓存定位。 setOpenGps
	 * //设置是否打开gps，使用gps前提是用户硬件打开gps。 setCoorType //设置返回值的坐标类型。返回国测局经纬度坐标系
	 * coor=gcj02 setScanSpan //设置定时定位的时间间隔3s setPriority //设置定位方式的优先级GpsFirst
	 * 
	 * @param locationClient
	 *            定位客户端
	 */
	private void setLocationOption(LocationClient locationClient) {
		LocationClientOption option = new LocationClientOption();
		option.disableCache(false);
		option.setOpenGps(true);
		option.setCoorType("gcj02"); // 设置坐标类型gcj02
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setScanSpan(3000);
		option.setPriority(LocationClientOption.GpsFirst);
		option.disableCache(true);
		locationClient.setLocOption(option);
	}
}
