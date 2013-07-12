package com.shnu.lbsshnu;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapParameterChangedListener;
import com.supermap.mapping.MapView;

public class HomeActivity extends BaseActivity {
	private static final String TAG = "HomeActivity";
	RelativeLayout locationImageView;
	TextView accuracyTextView;
	Switch wifiLayerSwitch;
	private Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeactivity);
		setSliderActionBar();
		handler = new Handler();
		locationImageView = (RelativeLayout) findViewById(R.id.locationRelativeLayout);
		wifiLayerSwitch = (Switch) findViewById(R.id.wifiswitch);
		locationImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLocated();
			}
		});
		wifiLayerSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							lbsApplication.mWifiLayerL.setVisible(true);
							lbsApplication.mWifiLayerS.setVisible(true);
							LBSApplication.refreshMap();
						} else {
							lbsApplication.mWifiLayerL.setVisible(false);
							lbsApplication.mWifiLayerS.setVisible(false);
							LBSApplication.refreshMap();
						}
					}
				});
		openData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LBSApplication.refreshMap();
		LBSApplication.isChange = true;
		DrawPointAndBuffer drawPointAndBuffer = new DrawPointAndBuffer();
		drawPointAndBuffer.start();
	}

	protected void onDestroy() {
		super.onDestroy();
		LBSApplication.getmMapControl().getMap().close();
		LBSApplication.getmMapControl().getMap().dispose();
		LBSApplication.getmMapControl().dispose();
		LBSApplication.getmWorkspace().close();
		LBSApplication.getmWorkspace().dispose();
	}

	/*
	 * 从SDcard添加底图数据
	 */
	private void openData() {
		// 打开工作空间
		LBSApplication.setmWorkspace(new Workspace());
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(LBSApplication.getSdCard()
				+ getString(R.string.data_path));
		info.setType(WorkspaceType.SMWU);
		LBSApplication.getmWorkspace().open(info);

		LBSApplication.setmMapView((MapView) findViewById(R.id.mapView));
		LBSApplication.setmMapControl(LBSApplication.getmMapView()
				.getMapControl());

		LBSApplication.getmMapControl().getMap()
				.setWorkspace(LBSApplication.getmWorkspace());
		String mapName = LBSApplication.getmWorkspace().getMaps().get(0);
		Log.i(TAG, "add Map: " + mapName);
		LBSApplication.getmMapControl().getMap().open(mapName);
		LBSApplication.setMlayers(LBSApplication.getmMapControl().getMap()
				.getLayers());
		lbsApplication.mWifiLayerS = LBSApplication.getMlayers().get(2);
		lbsApplication.mWifiLayerL = LBSApplication.getMlayers().get(3);
		lbsApplication.mWifiLayerL.setVisible(false);
		lbsApplication.mWifiLayerS.setVisible(false);
		LBSApplication.getmMapControl().getMap().setScale(1 / 1200);
		LBSApplication.getmMapControl().getMap().setAntialias(true);
		LBSApplication
				.getmMapControl()
				.getMap()
				.setLockedViewBounds(
						new Rectangle2D(121.412490774567, 31.1566896665659,
								121.426210646701, 31.1651384499396));
		LBSApplication.getmMapControl().getMap().setViewBoundsLocked(true);
		// 左: 121.412490774567; 上: 31.1651384499396; 右: 121.426210646701; 下:
		// 31.1566896665659; 宽: 0.01371987213399; 高: 0.00844878337370147

		LBSApplication.refreshMap();
		LBSApplication.setmTrackingLayer(LBSApplication.getmMapControl()
				.getMap().getTrackingLayer());
		LBSApplication.getmMapControl().setMapParamChangedListener(
				mapParameterChangedListener);
		Log.i(TAG, "Max:"
				+ LBSApplication.getmMapControl().getMap().getMaxScale()
				+ " Min:"
				+ LBSApplication.getmMapControl().getMap().getMinScale()
				+ " Dpi:"
				+ LBSApplication.getmMapControl().getMap().getMapDPI());
	}

	/*
	 * 监听地图参数变化
	 */
	MapParameterChangedListener mapParameterChangedListener = new MapParameterChangedListener() {

		@Override
		public void scaleChanged(double scale) {
			Log.i(TAG, "Scale:" + scale);
			LBSApplication.getLocationApi().addAccuracyBuffer(
					LBSApplication.getLastlocationPoint2d(), (float) scale);
			LBSApplication.refreshMap();
		}

		@Override
		public void boundsChanged(Point2D point2d) {
			// TODO Auto-generated method stub

		}
	};

	/*
	 * 定位后操作
	 */
	private void onLocated() {

		RelativeLayout mapRelativeLayout = (RelativeLayout) findViewById(R.id.mapViewRelativeLayout);
		if (!isPopUp) {
			viewPopup(0, -LBSApplication.Dp2Px(this, 96), mapRelativeLayout);
			isPopUp = true;
		} else {
			viewPopup(-LBSApplication.Dp2Px(this, 96), 0, mapRelativeLayout);
			isPopUp = false;
		}
		Log.d(TAG, "locationPoint2d:"
				+ LBSApplication.getLastlocationPoint2d().getX() + " , "
				+ LBSApplication.getLastlocationPoint2d().getY());
	}

	/*
	 * 地图框上移
	 */
	private void viewPopup(final float p1, final float p2,
			final RelativeLayout view) {
		TranslateAnimation animation = new TranslateAnimation(0, 0, p1, p2);
		// animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setDuration(500);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				Log.i(TAG,
						"Left: " + view.getLeft() + "Top: " + view.getTop()
								+ "Right: " + view.getRight() + "Bottom: "
								+ view.getBottom());
				locationImageView.setEnabled(false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
				view.clearAnimation();
				view.getLayoutParams().height = view.getBottom()
						+ (int) (p2 - p1);
				view.setVisibility(View.VISIBLE);
				Log.i(TAG,
						"Left: " + view.getLeft() + "Top: " + view.getTop()
								+ "Right: " + view.getRight() + "Bottom: "
								+ view.getBottom());

				if (LBSApplication.getLastlocationPoint2d() != null)
					LBSApplication.getmMapControl().getMap()
							.setCenter(LBSApplication.getLastlocationPoint2d());
				locationImageView.setEnabled(true);
			}
		});
		view.startAnimation(animation);
	}

	private class DrawPointAndBuffer extends Thread {

		@Override
		public void run() {
			super.run();
			while (LBSApplication.isChange) {
				try {
					handler.post(runnableUi);
					Thread.sleep(3000);
				} catch (Exception e) {
					Toast.makeText(LBSApplication.getContext(), e.toString(),
							Toast.LENGTH_LONG).show();
				} finally {
					// LBSApplication.isChange = false;
				}
			}
		}
	}

	Runnable runnableUi = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (LBSApplication.getLocationAccuracy() > 0) {
				try {
					LBSApplication.getLocationApi().addCallOutBall(
							LBSApplication.getLastlocationPoint2d(),
							LBSApplication.getmMapView(),
							LBSApplication.getContext());
					LBSApplication.getLocationApi().addAccuracyBuffer(
							LBSApplication.getLastlocationPoint2d(),
							LBSApplication.getLocationAccuracy());

				} catch (Exception e) {
					Log.e(TAG, e.toString());
					Log.e(TAG, "locationPoint2d:"
							+ LBSApplication.getLastlocationPoint2d().getX()
							+ " , "
							+ LBSApplication.getLastlocationPoint2d().getY()
							+ " , " + LBSApplication.getLocationAccuracy());
				}
			}
		}
	};

}
