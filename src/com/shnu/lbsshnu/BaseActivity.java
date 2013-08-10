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
 * 类BaseActivity<br>
 * 用于作为其他Activity的基类，并初始化各个Actionbar和左右Slider.<br>
 * Actionbar一共三种状态,1一般状态,2输入查询内容状态,3查询结果状态.<br>
 */
public class BaseActivity extends Activity {
	/**
	 * LbsApplication实例
	 */
	LbsApplication lbsApplication;
	/**
	 * 地图展示RelativeLayout实例
	 */
	RelativeLayout rllMapView;
	/**
	 * WLAN层开关实例，是否开WLAN层
	 */
	Switch swtWifi;
	/**
	 * 定位开关实例，是否开启定位
	 */
	Switch swtLocation;
	/**
	 * 查询结果展示类型（地图/列表）切换开关实例，on：列表；off：地图
	 */
	Switch swtResult;
	/**
	 * 美丽校园层开关实例，是否打开美丽校园
	 */
	Switch swtPhoto;
	/**
	 * 定位按钮实例，用于定位和反向地理编码
	 */
	RelativeLayout rllLocation;
	/**
	 * MainActionbar实例
	 */
	LinearLayout lnlMainActionbar;
	/**
	 * SearchActionbar实例
	 */
	LinearLayout lnlSearchActionbar;
	/**
	 * 左Slider按钮图片实例
	 */
	ImageView imgSliderLeft;
	/**
	 * 右Slider按钮图片实例
	 */
	ImageView imgSliderRight;
	/**
	 * SimpleSideDrawer实例，用于控制左右Slider
	 */
	SimpleSideDrawer mSimpleSideDrawer;
	/**
	 * Query实例
	 */
	Query mQuery;
	/**
	 * Event实例
	 */
	Event mEvent;
	/**
	 * 实例化缓冲区查询和普通查询结果List
	 */
	static List<Event> events = new ArrayList<Event>();
	/**
	 * 用于判别LocationDetail是否显示
	 */
	static boolean isPopUp = false;
	/**
	 * 用于判断是否是查询状态
	 */
	static boolean isSearch = false;
	/**
	 * 用于判断是否有Event需要在地图上定位
	 */
	static boolean hasDetail;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lbsApplication = (LbsApplication) getApplication();
		mQuery = new Query();
	}

	/**
	 * 用于整合各个Actionbar和Slider初始化方法，方便调用
	 */
	public void initActionbarAndSlider() {
		initMainActionbar();
		initActivityRightSilder();
		initWifiSwitch();
		initLocationSwitch();
		initPhotoSwitch();
	}

	/**
	 * 用于初始化ResultActionbar,当查询结果状态下调用<br>
	 * 具体方法如下：<br>
	 * 1)首先对ResultActionbar的返回按钮和查询状态切换开关赋值<br>
	 * 2)设置返回按钮的onClick事件,清空地图上的CallOut,查询结果List,退回到HomeActivity<br>
	 * 3)根据isMap设置查询状态切换开关切换事件<br>
	 * 4)在isMap状态下，需要传入QueryString到Intent中<br>
	 * 5)在!isMap状态下,需要将在地图上显示的Events传入Bundle中<br>
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
									LbsApplication.isQueryViaLocation = true;
								else {
									LbsApplication.isQueryViaLocation = false;
								}
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
	 * 用于初始化MainActionbar,一般状态下调用<br>
	 * 具体方法如下：<br>
	 * 1)首先实例化SimpleSideDrawer<br>
	 * 2)设置左右Slider按钮点击要显示的view<br>
	 * 3)设置SearchActionbar入口(为一个icon,是一个伪SearchActionbar)<br>
	 * 4)设置伪SearchActionbar的onClick事件,点击正式进入SearchActionbar<br>
	 * 5)设置左右Slider的onClick事件,点击显示相应的view<br>
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
	 * 用于初始化SearchActionbar,输入查询内容状态下调用<br>
	 * 具体方法如下：<br>
	 * 1)首先设置进入查询状态<br>
	 * 2)清空MainActionbar,并初始化SearchView<br>
	 * 3)设置SearchView的onClike事件,并传入QueryString到Intent中<br>
	 * 4)设置SearchActionbar返回按钮的onClick事件<br>
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
							LbsApplication.isQueryViaLocation = true;
						else {
							LbsApplication.isQueryViaLocation = false;
						}
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
	 * 用于初始化WLAN层开关<br>
	 * 具体方法如下：<br>
	 * 1)首先设置swtWifi的view<br>
	 * 2)设置swtWifi的切换事件,控制是否显示WLAN层<br>
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
	 * 用于初始化定位开关<br>
	 * 具体方法如下：<br>
	 * 1)首先设置swtLocation的view<br>
	 * 2)设置swtLocation的切换事件,控制是打开定位功能<br>
	 * 3)设置swtLocation的onTouch事件,用于判断是否网络和gps打开<br>
	 * 4)判断定位功能是否处于打开状态,并调用swtLocation的切换事件<br>
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
	 * 初始化美丽校园层开关<br>
	 * 具体方法如下：<br>
	 * 1)首先设置swtPhoto的view<br>
	 * 2)设置swtPhoto的切换事件,控制是打开定位功能<br>
	 * 3)在美丽校园层打开状态下其他功能都不能用<br>
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

	/**
	 * 初始化SliderRight的按钮<br>
	 * 具体方法如下：<br>
	 * 1)设置各个按钮的view<br>
	 * 2)设置各个按钮的onClick事件,传入相应的Bundle<br>
	 */
	private void initActivityRightSilder() {
		final Bundle mBundle = new Bundle();
		LinearLayout lnlSpeech = (LinearLayout) findViewById(R.id.lnlSpeech);
		LinearLayout lnlPlay = (LinearLayout) findViewById(R.id.lnlPlay);
		LinearLayout lnlCourse = (LinearLayout) findViewById(R.id.lnlCourse);
		LinearLayout lnlLike = (LinearLayout) findViewById(R.id.lnlLike);
		LinearLayout lnlAboutLayout = (LinearLayout) findViewById(R.id.lnlAbout);
		lnlSpeech.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						EventListActivity.class);
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
						EventListActivity.class);
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
						EventListActivity.class);
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
						EventListActivity.class);
				mBundle.putString("Tab", "3");
				intent.putExtras(mBundle);
				startActivityForResult(intent, LbsApplication.GET_EVENT);
				BaseActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_left2right);
			}
		});
		lnlAboutLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseActivity.this,
						AboutActivity.class);
				startActivity(intent);
				BaseActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_left2right);
			}
		});
	}
}