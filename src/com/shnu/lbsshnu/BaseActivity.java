package com.shnu.lbsshnu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	LBSApplication lbsApplication;
	SimpleSideDrawer simpleSideDrawer;
	Switch wifiLayerSwitch;
	RelativeLayout locationImageView;
	TextView accuracyTextView;
	TextView addressTextView;
	Handler handler;
	private long exitTime = 0;
	public static boolean isPopUp = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lbsApplication = (LBSApplication) getApplication();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	/*
	 * 设置slideractionbar
	 */
	public void setSliderActionBar() {
		simpleSideDrawer = new SimpleSideDrawer(this);
		simpleSideDrawer.setLeftBehindContentView(R.layout.userpref);
		simpleSideDrawer.setRightBehindContentView(R.layout.actionmore);
		ImageView userImageView = (ImageView) findViewById(R.id.userpref);
		ImageView moreImageView = (ImageView) findViewById(R.id.actionmore);
		userImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				simpleSideDrawer.toggleLeftDrawer();
			}
		});
		moreImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				simpleSideDrawer.toggleRightDrawer();
			}
		});
	}

	/*
	 * 设置退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次后退键退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * 设置wifi层
	 */
	public void setWifiLayer() {
		wifiLayerSwitch = (Switch) findViewById(R.id.wifiswitch);
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
	}

	/*
	 * 设置right slide的活动
	 */
	public void setActivityRightSilder() {
		final Bundle bundleDataBundle = new Bundle();
		LinearLayout lectureLinear = (LinearLayout) findViewById(R.id.linearLecture);
		LinearLayout playLinear = (LinearLayout) findViewById(R.id.linearPlay);
		LinearLayout courseLinear = (LinearLayout) findViewById(R.id.linearCourse);
		lectureLinear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						ActivityListView.class);
				bundleDataBundle.putString("Tab", "0");
				intent.putExtras(bundleDataBundle);
				startActivityForResult(intent, LBSApplication.getRequestCode());
			}
		});
		playLinear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						ActivityListView.class);
				bundleDataBundle.putString("Tab", "1");
				intent.putExtras(bundleDataBundle);
				startActivityForResult(intent, LBSApplication.getRequestCode());
			}
		});
		courseLinear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						ActivityListView.class);
				bundleDataBundle.putString("Tab", "2");
				intent.putExtras(bundleDataBundle);
				startActivityForResult(intent, LBSApplication.getRequestCode());
			}
		});
	}
}
