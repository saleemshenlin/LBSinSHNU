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
 * ��BaseActivity<br>
 * ������Ϊ����Activity�Ļ��࣬����ʼ������Actionbar������Slider.<br>
 * Actionbarһ������״̬,1һ��״̬,2�����ѯ����״̬,3��ѯ���״̬.<br>
 */
public class BaseActivity extends Activity {
	/**
	 * LbsApplicationʵ��
	 */
	LbsApplication lbsApplication;
	/**
	 * ��ͼչʾRelativeLayoutʵ��
	 */
	RelativeLayout rllMapView;
	/**
	 * WLAN�㿪��ʵ�����Ƿ�WLAN��
	 */
	Switch swtWifi;
	/**
	 * ��λ����ʵ�����Ƿ�����λ
	 */
	Switch swtLocation;
	/**
	 * ��ѯ���չʾ���ͣ���ͼ/�б��л�����ʵ����on���б�off����ͼ
	 */
	Switch swtResult;
	/**
	 * ����У԰�㿪��ʵ�����Ƿ������У԰
	 */
	Switch swtPhoto;
	/**
	 * ��λ��ťʵ�������ڶ�λ�ͷ���������
	 */
	RelativeLayout rllLocation;
	/**
	 * MainActionbarʵ��
	 */
	LinearLayout lnlMainActionbar;
	/**
	 * SearchActionbarʵ��
	 */
	LinearLayout lnlSearchActionbar;
	/**
	 * ��Slider��ťͼƬʵ��
	 */
	ImageView imgSliderLeft;
	/**
	 * ��Slider��ťͼƬʵ��
	 */
	ImageView imgSliderRight;
	/**
	 * SimpleSideDrawerʵ�������ڿ�������Slider
	 */
	SimpleSideDrawer mSimpleSideDrawer;
	/**
	 * Queryʵ��
	 */
	Query mQuery;
	/**
	 * Eventʵ��
	 */
	Event mEvent;
	/**
	 * ʵ������������ѯ����ͨ��ѯ���List
	 */
	static List<Event> events = new ArrayList<Event>();
	/**
	 * �����б�LocationDetail�Ƿ���ʾ
	 */
	static boolean isPopUp = false;
	/**
	 * �����ж��Ƿ��ǲ�ѯ״̬
	 */
	static boolean isSearch = false;
	/**
	 * �����ж��Ƿ���Event��Ҫ�ڵ�ͼ�϶�λ
	 */
	static boolean hasDetail;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lbsApplication = (LbsApplication) getApplication();
		mQuery = new Query();
	}

	/**
	 * �������ϸ���Actionbar��Slider��ʼ���������������
	 */
	public void initActionbarAndSlider() {
		initMainActionbar();
		initActivityRightSilder();
		initWifiSwitch();
		initLocationSwitch();
		initPhotoSwitch();
	}

	/**
	 * ���ڳ�ʼ��ResultActionbar,����ѯ���״̬�µ���<br>
	 * ���巽�����£�<br>
	 * 1)���ȶ�ResultActionbar�ķ��ذ�ť�Ͳ�ѯ״̬�л����ظ�ֵ<br>
	 * 2)���÷��ذ�ť��onClick�¼�,��յ�ͼ�ϵ�CallOut,��ѯ���List,�˻ص�HomeActivity<br>
	 * 3)����isMap���ò�ѯ״̬�л������л��¼�<br>
	 * 4)��isMap״̬�£���Ҫ����QueryString��Intent��<br>
	 * 5)��!isMap״̬��,��Ҫ���ڵ�ͼ����ʾ��Events����Bundle��<br>
	 * 
	 * @param isMap
	 *            �ж��ǽ����б�״̬���ǵ�ͼ״̬
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
	 * ���ڳ�ʼ��MainActionbar,һ��״̬�µ���<br>
	 * ���巽�����£�<br>
	 * 1)����ʵ����SimpleSideDrawer<br>
	 * 2)��������Slider��ť���Ҫ��ʾ��view<br>
	 * 3)����SearchActionbar���(Ϊһ��icon,��һ��αSearchActionbar)<br>
	 * 4)����αSearchActionbar��onClick�¼�,�����ʽ����SearchActionbar<br>
	 * 5)��������Slider��onClick�¼�,�����ʾ��Ӧ��view<br>
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
	 * ���ڳ�ʼ��SearchActionbar,�����ѯ����״̬�µ���<br>
	 * ���巽�����£�<br>
	 * 1)�������ý����ѯ״̬<br>
	 * 2)���MainActionbar,����ʼ��SearchView<br>
	 * 3)����SearchView��onClike�¼�,������QueryString��Intent��<br>
	 * 4)����SearchActionbar���ذ�ť��onClick�¼�<br>
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
	 * ���ڳ�ʼ��WLAN�㿪��<br>
	 * ���巽�����£�<br>
	 * 1)��������swtWifi��view<br>
	 * 2)����swtWifi���л��¼�,�����Ƿ���ʾWLAN��<br>
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
					Toast.makeText(BaseActivity.this, "��WLAN�㣬��ʾУ԰�����WIFI��",
							Toast.LENGTH_SHORT).show();
				} else {
					lbsApplication.mWifiLayerL.setVisible(false);
					lbsApplication.mWifiLayerS.setVisible(false);
					LbsApplication.refreshMap();
					Toast.makeText(BaseActivity.this, "�ر�WLAN�㣬����У԰�����WIFI��",
							Toast.LENGTH_SHORT).show();
				}
				mSimpleSideDrawer.toggleLeftDrawer();
			}
		});
	}

	/**
	 * ���ڳ�ʼ����λ����<br>
	 * ���巽�����£�<br>
	 * 1)��������swtLocation��view<br>
	 * 2)����swtLocation���л��¼�,�����Ǵ򿪶�λ����<br>
	 * 3)����swtLocation��onTouch�¼�,�����ж��Ƿ������gps��<br>
	 * 4)�ж϶�λ�����Ƿ��ڴ�״̬,������swtLocation���л��¼�<br>
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
							Toast.makeText(BaseActivity.this, "��λģ���ѿ���",
									Toast.LENGTH_SHORT).show();
						} else {
							LbsApplication.getLocationApi().stopLocate(
									LbsApplication.getLocationClient());
							Toast.makeText(BaseActivity.this, "��λģ���ѹر�",
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
						Toast.makeText(BaseActivity.this, "���ȴ��������ӻ�GPS��",
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
	 * ��ʼ������У԰�㿪��<br>
	 * ���巽�����£�<br>
	 * 1)��������swtPhoto��view<br>
	 * 2)����swtPhoto���л��¼�,�����Ǵ򿪶�λ����<br>
	 * 3)������У԰���״̬���������ܶ�������<br>
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
					Toast.makeText(BaseActivity.this, "������У԰������У԰����",
							Toast.LENGTH_SHORT).show();
				} else {
					imgSliderLeft.setImageResource(R.drawable.ic_action_left);
					lnlSearchActionbar.setVisibility(View.VISIBLE);
					imgSliderRight.setVisibility(View.VISIBLE);
					rllLocation.setVisibility(View.VISIBLE);
					LbsApplication.clearCallout();
					Toast.makeText(BaseActivity.this, "�ر�����У԰��������������",
							Toast.LENGTH_SHORT).show();
				}
				LbsApplication.refreshMap();
				mSimpleSideDrawer.toggleLeftDrawer();
			}
		});
	}

	/**
	 * ��ʼ��SliderRight�İ�ť<br>
	 * ���巽�����£�<br>
	 * 1)���ø�����ť��view<br>
	 * 2)���ø�����ť��onClick�¼�,������Ӧ��Bundle<br>
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