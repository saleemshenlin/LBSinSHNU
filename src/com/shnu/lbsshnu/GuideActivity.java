package com.shnu.lbsshnu;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 首次加载引导界面，通過ViewPager实现
 */
public class GuideActivity extends BaseActivity implements OnPageChangeListener {
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private List<View> views;

	/**
	 * 底部小点图片
	 */
	private ImageView[] dots;

	/**
	 * 记录当前选中位置
	 */
	private int intCurrentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_view);
		initViews();
		initDots();

	}

	public void onPageScrollStateChanged(int arg0) {
	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	/**
	 * 当新的页面被选中时调用，设置底部小点选中状态
	 */
	public void onPageSelected(int arg0) {
		setCurrentDot(arg0);
	}

	/**
	 * 初始化页面
	 */
	private void initViews() {
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		// 初始化引导图片列表
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_one, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_two, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_three, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_four, null));

		// 初始化Adapter
		mViewPagerAdapter = new ViewPagerAdapter(views, this);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mViewPagerAdapter);
		// 绑定回调
		mViewPager.setOnPageChangeListener(this);
	}

	/**
	 * 初始化底部小点
	 */
	private void initDots() {
		LinearLayout lnlGuide = (LinearLayout) findViewById(R.id.lnlGuide);

		dots = new ImageView[views.size()];

		// 循环取得小点图片
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) lnlGuide.getChildAt(i);
			dots[i].setEnabled(true);// 都设为灰色
		}

		intCurrentIndex = 0;
		dots[intCurrentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	/**
	 * 设置当前Dot
	 * 
	 * @param position
	 *            当前位置
	 */
	private void setCurrentDot(int position) {
		if (position < 0 || position > views.size() - 1
				|| intCurrentIndex == position) {
			return;
		}

		dots[position].setEnabled(false);
		dots[intCurrentIndex].setEnabled(true);

		intCurrentIndex = position;
	}

}
