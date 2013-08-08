package com.shnu.lbsshnu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

/**
 * 作为其他Activity的基类，主要用于初始化各个Actionbar和左右Slider.
 */
public class BaseActivity extends Activity {
	/**
	 * 实例化LbsApplication
	 */
	LbsApplication lbsApplication;
	/**
	 * 地图展示RelativeLayout
	 */
	RelativeLayout rllMapView;
	/**
	 * WLAN层开关，是否开WLAN层
	 */
	Switch swtWifi;
	/**
	 * 定位开关，是否开启定位
	 */
	Switch swtLocation;
	/**
	 * 查询结果展示类型（地图/列表）切换开关，on：列表；off：地图
	 */
	Switch swtResult;
	/**
	 * 美丽校园层开关，是否打开美丽校园
	 */
	Switch swtPhoto;
	/**
	 * 定位按钮，用于定位和反向地理编码
	 */
	RelativeLayout rllLocation;
	LinearLayout lnlMainActionbar;
	LinearLayout lnlSearchActionbar;
	ImageView imgSliderLeft;
	ImageView imgSliderRight;
	SimpleSideDrawer mSimpleSideDrawer;
	Query mQuery;
	static Event mEvent;
	/**
	 * 查询结果list
	 */
	static List<Event> events = new ArrayList<Event>();
	/**
	 * 判别RelativeLayout rllLocationDetail是否显示
	 */
	static boolean isPopUp = false;
	/**
	 * 判断是否是查询状态
	 */
	static boolean isSearch = false;
	/**
	 * 判断是否有Event需要在地图上定位
	 */
	static boolean hasDetail;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lbsApplication = (LbsApplication) getApplication();
		mQuery = new Query();
	}

	/**
	 * 初始化查询结果Actionbar
	 * 
	 * @param isMap
	 *            判断是进入列表状态还是地图状态
	 */
	public void initResultActionbar(final boolean isMap) {
		ImageView imgBackHome = (ImageView) findViewById(R.id.imgBackHome);
		swtResult = (Switch) findViewById(R.id.swtQuery);
		swtResult.setTextColor(Color.parseColor("#000000"));
		imgBackHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isSearch = false;
				LbsApplication.clearCallout();
				if (!events.isEmpty()) {
					events = new ArrayList<Event>();
				}
				Intent mIntent = new Intent(LbsApplication.getContext(),
						HomeActivity.class);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(mIntent);
				if (!isMap) {
					BaseActivity.this.overridePendingTransition(
							R.anim.anim_in_left2right,
							R.anim.anim_out_left2right);
				}
			}
		});
		swtResult
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							if (isMap) {
								Intent intent = new Intent(
										getApplicationContext(),
										ResultActivity.class);
								if (LbsApplication.isLocateStart())
									intent.setAction(LbsApplication.QUERY_WITH_LOCATION_FLAG);
								intent.putExtra("QueryString",
										LbsApplication.getQueryString());
								startActivityForResult(intent,
										LbsApplication.GET_QUERY);
								BaseActivity.this.overridePendingTransition(
										R.anim.anim_in_right2left,
										R.anim.anim_out_left2right);
							}
						} else {
							if (!isMap) {
								Intent intent = new Intent(LbsApplication
										.getContext(), HomeActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								if (events != null) {
									Bundle bundle = new Bundle();
									bundle.putParcelableArrayList("results",
											(ArrayList<Event>) events);
									intent.putExtras(bundle);
								}
								setResult(LbsApplication.GET_QUERY, intent);
								finish();
								BaseActivity.this.overridePendingTransition(
										R.anim.anim_in_left2right,
										R.anim.anim_out_left2right);
							}
						}
					}
				});
		if (isMap) {
			swtResult.setChecked(false);
		}
	}

	/**
	 * 初始化MainActionbar和左右Slider
	 */
	public void initActionbarAndSlider() {
		initMainActionbar();
		initActivityRightSilder();
		initWifiSwitch();
		initLocationSwitch();
		initPhotoSwitch();
	}

	/**
	 * 初始化MainActionbar
	 */
	private void initMainActionbar() {
		mSimpleSideDrawer = new SimpleSideDrawer(this);
		mSimpleSideDrawer.setLeftBehindContentView(R.layout.sliderleft);
		mSimpleSideDrawer.setRightBehindContentView(R.layout.sliderright);
		imgSliderLeft = (ImageView) findViewById(R.id.imgSliderLeft);
		imgSliderRight = (ImageView) findViewById(R.id.imgSliderRight);
		lnlSearchActionbar = (LinearLayout) findViewById(R.id.lnlSearch);
		lnlSearchActionbar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPopUp) {
					LbsApplication.clearCallout();
					mQuery.moveLocationDetail(
							-LbsApplication.Dp2Px(BaseActivity.this, 50), 0,
							rllMapView, rllLocation);
					isPopUp = false;
					hasDetail = false;
				}
				initSearchActionbar();
			}
		});
		imgSliderLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSimpleSideDrawer.toggleLeftDrawer();
			}
		});
		imgSliderRight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mSimpleSideDrawer.toggleRightDrawer();
			}
		});
	}

	/**
	 * 初始化WLAN层开关
	 */
	private void initWifiSwitch() {
		swtWifi = (Switch) findViewById(R.id.swtWifi);
		swtWifi.setTextColor(Color.WHITE);
		swtWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					lbsApplication.mWifiLayerL.setVisible(true);
					lbsApplication.mWifiLayerS.setVisible(true);
					LbsApplication.refreshMap();
					Toast.makeText(BaseActivity.this, "打开WLAN层，显示校园内免费WIFI点",
							Toast.LENGTH_SHORT).show();
				} else {
					lbsApplication.mWifiLayerL.setVisible(false);
					lbsApplication.mWifiLayerS.setVisible(false);
					LbsApplication.refreshMap();
					Toast.makeText(BaseActivity.this, "关闭WLAN层，隐藏校园内免费WIFI点",
							Toast.LENGTH_SHORT).show();
				}
				mSimpleSideDrawer.toggleLeftDrawer();
			}
		});
	}

	/**
	 * 初始化定位开关
	 */
	private void initLocationSwitch() {
		swtLocation = (Switch) findViewById(R.id.swtLocation);
		swtLocation.setTextColor(Color.WHITE);
		swtLocation
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							LbsApplication.getLocationApi().startLocate(
									LbsApplication.getLocationClient());
							Toast.makeText(BaseActivity.this, "定位模块已开启",
									Toast.LENGTH_SHORT).show();
						} else {
							LbsApplication.getLocationApi().stopLocate(
									LbsApplication.getLocationClient());
							Toast.makeText(BaseActivity.this, "定位模块已关闭",
									Toast.LENGTH_SHORT).show();
						}
						LbsApplication.clearTrackingLayer();
					}

				});
		swtLocation.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (!LbsApplication.isNetWork()
							&& !LbsApplication.isGPSOpen()
							&& !LbsApplication.isLocateStart()) {
						Toast.makeText(BaseActivity.this, "请先打开网络连接或GPS！",
								Toast.LENGTH_SHORT).show();
						return true;
					}
				}
				return false;
			}
		});
		if (LbsApplication.isLocateStart()) {
			swtLocation.setChecked(true);
		} else {
			swtLocation.setChecked(false);
		}
	}

	/**
	 * 初始化SliderRight的按钮
	 */
	private void initActivityRightSilder() {
		final Bundle mBundle = new Bundle();
		LinearLayout lnlSpeech = (LinearLayout) findViewById(R.id.lnlSpeech);
		LinearLayout lnlPlay = (LinearLayout) findViewById(R.id.lnlPlay);
		LinearLayout lnlCourse = (LinearLayout) findViewById(R.id.lnlCourse);
		LinearLayout lnlLike = (LinearLayout) findViewById(R.id.lnlLike);
		lnlSpeech.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						EventListView.class);
				mBundle.putString("Tab", "0");
				intent.putExtras(mBundle);
				startActivityForResult(intent, LbsApplication.GET_EVENT);
				BaseActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_left2right);
			}
		});
		lnlPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						EventListView.class);
				mBundle.putString("Tab", "1");
				intent.putExtras(mBundle);
				startActivityForResult(intent, LbsApplication.GET_EVENT);
				BaseActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_left2right);
			}
		});
		lnlCourse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						EventListView.class);
				mBundle.putString("Tab", "2");
				intent.putExtras(mBundle);
				startActivityForResult(intent, LbsApplication.GET_EVENT);
				BaseActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_left2right);
			}
		});
		lnlLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						EventListView.class);
				mBundle.putString("Tab", "3");
				intent.putExtras(mBundle);
				startActivityForResult(intent, LbsApplication.GET_EVENT);
				BaseActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_left2right);
			}
		});
	}

	/**
	 * 初始化查询输入Actionbar
	 */
	@SuppressWarnings("static-access")
	private void initSearchActionbar() {
		isSearch = true;
		lnlMainActionbar.removeAllViews();
		View.inflate(BaseActivity.this, R.layout.searchbar, lnlMainActionbar);
		SearchManager searchManager = (SearchManager) getSystemService(LbsApplication
				.getContext().SEARCH_SERVICE);
		SearchView mSearchView = (SearchView) findViewById(R.id.sevQuery);
		ImageView imgSearchBack = (ImageView) findViewById(R.id.imgSearchBack);
		mSearchView.setBackgroundColor(Color.parseColor("#000000"));
		mSearchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		mSearchView
				.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

					@Override
					public boolean onQueryTextSubmit(String query) {
						LbsApplication.setQueryString(query.trim());
						Intent mIntent = new Intent(getApplicationContext(),
								ResultActivity.class);
						if (LbsApplication.isLocateStart())
							mIntent.setAction(LbsApplication.QUERY_WITH_LOCATION_FLAG);
						mIntent.putExtra("QueryString", query.trim());
						startActivityForResult(mIntent,
								LbsApplication.GET_QUERY);
						BaseActivity.this.overridePendingTransition(
								R.anim.anim_in_right2left,
								R.anim.anim_out_left2right);
						return true;

					}

					@Override
					public boolean onQueryTextChange(String newText) {
						// TODO Auto-generated method stub
						return false;
					}
				});
		mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {

			@Override
			public boolean onClose() {
				return true;
			}
		});
		imgSearchBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (LbsApplication.isImeShow(lbsApplication.getContext()))
					LbsApplication.hideIme(BaseActivity.this);
				lnlMainActionbar.removeAllViews();
				View.inflate(BaseActivity.this, R.layout.main_actionbar,
						lnlMainActionbar);
				initActionbarAndSlider();
			}
		});
	}

	/**
	 * 初始化美丽校园层开关
	 */
	private void initPhotoSwitch() {
		swtPhoto = (Switch) findViewById(R.id.swtPhoto);
		swtPhoto.setTextColor(Color.WHITE);
		swtPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					if (isPopUp) {
						LbsApplication.clearCallout();
						mQuery.moveLocationDetail(
								-LbsApplication.Dp2Px(BaseActivity.this, 50),
								0, rllMapView, rllLocation);
						isPopUp = false;
						hasDetail = false;
					}
					imgSliderLeft.setImageResource(R.drawable.ic_action_back);
					lnlSearchActionbar.setVisibility(View.GONE);
					imgSliderRight.setVisibility(View.GONE);
					rllLocation.setVisibility(View.GONE);
					mQuery.initPhotoCallout(BaseActivity.this);
					Toast.makeText(BaseActivity.this, "打开美丽校园，欣赏校园美景",
							Toast.LENGTH_SHORT).show();
				} else {
					imgSliderLeft.setImageResource(R.drawable.ic_action_left);
					lnlSearchActionbar.setVisibility(View.VISIBLE);
					imgSliderRight.setVisibility(View.VISIBLE);
					rllLocation.setVisibility(View.VISIBLE);
					LbsApplication.clearCallout();
					Toast.makeText(BaseActivity.this, "关闭美丽校园，体验其他服务",
							Toast.LENGTH_SHORT).show();
				}
				LbsApplication.refreshMap();
				mSimpleSideDrawer.toggleLeftDrawer();
			}
		});
	}

}