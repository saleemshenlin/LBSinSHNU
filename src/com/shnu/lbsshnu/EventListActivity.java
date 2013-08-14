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
 * 类EventListActivity<br>
 * 用于Event检索的主体Activity，形式为ActionBar.NAVIGATION_MODE_TABS<br>
 * 分为“学术讲座”，“电影演出”，“精品课程” 和“我关注的”4个Tab<br>
 * 每个Tab映射一个EventListFragment，通过ViewPager实现两者匹配<br>
 * 可以通过点击Tab或进行左右切换EventListFragment。<br>
 * 
 */
public class EventListActivity extends FragmentActivity implements TabListener,
		Query.OnFragmengToActivityListener {
	/**
	 * 定义一个标签,在LogCat内表示EventListActivity
	 */
	private static final String TAG = "EventListActivity";
	/**
	 * 定义一个常数,用于统计Tab个数,值为4
	 */
	private static final int MAX_TAB_SIZE = 4;
	/**
	 * 定义一个常量,用于存放需要用哪个Tab,通知给EventListFragment初始化
	 */
	private static final String ARGUMENTS_NAME = "Index";
	/**
	 * 定义一个常数数,用于在外部需要直接定位到Event详细时<br>
	 * 存放Event的Type，并通知EventListFragment,显示Event详细
	 */
	private static int indexTab = 0;
	/**
	 * 定义一个常数,用于在外部需要直接定位到Event详细时,<br>
	 * 存放Event的Id，并通知Fragment显示Event详细
	 */
	public static int intEventId = 0;
	/**
	 * 定义一个常量,用于判断是否从Widget传入
	 */
	static boolean isFromWidget = false;
	/**
	 * 实例一个TabFragmentPagerAdapter
	 */
	private TabFragmentPagerAdapter mTabFragmentPagerAdapter;
	/**
	 * 实例一个Intent
	 */
	private static Intent mIntent;
	/**
	 * 实例一个Bundle
	 */
	private static Bundle mBundle;
	/**
	 * 实例一个ActionBar
	 */
	private ActionBar mActionBar;
	/**
	 * 实例一个ViewPager
	 */
	private ViewPager mViewPager;

	/**
	 * 创建EventListActivity<br>
	 * 1)判断是否来自于Widget<br>
	 * 2)初始化EventListActivity的View,调用initView()<br>
	 * 3)根据来源不同确定indexTab,如果从右Slider来,则读取传入的"Tab"<br>
	 * 如果从btnDetail来,则读取传入的mEvent的Type<br>
	 * 4)根据indexTab,显示相应的Tab
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
	 * 用于当选定某个Tab时,显示相应的EventListFragment
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
	 * 用于点击ActionBar的返回按钮,返回到HomeActivity
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
	 * 用于当点击Event详细中的定位按钮,需要将Event从EventListFragment传递给EventListActivity<br>
	 * 并将其传递给HomeActivity,实现Event定位
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
	 * 用于初始化EventListActivity<br>
	 * 具体方法如下:<br>
	 * 1)设置mViewPager<br>
	 * 2)获取ActionBar,并对ActionBar进行设置,如icon,点击返回上级...<br>
	 * 3)初始化一个TabFragmentPagerAdapter,给mViewPager绑相应内容<br>
	 * 4)设置mViewPager的OnPageChange事件,当Page改变时,Tab做相应的改变<br>
	 * 5)最后初始化 ActionBar,给每个Tab赋Title<br>
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
	 * 类TabFragmentPagerAdapter<br>
	 * 用于给Pager绑相应的EventListFragment
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
				return "学术讲座";
			case 1:
				return "电影演出";
			case 2:
				return "精品课程";
			case 3:
				return "我关注的";
			default:
				return "";
			}

		}
	}

}
