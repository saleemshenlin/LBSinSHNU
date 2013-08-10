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

/**
 * ��LocationAPI<br>
 * ����ʵ�ֶ�λ�ͻ��ƶ�λ��Ͷ�λ���Ȼ�����<br>
 * ʹ��Baidu��λAPIʵ�ֶ�λ
 */
public class LocationAPI {
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾLocationAPI
	 */
	private final static String TAG = "LocationAPI";

	/**
	 * ���ڿ�����λ <br>
	 * �ж��Ƿ�� ���ر����
	 * 
	 * @param locationClient
	 *            ��λ�ͻ���
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
	 * ���ڹرն�λ <br>
	 * �ж��Ƿ�ر� ������ر�
	 * 
	 * @param locationClient
	 *            ��λ�ͻ���
	 */
	public void stopLocate(LocationClient locationClient) {
		if (LbsApplication.isLocateStart()) {
			locationClient.stop();
			Log.d(TAG, "stop to locate");
			LbsApplication.setLocateStart(false);
		}
	}

	/**
	 * �����ڵ�ͼ�ϻ���λ��Ͷ�λ���Ȼ�����<br>
	 * ���巽������: <br>
	 * 1)��ȡ����λ�ú;���<br>
	 * 2)��ʼ��һ��GeoCircle,��ʾ��λ���Ȼ�����<br>
	 * 3)�뾶=����*5.577531914893617E-4����ͼ������mapscale��*0.02<br>
	 * 4)��ʼ����GeoCircle��ת��ΪGeoRegion,��ʾ��λ��<br>
	 * 5)�뾶=15 * 5.577531914893617E-4 * 0.005<br>
	 * 6)������ʽ,���ڸ��ٲ��ϻ���
	 * 
	 * @param location
	 *            ��ǰλ��
	 * @param mMapView
	 *            ��ͼmapView
	 * @param context
	 *            ������
	 * @param radius
	 *            ��λ����
	 */
	public void drawLocation(Point2D location, MapView mMapView,
			Context context, float radius) {
		LbsApplication.clearTrackingLayer();
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
	 * �����ж��Ƿ��ڵ�ͼ��Χ�� <br>
	 * ��: 121.412490774567<br>
	 * ��: 31.1651384499396<br>
	 * ��: 121.426210646701<br>
	 * ��: 31.1566896665659<br>
	 * ��: 0.01371987213399<br>
	 * ��: 0.00844878337370147<br>
	 * 
	 * @param point2d
	 *            ��ǰλ��
	 * @param mMapView
	 *            ��ͼmapView
	 * @return boolean �Ƿ��ڷ�Χ��
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
	 * �������ö�λSDK�Ķ�λ��ʽ�� <br>
	 * disableCache //true��ʾ���û��涨λ��false��ʾ���û��涨λ��<br>
	 * setOpenGps //�����Ƿ��gps��ʹ��gpsǰ�����û�Ӳ����gps�� <br>
	 * setCoorType //���÷���ֵ���������͡����ع���־�γ������ϵ coor=gcj02 <br>
	 * setScanSpan //���ö�ʱ��λ��ʱ����3s <br>
	 * setPriority //���ö�λ��ʽ�����ȼ�GpsFirst
	 * 
	 * @param locationClient
	 *            ��λ�ͻ���
	 */
	private void setLocationOption(LocationClient locationClient) {
		LocationClientOption option = new LocationClientOption();
		option.disableCache(false);
		option.setOpenGps(true);
		option.setCoorType("gcj02"); // ������������gcj02
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setScanSpan(3000);
		option.setPriority(LocationClientOption.GpsFirst);
		option.disableCache(true);
		locationClient.setLocOption(option);
	}
}
