package com.shnu.lbsshnu;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MenuItem;

/**
 * ��EventListActivity<br>
 * ����Event����������Activity����ʽΪActionBar.NAVIGATION_MODE_TABS<br>
 * ��Ϊ��ѧ��������������Ӱ�ݳ���������Ʒ�γ̡� �͡��ҹ�ע�ġ�4��Tab<br>
 * ÿ��Tabӳ��һ��EventListFragment��ͨ��ViewPagerʵ������ƥ��<br>
 * ����ͨ�����Tab����������л�EventListFragment��<br>
 * 
 */
public class EventListActivity extends FragmentActivity implements TabListener,
		Query.OnFragmengToActivityListener {
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾEventListActivity
	 */
	private static final String TAG = "EventListActivity";
	/**
	 * ����һ������,����ͳ��Tab����,ֵΪ4
	 */
	private static final int MAX_TAB_SIZE = 4;
	/**
	 * ����һ������,���ڴ����Ҫ���ĸ�Tab,֪ͨ��EventListFragment��ʼ��
	 */
	private static final String ARGUMENTS_NAME = "Index";
	/**
	 * ����һ��������,�������ⲿ��Ҫֱ�Ӷ�λ��Event��ϸʱ<br>
	 * ���Event��Type����֪ͨEventListFragment,��ʾEvent��ϸ
	 */
	private static int indexTab = 0;
	/**
	 * ����һ������,�������ⲿ��Ҫֱ�Ӷ�λ��Event��ϸʱ,<br>
	 * ���Event��Id����֪ͨFragment��ʾEvent��ϸ
	 */
	public static int intEventId = 0;
	/**
	 * ����һ������,�����ж��Ƿ��Widget����
	 */
	static boolean isFromWidget = false;
	/**
	 * ʵ��һ��TabFragmentPagerAdapter
	 */
	private TabFragmentPagerAdapter mTabFragmentPagerAdapter;
	/**
	 * ʵ��һ��Intent
	 */
	private static Intent mIntent;
	/**
	 * ʵ��һ��Bundle
	 */
	private static Bundle mBundle;
	/**
	 * ʵ��һ��ActionBar
	 */
	private ActionBar mActionBar;
	/**
	 * ʵ��һ��ViewPager
	 */
	private ViewPager mViewPager;

	/**
	 * ����EventListActivity<br>
	 * 1)�ж��Ƿ�������Widget<br>
	 * 2)��ʼ��EventListActivity��View,����initView()<br>
	 * 3)������Դ��ͬȷ��indexTab,�������Slider��,���ȡ�����"Tab"<br>
	 * �����btnDetail��,���ȡ�����mEvent��Type<br>
	 * 4)����indexTab,��ʾ��Ӧ��Tab
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list_activity_view);
		mIntent = getIntent();
		if (mIntent.getAction() != null) {
			isFromWidget = true;
		}
		mBundle = mIntent.getExtras();
		initView();
		if (mBundle.getString("Tab") != null) {
			indexTab = Integer.parseInt(mBundle.getString("Tab"));
			mActionBar.getTabAt(indexTab).select();
		} else {
			Event mEvent = mBundle.getParcelable("activity");
			if (mEvent != null) {
				intEventId = mEvent.getEventId();
				switch (mEvent.getEventType()) {
				case 1:
					indexTab = 1;
					break;
				case 2:
					indexTab = 0;
					break;
				case 3:
					indexTab = 2;
					break;
				case 4:
					indexTab = 3;
					break;
				}
				mActionBar.getTabAt(indexTab).select();
			} else {
				intEventId = 0;
				mActionBar.getTabAt(0).select();
			}

		}

	}

	/**
	 * ���ڵ�ѡ��ĳ��Tabʱ,��ʾ��Ӧ��EventListFragment
	 */
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	/**
	 * ���ڵ��ActionBar�ķ��ذ�ť,���ص�HomeActivity
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			isFromWidget = false;
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			this.overridePendingTransition(R.anim.anim_in_left2right,
					R.anim.anim_out_left2right);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * ���ڵ����Event��ϸ�еĶ�λ��ť,��Ҫ��Event��EventListFragment���ݸ�EventListActivity<br>
	 * �����䴫�ݸ�HomeActivity,ʵ��Event��λ
	 * 
	 * @see com.shnu.lbsshnu.CommonUIFragment.OnHeadlineSelectedListener#
	 *      onEventLocated(long)
	 */
	public void onEventLocated(Event activity) {
		try {
			Intent intent = new Intent(this, HomeActivity.class);
			Log.i(TAG, activity.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			Bundle bundle = new Bundle();
			bundle.putParcelable("activity", activity);
			intent.putExtras(bundle);
			setResult(LbsApplication.GET_EVENT, intent);
			finish();
			this.overridePendingTransition(R.anim.anim_in_left2right,
					R.anim.anim_out_left2right);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
		}
	}

	/**
	 * ���ڳ�ʼ��EventListActivity<br>
	 * ���巽������:<br>
	 * 1)����mViewPager<br>
	 * 2)��ȡActionBar,����ActionBar��������,��icon,��������ϼ�...<br>
	 * 3)��ʼ��һ��TabFragmentPagerAdapter,��mViewPager����Ӧ����<br>
	 * 4)����mViewPager��OnPageChange�¼�,��Page�ı�ʱ,Tab����Ӧ�ĸı�<br>
	 * 5)����ʼ�� ActionBar,��ÿ��Tab��Title<br>
	 */
	private void initView() {
		mViewPager = (ViewPager) this.findViewById(R.id.vpgEvent);
		mActionBar = getActionBar();
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setIcon(R.drawable.ic_action_back);
		mActionBar.setCustomView(R.layout.query_result_actionbar_title);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mTabFragmentPagerAdapter = new TabFragmentPagerAdapter(
				getSupportFragmentManager());
		mViewPager.setAdapter(mTabFragmentPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mActionBar.setSelectedNavigationItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		for (int i = 0; i < MAX_TAB_SIZE; i++) {
			Tab tab = mActionBar.newTab();
			tab.setText(mTabFragmentPagerAdapter.getPageTitle(i))
					.setTabListener(this);
			mActionBar.addTab(tab);
		}
	}

	/**
	 * ��TabFragmentPagerAdapter<br>
	 * ���ڸ�Pager����Ӧ��EventListFragment
	 */
	private static class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			Fragment ft = null;
			switch (index) {

			default:
				ft = new EventListFragment();

				Bundle args = new Bundle();
				args.putInt(ARGUMENTS_NAME, index);
				ft.setArguments(args);

				break;
			}
			return ft;
		}

		@Override
		public int getCount() {

			return MAX_TAB_SIZE;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "ѧ������";
			case 1:
				return "��Ӱ�ݳ�";
			case 2:
				return "��Ʒ�γ�";
			case 3:
				return "�ҹ�ע��";
			default:
				return "";
			}

		}
	}

}
