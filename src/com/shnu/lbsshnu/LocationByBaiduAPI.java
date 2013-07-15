package com.shnu.lbsshnu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.supermap.data.Color;
import com.supermap.data.GeoCircle;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapView;

public class LocationByBaiduAPI {
	private final static String TAG = "BaiduAPI";
	boolean isStart = false;
	private static final double PI = 3.1459266;
	private static final double HALF_PI = 1.57079633;
	private static final int SCREEN_MAERGIN = 24;

	// 设置相关参数
	private void setLocationOption(LocationClient locationClient) {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps_mGpsCheck.isChecked()
		option.setCoorType("gcj02"); // 设置坐标类型gcj02
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setScanSpan(1000);
		if (LBSApplication.isNetWork()) {
			option.setPriority(LocationClientOption.NetWorkFirst);
		}
		option.disableCache(true);
		locationClient.setLocOption(option);
	}

	public void startLocate(LocationClient locationClient) {
		if (!isStart) {
			setLocationOption(locationClient);
			locationClient.start();
			Log.d(TAG, "version:" + locationClient.getVersion());
			// locationClient.requestLocation();
			isStart = true;
		}
	}

	public void stopLocate(LocationClient locationClient) {
		if (isStart) {
			locationClient.stop();
			isStart = false;
		}
	}

	public void addCallOutBall(Point2D location, MapView mMapView,
			Context context) {
		mMapView.removeAllCallOut();
		CallOut callout = new CallOut(context);
		callout.setStyle(CalloutAlignment.CENTER);
		callout.setCustomize(true);
		ImageView image = new ImageView(context);
		callout.setLocation(location.getX(), location.getY());
		image.setBackgroundResource(R.drawable.ic_action_ball);
		callout.setContentView(image);
		mMapView.addCallout(callout);
	}

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

	public void addCallOutArrow(Point2D pntLoc, MapView mMapView,
			Context context) {
		Point2D pntMapCenter = mMapView.getMapControl().getMap().getCenter();
		Rectangle2D rcMapViewBounds = mMapView.getMapControl().getMap()
				.getViewBounds();

		double userX = (pntLoc.getX() - pntMapCenter.getX())
				/ rcMapViewBounds.getWidth() * LBSApplication.getScreenWidth();
		double userY = (pntLoc.getY() - pntMapCenter.getY())
				/ rcMapViewBounds.getHeight()
				* LBSApplication.getScreenHeight();

		Point marginBounds = new Point(LBSApplication.getScreenWidth()
				- SCREEN_MAERGIN, LBSApplication.getScreenHeight()
				- SCREEN_MAERGIN);

		Point arrowPosition = new Point(SCREEN_MAERGIN, SCREEN_MAERGIN);

		double arrowRotation = 0;

		if (userX == 0) {
			arrowPosition.setX(LBSApplication.getScreenWidth() / 2);
			if (userY < 0) {
				arrowPosition.setX(marginBounds.getY());
				arrowRotation = PI;
			} else {
				arrowPosition.setX(marginBounds.getX());
				arrowRotation = 0;
			}
			rotateArrow(arrowPosition, arrowRotation, mMapView, context);
			return;
		}
		if (userY == 0) {
			arrowPosition.setY(LBSApplication.getScreenHeight() / 2);
			if (userX > 0) {
				arrowPosition.setX(marginBounds.getX());
				arrowRotation = HALF_PI;
			} else {
				arrowRotation = PI + HALF_PI;
			}
			rotateArrow(arrowPosition, arrowRotation, mMapView, context);
			return;
		}
		double angle = Math.atan(userY / userX);

		double dCorner = Math.atan(LBSApplication.getScreenHeight()
				/ LBSApplication.getScreenWidth());

		int ballX;
		int ballY;

		if (userY > 0) {
			if (userX > 0) {// 右上
				arrowRotation = HALF_PI - angle;
				if (angle < dCorner) {// 小于45度，设定X，计算Y
					arrowPosition.setX(marginBounds.getX());
					ballY = (int) ((LBSApplication.getScreenHeight() - LBSApplication
							.getScreenWidth() * Math.tan(angle)) / 2);
					arrowPosition.setY(ballY);
				} else {// 大于45度，设定Y,计算X
					arrowPosition.setY(SCREEN_MAERGIN);
					ballX = (int) ((LBSApplication.getScreenWidth() + LBSApplication
							.getScreenHeight() / Math.tan(angle)) / 2);
					arrowPosition.setX(ballX);
				}
			} else { // 左上
				arrowRotation = PI + HALF_PI - angle;
				if (angle > -dCorner) {// 小于45度，设定X，计算Y
					arrowPosition.setX(SCREEN_MAERGIN);
					ballY = (int) (0.5 * (LBSApplication.getScreenHeight() + LBSApplication
							.getScreenWidth() * Math.tan(angle)));
					arrowPosition.setY(ballY);
				} else {// 大于45度，设定Y,计算X
					arrowPosition.setY(SCREEN_MAERGIN);
					ballX = (int) (0.5 * (LBSApplication.getScreenWidth() + LBSApplication
							.getScreenHeight() / Math.tan(angle)));
					arrowPosition.setX(ballX);
				}
			}
		} else if (userY < 0) {
			if (userX > 0) {// 右下
				arrowRotation = HALF_PI - angle;
				if (angle > -dCorner) {// 小于45度，设定X，计算Y
					arrowPosition.setX(marginBounds.getX());
					ballY = (int) ((LBSApplication.getScreenHeight() - LBSApplication
							.getScreenWidth() * Math.tan(angle)) / 2);
					arrowPosition.setY(ballY);
				} else {// 大于45度，设定Y,计算X
					arrowPosition.setY(marginBounds.getY());
					ballX = (int) ((LBSApplication.getScreenWidth() - LBSApplication
							.getScreenHeight() / Math.tan(angle)) / 2);
					arrowPosition.setX(ballX);
				}
			} else if (userX < 0) { // 左下
				arrowRotation = PI + HALF_PI - angle;
				if (angle < dCorner) {// 小于45度，设定X，计算Y
					arrowPosition.setX(SCREEN_MAERGIN);
					ballY = (int) ((LBSApplication.getScreenWidth()
							* Math.tan(angle) + LBSApplication
							.getScreenHeight()) / 2);
					arrowPosition.setY(ballY);
				} else {// 大于45度，设定Y,计算X
					arrowPosition.setY(marginBounds.getY());
					ballX = (int) ((LBSApplication.getScreenWidth() - LBSApplication
							.getScreenHeight() / Math.tan(angle)) / 2);
					arrowPosition.setX(ballX);
				}
			}
		}
		if (arrowPosition.getX() > marginBounds.getX()) {
			arrowPosition.setX(marginBounds.getX());
		} else if (arrowPosition.getX() < SCREEN_MAERGIN) {
			arrowPosition.setX(SCREEN_MAERGIN);
		}
		if (arrowPosition.getY() > marginBounds.getY()) {
			arrowPosition.setY(marginBounds.getY());
		} else if (arrowPosition.getY() < SCREEN_MAERGIN) {
			arrowPosition.setY(SCREEN_MAERGIN);
		}

		rotateArrow(arrowPosition, arrowRotation, mMapView, context);
	}

