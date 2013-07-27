package com.shnu.lbsshnu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;

public class BaseActivity extends Activity {
	LBSApplication lbsApplication;
	SimpleSideDrawer simpleSideDrawer;
	Switch wifiLayerSwitch;
	Switch locationSwitch;
	RelativeLayout locationImageView;
	LinearLayout actionbarView;
	Handler handler;
	List<Result> results;

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
	public void initMainBar() {
		LBSApplication.setSearch(false);
		simpleSideDrawer = new SimpleSideDrawer(this);
		simpleSideDrawer.setLeftBehindContentView(R.layout.sliderleft);
		simpleSideDrawer.setRightBehindContentView(R.layout.sliderright);
		ImageView userImageView = (ImageView) findViewById(R.id.imgUser);
		ImageView moreImageView = (ImageView) findViewById(R.id.imgMore);
		LinearLayout searchImageView = (LinearLayout) findViewById(R.id.lnySearch);
		searchImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initSearchBar();
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
		initActivityRightSilder();
		initWifiLayer();
		initLocation();
	}

	/*
	 * 设置wifi层
	 */
	public void initWifiLayer() {
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
	public void initLocation() {
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
	public void initActivityRightSilder() {
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
	 * 设置缓冲区查询actionbar
	 */
	@SuppressWarnings("static-access")
	public void initSearchBar() {
		LBSApplication.setSearch(true);
		actionbarView.removeAllViews();
		View.inflate(BaseActivity.this, R.layout.searchbar, actionbarView);
		SearchManager searchManager = (SearchManager) getSystemService(LBSApplication
				.getContext().SEARCH_SERVICE);
		SearchView searchView = (SearchView) findViewById(R.id.bufferSearch);
		ImageView backImageView = (ImageView) findViewById(R.id.imgSearchBack);
		searchView.setBackgroundColor(Color.parseColor("#000000"));
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				LBSApplication.setQueryString(query);
				Intent intent = new Intent(getApplicationContext(),
						BufferQueryResult.class);
				intent.putExtra("QueryString", query);
				startActivityForResult(intent,
						LBSApplication.getBufferQueryCode());
				BaseActivity.this.overridePendingTransition(
						R.anim.in_right2left, R.anim.out_left2right);
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
				return true;
			}
		});
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (LBSApplication.isImeShow(lbsApplication.getContext()))
					LBSApplication.hideIme(BaseActivity.this);
				actionbarView.removeAllViews();
				View.inflate(BaseActivity.this, R.layout.actionbar,
						actionbarView);
				initMainBar();
			}
		});
	}

	/*
	 * 设置查询结果actionbar
	 */
	public void initResultBar(final String flag) {
		ImageView backImageView = (ImageView) findViewById(R.id.imgBackHome);
		final Switch resultSwitch = (Switch) findViewById(R.id.swtQuery);
		resultSwitch.setTextColor(Color.parseColor("#000000"));
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LBSApplication.setSearch(false);
				LBSApplication.clearCallout();
				results.clear();
				Intent intent = new Intent(LBSApplication.getContext(),
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				if (flag.equals("list")) {
					BaseActivity.this.overridePendingTransition(
							R.anim.in_right2left, R.anim.out_left2right);
				}
			}
		});
		resultSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							if (flag.equals("map")) {
								Intent intent = new Intent(
										getApplicationContext(),
										BufferQueryResult.class);
								intent.putExtra("QueryString",
										LBSApplication.getQueryString());
								startActivityForResult(intent,
										LBSApplication.getBufferQueryCode());
								BaseActivity.this.overridePendingTransition(
										R.anim.in_right2left,
										R.anim.out_left2right);
							}
						} else {
							if (flag.equals("list")) {
								Intent intent = new Intent(LBSApplication
										.getContext(), HomeActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								if (BufferQueryResult.getResults() != null) {
									Bundle bundle = new Bundle();
									bundle.putParcelableArrayList(
											"results",
											(ArrayList<Result>) BufferQueryResult
													.getResults());
									intent.putExtras(bundle);
								}
								setResult(LBSApplication.getBufferQueryCode(),
										intent);
								BaseActivity.this.overridePendingTransition(
										R.anim.in_right2left,
										R.anim.out_left2right);
								finish();
							}
						}
					}
				});
		if (flag.equals("map")) {
			resultSwitch.setChecked(false);
		}
	}
}