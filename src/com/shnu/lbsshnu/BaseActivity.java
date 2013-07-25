package com.shnu.lbsshnu;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;

public class BaseActivity extends Activity {
	LBSApplication lbsApplication;
	SimpleSideDrawer simpleSideDrawer;
	Switch wifiLayerSwitch;
	Switch locationSwitch;
	Handler handler;
	LinearLayout actionbarView;
	long exitTime = 0;
	public static boolean isPopUp = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lbsApplication = (LBSApplication) getApplication();
	}

	/*
	 * 设置slideractionbar
	 */
	public void setSliderActionBar() {
		LBSApplication.setSearch(false);
		actionbarView.removeAllViews();
		View.inflate(BaseActivity.this, R.layout.actionbar, actionbarView);
		simpleSideDrawer = new SimpleSideDrawer(this);
		simpleSideDrawer.setLeftBehindContentView(R.layout.sliderleft);
		simpleSideDrawer.setRightBehindContentView(R.layout.sliderright);
		ImageView userImageView = (ImageView) findViewById(R.id.imgUser);
		ImageView moreImageView = (ImageView) findViewById(R.id.imgMore);
		ImageView searchImageView = (ImageView) findViewById(R.id.imgSearch);
		searchImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSearchView();
			}
		});
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
	 * 设置wifi层
	 */
	public void setWifiLayer() {
		wifiLayerSwitch = (Switch) findViewById(R.id.swtWifi);
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
	 * 设置是否开启定位
	 */
	public void setLocation() {
		locationSwitch = (Switch) findViewById(R.id.swtLocation);
		locationSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							LBSApplication.getLocationApi().startLocate(
									LBSApplication.getLocationClient());
							LBSApplication.setStart(true);
						} else {
							LBSApplication.getLocationApi().stopLocate(
									LBSApplication.getLocationClient());
							LBSApplication.setStart(false);
						}
						LBSApplication.clearTrackingLayer();
						LBSApplication.refreshMap();
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
		LinearLayout likeLinear = (LinearLayout) findViewById(R.id.linearLike);
		lectureLinear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						ActivityListView.class);
				bundleDataBundle.putString("Tab", "0");
				intent.putExtras(bundleDataBundle);
				startActivityForResult(intent, LBSApplication.getRequestCode());
				BaseActivity.this.overridePendingTransition(R.anim.popup_enter,
						R.anim.popup_exit);
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
				BaseActivity.this.overridePendingTransition(R.anim.popup_enter,
						R.anim.popup_exit);
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
				BaseActivity.this.overridePendingTransition(R.anim.popup_enter,
						R.anim.popup_exit);
			}
		});
		likeLinear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						ActivityListView.class);
				bundleDataBundle.putString("Tab", "3");
				intent.putExtras(bundleDataBundle);
				startActivityForResult(intent, LBSApplication.getRequestCode());
				BaseActivity.this.overridePendingTransition(R.anim.popup_enter,
						R.anim.popup_exit);
			}
		});
	}

	/*
	 * 设置缓冲区查询入口
	 */
	@SuppressWarnings("static-access")
	public void setSearchView() {
		LBSApplication.setSearch(true);
		actionbarView.removeAllViews();
		View.inflate(BaseActivity.this, R.layout.searchbar, actionbarView);
		SearchManager searchManager = (SearchManager) getSystemService(LBSApplication
				.getContext().SEARCH_SERVICE);
		SearchView searchView = (SearchView) findViewById(R.id.bufferSearch);
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				Intent intent = new Intent(getApplicationContext(),
						QueryResult.class);
				intent.putExtra("Query", query);
				startActivityForResult(intent, LBSApplication.getRequestCode());
				return true;

			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		searchView.setOnCloseListener(new SearchView.OnCloseListener() {

			@Override
			public boolean onClose() {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
}
