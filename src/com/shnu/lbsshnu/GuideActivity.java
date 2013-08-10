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
 * 类GuideActivity<br>
 * 用于首次加载时的引导界面<br>
 * 在一个Activity内,包含4个View<br>
 * 通过ViewPager实现4个页面左右相互切换
 */
public class GuideActivity extends BaseActivity implements OnPageChangeListener {
	/**
	 * 定义一个常数,用于记录当前选中位置
	 */
	private int intCurrentIndex;
	/**
	 * 实例一个ViewPager
	 */
	private ViewPager mViewPager;
	/**
	 * 实例一个ViewPagerAdapter
	 */
	private ViewPagerAdapter mViewPagerAdapter;
	/**
	 * 实例一个List<View>,用于存放4个View
	 */
	private List<View> views;

	/**
	 * 实例一个ImageView[],用于存放底部切换的小点
	 */
	private ImageView[] dots;

	/**
	 * 创建GuideActivity<br>
	 * 1)调用initViews(),初始化View<br>
	 * 2)调用initDots(),初始化底部小点<br>
	 */
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
	 * 用于当新的页面被选中时，调用setCurrentDot(),设置底部小点选中状态
	 */
	public void onPageSelected(int arg0) {
		setCurrentDot(arg0);
	}

	/**
	 * 用于初始化页面<br>
	 * 具体方法如下:<br>
	 * 1)初始化引导图片列表views;<br>
	 * 2)初始化mViewPagerAdapter<br>
	 * 3)mViewPager绑定OnPageChange事件<br>
	 */
	private void initViews() {
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_one, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_two, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_three, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_four, null));

		mViewPagerAdapter = new ViewPagerAdapter(views, this);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
	}

	/**
	 * 用于 初始化底部小点<br>
	 * 具体方法如下:<br>
	 * 1)根据view数量初始化小点图片列表dots<br>
	 * 2)循环取得小点图片,将小点都设为灰色<br>
	 * 3)view处于选中状态时,将小点设置为白色<br>
	 */
	private void initDots() {
		LinearLayout lnlGuide = (LinearLayout) findViewById(R.id.lnlGuide);

		dots = new ImageView[views.size()];

		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) lnlGuide.getChildAt(i);
			dots[i].setEnabled(true);
		}

		intCurrentIndex = 0;
		dots[intCurrentIndex].setEnabled(false);
	}

	/**
	 * 用于view切换时,设置当前小点的颜色
	 * 
	 * @param position
	 *            view当前位置
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
