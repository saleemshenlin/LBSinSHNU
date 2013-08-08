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
 * Event����������Activity������ActiobarTab����Ϊ��ѧ��������������Ӱ�ݳ���������Ʒ�γ̡�
 * �͡��ҹ�ע�ġ�4TAB��ÿ��Tab��Fragment���֣�ͨ��ViewPager���������л���
 * 
 */
public class EventListView extends FragmentActivity implements TabListener,
		Query.OnFragmengToActivityListener {
	/**
	 * ���ⲿ��Ҫֱ�Ӷ�λ��Event��ϸʱ�����EventId����֪ͨFragment��ʾEvent��ϸ
	 */
	public static int intEventId = 0;
	private ActionBar actionBar;
	private ViewPager mViewPager;
	/**
	 * ActiobarTab 4��
	 */
	private static final int MAX_TAB_SIZE = 4;
	/**
	 * ���ݸ�Fragment����
	 */
	private static final String ARGUMENTS_NAME = "Index";
	private static int indexTab = 0;
	private static final String TAG = "ActivityListView";
	private TabFragmentPagerAdapter mTabFragmentPagerAdapter;
	private static Intent mIntent;
	private static Bundle mBundle;
	/**
	 * �ж��Ƿ��widget����
	 */
	static boolean isFromWidget = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list_fragment);
		mIntent = getIntent();
		if (mIntent.getAction() != null) {
			isFromWidget = true;
		}
		mBundle = mIntent.getExtras();
		initView();
		if (mBundle.getString("Tab") != null) {
			indexTab = Integer.parseInt(mBundle.getString("Tab"));
		} else {
			Event activity = mBundle.getParcelable("activity");
			intEventId = activity.getEventId();
			switch (activity.getEventType()) {
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
		}
		actionBar.getTabAt(indexTab).select();
	}

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
	 * �����ϼ�
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

	/*
	 * (non-Javadoc) Fragment �� activity ������ͨ��
	 * 
	 * @see com.shnu.lbsshnu.CommonUIFragment.OnHeadlineSelectedListener#
	 * onArticleSelected(long)
	 */
	public void onArticleSelected(Event activity) {
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
	 * ��ʼ��ActionbarTab
	 * 
	 */
	private void initView() {
		mViewPager = (ViewPager) this.findViewById(R.id.vpgEvent);
		actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setIcon(R.drawable.ic_action_back);
		actionBar.setCustomView(R.layout.query_result_actionbar_title);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mTabFragmentPagerAdapter = new TabFragmentPagerAdapter(
				getSupportFragmentManager());
		mViewPager.setAdapter(mTabFragmentPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

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
		// ��ʼ�� ActionBar
		for (int i = 0; i < MAX_TAB_SIZE; i++) {
			Tab tab = actionBar.newTab();
			tab.setText(mTabFragmentPagerAdapter.getPageTitle(i))
					.setTabListener(this);
			actionBar.addTab(tab);
		}
	}

	/**
	 * ActionbarTab Adapter
	 * 
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