	private void rotateArrow(Point point, double angle, MapView mMapView,
			Context context) {
		mMapView.removeAllCallOut();

		ImageView imageArrow = new ImageView(context);

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_action_arrow);
		float frotation = (float) (angle / PI * 180);

		// 创建操作图片是用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片
		float scale = (float) 1.5;
		matrix.postScale(scale, scale);
		// 旋转图片动作
		matrix.postRotate(frotation);
		// 创建新图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		BitmapDrawable bmd = new BitmapDrawable(context.getResources(),
				resizedBitmap);
		imageArrow.setAdjustViewBounds(true);
		imageArrow.setImageDrawable(bmd);

		Point2D point2d = mMapView.getMapControl().getMap().pixelToMap(point);

		CallOut callout = new CallOut(context);
		callout.setStyle(CalloutAlignment.CENTER);
		callout.setCustomize(true);
		callout.setLocation(point2d.getX(), point2d.getY());
		ImageView image = new ImageView(context);
		image.setImageDrawable(bmd);
		callout.setContentView(image);
		mMapView.addCallout(callout);
	}

	/*
	 * 增加定位精度buffer 半径=精度(单位：米)*地图scale*0.02
	 */
	public void addAccuracyBuffer(Point2D location, float radius) {
		LBSApplication.clearTrackingLayer();
		double mapScale = LBSApplication.getmMapControl().getMap().getScale();
		GeoCircle accuracyBuffer = new GeoCircle(location, radius * mapScale
				* 0.01);
		GeoStyle geoStyle_R = new GeoStyle();
		geoStyle_R.setFillOpaqueRate(10);
		geoStyle_R.setLineSymbolID(0);
		geoStyle_R.setLineWidth(0.5);
		geoStyle_R.setLineColor(new Color(0, 153, 204));
		accuracyBuffer.setStyle(geoStyle_R);
		LBSApplication.getmTrackingLayer()
				.add(accuracyBuffer, "accuracyBuffer");
		LBSApplication.refreshMap();
	}
}
