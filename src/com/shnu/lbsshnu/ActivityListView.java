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

public class ActivityListView extends FragmentActivity implements TabListener,
		CommonUIFragment.OnFragmeng2ActivityListener {

	private ViewPager viewPager;
	private static final int MAX_TAB_SIZE = 4;
	private static final String ARGUMENTS_NAME = "Index";
	private static int indexTab = 0;
	private static int activityId = 0;
	private static final String TAG = "ActivityListView";
	private TabFragmentPagerAdapter tabFragmentPagerAdapter;
	Intent intent;
	static Bundle tabBundle;
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activityview);
		intent = getIntent();
		tabBundle = intent.getExtras();
		initView();
		if (tabBundle.getString("Tab") != null) {
			indexTab = Integer.parseInt(tabBundle.getString("Tab"));
		} else {
			ActivityClass activity = tabBundle.getParcelable("activity");
			activityId = activity.getActivityId();
			switch (activity.getActivityType()) {
			case 1:
				indexTab = 1;
				break;
			case 2:
				indexTab = 0;
			case 3:
				indexTab = 2;
			case 4:
				indexTab = 3;
			default:
				break;
			}
		}
		actionBar.getTabAt(indexTab).select();
	}

	private void initView() {
		viewPager = (ViewPager) this.findViewById(R.id.pager);
		actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setIcon(R.drawable.ic_action_back);
		actionBar.setCustomView(R.layout.listviewbar);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		tabFragmentPagerAdapter = new TabFragmentPagerAdapter(
				getSupportFragmentManager());
		viewPager.setAdapter(tabFragmentPagerAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				actionBar.setSelectedNavigationItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		// 初始化 ActionBar
		for (int i = 0; i < MAX_TAB_SIZE; i++) {
			Tab tab = actionBar.newTab();
			tab.setText(tabFragmentPagerAdapter.getPageTitle(i))
					.setTabListener(this);
			actionBar.addTab(tab);
		}
	}

	public static class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			Fragment ft = null;
			switch (index) {

			default:
				ft = new CommonUIFragment();

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

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	/*
	 * 返回上级
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			this.overridePendingTransition(R.anim.popup_exit, R.anim.popup_exit);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc) Fragment 和 activity 的数据通信
	 * 
	 * @see com.shnu.lbsshnu.CommonUIFragment.OnHeadlineSelectedListener#
	 * onArticleSelected(long)
	 */
	public void onArticleSelected(ActivityClass activity) {
		Intent intent = new Intent(this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		Log.i(TAG, activity.toString());
		Bundle bundle = new Bundle();
		bundle.putParcelable("activity", activity);
		intent.putExtras(bundle);
		setResult(LBSApplication.getRequestCode(), intent);
		this.overridePendingTransition(R.anim.popup_exit, R.anim.popup_exit);
		finish();
	}

	public static int getActivityId() {
		return activityId;
	}

	public static void setActivityId(int value) {
		activityId = value;
	}
}
